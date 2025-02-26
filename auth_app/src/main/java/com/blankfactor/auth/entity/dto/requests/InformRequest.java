package com.blankfactor.auth.entity.dto.requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformRequest {

    private Long id;

    private String role;

}
