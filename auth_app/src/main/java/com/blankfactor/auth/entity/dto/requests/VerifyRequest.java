package com.blankfactor.auth.entity.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyRequest {

    @NotNull(message = "{uuid.required}")
    private String uuid;

    @NotNull(message = "{code.required}")
    @Size(min = 6, max = 6, message = "{code.invalid}")
    private String code;

}
