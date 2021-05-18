package com.kafkacsye7125.processorwebapp;

import com.kafkacsye7125.processorwebapp.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.function.Consumer;

@SpringBootApplication
public class ProcessorWebappApplication {
	@Autowired
	ConsumerService consumerService;

	public static void main(String[] args) {

		SpringApplication.run(ProcessorWebappApplication.class, args);

	}


}
