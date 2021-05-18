package com.hacker.noonbest.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/v1")
public class Beststories {

    private static final String TOPIC = "beststories";

    private final List<String> CONSUMED_MESSAGES = new ArrayList<>();

    @Autowired
    public KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(Beststories.class);

    @Autowired
    public Beststories(KafkaTemplate<String, String> template) {
        this.kafkaTemplate = template;
    }



    @GetMapping("/beststories")
    public void hit(Counter counter, Timer getKafkaPublishTime) throws IOException {
        long start = System.currentTimeMillis();
        logger.info("Inside Best Stories publisher");
        final String uri = "https://hacker-news.firebaseio.com/v0/beststories.json?print=pretty";

        String output="";
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        result = result.replaceAll("\\[","").replaceAll("\\]","");
        String[] arrOfStr = result.split(",");
        System.out.println("Hitting HackerNoon API for 500 Best Stories .......................");
        for (String a : arrOfStr){
            counter.increment();
            logger.info("Publishing message to Kafka Topic: "+TOPIC+" with message id: "+a);
            kafkaTemplate.send(TOPIC, a);
        }
        getKafkaPublishTime.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
    }

}
