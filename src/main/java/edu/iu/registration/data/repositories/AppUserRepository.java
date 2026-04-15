package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    // Eagerly join completedCourses only when explicitly needed — avoids LAZY init exceptions
    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.completedCourses WHERE u.username = :username")
    Optional<AppUser> findByUsernameWithCourses(@Param("username") String username);
}
