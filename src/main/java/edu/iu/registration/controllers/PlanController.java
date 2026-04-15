package edu.iu.registration.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.StudentPlan;
import edu.iu.registration.data.repositories.AppUserRepository;
import edu.iu.registration.data.repositories.TermRepository;
import edu.iu.registration.services.CourseService;
import edu.iu.registration.services.DegreeMapService;
import edu.iu.registration.services.PlanService;

@Controller
public class PlanController {

    private final PlanService planService;
    private final CourseService courseService;
    private final DegreeMapService degreeMapService;
    private final AppUserRepository appUserRepository;
    private final TermRepository termRepository;

    public PlanController(PlanService planService, CourseService courseService,
            DegreeMapService degreeMapService, AppUserRepository appUserRepository,
            TermRepository termRepository) {
        this.planService = planService;
        this.courseService = courseService;
        this.degreeMapService = degreeMapService;
        this.appUserRepository = appUserRepository;
        this.termRepository = termRepository;
    }

    @GetMapping("/plan")
    public String showPlan(Model model, Principal principal) {
        AppUser user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Map<String, List<StudentPlan>> grouped = planService.getPlansGroupedBySemester(user);
        Set<String> completedCodes = planService.getCompletedCourseCodes(user);

        Map<Long, List<String>> missingPrereqs = planService.getMissingPrereqsForUser(user);
        Map<Long, String> statuses = planService.getStatusesForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("groupedPlan", grouped);
        model.addAttribute("completedCodes", completedCodes);
        model.addAttribute("missingPrereqs", missingPrereqs);
        model.addAttribute("statuses", statuses);
        model.addAttribute("allTerms", termRepository.findAllByOrderByIdAsc());
        return "plan";
    }

    @PostMapping("/plan/save")
    public String saveCompletedCourses(
            @RequestParam(name = "completedCourses", required = false) List<String> completedCourses,
            Principal principal) {
        AppUser user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        planService.saveCompletedCourses(user, completedCourses);
        return "redirect:/plan";
    }

    @GetMapping("/plan/edit")

    public String editTerm(@RequestParam String term, Model model, Principal principal) {
        AppUser user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Set<String> completedCodes = planService.getCompletedCourseCodes(user);
        Set<Course> relevantCourses = degreeMapService.getRelevantCourses(user);
        Set<String> plannedForTerm = planService.getPlannedCourseCodesForTerm(user, term);

        List<CourseOffering> termOfferings = courseService.getOfferingsForTermLabel(term);

        List<CourseOffering> eligibleOfferings = courseService.getAvailableOfferingsForStudent(termOfferings, completedCodes)
                .stream()
                .filter(o -> relevantCourses.contains(o.getCourse()))
                .collect(Collectors.toList());

        List<CourseOffering> plannedOfferings = planService.getPlansForUserAndTerm(user, term).stream()
                .map(StudentPlan::getCourseOffering)
                .toList();

        Map<Long, CourseOffering> merged = new LinkedHashMap<>();

        for (CourseOffering offering : plannedOfferings) {
            merged.put(offering.getId(), offering);
        }

        for (CourseOffering offering : eligibleOfferings) {
            merged.putIfAbsent(offering.getId(), offering);
        }

        model.addAttribute("term", term);
        model.addAttribute("availableOfferings", new ArrayList<>(merged.values()));
        model.addAttribute("plannedCodes", plannedForTerm);
        return "plan-edit";
    }

    @PostMapping("/plan/edit/save")
    public String saveTermEdits(
            @RequestParam String term,
            @RequestParam(name = "selectedCourses", required = false) List<String> selectedCourses,
            Principal principal) {
        AppUser user = resolveUser(principal);
        if (user == null) {
            return "redirect:/login";
        }
        planService.replaceTermPlan(user, term, selectedCourses);
        return "redirect:/plan";
    }

    private AppUser resolveUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return appUserRepository.findByUsernameWithCourses(principal.getName()).orElse(null);
    }
}
