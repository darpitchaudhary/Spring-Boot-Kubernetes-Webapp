package com.hacker.noon;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import  com.hacker.noon.controller.FetchDataController;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

@SpringBootApplication
public class NoonApplication {

	@Autowired
	public KafkaTemplate<String, String> kafkaTemplate;

	private final Counter counter;
	private final Timer getKafkaPublishTime;

	public NoonApplication(MeterRegistry meterRegistry){
		this.counter = meterRegistry.counter("Top_stories_counter");
		this.getKafkaPublishTime = meterRegistry.timer("getKafkaPublish_TopStories.timer", "Kafka Publish Timer", "timer");
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(NoonApplication.class, args);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void doSomethingAfterStartup() throws IOException {
		FetchDataController fs= new FetchDataController(kafkaTemplate);
		fs.hit(counter, getKafkaPublishTime);
	}

}
