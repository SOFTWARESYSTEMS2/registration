package edu.iu.registration.data.entities;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Prerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Course course;

    @ManyToOne(optional = false)
    private Course requiredCourse;

    public Prerequisite() {
    }

    public Prerequisite(Course course, Course requiredCourse) {
        this.course = course;
        this.requiredCourse = requiredCourse;
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public Course getRequiredCourse() {
        return requiredCourse;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setRequiredCourse(Course requiredCourse) {
        this.requiredCourse = requiredCourse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prerequisite that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}