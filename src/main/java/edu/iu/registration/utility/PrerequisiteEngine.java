package edu.iu.registration.utility;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.PlanEntry;
import edu.iu.registration.services.CourseService;

@Component
public class PrerequisiteEngine {

    private final CourseService courseService;

    public PrerequisiteEngine(CourseService courseService) {
        this.courseService = courseService;
    }

    public List<String> getSemesters() {
        List<String> semesters = new ArrayList<>();
        semesters.add("Fall 2025");
        semesters.add("Spring 2026");
        semesters.add("Fall 2026");
        semesters.add("Spring 2027");
        return semesters;
    }

    public Set<String> getSatisfiedCourseCodesBeforeSemester(
            String targetSemester,
            List<PlanEntry> allEntries,
            Set<String> completedCourseCodes) {

        Set<String> satisfiedCodes = new LinkedHashSet<>();

        if (completedCourseCodes != null) {
            satisfiedCodes.addAll(completedCourseCodes);
        }

        int targetIndex = getSemesters().indexOf(targetSemester);
        if (targetIndex == -1 || allEntries == null) {
            return satisfiedCodes;
        }

        for (PlanEntry entry : allEntries) {
            if (entry == null
                    || entry.getCourseOffering() == null
                    || entry.getCourseOffering().getTerm() == null
                    || entry.getCourseOffering().getCourse() == null) {
                continue;
            }

            String entrySemester = entry.getCourseOffering().getTerm().getLabel();
            int entryIndex = getSemesters().indexOf(entrySemester);

            if (entryIndex != -1 && entryIndex < targetIndex) {
                satisfiedCodes.add(entry.getCourseOffering().getCourse().getCode());
            }
        }

        return satisfiedCodes;
    }

    public List<String> findMissingPrerequisitesForEntry(
            CourseOffering offering,
            List<PlanEntry> allEntries,
            Set<String> completedCourseCodes) {

        if (offering == null || offering.getCourse() == null || offering.getTerm() == null) {
            return List.of();
        }

        Set<String> satisfiedCodes = getSatisfiedCourseCodesBeforeSemester(
                offering.getTerm().getLabel(),
                allEntries,
                completedCourseCodes);

        return courseService.getMissingPrerequisites(offering.getCourse(), satisfiedCodes);
    }

    public String determineStatus(
            CourseOffering offering,
            List<PlanEntry> allEntries,
            Set<String> completedCourseCodes) {

        List<String> missingPrerequisites = findMissingPrerequisitesForEntry(
                offering,
                allEntries,
                completedCourseCodes);

        return missingPrerequisites.isEmpty() ? "OK" : "WARNING";
    }

    public void recalculateStatuses(
            List<PlanEntry> allEntries,
            Set<String> completedCourseCodes) {

        if (allEntries == null) {
            return;
        }

        for (PlanEntry entry : allEntries) {
            if (entry == null || entry.getCourseOffering() == null) {
                continue;
            }

            String status = determineStatus(
                    entry.getCourseOffering(),
                    allEntries,
                    completedCourseCodes);

            entry.setStatus(status);
        }
    }
}