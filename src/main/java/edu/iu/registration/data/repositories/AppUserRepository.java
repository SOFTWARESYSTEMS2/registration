package edu.iu.registration.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iu.registration.data.entities.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}