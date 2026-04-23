package edu.iu.registration.dto;

public class StudentPlanDto {

    private Long id;
    private String username;
    private Long offeringId;
    private String courseCode;
    private String courseTitle;
    private String termLabel;

    public StudentPlanDto(Long id, String username, Long offeringId, String courseCode, String courseTitle, String termLabel) {
        this.id = id;
        this.username = username;
        this.offeringId = offeringId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.termLabel = termLabel;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getOfferingId() {
        return offeringId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getTermLabel() {
        return termLabel;
    }
}