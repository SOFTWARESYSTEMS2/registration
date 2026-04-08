package edu.iu.registration.data.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iu.registration.data.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
}