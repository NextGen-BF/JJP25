package com.backend.entity.dto.incoming;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotNull(message = "{id.required}")
    @Positive(message = "{id.invalid}")
    private Long id;

    @NotNull(message = "{role.required}")
    @Pattern(regexp = "^(ATTENDEE|ORGANISER)$", message = "{role.invalid}")
    private String role;

}
