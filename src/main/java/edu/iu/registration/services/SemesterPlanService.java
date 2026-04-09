package edu.iu.registration.services;

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
import edu.iu.registration.models.PlanCourse;

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


        String status = missingPrerequisites.isEmpty() ? "OK" : "WARNING";

        PlanCourse planEntry = new PlanCourse(selectedCourse, status, missingPrerequisites);
        planEntries.add(planEntry);

        recalculateStatuses();
    }

    public Map<String, List<PlanCourse>> getPlanGroupedBySemester() {
        Map<String, List<PlanCourse>> groupedPlan = new LinkedHashMap<>();

        for (String semester : getSemesters()) {
            groupedPlan.put(semester, new ArrayList<>());
        }

        for (PlanCourse entry : planEntries) {
            if (groupedPlan.containsKey(entry.getTerm().getLabel())) {
                groupedPlan.get(entry.getTerm().getLabel()).add(entry);
            }
        }

        return groupedPlan;
    }

    private List<Course> getCoursesBeforeSemester(String targetSemester) {
        List<Course> completedCourses = new ArrayList<>();
        List<String> semesters = getSemesters();
        int targetIndex = semesters.indexOf(targetSemester);

        for (PlanCourse entry : planEntries) {
            int entryIndex = semesters.indexOf(entry.getTerm().getLabel());

            if (entryIndex != -1 && targetIndex != -1 && entryIndex < targetIndex) {
                completedCourses.add(entry.getCourse());
            }
        }

        return completedCourses;
    }


    public List<PlanCourse> getPlanEntriesForTerm(String term) {
    return planEntries.stream()
            .filter(entry -> entry.getTerm().getLabel().equals(term))
            .toList();
    }

    public List<CourseOffering> getAvailableOfferingsForTerm(String term) {
        return courseService.getOfferingsForTermLabel(term);
    }

    public void replaceCoursesForTerm(String term, List<String> selectedCourseCodes) {
        planEntries.removeIf(entry -> entry.getTerm().getLabel().equals(term));

        if (selectedCourseCodes == null) {
            return;
        }

        for (String courseCode : selectedCourseCodes) {
            addCourseToSemester(term, courseCode);
        }

        recalculateStatuses();

    }

    private void recalculateStatuses() {
    for (int i = 0; i < planEntries.size(); i++) {
        PlanCourse entry = planEntries.get(i);

        Set<String> previouslyPlannedCourses = getCoursesBeforeSemester(entry.getTerm().getLabel()).stream()
                .map(Course::getCode)
                .collect(Collectors.toSet());

        List<String> missingPrerequisites = courseService.getMissingPrerequisites(
                entry.getCourse(), previouslyPlannedCourses);

        String status = missingPrerequisites.isEmpty() ? "OK" : "WARNING";

        planEntries.set(i, new PlanCourse(entry.getOffering(), status, missingPrerequisites));
    }
}
}