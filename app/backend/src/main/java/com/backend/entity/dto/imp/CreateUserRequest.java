package com.backend.entity.dto.imp;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {

    @NotNull(message = "{id.required}")
    private Long id;

    @NotNull(message = "{role.required}")
    @Pattern(regexp = "^(ATTENDEE|ORGANISER)$", message = "{role.invalid}")
    private String userType;

}
