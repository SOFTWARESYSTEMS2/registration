package edu.iu.registration.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Prerequisite;
import edu.iu.registration.data.entities.Term;
import edu.iu.registration.data.repositories.CourseOfferingRepository;
import edu.iu.registration.data.repositories.CourseRepository;
import edu.iu.registration.data.repositories.PrerequisiteRepository;
import edu.iu.registration.data.repositories.TermRepository;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final PrerequisiteRepository prerequisiteRepository;
    private final TermRepository termRepository;

    public CourseService(
            CourseRepository courseRepository,
            CourseOfferingRepository courseOfferingRepository,
            PrerequisiteRepository prerequisiteRepository,
            TermRepository termRepository) {
        this.courseRepository = courseRepository;
        this.courseOfferingRepository = courseOfferingRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.termRepository = termRepository;
    }

    // return every course in catalog
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // return course with given code if exists
    public Optional<Course> getCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return Optional.empty();
        }
        return courseRepository.findByCode(normalize(courseCode));
    }

        // return course offering with given code if exists
    public Optional<CourseOffering> getCourseOffering(String courseCode, String termLabel) {
        if (courseCode.isBlank()) {
            return Optional.empty();
        }
        Optional<Course> courseOpt = courseRepository.findByCode(normalize(courseCode));
        Optional<Term> termOpt = termRepository.findByLabel(termLabel);
        if (courseOpt.isEmpty() || termOpt.isEmpty()) {
            return Optional.empty();
        }

        return courseOfferingRepository.findByCourseAndTerm(courseOpt.get(), termOpt.get());
    }

    // return offerings for a specific term
    public List<CourseOffering> getOfferingsForTerm(Term term) {
        if (term == null) {
            return Collections.emptyList();
        }
        return courseOfferingRepository.findByTerm(term);
    }

    // returns offering for current term
    public List<CourseOffering> getOfferingsForActiveTerm() {
        return termRepository.findByActiveTrue()
                .map(courseOfferingRepository::findByTerm)
                .orElseGet(Collections::emptyList);
    }

    // returns offerings for a term label if the term exists
    public List<CourseOffering> getOfferingsForTermLabel(String termLabel) {
        if (termLabel == null || termLabel.isBlank()) {
            return Collections.emptyList();
        }

        return termRepository.findByLabel(termLabel)
                .map(courseOfferingRepository::findByTerm)
                .orElseGet(Collections::emptyList);
    }

    // Returns offerings that are not full
    public List<CourseOffering> filterOpenOfferings(List<CourseOffering> offerings) {
        if (offerings == null || offerings.isEmpty()) {
            return Collections.emptyList();
        }

        List<CourseOffering> open = new ArrayList<>();
        for (CourseOffering offering : offerings) {
            if (!isFull(offering)) {
                open.add(offering);
            }
        }

        return open;
    }

    // Returns offerings student is eligible to take
    public List<CourseOffering> filterEligibleOfferings(List<CourseOffering> offerings,
            Set<String> completedCourseCodes) {
        if (offerings == null || offerings.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> normalizedCompleted = normalizeCourseCodeSet(completedCourseCodes);
        List<CourseOffering> eligible = new ArrayList<>();

        for (CourseOffering offering : offerings) {
            if (isEligible(offering.getCourse(), normalizedCompleted)) {
                eligible.add(offering);
            }
        }

        return eligible;
    }

    // returns offerings in given term that aren't full and student is eligible to
    // take
    public List<CourseOffering> getAvailableOfferingsForStudent(Term term, Set<String> completedCourseCodes) {
        List<CourseOffering> termOfferings = getOfferingsForTerm(term);
        List<CourseOffering> openOfferings = filterOpenOfferings(termOfferings);
        return filterEligibleOfferings(openOfferings, completedCourseCodes);
    }

    // returns offerings in current term that aren't full and student is eligible to
    // take
    public List<CourseOffering> getAvailableOfferingsForStudent(Set<String> completedCourseCodes) {
        List<CourseOffering> activeOfferings = getOfferingsForActiveTerm();
        List<CourseOffering> openOfferings = filterOpenOfferings(activeOfferings);
        return filterEligibleOfferings(openOfferings, completedCourseCodes);
    }

    // returns true if course offering is at capacity
    public boolean isFull(CourseOffering offering) {
        if (offering == null) {
            return true;
        }
        return offering.getEnrolledCount() >= offering.getCapacity();
    }

    // returns true if student has completed all prerequisites for course
    public boolean isEligible(Course course, Set<String> completedCourseCodes) {
        if (course == null) {
            return false;
        }

        Set<String> normalizedCompleted = normalizeCourseCodeSet(completedCourseCodes);
        List<Prerequisite> prerequisites = prerequisiteRepository.findByCourse(course);

        for (Prerequisite prerequisite : prerequisites) {
            String requiredCode = prerequisite.getRequiredCourse().getCode();
            if (!normalizedCompleted.contains(normalize(requiredCode))) {
                return false;
            }
        }

        return true;
    }

    // returns a list of missing prerequisite codes missing for a course
    public List<String> getMissingPrerequisites(Course course, Set<String> completedCourseCodes) {
        if (course == null) {
            return Collections.emptyList();
        }

        Set<String> normalizeCompleted = normalizeCourseCodeSet(completedCourseCodes);
        List<String> missing = new ArrayList<>();
        List<Prerequisite> prerequisites = prerequisiteRepository.findByCourse(course);

        for (Prerequisite prerequisite : prerequisites) {
            String requiredCode = prerequisite.getRequiredCourse().getCode();
            String normalizedRequiredCode = normalize(requiredCode);

            if (!normalizeCompleted.contains(normalizedRequiredCode)) {
                missing.add(requiredCode);
            }
        }

        return missing;
    }

    // Returns prerequisite chain for a course
    public List<String> getPrerequisiteCodes(Course course) {
        if (course == null) {
            return Collections.emptyList();
        }

        List<String> prereqCodes = new ArrayList<>();
        List<Prerequisite> prerequisites = prerequisiteRepository.findByCourse(course);

        for (Prerequisite prerequisite : prerequisites) {
            prereqCodes.add(prerequisite.getRequiredCourse().getCode());
        }

        return prereqCodes;
    }

    // Filters given courses to only ones students can take
    public List<Course> filterEligibleCourses(List<Course> courses, Set<String> completedCourseCodes) {
        if (courses == null || courses.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> normalizedCompleted = normalizeCourseCodeSet(completedCourseCodes);
        List<Course> eligible = new ArrayList<>();

        for (Course course : courses) {
            if (isEligible(course, normalizedCompleted)) {
                eligible.add(course);
            }
        }

        return eligible;
    }

    // returns all courses student can take in active term
    public List<CourseOffering> getFilteredCatalogForStudent(Set<String> completedCourseCodes) {
        return getAvailableOfferingsForStudent(completedCourseCodes);
    }

    private Set<String> normalizeCourseCodeSet(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> normalized = new HashSet<>();
        for (String code : codes) {
            if (code != null && !code.isBlank()) {
                normalized.add(normalize(code));
            }
        }

        return normalized;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}