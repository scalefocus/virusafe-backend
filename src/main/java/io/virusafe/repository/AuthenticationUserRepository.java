package io.virusafe.repository;

import io.virusafe.domain.entity.AuthenticationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationUserRepository extends JpaRepository<AuthenticationUser, Long> {

    /**
     * Find an active AuthenticationUser with a given username and password, returning empty Optional if not found.
     *
     * @param username the username to search for
     * @param password the password to search for
     * @return Optional containing either the found AuthenticationUser or empty if nothing was found
     */
    Optional<AuthenticationUser> findByUsernameAndPasswordAndActiveTrue(String username, String password);
}
