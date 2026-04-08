package edu.iu.registration.models;

import java.util.ArrayList;
import java.util.List;

public class PlanCourse {

    private String semester;
    private Course course;
    private String status;
    private List<String> missingPrerequisites;

    public PlanCourse(String semester, Course course, String status,
            List<String> missingPrerequisites) {
        this.semester = semester;
        this.course = course;
        this.status = status;
        this.missingPrerequisites = new ArrayList<>(missingPrerequisites);
    }

    public String getSemester() {
        return semester;
    }

    public Course getCourse() {
        return course;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getMissingPrerequisites() {
        return missingPrerequisites;
    }

    public String getMissingPrerequisitesText() {
        String text = "";

        for (int i = 0; i < missingPrerequisites.size(); i++) {
            text += missingPrerequisites.get(i);

            if (i < missingPrerequisites.size() - 1) {
                text += ", ";
            }
        }

        return text;
    }
}