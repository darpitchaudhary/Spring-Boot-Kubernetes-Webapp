package com.kafkacsye7125.processorwebapp.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkacsye7125.processorwebapp.model.Story;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class StoryDao {

    @Value("${spring.kafka.topic}")
    private String INDEX;

    private final String TYPE = "story";

    private RestHighLevelClient restHighLevelClient;

    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(StoryDao.class);


    public StoryDao(ObjectMapper objectMapper, @Qualifier("elasticSearchConfiguration") RestHighLevelClient restHighLevelClient) {
        this.objectMapper = objectMapper;
        this.restHighLevelClient = restHighLevelClient;
    }

    public void createIndex(Story story){
        logger.info("Creating elastic search index");
        Map<String, Object> dataMap = objectMapper.convertValue(story, Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, story.getTable_id().toString())
                .source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
        } catch(ElasticsearchException e) {
            logger.error("Exception while creating index"+e.getMessage());
            e.getDetailedMessage();
        } catch (IOException ex){
            logger.error("Exception while creating index"+ex.getMessage());
            ex.getLocalizedMessage();
        }

    }

    public void insertStory(Story story){
        logger.info("Adding story inside Elastic Search cluster: "+story.getTable_id());
        Map<String, Object> dataMap = objectMapper.convertValue(story, Map.class);
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, story.getTable_id().toString())
                .source(dataMap);
        try {
            IndexResponse response = restHighLevelClient.index(indexRequest);
        } catch(ElasticsearchException e) {
            logger.error("Exception while adding stroy to ES cluster"+e.getMessage());
            e.getDetailedMessage();
        } catch (IOException ex){
            logger.error("Exception while adding stroy to ES cluster"+ex.getMessage());
            ex.getLocalizedMessage();
        }

    }



    public List<Story> getBookById(String title) throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        List<Story> employeeList = new ArrayList();
        for (SearchHit hit : searchHit) {
            employeeList.add(objectMapper.convertValue(hit.getSourceAsMap(), Story.class));
        }
        return employeeList;
    }


}
