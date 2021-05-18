package com.hacker.noonnew;

import com.hacker.noonnew.controller.Newstories;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

@SpringBootApplication
public class NoonnewApplication {

	@Autowired
	public KafkaTemplate<String, String> kafkaTemplate;

	private final Counter counter;
	private final Timer getKafkaPublishTime;

	public NoonnewApplication(MeterRegistry meterRegistry){
		this.counter = meterRegistry.counter("New_stories_counter");
		this.getKafkaPublishTime = meterRegistry.timer("getKafkaPublish_NewStories.timer", "Kafka Publish Timer", "timer");
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(NoonnewApplication.class, args);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void doSomethingAfterStartup() throws IOException {
		Newstories news= new Newstories(kafkaTemplate);
		news.hit(counter, getKafkaPublishTime);
	}

}
