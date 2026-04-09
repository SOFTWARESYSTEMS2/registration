package edu.iu.registration.services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.repositories.AppUserRepository;
import edu.iu.registration.data.repositories.CourseRepository;
import edu.iu.registration.models.PlanCourse;

@Service
public class PlanService {

    private final SemesterPlanService semesterPlanService;
    private final AppUserRepository appUserRepository;
    private final CourseRepository courseRepository;
    private final Set<String> completedCourseCodes = new HashSet<>();

    public PlanService(
            SemesterPlanService semesterPlanService,
            AppUserRepository appUserRepository,
            CourseRepository courseRepository) {
        this.semesterPlanService = semesterPlanService;
        this.appUserRepository = appUserRepository;
        this.courseRepository = courseRepository;
    }

    public Map<String, List<PlanCourse>> getPlanGroupedBySemester() {
        return semesterPlanService.getPlanGroupedBySemester();
    }

    public Set<String> getCompletedCourseCodes() {
        return completedCourseCodes;
    }

    public void loadCompletedCoursesForUser(String username) {
        completedCourseCodes.clear();

        AppUser user = appUserRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return;
        }

        for (Course course : user.getCourses()) {
            completedCourseCodes.add(course.getCode());
        }
    }

    public void saveCompletedCourses(String username, List<String> selectedCourseCodes) {
        completedCourseCodes.clear();

        AppUser user = appUserRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return;
        }

        user.getCourses().clear();

        if (selectedCourseCodes != null) {
            completedCourseCodes.addAll(selectedCourseCodes);

            for (String code : selectedCourseCodes) {
                courseRepository.findByCode(code).ifPresent(user::addCourse);
            }
        }

        appUserRepository.save(user);
    }

    public boolean isCourseComplete(String courseCode) {
        return completedCourseCodes.contains(courseCode);
    }

    public List<CourseOffering> getAvailableOfferingsForTerm(String term) {
        return semesterPlanService.getAvailableOfferingsForTerm(term);
    }

    public Set<String> getSelectedCourseCodesForTerm(String term) {
        return semesterPlanService.getPlanEntriesForTerm(term)
                .stream()
                .map(planCourse -> planCourse.getCourse().getCode())
                .collect(Collectors.toSet());
    }

    public void saveCoursesForTerm(String term, List<String> selectedCourseCodes) {
        semesterPlanService.replaceCoursesForTerm(term, selectedCourseCodes);
    }
}