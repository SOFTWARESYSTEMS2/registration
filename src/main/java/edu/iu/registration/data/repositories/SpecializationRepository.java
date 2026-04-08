package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
}