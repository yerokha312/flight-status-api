package dev.yerokha.flightstatusapi.domain.repository;

import dev.yerokha.flightstatusapi.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
