package edu.iu.registration.models;

import java.util.ArrayList;
import java.util.List;

import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Term;
import edu.iu.registration.data.entities.Course;


public class PlanCourse {
    private CourseOffering offering;
    private String status;
    private List<String> missingPrerequisites;

    public PlanCourse(CourseOffering offering, String status,
            List<String> missingPrerequisites) {
        this.offering = offering;
        this.status = status;
        this.missingPrerequisites = new ArrayList<>(missingPrerequisites);
    }

    public CourseOffering getOffering() {
        return offering;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getMissingPrerequisites() {
        return missingPrerequisites;
    }

    public Term getTerm() {
        return offering.getTerm();
    }

    public Course getCourse() {
        return offering.getCourse();
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