package org.zerock.apiserver.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.apiserver.domain.auth.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}