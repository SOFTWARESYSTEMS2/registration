package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByName(String name);
}
