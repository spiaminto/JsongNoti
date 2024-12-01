package io.spiaminto.jsongnoti.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
@Getter @EqualsAndHashCode @ToString
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private boolean verified;   // 이메일 인증 여부

    // 이메일 인증 및 탈퇴 인증 시 사용
    private String authenticationToken;
    private LocalDateTime authenticationTimestamp;
    private int authenticationRetry; // 최대 3

    public void clearAuth() {
        this.authenticationToken = null;
        this.authenticationTimestamp = null;
        this.authenticationRetry = 0;
    }

}
