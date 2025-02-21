package com.blankfactor.auth.entity.dto.exp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformResponse {

    private Long id;

    private String phone;

    private String profilePicture;

    private String userType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
