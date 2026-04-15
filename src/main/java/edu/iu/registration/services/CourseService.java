package edu.iu.registration.services;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    // ── Catalog queries ────────────────────────────────────────────────────

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return courseRepository.findByCode(normalize(code));
    }

    // ── Offering queries ───────────────────────────────────────────────────

    public Optional<CourseOffering> getOffering(String courseCode, String termLabel) {
        if (courseCode == null || courseCode.isBlank() || termLabel == null || termLabel.isBlank()) {
            return Optional.empty();
        }

        return courseRepository.findByCode(normalize(courseCode))
                .flatMap(course -> termRepository.findByLabel(termLabel)
                        .flatMap(term -> courseOfferingRepository.findByCourseAndTerm(course, term)));
    }

    public List<CourseOffering> getOfferingsForTerm(Term term) {
        if (term == null) {
            return Collections.emptyList();
        }
        return courseOfferingRepository.findByTerm(term);
    }

    public List<CourseOffering> getOfferingsForActiveTerm() {
        return termRepository.findByActiveTrue()
                .map(courseOfferingRepository::findByTerm)
                .orElse(Collections.emptyList());
    }

    public List<CourseOffering> getOfferingsForTermLabel(String termLabel) {
        if (termLabel == null || termLabel.isBlank()) {
            return Collections.emptyList();
        }
        return termRepository.findByLabel(termLabel)
                .map(courseOfferingRepository::findByTerm)
                .orElse(Collections.emptyList());
    }

    // ── Filtering ──────────────────────────────────────────────────────────

    public List<CourseOffering> filterOpenOfferings(List<CourseOffering> offerings) {
        if (offerings == null || offerings.isEmpty()) {
            return Collections.emptyList();
        }

        return offerings.stream()
                .filter(Objects::nonNull)
                .filter(o -> !o.isFull())
                .collect(Collectors.toList());
    }

    public List<CourseOffering> filterEligibleOfferings(
            List<CourseOffering> offerings,
            Set<String> satisfiedCourseCodes) {

        if (offerings == null || offerings.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> satisfied = normalizeSet(satisfiedCourseCodes);

        List<Course> courses = offerings.stream()
                .filter(Objects::nonNull)
                .map(CourseOffering::getCourse)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, List<Prerequisite>> prereqsByCourseId = prerequisiteRepository.findByCourseIn(courses)
                .stream()
                .collect(Collectors.groupingBy(p -> p.getCourse().getId()));

        return offerings.stream()
                .filter(Objects::nonNull)
                .filter(o -> o.getCourse() != null)
                .filter(o -> isEligible(o.getCourse(), satisfied, prereqsByCourseId))
                .collect(Collectors.toList());
    }

    public List<CourseOffering> filterOutCompletedCourses(
            List<CourseOffering> offerings,
            Set<String> completedCourseCodes) {

        if (offerings == null || offerings.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> completed = normalizeSet(completedCourseCodes);

        return offerings.stream()
                .filter(Objects::nonNull)
                .filter(o -> o.getCourse() != null)
                .filter(o -> o.getCourse().isRepeatable()
                        || !completed.contains(normalize(o.getCourse().getCode())))
                .collect(Collectors.toList());
    }

    public List<CourseOffering> getAvailableOfferingsForStudent(
            List<CourseOffering> offerings,
            Set<String> completedCourseCodes) {

        List<CourseOffering> open = filterOpenOfferings(offerings);
        List<CourseOffering> eligible = filterEligibleOfferings(open, completedCourseCodes);
        return filterOutCompletedCourses(eligible, completedCourseCodes);
    }

    // ── Direct prerequisite helpers ────────────────────────────────────────

    public List<String> getPrerequisiteCodes(Course course) {
        if (course == null) {
            return Collections.emptyList();
        }

        return prerequisiteRepository.findByCourse(course).stream()
                .map(Prerequisite::getRequiredCourse)
                .filter(Objects::nonNull)
                .map(Course::getCode)
                .collect(Collectors.toList());
    }

    public List<String> getMissingPrerequisites(Course course, Set<String> satisfiedCourseCodes) {
        if (course == null) {
            return Collections.emptyList();
        }

        Set<String> satisfied = normalizeSet(satisfiedCourseCodes);

        return prerequisiteRepository.findByCourse(course).stream()
                .map(Prerequisite::getRequiredCourse)
                .filter(Objects::nonNull)
                .map(Course::getCode)
                .filter(code -> !satisfied.contains(normalize(code)))
                .collect(Collectors.toList());
    }

    public boolean isEligibleForCourse(Course course, Set<String> satisfiedCourseCodes) {
        return getMissingPrerequisites(course, satisfiedCourseCodes).isEmpty();
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private boolean isEligible(
            Course course,
            Set<String> normalizedSatisfied,
            Map<Long, List<Prerequisite>> prereqsByCourseId) {

        List<Prerequisite> prereqs = prereqsByCourseId.getOrDefault(
                course.getId(),
                Collections.emptyList()
        );

        return prereqs.stream()
                .map(Prerequisite::getRequiredCourse)
                .filter(Objects::nonNull)
                .map(Course::getCode)
                .allMatch(code -> normalizedSatisfied.contains(normalize(code)));
    }

    private Set<String> normalizeSet(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptySet();
        }

        return codes.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(this::normalize)
                .collect(Collectors.toSet());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}