package com.teamcrazyperformance.blockitserver.service;

import com.teamcrazyperformance.blockitserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teamcrazyperformance.blockitserver.entity.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User findUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findUserByName(String name) {
        return userRepository.findByName(name);
    }
}
