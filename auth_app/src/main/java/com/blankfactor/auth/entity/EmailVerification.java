package com.blankfactor.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "email_verifications")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column
    private String code;

    @Column(name = "code_expiration_date")
    private LocalDateTime codeExpirationDate;

    @OneToOne(mappedBy = "emailVerification")
    private User user;

}
