package edu.iu.registration.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.Prerequisite;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {
    List<Prerequisite> findByCourse(Course course);
}