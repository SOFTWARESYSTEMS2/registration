package edu.iu.registration.data.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Term;


public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
    List<CourseOffering> findByTerm(Term term);

    Optional<CourseOffering> findByCourseAndTerm(Course Course, Term term);
}