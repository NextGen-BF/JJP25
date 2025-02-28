package com.backend.service.user;

import com.backend.entity.dto.outgoing.RegisterResponse;
import com.backend.entity.dto.incoming.RegisterRequest;
import com.backend.entity.user.User;
import com.backend.entity.user.UserType;
import com.backend.exception.user.UserFoundException;
import com.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private static final String USER_FOUND = "User with id: %s already exists.";

    private final UserRepository userRepository;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        log.debug("Registering user with id: {}", registerRequest.getId());
        Optional<User> byId = this.userRepository.findById(registerRequest.getId());
        if (byId.isPresent()) {
            throw new UserFoundException(String.format(USER_FOUND, registerRequest.getId()));
        }
        User user = this.userRepository.saveAndFlush(User.builder()
                .id(registerRequest.getId())
                .type(registerRequest.getRole().equals(UserType.ATTENDEE.name()) ? UserType.ATTENDEE : UserType.ORGANISER)
                .build());
        log.debug("User with id {} registered successfully", registerRequest.getId());
        return RegisterResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .profilePicture(user.getProfilePicture())
                .userType(user.getType().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
