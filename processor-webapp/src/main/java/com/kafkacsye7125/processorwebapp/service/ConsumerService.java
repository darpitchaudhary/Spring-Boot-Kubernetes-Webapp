package com.kafkacsye7125.processorwebapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkacsye7125.processorwebapp.dao.StoryDao;
import com.kafkacsye7125.processorwebapp.model.Story;
import com.kafkacsye7125.processorwebapp.repository.StoryRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ConsumerService {

	@Autowired
	StoryRepository storyRepository;
	@Autowired
	StoryDao storyDao;

	Boolean createIndexFlag = false;

	private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

	private final Counter counter;
	private final Timer getUserTimer;

	public ConsumerService(MeterRegistry meterRegistry){
		counter = meterRegistry.counter("${spring.kafka.topic}"+"counter_consumer");
		this.getUserTimer = meterRegistry.timer("${spring.kafka.topic}"+"timer_consumer", "Get User", "timer");
	}

	@KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.topic.groupId}")
	public void consumetop(String message) throws IOException {
		long start = System.currentTimeMillis();
		logger.info("Inside Kafka Consumer Service");

		if(!createIndexFlag){
			createIndexFlag = true;
			createElasticsearchIndex();
		}

		message = message.trim();
		logger.info("Consumed message from Kafka Topic: "+"${spring.kafka.topic}"+ "message id: "+message);
		if(!storyRepository.existsStoryById(Long.parseLong(message))) {
			counter.increment();
			hackerNoon(message);
		}else{
			logger.info("Message already consumed");
		}
		getUserTimer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
	}


	private void hackerNoon(String message) throws JsonProcessingException {
		try{
			logger.info("Calling hacker news api for message: "+message);
			final String uri = "https://hacker-news.firebaseio.com/v0/item/"+message+".json?print=pretty";
			ObjectMapper objectMapper = new ObjectMapper();
			String output="";
			RestTemplate restTemplate = new RestTemplate();

			String result = restTemplate.getForObject(uri, String.class);
			System.out.println("Result: "+result);
			JsonNode jsonNode = objectMapper.readTree(result);

			Story story = objectMapper.treeToValue(jsonNode, Story.class);
			storyRepository.save(story);

			storyDao.insertStory(story);



		}catch(Exception e){
			logger.error("Exception occured while saving consumed message data"+e.getMessage());
		}

	}

	public void createElasticsearchIndex(){
		Story story = new Story();
		story.setTitle("index create title");
		story.setTable_id(UUID.randomUUID());
		storyDao.createIndex(story);
	}
}
