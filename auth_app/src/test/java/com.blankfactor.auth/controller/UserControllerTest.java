package com.blankfactor.auth.controller;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.service.UserService;
import com.blankfactor.auth.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private User dummyUser;

    private static final String USER_ENDPOINT = "/api/v1/users/my-profile";

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        dummyUser.setId(1L);
        dummyUser.setEmail("test@example.com");
        dummyUser.setUsername("testuser");
        dummyUser.setFirstName("Test");
        dummyUser.setLastName("User");
        dummyUser.setBirthDate(LocalDateTime.of(2000, 1, 1, 0, 0));
        dummyUser.setEnabled(true);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dummyUser, null, dummyUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthenticatedUser_shouldReturnUserResponse() throws Exception {
        mockMvc.perform(get(USER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(dummyUser.getEmail())))
                .andExpect(jsonPath("$.username", is(dummyUser.getUsername())))
                .andExpect(jsonPath("$.firstName", is(dummyUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(dummyUser.getLastName())))
                .andExpect(jsonPath("$.birthDate", is(dummyUser.getBirthDate().toString() + ":00")));
    }
}