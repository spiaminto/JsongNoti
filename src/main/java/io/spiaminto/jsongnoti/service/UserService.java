package io.spiaminto.jsongnoti.service;

import io.spiaminto.jsongnoti.domain.User;
import io.spiaminto.jsongnoti.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 인증 정보 초기화
     */
    public void clearAuthentications() {
        userRepository.findAllHasDeletableAuthenticationInfo(LocalDateTime.now().minusMinutes(30)).forEach(User::clearAuth);
    }

}
