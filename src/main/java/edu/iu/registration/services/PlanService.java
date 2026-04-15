package edu.iu.registration.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.StudentPlan;
import edu.iu.registration.data.entities.Term;
import edu.iu.registration.data.repositories.AppUserRepository;
import edu.iu.registration.data.repositories.CourseOfferingRepository;
import edu.iu.registration.data.repositories.CourseRepository;
import edu.iu.registration.data.repositories.StudentPlanRepository;
import edu.iu.registration.data.repositories.TermRepository;
import edu.iu.registration.utility.PrerequisiteEngine;

@Service
@Transactional
public class PlanService {

    private final StudentPlanRepository studentPlanRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseRepository courseRepository;
    private final AppUserRepository appUserRepository;
    private final TermRepository termRepository;
    private final PrerequisiteEngine prerequisiteEngine;

    public PlanService(
            StudentPlanRepository studentPlanRepository,
            CourseOfferingRepository courseOfferingRepository,
            CourseRepository courseRepository,
            AppUserRepository appUserRepository,
            TermRepository termRepository,
            PrerequisiteEngine prerequisiteEngine) {

        this.studentPlanRepository = studentPlanRepository;
        this.courseOfferingRepository = courseOfferingRepository;
        this.courseRepository = courseRepository;
        this.appUserRepository = appUserRepository;
        this.termRepository = termRepository;
        this.prerequisiteEngine = prerequisiteEngine;
    }

    // ── Read ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<StudentPlan> getPlansForUser(AppUser user) {
        return studentPlanRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<StudentPlan> getPlansForUserAndTerm(AppUser user, String termLabel) {
        return termRepository.findByLabel(termLabel)
                .map(term -> studentPlanRepository.findByUserAndTerm(user, term))
                .orElse(Collections.emptyList());
    }

    // Returns all planned courses grouped by term label, preserving term order
    @Transactional(readOnly = true)
    public Map<String, List<StudentPlan>> getPlansGroupedBySemester(AppUser user) {
        List<StudentPlan> all = studentPlanRepository.findByUser(user);

        // Maintain insertion order by term label sorted as stored in DB
        Map<String, List<StudentPlan>> grouped = new LinkedHashMap<>();
        for (StudentPlan plan : all) {
            String label = plan.getTerm().getLabel();
            grouped.computeIfAbsent(label, k -> new ArrayList<>()).add(plan);
        }
        return grouped;
    }

    @Transactional(readOnly = true)
    public Set<String> getPlannedCourseCodesForTerm(AppUser user, String termLabel) {
        return getPlansForUserAndTerm(user, termLabel).stream()
                .map(sp -> sp.getCourse().getCode())
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<String> getOrderedTermLabels() {
        return termRepository.findAllByOrderByIdAsc()
                .stream()
                .map(Term::getLabel)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getMissingPrereqsForUser(AppUser user) {
        List<StudentPlan> plans = studentPlanRepository.findByUser(user);
        Set<String> completed = getCompletedCourseCodes(user);
        List<String> orderedTerms = getOrderedTermLabels();

        return prerequisiteEngine.getMissingPrerequisitesByPlanId(
                plans,
                completed,
                orderedTerms
        );
    }
    @Transactional(readOnly = true)
    public Map<Long, String> getStatusesForUser(AppUser user) {
        List<StudentPlan> plans = studentPlanRepository.findByUser(user);
        Set<String> completed = getCompletedCourseCodes(user);
        List<String> orderedTerms = getOrderedTermLabels();

        return prerequisiteEngine.getStatusesByPlanId(
                plans,
                completed,
                orderedTerms
        );
    }

    // ── Write ──────────────────────────────────────────────────────────────
    public StudentPlan addToPlan(AppUser user, Long courseOfferingId) {
        CourseOffering offering = courseOfferingRepository.findById(courseOfferingId)
                .orElseThrow(() -> new IllegalArgumentException("Offering not found: " + courseOfferingId));

        // Idempotent — return existing plan entry if already added
        return studentPlanRepository.findByUserAndCourseOffering(user, offering)
                .orElseGet(() -> studentPlanRepository.save(new StudentPlan(user, offering)));
    }

    public void removeFromPlan(AppUser user, Long courseOfferingId) {
        courseOfferingRepository.findById(courseOfferingId).ifPresent(offering
                -> studentPlanRepository.findByUserAndCourseOffering(user, offering)
                        .ifPresent(studentPlanRepository::delete));
    }

    // Adds new courses for the term and removes any that were unselected (and removes them from completed courses)
    // Skips any courses that were already checked and added to the plan, so they won't be deleted and re-added
    public void replaceTermPlan(AppUser user, String termLabel, List<String> courseCodes) {
        Term term = termRepository.findByLabel(termLabel)
                .orElseThrow(() -> new IllegalArgumentException("Term not found: " + termLabel));

        List<StudentPlan> existingPlans = studentPlanRepository.findByUserAndTerm(user, term);

        Set<String> submittedCodes = new LinkedHashSet<>();
        if (courseCodes != null) {
            for (String code : courseCodes) {
                if (code != null && !code.isBlank()) {
                    submittedCodes.add(code.trim().toUpperCase(Locale.ROOT));
                }
            }
        }

        Set<String> existingCodes = existingPlans.stream()
                .map(sp -> sp.getCourse().getCode())
                .collect(Collectors.toSet());

        for (StudentPlan existingPlan : existingPlans) {
            String existingCode = existingPlan.getCourse().getCode();

            if (!submittedCodes.contains(existingCode)) {
                studentPlanRepository.delete(existingPlan);

                courseRepository.findByCode(existingCode)
                        .ifPresent(user::removeCompletedCourse);
            }
        }

        for (String code : submittedCodes) {
            if (!existingCodes.contains(code)) {
                courseRepository.findByCode(code)
                        .flatMap(course -> courseOfferingRepository.findByCourseAndTerm(course, term))
                        .ifPresent(offering -> {
                            if (studentPlanRepository.findByUserAndCourseOffering(user, offering).isEmpty()) {
                                studentPlanRepository.save(new StudentPlan(user, offering));
                            }
                        });
            }
        }

        appUserRepository.save(user);
    }

    // ── Completed courses ──────────────────────────────────────────────────
    // Save a new set of completed courses for the user (replaces current set)
    public void saveCompletedCourses(AppUser user, List<String> courseCodes) {
        user.getCompletedCourses().clear();

        if (courseCodes != null) {
            for (String code : courseCodes) {
                courseRepository.findByCode(code.trim().toUpperCase(Locale.ROOT))
                        .ifPresent(user::addCompletedCourse);
            }
        }

        appUserRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Set<String> getCompletedCourseCodes(AppUser user) {
        return user.getCompletedCourses().stream()
                .map(Course::getCode)
                .collect(Collectors.toSet());
    }
}
