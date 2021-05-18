package com.csye7125.webapp.controller;

import com.csye7125.webapp.model.Alert;
import com.csye7125.webapp.model.AlertReturn;
import com.csye7125.webapp.model.User;
import com.csye7125.webapp.model.UserReturn;
import com.csye7125.webapp.repository.AlertRepository;
import com.csye7125.webapp.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1")
public class AlertController {

    @Autowired
    AlertRepository alertRepository;

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    private final Counter postTotalAlertCounter;
    private final Counter postSuccessAlertCounter;
    private final Counter postFailedAlertCounter;
    private final Timer postAlertTimer;

    private final Counter deleteTotalAlertCounter;
    private final Counter deleteSuccessAlertCounter;
    private final Counter deleteFailedAlertCounter;
    private final Timer deleteAlertTimer;

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    public AlertController(MeterRegistry meterRegistry){
        long start = System.currentTimeMillis();
        postTotalAlertCounter = meterRegistry.counter("post_total_alert_counter");
        postSuccessAlertCounter = meterRegistry.counter("post_successful_alert_counter");
        postFailedAlertCounter = meterRegistry.counter("post_failed_alert_counter");
        this.postAlertTimer = meterRegistry.timer("postAlert.timer", "Post Alert", "timer");

        deleteTotalAlertCounter = meterRegistry.counter("delete_total_alert_counter");
        deleteSuccessAlertCounter = meterRegistry.counter("delete_successful_alert_counter");
        deleteFailedAlertCounter = meterRegistry.counter("delete_failed_alert_counter");
        this.deleteAlertTimer = meterRegistry.timer("deleteAlert.timer", "Delete Alert", "timer");
    }

    @PostMapping(value = "/alert")
    public ResponseEntity<AlertReturn> postAlert(@Valid @RequestBody Alert alert, @RequestHeader HttpHeaders headers) {
        long start = System.currentTimeMillis();
        postTotalAlertCounter.increment();
        logger.info("Inside create alert");
        Set<String> alertCategory = new HashSet();
        AlertReturn alertReturn= null;
        alertCategory.add("New");
        alertCategory.add("Top");
        alertCategory.add("Best");
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
            if(alertCategory.contains(alert.getCategory())){
                Alert alertSave = new Alert(alert.getCategory(), alert.getKeyword(), alert.getExpiry(), alert.getAlert_created(), alert.getAlert_expiry(), _user);
                Alert al = alertRepository.save(alertSave);
                UserReturn returnUser = new UserReturn(_user.getId(), _user.getEmail(), _user.getFirst_name(), _user.getLast_name(), _user.getAccount_created(), _user.getAccount_updated());
                alertReturn = new AlertReturn(al.getId(), al.getCategory(), al.getKeyword(), al.getExpiry(), al.getAlert_created(), al.getAlert_expiry(), returnUser);

            }
            else{
                postFailedAlertCounter.increment();
                logger.info("Please enter correct category for alert");
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            postFailedAlertCounter.increment();
            logger.error("Authentication error! Please check username and password");
            return ResponseEntity.badRequest().body(null);
        }
        logger.info("Alert created successfully");
        postSuccessAlertCounter.increment();
        postAlertTimer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok().body(alertReturn);
    }

    @DeleteMapping(value = "/delete/{alertId}")
    public ResponseEntity<String> deleteAlert(@PathVariable (value = "alertId") String alertId, @RequestHeader HttpHeaders headers) {
        long start = System.currentTimeMillis();
        deleteTotalAlertCounter.increment();
        logger.info("Inside delete alert");
        String username = "";
        String password = "";
        UUID Id = UUID.fromString(alertId);
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
            Optional<Alert> alert = null;
            alert = alertRepository.findById(Id);



            if(!alert.isPresent()){
                deleteFailedAlertCounter.increment();
                logger.error("Alert is not present in the database");
                return ResponseEntity.notFound().build();
            }
            else{
                if( alert.get().getUser().getId()==_user.getId())
                    alertRepository.delete(alert.get());
                else{
                    deleteFailedAlertCounter.increment();
                    logger.error("Logged in user cannot delete this alert");
                    return ResponseEntity.notFound().build();
                }
            }
        } else {
            deleteFailedAlertCounter.increment();
            logger.error("Authentication error! Please check username and password");
            return ResponseEntity.badRequest().body("User credential not matched");
        }
        logger.info("Alert deleted successfully");
        deleteSuccessAlertCounter.increment();
        deleteAlertTimer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok().body("Alert with Id: "+alertId+" is deleted successfully");
    }
}
