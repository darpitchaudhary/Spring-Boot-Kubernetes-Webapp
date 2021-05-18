package com.csye7125.notifier.controller;


import com.csye7125.notifier.service.PeriodicAlerts;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1")
public class MainController {

	@Autowired
	PeriodicAlerts periodicAlerts;

	private final Counter getTotalAlertsNotifierCounter;
	private final Timer getElasticSearchTimer;

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	public MainController(MeterRegistry meterRegistry){
		long start = System.currentTimeMillis();
		getTotalAlertsNotifierCounter = meterRegistry.counter("total_alerts_notifier_counter");
		this.getElasticSearchTimer = meterRegistry.timer("getElasticSearch.timer", "ElasticSearch Timer", "timer");

	}

	@Scheduled(fixedRateString = "${spring.data.schedule}")
	public void finaAlerts() throws IOException {
		logger.info("Notifier Alert started");
		periodicAlerts.getAlerts(getTotalAlertsNotifierCounter, getElasticSearchTimer);
	}


}
