package com.backend.service.user;

import com.backend.entity.dto.exp.RegisterResponse;
import com.backend.entity.dto.imp.RegisterRequest;
import com.backend.entity.user.User;
import com.backend.entity.user.UserType;
import com.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
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
