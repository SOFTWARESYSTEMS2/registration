package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Major;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorRepository extends JpaRepository<Major, Long> {
}