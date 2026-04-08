package edu.iu.registration.services;

import edu.iu.registration.models.Course;
import edu.iu.registration.models.PlanCourse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SemesterPlanService {

    private final PreRequisiteService preRequisiteService;
    private final CourseCatalogService courseCatalogService;
    private final List<PlanCourse> planEntries;

    public SemesterPlanService(PreRequisiteService preRequisiteService,
            CourseCatalogService courseCatalogService) {
        this.preRequisiteService = preRequisiteService;
        this.courseCatalogService = courseCatalogService;
        this.planEntries = new ArrayList<>();
    }

    public List<String> getSemesters() {
        List<String> semesters = new ArrayList<>();
        semesters.add("Fall 2025");
        semesters.add("Spring 2026");
        semesters.add("Fall 2026");
        semesters.add("Spring 2027");
        return semesters;
    }

    public List<Course> getAvailableCourses() {
        return courseCatalogService.getAllCourses();
    }

    public void addCourseToSemester(String semester, String courseCode) {
        Course selectedCourse = courseCatalogService.findByCode(courseCode);

        if (selectedCourse == null) {
            return;
        }

        List<Course> previouslyPlannedCourses = getCoursesBeforeSemester(semester);
        List<String> missingPrerequisites = preRequisiteService.findMissingPrerequisites(
                selectedCourse, previouslyPlannedCourses);

        String status = "OK";
        if (!missingPrerequisites.isEmpty()) {
            status = "WARNING";
        }

        PlanCourse planEntry = new PlanCourse(semester, selectedCourse, status,
                missingPrerequisites);
        planEntries.add(planEntry);
    }

    public Map<String, List<PlanCourse>> getPlanGroupedBySemester() {
        Map<String, List<PlanCourse>> groupedPlan = new LinkedHashMap<>();

        for (String semester : getSemesters()) {
            groupedPlan.put(semester, new ArrayList<>());
        }

        for (PlanCourse entry : planEntries) {
            if (groupedPlan.containsKey(entry.getSemester())) {
                groupedPlan.get(entry.getSemester()).add(entry);
            }
        }

        return groupedPlan;
    }

    private List<Course> getCoursesBeforeSemester(String targetSemester) {
        List<Course> completedCourses = new ArrayList<>();
        List<String> semesters = getSemesters();
        int targetIndex = semesters.indexOf(targetSemester);

        for (PlanCourse entry : planEntries) {
            int entryIndex = semesters.indexOf(entry.getSemester());

            if (entryIndex != -1 && targetIndex != -1 && entryIndex < targetIndex) {
                completedCourses.add(entry.getCourse());
            }
        }

        return completedCourses;
    }
}