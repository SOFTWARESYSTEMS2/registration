package edu.iu.registration.data.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Plan;
import edu.iu.registration.data.entities.PlanEntry;
import edu.iu.registration.data.repositories.AppUserRepository;
import edu.iu.registration.data.repositories.CourseRepository;
import edu.iu.registration.data.repositories.PlanEntryRepository;
import edu.iu.registration.data.repositories.PlanRepository;

@Component
@Transactional
public class PlanAccess {

    private final PlanRepository planRepository;
    private final PlanEntryRepository planEntryRepository;
    private final AppUserRepository appUserRepository;
    private final CourseRepository courseRepository;

    public PlanAccess(
            PlanRepository planRepository,
            PlanEntryRepository planEntryRepository,
            AppUserRepository appUserRepository,
            CourseRepository courseRepository) {
        this.planRepository = planRepository;
        this.planEntryRepository = planEntryRepository;
        this.appUserRepository = appUserRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Returns the AppUser if found.
     */
    public Optional<AppUser> findUserByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return appUserRepository.findByUsername(username);
    }

    /**
     * Returns the student's plan if it exists.
     */
    public Optional<Plan> findPlanByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return planRepository.findByStudentUsername(username);
    }

    /**
     * Finds the student's plan, creating and saving one if missing.
     */
    public Optional<Plan> getOrCreatePlan(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        Optional<Plan> existingPlan = planRepository.findByStudentUsername(username);
        if (existingPlan.isPresent()) {
            return existingPlan;
        }

        Optional<AppUser> userOpt = appUserRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        Plan plan = new Plan();
        plan.setStudent(userOpt.get());

        return Optional.of(planRepository.save(plan));
    }

    /**
     * Saves the plan.
     */
    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }

    /**
     * Saves a single plan entry.
     */
    public PlanEntry savePlanEntry(PlanEntry entry) {
        return planEntryRepository.save(entry);
    }

    /**
     * Returns all entries for a student's plan.
     */
    public List<PlanEntry> findPlanEntriesByUsername(String username) {
        Optional<Plan> planOpt = findPlanByUsername(username);
        if (planOpt.isEmpty()) {
            return Collections.emptyList();
        }

        return planEntryRepository.findByPlan(planOpt.get());
    }

    /**
     * Returns all entries for a student's plan, creating the plan first if needed.
     */
    public List<PlanEntry> getPlanEntries(String username) {
        Optional<Plan> planOpt = getOrCreatePlan(username);
        if (planOpt.isEmpty()) {
            return Collections.emptyList();
        }

        return planEntryRepository.findByPlan(planOpt.get());
    }

    /**
     * Returns entries in a specific term label, like "Fall 2026".
     */
    public List<PlanEntry> getPlanEntriesForTerm(String username, String termLabel) {
        if (termLabel == null || termLabel.isBlank()) {
            return Collections.emptyList();
        }

        List<PlanEntry> allEntries = getPlanEntries(username);
        List<PlanEntry> matchingEntries = new ArrayList<>();

        for (PlanEntry entry : allEntries) {
            CourseOffering offering = entry.getCourseOffering();
            if (offering != null
                    && offering.getTerm() != null
                    && termLabel.equals(offering.getTerm().getLabel())) {
                matchingEntries.add(entry);
            }
        }

        return matchingEntries;
    }

    /**
     * Adds a new entry to the student's plan and persists it.
     */
    public Optional<PlanEntry> addPlanEntry(String username, CourseOffering offering, String status) {
        if (offering == null || status == null || status.isBlank()) {
            return Optional.empty();
        }

        Optional<Plan> planOpt = getOrCreatePlan(username);
        if (planOpt.isEmpty()) {
            return Optional.empty();
        }

        PlanEntry entry = new PlanEntry();
        entry.setPlan(planOpt.get());
        entry.setCourseOffering(offering);
        entry.setStatus(status);

        return Optional.of(planEntryRepository.save(entry));
    }

    /**
     * Deletes a single plan entry.
     */
    public void deletePlanEntry(PlanEntry entry) {
        if (entry != null) {
            planEntryRepository.delete(entry);
        }
    }

    /**
     * Deletes all entries for the user's plan in a given term.
     */
    public void deletePlanEntriesForTerm(String username, String termLabel) {
        List<PlanEntry> entries = getPlanEntriesForTerm(username, termLabel);
        if (entries.isEmpty()) {
            return;
        }

        planEntryRepository.deleteAll(entries);
    }

    /**
     * Updates the status of a plan entry and saves it.
     */
    public Optional<PlanEntry> updatePlanEntryStatus(PlanEntry entry, String status) {
        if (entry == null || status == null || status.isBlank()) {
            return Optional.empty();
        }

        entry.setStatus(status);
        return Optional.of(planEntryRepository.save(entry));
    }

    /**
     * Returns the student's completed courses from AppUser.courses.
     */
    public Set<Course> findCompletedCoursesForUser(String username) {
        return findUserByUsername(username)
                .map(AppUser::getCourses)
                .map(LinkedHashSet::new)
                .orElseGet(LinkedHashSet::new);
    }

    /**
     * Returns completed course codes from AppUser.courses.
     */
    public Set<String> findCompletedCourseCodesForUser(String username) {
        Set<Course> completedCourses = findCompletedCoursesForUser(username);
        Set<String> completedCodes = new LinkedHashSet<>();

        for (Course course : completedCourses) {
            if (course != null && course.getCode() != null) {
                completedCodes.add(course.getCode());
            }
        }

        return completedCodes;
    }

    /**
     * Replaces the student's completed courses with the provided course codes.
     */
    public void replaceCompletedCourses(String username, List<String> selectedCourseCodes) {
        Optional<AppUser> userOpt = findUserByUsername(username);
        if (userOpt.isEmpty()) {
            return;
        }

        AppUser user = userOpt.get();
        user.getCourses().clear();

        if (selectedCourseCodes != null) {
            for (String courseCode : selectedCourseCodes) {
                if (courseCode == null || courseCode.isBlank()) {
                    continue;
                }

                courseRepository.findByCode(courseCode).ifPresent(user::addCourse);
            }
        }

        appUserRepository.save(user);
    }
}