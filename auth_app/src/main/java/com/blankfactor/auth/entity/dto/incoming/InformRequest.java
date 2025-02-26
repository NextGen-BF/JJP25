package com.blankfactor.auth.entity.dto.incoming;

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
