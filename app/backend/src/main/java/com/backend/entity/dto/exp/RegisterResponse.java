package com.backend.entity.dto.exp;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse implements Serializable {

    private Long id;

    private String phone;

    private String profilePicture;

    private String userType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
