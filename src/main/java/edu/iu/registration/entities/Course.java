package edu.iu.registration.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // class code (CSCI-C 323)
    private String title;
    private int credits;

    private String department;

    private String semester;

    // store prereqs as class codes
    @ElementCollection
    private List<String> prerequisites;

    // constructor

    public Course() {
    }

    public Course(String code, String title, int credits, String department, String semester,
            List<String> prerequisites) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.department = department;
        this.semester = semester;
        this.prerequisites = prerequisites;
    }

    // getters / setters

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }
}
