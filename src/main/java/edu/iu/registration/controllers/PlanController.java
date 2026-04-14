package edu.iu.registration.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.iu.registration.services.CourseService;
import edu.iu.registration.services.PlanService;

@Controller
public class PlanController {

    private final PlanService planService;
    private final CourseService courseService;

    public PlanController(PlanService planService, CourseService courseService) {
        this.planService = planService;
        this.courseService = courseService;
    }

    @GetMapping("/plan")
    public String showPlan(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        model.addAttribute("groupedPlan", planService.getPlanGroupedBySemester(username));
        model.addAttribute("completedCourseCodes", planService.getCompletedCourseCodes(username));
        return "plan";
    }

    @PostMapping("/plan/save")
    public String savePlanCompletions(
            @RequestParam(name = "completedCourses", required = false) List<String> completedCourses,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        planService.saveCompletedCourses(principal.getName(), completedCourses);
        return "redirect:/plan";
    }

    @GetMapping("/plan/edit")
    public String editSemester(
            @RequestParam String term,
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        model.addAttribute("term", term);
        model.addAttribute("selectedCourseCodes", planService.getSelectedCourseCodesForTerm(username, term));
        model.addAttribute("availableOfferings", courseService.getOfferingsForTermLabel(term));
        return "plan-edit";
    }

    @PostMapping("/plan/edit/save")
    public String saveSemesterEdits(
            @RequestParam String term,
            @RequestParam(name = "selectedCourses", required = false) List<String> selectedCourses,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        planService.saveCoursesForTerm(principal.getName(), term, selectedCourses);
        return "redirect:/plan";
    }
}