package com.csye7125.webapp.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye7125.webapp.model.User;
import com.csye7125.webapp.model.UserReturn;
import com.csye7125.webapp.repository.UserRepository;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1")
public class UserController {

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final Counter getTotalUserCounter;
    private final Counter getSuccessUserCounter;
    private final Counter getFailedUserCounter;
    private final Timer getUserTimer;

    private final Counter postTotalUserCounter;
    private final Counter postSuccessUserCounter;
    private final Counter postFailedUserCounter;
    private final Timer postUserTimer;

    private final Counter putTotalUserCounter;
    private final Counter putSuccessUserCounter;
    private final Counter putFailedUserCounter;
    private final Timer putUserTimer;


    public UserController(MeterRegistry meterRegistry){
        long start = System.currentTimeMillis();
        getTotalUserCounter = meterRegistry.counter("get_total_user_counter");
        getSuccessUserCounter = meterRegistry.counter("get_successful_user_counter");
        getFailedUserCounter = meterRegistry.counter("get_failed_user_counter");
        this.getUserTimer = meterRegistry.timer("getUser.timer", "Get User", "timer");

        postTotalUserCounter = meterRegistry.counter("post_total_user_counter");
        postSuccessUserCounter = meterRegistry.counter("post_successful_user_counter");
        postFailedUserCounter = meterRegistry.counter("post_failed_user_counter");
        this.postUserTimer = meterRegistry.timer("postUser.timer", "Post User", "timer");

        putTotalUserCounter = meterRegistry.counter("put_total_user_counter");
        putSuccessUserCounter = meterRegistry.counter("put_successful_user_counter");
        putFailedUserCounter = meterRegistry.counter("put_failed_user_counter");
        this.putUserTimer = meterRegistry.timer("putUser.timer", "Put User", "timer");
    }

    @GetMapping("/liveness/test")
    public ResponseEntity livenesstest() {
        return ResponseEntity.ok("Liveness Test Connection");
    }

    @GetMapping("/readiness/test")
    public ResponseEntity readinesstest() {
//        repository.findone();
        return ResponseEntity.ok("Readiness Test Connection");
    }

    @GetMapping("/user/self")
    public ResponseEntity<UserReturn> getUser(@RequestHeader HttpHeaders headers) {
        long start = System.currentTimeMillis();
        getTotalUserCounter.increment();
        logger.info("Inside GET user API");
        String username = "";
        String password = "";
        User _user = null;
        final String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            username = values[0];
            password = values[1];
        }
        _user = repository.findByEmail(username);
        if (repository.existsByEmail(username) && encoder.matches(password, _user.getPassword())) {
            UserReturn returnUser = new UserReturn(_user.getId(), _user.getEmail(), _user.getFirst_name(), _user.getLast_name(), _user.getAccount_created(), _user.getAccount_updated());
            logger.info("Get user success"+returnUser.getFirst_name());
            getSuccessUserCounter.increment();
            getUserTimer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return ResponseEntity.ok().body(returnUser);

        } else {
            getFailedUserCounter.increment();
            logger.error("Authentication error! Please check username and password");
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping(value = "/user")
    public ResponseEntity<UserReturn> postUser(@Valid @RequestBody User user) throws InterruptedException {
        long start = System.currentTimeMillis();
        postTotalUserCounter.increment();
        logger.info("Inside POST user API");
        String regex = "^(.+)@(.+)$";

        Pattern pattern = Pattern.compile(regex);


        if (!user.getEmail().matches(regex)) {
            logger.error("Please check input email");
            postFailedUserCounter.increment();
            return ResponseEntity.badRequest().body(null);
        }
        if (repository.existsByEmail(user.getEmail())) {
            logger.error("Entered email already exist in the database");
            postFailedUserCounter.increment();
            return ResponseEntity.badRequest().body(null);
        }
        String specialChars = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
        if (!user.getPassword().matches(specialChars)) {
            logger.error("Entered password is not strong enough. Try again!");
            postFailedUserCounter.increment();
            return ResponseEntity.badRequest().body(null);
        }
        User _user = repository.save(new User(user.getEmail(), encoder.encode(user.getPassword()), user.getFirst_name(), user.getLast_name(), "", "", null));
        UserReturn returnUser = new UserReturn(_user.getId(), _user.getEmail(), _user.getFirst_name(), _user.getLast_name(), _user.getAccount_created(), _user.getAccount_updated());
        logger.info("User creation success!");
        postSuccessUserCounter.increment();
        postUserTimer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok().body(returnUser);
    }

    @PutMapping(value = "/user/self")
    public ResponseEntity<String> updateUser(@RequestBody User user, @RequestHeader HttpHeaders headers) {
        long start = System.currentTimeMillis();
        putTotalUserCounter.increment();
        logger.info("Inside PUT user API");
        String username = "";
        String password = "";
        User _user = null;
        final String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            username = values[0];
            password = values[1];
        }
        _user = repository.findByEmail(username);
        if (_user != null && encoder.matches(password, _user.getPassword())) {
            if (repository.existsByEmail(user.getEmail())) {

//	            return ResponseEntity.badRequest().body("Existing Email Address");

                String specialChars = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
                if (!user.getPassword().matches(specialChars)) {
                    putFailedUserCounter.increment();
                    logger.error("Entered password is not strong enough. Try again!");
                    return ResponseEntity.badRequest().body("Please enter Strong password");
                }


//	        _user.setEmail(user.getEmail());
                _user.setPassword(encoder.encode(user.getPassword()));
                _user.setFirst_name(user.getFirst_name());
                _user.setLast_name(user.getLast_name());
                _user.setAccount_updated(LocalDateTime.now().toString());

                repository.save(_user);
                logger.info("User update success");
                putSuccessUserCounter.increment();
                putUserTimer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                return ResponseEntity.noContent().build();
            } else {
                putFailedUserCounter.increment();
                logger.error("Email cannot be updated in PUT request");
                return ResponseEntity.badRequest().body("Email cannot be updated");
            }
        } else {
            putFailedUserCounter.increment();
            logger.error("Authentication error! Please check username and password");
            return ResponseEntity.badRequest().body("Please enter correct credentials");
        }

    }


}

