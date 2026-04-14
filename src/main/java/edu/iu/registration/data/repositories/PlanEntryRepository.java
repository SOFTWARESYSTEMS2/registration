package edu.iu.registration.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iu.registration.data.entities.Plan;
import edu.iu.registration.data.entities.PlanEntry;

public interface PlanEntryRepository extends JpaRepository<PlanEntry, Long> {
    List<PlanEntry> findByPlan(Plan plan);
}