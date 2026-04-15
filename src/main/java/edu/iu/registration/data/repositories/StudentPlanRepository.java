package edu.iu.registration.data.repositories;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.StudentPlan;
import edu.iu.registration.data.entities.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentPlanRepository extends JpaRepository<StudentPlan, Long> {

    List<StudentPlan> findByUser(AppUser user);

    @Query("SELECT sp FROM StudentPlan sp WHERE sp.user = :user AND sp.courseOffering.term = :term")
    List<StudentPlan> findByUserAndTerm(@Param("user") AppUser user, @Param("term") Term term);

    Optional<StudentPlan> findByUserAndCourseOffering(AppUser user, CourseOffering courseOffering);
}
