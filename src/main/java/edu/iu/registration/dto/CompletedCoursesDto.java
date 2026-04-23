package edu.iu.registration.dto;

import java.util.Set;

public class CompletedCoursesDto {

    private Set<String> completedCourses;

    public CompletedCoursesDto(Set<String> completedCourses) {
        this.completedCourses = completedCourses;
    }

    public Set<String> getCompletedCourses() {
        return completedCourses;
    }
}