package com.backend.service.integration;

import com.backend.repository.user.UserRepository;
import com.backend.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceIT {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserServiceIT(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @BeforeEach
    public void setup() {
        this.userRepository.deleteAll();
    }

}
