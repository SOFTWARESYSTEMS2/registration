package edu.iu.registration.controllers;

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
    public String showPlan(Model model) {
        model.addAttribute("groupedPlan", planService.getPlanGroupedBySemester());
        model.addAttribute("completedCourseCodes", planService.getCompletedCourseCodes());
        return "plan";
    }

    @PostMapping("/plan/save")
    public String savePlanCompletions(
            @RequestParam(name = "completedCourses", required = false) List<String> completedCourses) {
        planService.saveCompletedCourses(completedCourses);
        return "redirect:/plan";
    }
}