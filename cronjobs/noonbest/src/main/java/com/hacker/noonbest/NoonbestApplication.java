package com.hacker.noonbest;

import com.hacker.noonbest.controller.Beststories;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
public class NoonbestApplication {
	@Autowired
	public KafkaTemplate<String, String> kafkaTemplate;

	private final Counter counter;
	private final Timer getKafkaPublishTime;

	public NoonbestApplication(MeterRegistry meterRegistry){
		this.counter = meterRegistry.counter("Best_stories_counter");
		this.getKafkaPublishTime = meterRegistry.timer("getKafkaPublish_BestStories.timer", "Kafka Publish Timer", "timer");
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(NoonbestApplication.class, args);

	}
	@EventListener(ContextRefreshedEvent.class)
	public void doSomethingAfterStartup() throws IOException {
		Beststories beststories=new Beststories(kafkaTemplate);
		beststories.hit(counter, getKafkaPublishTime);
	}

}
