package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByDepartment(Department department);
}
