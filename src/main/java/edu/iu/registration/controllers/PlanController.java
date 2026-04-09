package edu.iu.registration.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.iu.registration.services.PlanService;

@Controller
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/plan")
    public String showPlan(Model model, Principal principal) {
        if (principal != null) {
            planService.loadCompletedCoursesForUser(principal.getName());
        }

        model.addAttribute("groupedPlan", planService.getPlanGroupedBySemester());
        model.addAttribute("completedCourseCodes", planService.getCompletedCourseCodes());
        return "plan";
    }

    @PostMapping("/plan/save")
    public String savePlanCompletions(
            @RequestParam(name = "completedCourses", required = false) List<String> completedCourses,
            Principal principal) {

        if (principal != null) {
            planService.saveCompletedCourses(principal.getName(), completedCourses);
        }

        return "redirect:/plan";
    }

    @GetMapping("/plan/edit")
    public String editSemester(@RequestParam String term, Model model) {
        model.addAttribute("term", term);
        model.addAttribute("selectedCourseCodes", planService.getSelectedCourseCodesForTerm(term));
        model.addAttribute("availableOfferings", planService.getAvailableOfferingsForTerm(term));
        return "plan-edit";
    }

    @PostMapping("/plan/edit/save")
    public String saveSemesterEdits(
            @RequestParam String term,
            @RequestParam(name = "selectedCourses", required = false) List<String> selectedCourses) {
        planService.saveCoursesForTerm(term, selectedCourses);
        return "redirect:/plan";
    }
}