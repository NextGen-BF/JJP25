package com.blankfactor.auth.entity.dto.request;

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
