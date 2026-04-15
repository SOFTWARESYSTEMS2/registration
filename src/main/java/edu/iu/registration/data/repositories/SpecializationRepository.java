package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Major;
import edu.iu.registration.data.entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    Optional<Specialization> findByName(String name);
    List<Specialization> findByMajor(Major major);
}
