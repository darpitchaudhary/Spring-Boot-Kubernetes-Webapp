package com.kafkacsye7125.processorwebapp.repository;

import com.kafkacsye7125.processorwebapp.model.Story;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story,Long>{
    public boolean existsStoryById(Long id);


}
