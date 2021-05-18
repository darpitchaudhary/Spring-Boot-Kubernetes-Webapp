package com.csye7125.webapp.repository;

import com.csye7125.webapp.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
//    public Alert findAlertByIdEqualsAndUser(UUID id, User user);


}
