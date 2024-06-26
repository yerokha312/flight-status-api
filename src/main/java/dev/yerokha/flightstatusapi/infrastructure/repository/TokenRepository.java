package dev.yerokha.flightstatusapi.infrastructure.repository;

import dev.yerokha.flightstatusapi.infrastructure.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<RefreshToken, String> {
    List<RefreshToken> findNotRevokedByUserEntity_Username(String username);
}
