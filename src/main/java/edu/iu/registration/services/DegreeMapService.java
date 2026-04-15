package edu.iu.registration.services;

import edu.iu.registration.data.entities.*;
import edu.iu.registration.data.repositories.RequirementRepository;
import edu.iu.registration.models.DegreeProgress;
import edu.iu.registration.models.RequirementStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DegreeMapService {

    private static final int TOTAL_CREDITS_REQUIRED = 120;
    private static final int GEN_ED_CREDITS_REQUIRED = 30;

    private final RequirementRepository requirementRepository;

    public DegreeMapService(RequirementRepository requirementRepository) {
        this.requirementRepository = requirementRepository;
    }

    // ── Requirement status ─────────────────────────────────────────────────

    // Returns the status of every requirement across the student's major, minor, and spec
    public List<RequirementStatus> getRequirementStatuses(AppUser user) {
        Set<Course> completed = user.getCompletedCourses();
        List<Requirement> allRequirements = loadRequirementsFor(user);

        return allRequirements.stream()
                .map(req -> buildStatus(req, completed))
                .collect(Collectors.toList());
    }

    // Returns only requirements that haven't been fully satisfied yet
    public List<RequirementStatus> getUnmetRequirements(AppUser user) {
        return getRequirementStatuses(user).stream()
                .filter(rs -> !rs.isSatisfied())
                .collect(Collectors.toList());
    }

    // Returns only satisfied requirements
    public List<RequirementStatus> getMetRequirements(AppUser user) {
        return getRequirementStatuses(user).stream()
                .filter(RequirementStatus::isSatisfied)
                .collect(Collectors.toList());
    }

    // ── Degree progress summary ────────────────────────────────────────────

    public DegreeProgress getProgress(AppUser user) {
        Set<Course> completed = user.getCompletedCourses();
        int totalEarned = completed.stream().mapToInt(Course::getCredits).sum();

        List<Requirement> majorReqs = requirementRepository.findByMajor(user.getMajor());
        int majorRequired = majorReqs.stream().mapToInt(Requirement::getCreditsNeeded).sum();
        int majorEarned   = creditsEarnedAgainst(majorReqs, completed);

        int minorRequired = 0;
        int minorEarned   = 0;
        if (user.getMinor() != null) {
            List<Requirement> minorReqs = requirementRepository.findByMinor(user.getMinor());
            minorRequired = minorReqs.stream().mapToInt(Requirement::getCreditsNeeded).sum();
            minorEarned   = creditsEarnedAgainst(minorReqs, completed);
        }

        int specRequired = 0;
        int specEarned   = 0;
        if (user.getSpecialization() != null) {
            List<Requirement> specReqs = requirementRepository.findBySpecialization(user.getSpecialization());
            specRequired = specReqs.stream().mapToInt(Requirement::getCreditsNeeded).sum();
            specEarned   = creditsEarnedAgainst(specReqs, completed);
        }

        // Gen Ed: credits from any course that has no major/minor/spec requirement match
        int genEdEarned = computeGenEdCredits(completed, user);

        return new DegreeProgress(
                totalEarned, TOTAL_CREDITS_REQUIRED,
                majorEarned, majorRequired,
                minorEarned, minorRequired,
                specEarned,  specRequired,
                genEdEarned, GEN_ED_CREDITS_REQUIRED
        );
    }

    // ── Course relevance check ─────────────────────────────────────────────

    // Returns true if the given course can count toward any of the student's requirements
    public boolean isRelevantToStudent(Course course, AppUser user) {
        return loadRequirementsFor(user).stream()
                .anyMatch(req -> req.getEligibleCourses().contains(course));
    }

    // Returns all courses relevant to the student's degree (across all requirements)
    public Set<Course> getRelevantCourses(AppUser user) {
        return loadRequirementsFor(user).stream()
                .flatMap(req -> req.getEligibleCourses().stream())
                .collect(Collectors.toSet());
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private List<Requirement> loadRequirementsFor(AppUser user) {
        List<Requirement> all = new ArrayList<>(requirementRepository.findByMajor(user.getMajor()));
        if (user.getMinor() != null) all.addAll(requirementRepository.findByMinor(user.getMinor()));
        if (user.getSpecialization() != null) all.addAll(requirementRepository.findBySpecialization(user.getSpecialization()));
        return all;
    }

    private RequirementStatus buildStatus(Requirement req, Set<Course> completedCourses) {
        Set<Course> completedEligible = req.getEligibleCourses().stream()
                .filter(completedCourses::contains)
                .collect(Collectors.toSet());
        int earned = completedEligible.stream().mapToInt(Course::getCredits).sum();
        return new RequirementStatus(req, completedEligible, earned);
    }

    private int creditsEarnedAgainst(List<Requirement> requirements, Set<Course> completed) {
        // Sum credits earned per requirement, capped at creditsNeeded to avoid double-counting
        return requirements.stream()
                .mapToInt(req -> {
                    int earned = req.getEligibleCourses().stream()
                            .filter(completed::contains)
                            .mapToInt(Course::getCredits)
                            .sum();
                    return Math.min(earned, req.getCreditsNeeded());
                })
                .sum();
    }

    private int computeGenEdCredits(Set<Course> completed, AppUser user) {
        Set<Course> relevantCourses = getRelevantCourses(user);
        // Gen Ed credits = completed courses that don't satisfy any specific program requirement
        return completed.stream()
                .filter(c -> !relevantCourses.contains(c))
                .mapToInt(Course::getCredits)
                .sum();
    }
}
