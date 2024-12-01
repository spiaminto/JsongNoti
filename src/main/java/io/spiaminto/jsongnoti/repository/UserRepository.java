package io.spiaminto.jsongnoti.repository;

import io.spiaminto.jsongnoti.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByVerifiedIsTrue();

    /**
     * 삭제 가능한 인증토큰이 있는 사용자 삭제
     * @param time : 현재시간 - 30분
     * @return
     */
    @Query("select u from User u where u.authenticationToken is not null and u.authenticationTimestamp < :time")
    List<User> findAllHasDeletableAuthenticationInfo(LocalDateTime time);

}
