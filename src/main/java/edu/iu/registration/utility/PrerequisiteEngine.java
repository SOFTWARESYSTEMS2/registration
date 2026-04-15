package edu.iu.registration.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.StudentPlan;
import edu.iu.registration.services.CourseService;

@Component
public class PrerequisiteEngine {

    private final CourseService courseService;

    public PrerequisiteEngine(CourseService courseService) {
        this.courseService = courseService;
    }

    public Set<String> getSatisfiedCourseCodesBeforeTerm(
            String targetTermLabel,
            List<StudentPlan> allPlans,
            Set<String> completedCourseCodes,
            List<String> orderedTerms) {

        Set<String> satisfied = new LinkedHashSet<>();

        if (completedCourseCodes != null) {
            satisfied.addAll(completedCourseCodes);
        }

        if (targetTermLabel == null || orderedTerms == null || allPlans == null) {
            return satisfied;
        }

        int targetIndex = orderedTerms.indexOf(targetTermLabel);
        if (targetIndex == -1) {
            return satisfied;
        }

        for (StudentPlan plan : allPlans) {
            if (plan == null || plan.getCourseOffering() == null || plan.getTerm() == null || plan.getCourse() == null) {
                continue;
            }

            String planTerm = plan.getTerm().getLabel();
            int planIndex = orderedTerms.indexOf(planTerm);

            if (planIndex != -1 && planIndex < targetIndex) {
                satisfied.add(plan.getCourse().getCode());
            }
        }

        return satisfied;
    }

    public List<String> getMissingPrerequisitesForPlannedOffering(
            CourseOffering offering,
            List<StudentPlan> allPlans,
            Set<String> completedCourseCodes,
            List<String> orderedTerms) {

        if (offering == null || offering.getCourse() == null || offering.getTerm() == null) {
            return Collections.emptyList();
        }

        Set<String> satisfiedBeforeTerm = getSatisfiedCourseCodesBeforeTerm(
                offering.getTerm().getLabel(),
                allPlans,
                completedCourseCodes,
                orderedTerms
        );

        return courseService.getMissingPrerequisites(offering.getCourse(), satisfiedBeforeTerm);
    }

    public boolean isPlannedOfferingEligible(
            CourseOffering offering,
            List<StudentPlan> allPlans,
            Set<String> completedCourseCodes,
            List<String> orderedTerms) {

        return getMissingPrerequisitesForPlannedOffering(
                offering,
                allPlans,
                completedCourseCodes,
                orderedTerms
        ).isEmpty();
    }

    public String getStatusForPlannedOffering(
            CourseOffering offering,
            List<StudentPlan> allPlans,
            Set<String> completedCourseCodes,
            List<String> orderedTerms) {

        return isPlannedOfferingEligible(
                offering,
                allPlans,
                completedCourseCodes,
                orderedTerms
        ) ? "OK" : "WARNING";
    }

    public Map<Long, List<String>> getMissingPrerequisitesByPlanId(
            List<StudentPlan> allPlans,
            Set<String> completedCourseCodes,
            List<String> orderedTerms) {

        Map<Long, List<String>> missingByPlanId = new LinkedHashMap<>();

        if (allPlans == null || allPlans.isEmpty()) {
            return missingByPlanId;
        }

        for (StudentPlan plan : allPlans) {
            if (plan == null || plan.getId() == null || plan.getCourseOffering() == null) {
                continue;
            }

            missingByPlanId.put(
                    plan.getId(),
                    getMissingPrerequisitesForPlannedOffering(
                            plan.getCourseOffering(),
                            allPlans,
                            completedCourseCodes,
                            orderedTerms
                    )
            );
        }

        return missingByPlanId;
    }

    public Map<Long, String> getStatusesByPlanId(
            List<StudentPlan> allPlans,
            Set<String> completedCourseCodes,
            List<String> orderedTerms) {

        Map<Long, String> statusesByPlanId = new LinkedHashMap<>();

        if (allPlans == null || allPlans.isEmpty()) {
            return statusesByPlanId;
        }

        for (StudentPlan plan : allPlans) {
            if (plan == null || plan.getId() == null || plan.getCourseOffering() == null) {
                continue;
            }

            statusesByPlanId.put(
                    plan.getId(),
                    getStatusForPlannedOffering(
                            plan.getCourseOffering(),
                            allPlans,
                            completedCourseCodes,
                            orderedTerms
                    )
            );
        }

        return statusesByPlanId;
    }

    public Map<String, List<StudentPlan>> groupPlansByTerm(List<StudentPlan> plans, List<String> orderedTerms) {
        Map<String, List<StudentPlan>> grouped = new LinkedHashMap<>();

        if (orderedTerms != null) {
            for (String term : orderedTerms) {
                grouped.put(term, new ArrayList<>());
            }
        }

        if (plans == null || plans.isEmpty()) {
            return grouped;
        }

        for (StudentPlan plan : plans) {
            if (plan == null || plan.getTerm() == null) {
                continue;
            }

            String termLabel = plan.getTerm().getLabel();
            grouped.computeIfAbsent(termLabel, k -> new ArrayList<>()).add(plan);
        }

        return grouped;
    }
}