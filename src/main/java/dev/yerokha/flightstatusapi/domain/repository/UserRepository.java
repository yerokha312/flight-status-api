package dev.yerokha.flightstatusapi.domain.repository;

import dev.yerokha.flightstatusapi.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u " +
           "FROM UserEntity u " +
           "JOIN FETCH u.role " +
           "WHERE u.username = :username")
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
