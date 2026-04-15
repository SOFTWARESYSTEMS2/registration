package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.Prerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {
    List<Prerequisite> findByCourse(Course course);

    // Fetch all prerequisites for a batch of courses in one query — avoids N+1
    @Query("SELECT p FROM Prerequisite p WHERE p.course IN :courses")
    List<Prerequisite> findByCourseIn(@Param("courses") List<Course> courses);
}
