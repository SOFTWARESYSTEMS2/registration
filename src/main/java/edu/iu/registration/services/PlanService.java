package edu.iu.registration.services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import edu.iu.registration.models.PlanCourse;

@Service
public class PlanService {

    private final SemesterPlanService semesterPlanService;
    private final Set<String> completedCourseCodes = new HashSet<>();

    public PlanService(SemesterPlanService semesterPlanService) {
        this.semesterPlanService = semesterPlanService;
    }

    public Map<String, List<PlanCourse>> getPlanGroupedBySemester() {
        return semesterPlanService.getPlanGroupedBySemester();
    }

    public Set<String> getCompletedCourseCodes() {
        return completedCourseCodes;
    }

    public void saveCompletedCourses(List<String> selectedCourseCodes) {
        completedCourseCodes.clear();

        if (selectedCourseCodes != null) {
            completedCourseCodes.addAll(selectedCourseCodes);
        }
    }

    public boolean isCourseComplete(String courseCode) {
        return completedCourseCodes.contains(courseCode);
    }
}