package edu.iu.registration.controllers;

import edu.iu.registration.services.SemesterPlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SemesterPlanController {

    private final SemesterPlanService semesterPlanService;

    public SemesterPlanController(SemesterPlanService semesterPlanService) {
        this.semesterPlanService = semesterPlanService;
    }

    @GetMapping("/semester-plan")
    public String showSemesterPlan(Model model) {
        model.addAttribute("semesters", semesterPlanService.getSemesters());
        model.addAttribute("courses", semesterPlanService.getAvailableCourses());
        model.addAttribute("groupedPlan", semesterPlanService.getPlanGroupedBySemester());
        return "semester-plan";
    }

    @PostMapping("/semester-plan/add")
    public String addCourseToSemester(@RequestParam String semester,
            @RequestParam String courseCode) {
        semesterPlanService.addCourseToSemester(semester, courseCode);
        return "redirect:/semester-plan";
    }
}
