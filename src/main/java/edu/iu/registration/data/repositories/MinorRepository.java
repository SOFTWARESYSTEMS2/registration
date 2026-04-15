package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Minor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MinorRepository extends JpaRepository<Minor, Long> {
    Optional<Minor> findByName(String name);
}
