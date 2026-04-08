package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Minor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinorRepository extends JpaRepository<Minor, Long> {
}