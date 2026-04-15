package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
    List<CourseOffering> findByTerm(Term term);
    Optional<CourseOffering> findByCourseAndTerm(Course course, Term term);
}
