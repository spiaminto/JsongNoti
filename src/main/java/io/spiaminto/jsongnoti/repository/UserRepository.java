package io.spiaminto.jsongnoti.repository;

import io.spiaminto.jsongnoti.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByVerifiedIsTrue();

}
