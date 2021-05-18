package com.csye7125.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.csye7125.webapp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    String FIND_ONE = "SELECT * from users";

    @Query(value = FIND_ONE, nativeQuery = true)
    public User findone();

    Boolean existsByEmail(String email);

    User findByEmail(String email);

}
