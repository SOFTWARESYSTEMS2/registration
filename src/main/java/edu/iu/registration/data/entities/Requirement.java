package edu.iu.registration.data.entities;

import edu.iu.registration.data.enums.RequirementCategory;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "requirement")
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementCategory category;

    // Minimum credits from eligibleCourses needed to satisfy this requirement
    @Column(nullable = false)
    private int creditsNeeded;

    // Exactly one of these will be non-null — the program this requirement belongs to
    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    @ManyToOne
    @JoinColumn(name = "minor_id")
    private Minor minor;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

    // The set of courses that can count toward satisfying this requirement
    @ManyToMany
    @JoinTable(
        name = "requirement_courses",
        joinColumns = @JoinColumn(name = "requirement_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> eligibleCourses = new HashSet<>();

    public Requirement() {}

    // Constructor for a major requirement
    public Requirement(String name, RequirementCategory category, int creditsNeeded, Major major) {
        this.name = name;
        this.category = category;
        this.creditsNeeded = creditsNeeded;
        this.major = major;
    }

    // Constructor for a minor requirement
    public Requirement(String name, RequirementCategory category, int creditsNeeded, Minor minor) {
        this.name = name;
        this.category = category;
        this.creditsNeeded = creditsNeeded;
        this.minor = minor;
    }

    // Constructor for a specialization requirement
    public Requirement(String name, RequirementCategory category, int creditsNeeded, Specialization specialization) {
        this.name = name;
        this.category = category;
        this.creditsNeeded = creditsNeeded;
        this.specialization = specialization;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public RequirementCategory getCategory() { return category; }
    public int getCreditsNeeded() { return creditsNeeded; }
    public Major getMajor() { return major; }
    public Minor getMinor() { return minor; }
    public Specialization getSpecialization() { return specialization; }
    public Set<Course> getEligibleCourses() { return eligibleCourses; }

    public void setName(String name) { this.name = name; }
    public void setCategory(RequirementCategory category) { this.category = category; }
    public void setCreditsNeeded(int creditsNeeded) { this.creditsNeeded = creditsNeeded; }
    public void setMajor(Major major) { this.major = major; }
    public void setMinor(Minor minor) { this.minor = minor; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }
    public void setEligibleCourses(Set<Course> eligibleCourses) { this.eligibleCourses = eligibleCourses; }

    public void addEligibleCourse(Course course) { this.eligibleCourses.add(course); }

    // Returns the name of whichever program this requirement belongs to
    public String getProgramName() {
        if (major != null) return major.getName();
        if (minor != null) return minor.getName();
        if (specialization != null) return specialization.getName();
        return "Unknown";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Requirement r)) return false;
        return Objects.equals(id, r.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
