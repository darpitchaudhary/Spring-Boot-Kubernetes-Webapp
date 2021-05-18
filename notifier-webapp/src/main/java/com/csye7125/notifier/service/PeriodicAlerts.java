package com.csye7125.notifier.service;

import com.csye7125.notifier.dao.StoryDao;
import com.csye7125.notifier.dto.EmailRequestDto;
import com.csye7125.notifier.model.alerts.AlertMapping;
import com.csye7125.notifier.model.story.Story;
import com.csye7125.notifier.model.users.Alert;
import com.csye7125.notifier.repository.alerts.AlertMappingRepository;
import com.csye7125.notifier.repository.users.AlertRepository;
import com.csye7125.notifier.util.EmailUtil;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import io.micrometer.core.instrument.Timer;

@Service
public class PeriodicAlerts {

	private static final Logger logger = LoggerFactory.getLogger(PeriodicAlerts.class);
//	static final Counter requests = Counter.build()
//			.name("requests_total").help("Total requests.").register();
//
//	static final Gauge inprogressRequests = Gauge.build()
//			.name("inprogress_requests").help("Inprogress requests.").register();

	@Autowired
	AlertRepository alertRepository;

	@Autowired
	AlertMappingRepository alertMappingRepository;

	@Autowired
	StoryDao storyDao;

	@Autowired
	AwsSesService awsSesService;

	public void getAlerts(Counter getTotalAlertsNotifierCounter, Timer getElasticSearchTimer) throws IOException {
		Map<String,String> topic_map = new HashMap<>();
		topic_map.put("New","newstories");
		topic_map.put("Top","topstories");
		topic_map.put("Best","beststories");
		try {
			List<Alert> alerts=alertRepository.findAll();
			List<Alert> alertsResult=this.checkExpiredAlerts(alerts);
			for(Alert alrt:alertsResult){
				logger.info("keyword search for alert: "+alrt.getId()+" "+alrt.getKeyword()+" "+ alrt.getCategory());
			List<Story> srty=storyDao.searchStory(topic_map.get(alrt.getCategory()),alrt.getKeyword(),getTotalAlertsNotifierCounter, getElasticSearchTimer);
			logger.info("After keyword search: "+srty);
			if(!srty.isEmpty()){
				StringBuilder body = new StringBuilder();
				body.append("Alert id: "+alrt.getId());
				body.append("\n");
				body.append("\n");
				Boolean flag = false;
				for(Story st:srty)
				{

					if(this.checkStoryAlreadySend(alrt.getKeyword(),alrt.getUser().getId(),st.getTitle())){
						flag = true;
						body.append("Keyword "+alrt.getKeyword()+" Found"+" in story:"+st.getTitle()+" for category "+alrt.getCategory()+" with story id: "+st.getId());
						body.append("\n");
						body.append("\n");
						alertMappingRepository.save(new AlertMapping(alrt.getKeyword(),st.getTitle(),true,alrt.getUser().getId()));
					}else{
						continue;
					}
				}
				if(flag){
					getTotalAlertsNotifierCounter.increment();
					logger.info("sending an email to user: "+ alrt.getUser().getId()+ " name: "+alrt.getUser().getEmail());
					EmailUtil emailUtil= new EmailUtil(awsSesService);
					EmailRequestDto dto=new EmailRequestDto();
					dto.setEmail(alrt.getUser().getEmail());
					dto.setBody(body.toString());
					emailUtil.sendEmail(dto);
				}
				else{
					logger.info("Email has already been sent for this story");
				}


			}
			else{
				getTotalAlertsNotifierCounter.increment();
				logger.info("No stories with keyword matched");
			}
			}
		}catch (Exception e){
			logger.error("Some exception occured in notifier alert "+e.getMessage());
		}
//		inprogressRequests.dec();
	}

	private List<Alert> checkExpiredAlerts(List<Alert> alerts){
		List<Alert> validAlerts=new ArrayList<>();
		for(Alert alrt:alerts){
			logger.info("Alert is: "+alrt.getId());
			if(LocalDateTime.parse(alrt.getAlert_expiry()).isAfter(LocalDateTime.now())){
				logger.info("It is a valid Alert: "+alrt.getId());
					validAlerts.add(alrt);
			}
		}
		return validAlerts;
	}

	private boolean checkStoryAlreadySend(String keyword, UUID user_id, String title){
		AlertMapping alertMapping=new AlertMapping();
		alertMapping.setIs_done(true);
		alertMapping.setKeyword(keyword);
		alertMapping.setUser_id(user_id);
		alertMapping.setTitle(title);
		Example<AlertMapping> example = Example.of(alertMapping);
		Optional<AlertMapping> alert_actual = alertMappingRepository.findOne(example);
		if(!alert_actual.isPresent()){
			return true;
		}
		return false;
	}
}
