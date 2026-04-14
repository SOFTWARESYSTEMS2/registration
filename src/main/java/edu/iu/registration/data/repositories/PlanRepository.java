package edu.iu.registration.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByStudent(AppUser student);
    Optional<Plan> findByStudentUsername(String username);
}