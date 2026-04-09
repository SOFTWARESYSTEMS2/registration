package edu.iu.registration.data.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int credits;

    @ManyToOne(optional = false)
    private Department department;

    @Column(nullable = false)
    private boolean repeatable = false;

    @ManyToMany(mappedBy = "courses")
    private Set<AppUser> students = new HashSet<>();

    public Course() {
    }

    public Course(String code, String title, int credits, Department department, boolean repeatable) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.department = department;
        this.repeatable = repeatable;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public Department getDepartment() {
        return department;
    }

    public Set<AppUser> getStudents() {
        return students;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void setStudents(Set<AppUser> students) {
        this.students = students;
    }

    public void addStudent(AppUser student) {
        this.students.add(student);
    }

    public void removeStudent(AppUser student) {
        this.students.remove(student);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}