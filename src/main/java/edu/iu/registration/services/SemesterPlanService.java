package edu.iu.registration.services;

import edu.iu.registration.models.PlanCourse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;

@Service
public class SemesterPlanService {

    private final CourseService courseService;
    private final List<PlanCourse> planEntries;

    public SemesterPlanService(CourseService courseService) {
        this.courseService = courseService;
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
        return courseService.getAllCourses();
    }

    public void addCourseToSemester(String semester, String courseCode) {
        Optional<CourseOffering> selectedCourseOp = courseService.getCourseOffering(courseCode, semester);

        if (selectedCourseOp.isEmpty()) {
            return;
        }
        CourseOffering selectedCourse = selectedCourseOp.get();


        Set<String> previouslyPlannedCourses = getCoursesBeforeSemester(semester).stream()
                .map(Course::getCode)
                .collect(Collectors.toSet());
        List<String> missingPrerequisites = courseService.getMissingPrerequisites(
                selectedCourse.getCourse(), previouslyPlannedCourses);
//List<String> getMissingPrerequisites(Course course, Set<String> completedCourseCodes)
        String status = "OK";
        if (!missingPrerequisites.isEmpty()) {
            status = "WARNING";
        }

        PlanCourse planEntry = new PlanCourse(selectedCourse, status, missingPrerequisites);
        planEntries.add(planEntry);
    }

    public Map<String, List<PlanCourse>> getPlanGroupedBySemester() {
        Map<String, List<PlanCourse>> groupedPlan = new LinkedHashMap<>();

        for (String semester : getSemesters()) {
            groupedPlan.put(semester, new ArrayList<>());
        }

        for (PlanCourse entry : planEntries) {
            if (groupedPlan.containsKey(entry.getTerm())) {
                groupedPlan.get(entry.getTerm()).add(entry);
            }
        }

        return groupedPlan;
    }

    private List<Course> getCoursesBeforeSemester(String targetSemester) {
        List<Course> completedCourses = new ArrayList<>();
        List<String> semesters = getSemesters();
        int targetIndex = semesters.indexOf(targetSemester);

        for (PlanCourse entry : planEntries) {
            int entryIndex = semesters.indexOf(entry.getTerm());

            if (entryIndex != -1 && targetIndex != -1 && entryIndex < targetIndex) {
                completedCourses.add(entry.getCourse());
            }
        }

        return completedCourses;
    }
}