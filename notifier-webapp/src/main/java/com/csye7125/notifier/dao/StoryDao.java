package com.csye7125.notifier.dao;

import com.csye7125.notifier.model.story.Story;
import com.csye7125.notifier.service.PeriodicAlerts;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class StoryDao {

    private RestHighLevelClient restHighLevelClient;

    private ObjectMapper objectMapper;

    @Autowired
	RestHighLevelClient client;

	private static final Logger logger = LoggerFactory.getLogger(StoryDao.class);

    public StoryDao(ObjectMapper objectMapper, @Qualifier("elasticSearchConfiguration") RestHighLevelClient restHighLevelClient) {
        this.objectMapper = objectMapper;
        this.restHighLevelClient = restHighLevelClient;
    }

	public List<Story> searchStory(String category, String keyword, Counter getTotalAlertsNotifierCounter, Timer getElasticSearchTimer) throws IOException {
		long start = System.currentTimeMillis();
    	try{
			logger.info("Inside search story in Elastic Search");
			SearchRequest searchRequest = new SearchRequest(category);
			QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keyword);
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(matchQueryBuilder);
			searchRequest.source(sourceBuilder);

			SearchResponse searchResponse = null;
			try {
				searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

			}
			catch(Exception e){
				logger.error("Exception occured while searching in ES cluster"+e.getMessage());
				e.printStackTrace();
			}



			List<Story> storyList = new ArrayList();

			for(SearchHit searchHit : searchResponse.getHits().getHits()){
				Story story = new ObjectMapper().readValue(searchHit.getSourceAsString(),Story.class);
				storyList.add(story);
			}

			logger.info("Returning matched stories with the keyword: "+keyword);
			getElasticSearchTimer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
			return storyList;
		}catch (Exception e){
    		logger.error("Exception occured while searching in ES cluster"+e.getMessage());
			List<Story> arrlist = new ArrayList();
			e.printStackTrace();
    		return arrlist;
		}

	}

}
