package com.csye7125.notifier.repository.users;

import com.csye7125.notifier.model.users.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert,Long> {
}
