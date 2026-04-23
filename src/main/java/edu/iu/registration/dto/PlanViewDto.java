package edu.iu.registration.dto;

import java.util.List;

public class PlanViewDto {

    private List<GroupedPlansDto> groupedPlan;
    private CompletedCoursesDto completedCourses;

    public PlanViewDto(List<GroupedPlansDto> groupedPlan, CompletedCoursesDto completedCourses) {
        this.groupedPlan = groupedPlan;
        this.completedCourses = completedCourses;
    }

    public List<GroupedPlansDto> getGroupedPlan() {
        return groupedPlan;
    }

    public CompletedCoursesDto getCompletedCourses() {
        return completedCourses;
    }
}