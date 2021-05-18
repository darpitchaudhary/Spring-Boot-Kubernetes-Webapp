package com.csye7125.notifier.repository.users;

import com.csye7125.notifier.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
