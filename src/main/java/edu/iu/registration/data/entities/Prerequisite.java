package edu.iu.registration.data.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "prerequisite",
    uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "required_course_id"}))
public class Prerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "required_course_id")
    private Course requiredCourse;

    public Prerequisite() {}

    public Prerequisite(Course course, Course requiredCourse) {
        this.course = course;
        this.requiredCourse = requiredCourse;
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public Course getRequiredCourse() { return requiredCourse; }
    public void setCourse(Course course) { this.course = course; }
    public void setRequiredCourse(Course requiredCourse) { this.requiredCourse = requiredCourse; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prerequisite p)) return false;
        return Objects.equals(id, p.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
