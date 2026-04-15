package edu.iu.registration.data.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    // e.g. "Freshman", "Sophomore", "Junior", "Senior"
    @Column(nullable = false)
    private String classYear;

    @ManyToOne(optional = false)
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne
    @JoinColumn(name = "minor_id")
    private Minor minor;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    // Courses the student has already completed — LAZY to avoid loading on every auth check
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "completed_courses",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> completedCourses = new HashSet<>();

    public AppUser() {}

    public AppUser(String username, String password, String role, String classYear,
                   Major major, Minor minor, Specialization specialization) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.classYear = classYear;
        this.major = major;
        this.minor = minor;
        this.specialization = specialization;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getClassYear() { return classYear; }
    public Major getMajor() { return major; }
    public Minor getMinor() { return minor; }
    public Specialization getSpecialization() { return specialization; }
    public Set<Course> getCompletedCourses() { return completedCourses; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setClassYear(String classYear) { this.classYear = classYear; }
    public void setMajor(Major major) { this.major = major; }
    public void setMinor(Minor minor) { this.minor = minor; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }
    public void setCompletedCourses(Set<Course> completedCourses) { this.completedCourses = completedCourses; }

    public void addCompletedCourse(Course course) { this.completedCourses.add(course); }
    public void removeCompletedCourse(Course course) { this.completedCourses.remove(course); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser u)) return false;
        return Objects.equals(id, u.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
