package com.teamcrazyperformance.blockitserver.repository;

import com.teamcrazyperformance.blockitserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByName(String name);
}
