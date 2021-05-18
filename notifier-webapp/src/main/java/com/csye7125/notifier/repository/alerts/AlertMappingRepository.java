package com.csye7125.notifier.repository.alerts;

import com.csye7125.notifier.model.alerts.AlertMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlertMappingRepository extends JpaRepository<AlertMapping,Long>  {
}
