package edu.iu.registration.data.access;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Prerequisite;
import edu.iu.registration.data.entities.Term;
import edu.iu.registration.data.repositories.CourseOfferingRepository;
import edu.iu.registration.data.repositories.CourseRepository;
import edu.iu.registration.data.repositories.PrerequisiteRepository;
import edu.iu.registration.data.repositories.TermRepository;

@Component
@Transactional(readOnly = true)
public class CourseAccess {

    private final CourseRepository courseRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final PrerequisiteRepository prerequisiteRepository;
    private final TermRepository termRepository;

    public CourseAccess(
            CourseRepository courseRepository,
            CourseOfferingRepository courseOfferingRepository,
            PrerequisiteRepository prerequisiteRepository,
            TermRepository termRepository) {
        this.courseRepository = courseRepository;
        this.courseOfferingRepository = courseOfferingRepository;
        this.prerequisiteRepository = prerequisiteRepository;
        this.termRepository = termRepository;
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> findCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return Optional.empty();
        }
        return courseRepository.findByCode(courseCode);
    }

    public Optional<Term> findTermByLabel(String termLabel) {
        if (termLabel == null || termLabel.isBlank()) {
            return Optional.empty();
        }
        return termRepository.findByLabel(termLabel);
    }

    public Optional<Term> findActiveTerm() {
        return termRepository.findByActiveTrue();
    }

    public Optional<CourseOffering> findCourseOffering(Course course, Term term) {
        if (course == null || term == null) {
            return Optional.empty();
        }
        return courseOfferingRepository.findByCourseAndTerm(course, term);
    }

    public Optional<CourseOffering> findCourseOffering(String courseCode, String termLabel) {
        Optional<Course> courseOpt = findCourseByCode(courseCode);
        Optional<Term> termOpt = findTermByLabel(termLabel);

        if (courseOpt.isEmpty() || termOpt.isEmpty()) {
            return Optional.empty();
        }

        return courseOfferingRepository.findByCourseAndTerm(courseOpt.get(), termOpt.get());
    }

    public List<CourseOffering> findOfferingsByTerm(Term term) {
        if (term == null) {
            return Collections.emptyList();
        }
        return courseOfferingRepository.findByTerm(term);
    }

    public List<CourseOffering> findOfferingsByTermLabel(String termLabel) {
        return findTermByLabel(termLabel)
                .map(courseOfferingRepository::findByTerm)
                .orElseGet(Collections::emptyList);
    }

    public List<CourseOffering> findOfferingsForActiveTerm() {
        return findActiveTerm()
                .map(courseOfferingRepository::findByTerm)
                .orElseGet(Collections::emptyList);
    }

    public List<Prerequisite> findPrerequisitesByCourse(Course course) {
        if (course == null) {
            return Collections.emptyList();
        }
        return prerequisiteRepository.findByCourse(course);
    }
}