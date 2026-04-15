package edu.iu.registration.data.entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "course_offering",
    uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "term_id"}))
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "term_id")
    private Term term;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int enrolledCount;

    public CourseOffering() {}

    public CourseOffering(Course course, Term term, int capacity, int enrolledCount) {
        this.course = course;
        this.term = term;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public Term getTerm() { return term; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }

    public void setCourse(Course course) { this.course = course; }
    public void setTerm(Term term) { this.term = term; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }

    @Transient
    public int getAvailableSeats() { return capacity - enrolledCount; }

    @Transient
    public boolean isFull() { return enrolledCount >= capacity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseOffering co)) return false;
        return Objects.equals(id, co.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
