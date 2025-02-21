package com.backend.service.user;

import com.backend.entity.dto.exp.RegisterResponse;
import com.backend.entity.dto.imp.RegisterRequest;
import com.backend.entity.user.User;
import com.backend.entity.user.UserType;
import com.backend.exception.user.UserFoundException;
import com.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_FOUND = "User with id: %s already exists.";

    private final UserRepository userRepository;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        Optional<User> byId = this.userRepository.findById(registerRequest.getId());
        if (byId.isPresent()) {
            throw new UserFoundException(String.format(USER_FOUND, registerRequest.getId()));
        }
        User user = User.builder()
                .id(registerRequest.getId())
                .phone(null)
                .profilePicture(null)
                .type(registerRequest.getRole().equals(UserType.ATTENDEE.name()) ? UserType.ATTENDEE : UserType.ORGANISER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        this.userRepository.saveAndFlush(user);
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
