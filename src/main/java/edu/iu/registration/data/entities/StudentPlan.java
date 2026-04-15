package edu.iu.registration.data.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "student_plan",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_offering_id"}))
public class StudentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_offering_id")
    private CourseOffering courseOffering;

    public StudentPlan() {}

    public StudentPlan(AppUser user, CourseOffering courseOffering) {
        this.user = user;
        this.courseOffering = courseOffering;
    }

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public CourseOffering getCourseOffering() { return courseOffering; }

    // Convenience pass-throughs so templates/services don't chain through offering
    public Course getCourse() { return courseOffering.getCourse(); }
    public Term getTerm() { return courseOffering.getTerm(); }

    public void setUser(AppUser user) { this.user = user; }
    public void setCourseOffering(CourseOffering courseOffering) { this.courseOffering = courseOffering; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentPlan sp)) return false;
        return Objects.equals(id, sp.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
