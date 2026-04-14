package edu.iu.registration.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.registration.data.access.PlanAccess;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.PlanEntry;
import edu.iu.registration.models.PlanCourse;
import edu.iu.registration.utility.PrerequisiteEngine;

@Service
@Transactional
public class PlanService {

    private final PlanAccess planAccess;
    private final CourseService courseService;
    private final PrerequisiteEngine prerequisiteEngine;

    public PlanService(
            PlanAccess planAccess,
            CourseService courseService,
            PrerequisiteEngine prerequisiteEngine) {
        this.planAccess = planAccess;
        this.courseService = courseService;
        this.prerequisiteEngine = prerequisiteEngine;
    }

    public Set<String> getCompletedCourseCodes(String username) {
        return new LinkedHashSet<>(planAccess.findCompletedCourseCodesForUser(username));
    }

    public void saveCompletedCourses(String username, List<String> selectedCourseCodes) {
        planAccess.replaceCompletedCourses(username, selectedCourseCodes);
        recalculateAndPersistStatuses(username);
    }

    public boolean isCourseComplete(String username, String courseCode) {
        return getCompletedCourseCodes(username).contains(courseCode);
    }

    public List<String> getSemesters() {
        return prerequisiteEngine.getSemesters();
    }

    public Map<String, List<PlanCourse>> getPlanGroupedBySemester(String username) {
        Map<String, List<PlanCourse>> groupedPlan = new LinkedHashMap<>();

        for (String semester : getSemesters()) {
            groupedPlan.put(semester, new ArrayList<>());
        }

        List<PlanEntry> allEntries = planAccess.getPlanEntries(username);
        Set<String> completedCourseCodes = planAccess.findCompletedCourseCodesForUser(username);

        for (PlanEntry entry : allEntries) {
            if (entry == null
                    || entry.getCourseOffering() == null
                    || entry.getCourseOffering().getTerm() == null) {
                continue;
            }

            String semester = entry.getCourseOffering().getTerm().getLabel();

            groupedPlan.computeIfAbsent(semester, k -> new ArrayList<>())
                       .add(toPlanCourse(entry, allEntries, completedCourseCodes));
        }

        return groupedPlan;
    }

    public List<PlanCourse> getPlanEntriesForTerm(String username, String term) {
        List<PlanCourse> result = new ArrayList<>();

        List<PlanEntry> allEntries = planAccess.getPlanEntries(username);
        Set<String> completedCourseCodes = planAccess.findCompletedCourseCodesForUser(username);

        for (PlanEntry entry : planAccess.getPlanEntriesForTerm(username, term)) {
            result.add(toPlanCourse(entry, allEntries, completedCourseCodes));
        }

        return result;
    }

    public Set<String> getSelectedCourseCodesForTerm(String username, String term) {
        Set<String> selected = new LinkedHashSet<>();

        for (PlanEntry entry : planAccess.getPlanEntriesForTerm(username, term)) {
            if (entry != null
                    && entry.getCourseOffering() != null
                    && entry.getCourseOffering().getCourse() != null) {
                selected.add(entry.getCourseOffering().getCourse().getCode());
            }
        }

        return selected;
    }

    public void addCourseToSemester(String username, String semester, String courseCode) {
        addCourseToSemesterInternal(username, semester, courseCode);
        recalculateAndPersistStatuses(username);
    }

    public void saveCoursesForTerm(String username, String term, List<String> selectedCourseCodes) {
        planAccess.deletePlanEntriesForTerm(username, term);

        if (selectedCourseCodes != null) {
            for (String courseCode : selectedCourseCodes) {
                addCourseToSemesterInternal(username, term, courseCode);
            }
        }

        recalculateAndPersistStatuses(username);
    }

    private void addCourseToSemesterInternal(String username, String semester, String courseCode) {
        Optional<CourseOffering> offeringOpt = courseService.getCourseOffering(courseCode, semester);
        if (offeringOpt.isEmpty()) {
            return;
        }

        CourseOffering offering = offeringOpt.get();
        List<PlanEntry> existingEntries = planAccess.getPlanEntries(username);
        Set<String> completedCourseCodes = planAccess.findCompletedCourseCodesForUser(username);

        String status = prerequisiteEngine.determineStatus(
                offering,
                existingEntries,
                completedCourseCodes
        );

        planAccess.addPlanEntry(username, offering, status);
    }

    private PlanCourse toPlanCourse(
            PlanEntry entry,
            List<PlanEntry> allEntries,
            Set<String> completedCourseCodes) {

        List<String> missingPrerequisites = prerequisiteEngine.findMissingPrerequisitesForEntry(
                entry.getCourseOffering(),
                allEntries,
                completedCourseCodes
        );

        return new PlanCourse(
                entry.getCourseOffering(),
                entry.getStatus(),
                missingPrerequisites
        );
    }

    private void recalculateAndPersistStatuses(String username) {
        List<PlanEntry> allEntries = planAccess.getPlanEntries(username);
        Set<String> completedCourseCodes = planAccess.findCompletedCourseCodesForUser(username);

        prerequisiteEngine.recalculateStatuses(allEntries, completedCourseCodes);

        for (PlanEntry entry : allEntries) {
            planAccess.savePlanEntry(entry);
        }
    }
}