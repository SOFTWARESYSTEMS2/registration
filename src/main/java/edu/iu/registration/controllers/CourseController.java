package edu.iu.registration.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.util.StringUtils;

import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.services.CourseService;

@Controller
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public String catalog(@RequestParam(name = "term", required = false) String term, Model model) {
        // Demo data for sprint 1
        Set<String> completedCourses = Set.of("CSCI-C200", "MATH-M101");

        List<CourseOffering> offerings;

        if (StringUtils.hasText(term)) {
            offerings = courseService.getOfferingsForTermLabel(term);
            offerings = courseService.filterOpenOfferings(offerings);
            offerings = courseService.filterEligibleOfferings(offerings, completedCourses);
        } else {
            offerings = courseService.getAvailableOfferingsForStudent(completedCourses);
        }

        model.addAttribute("offerings", offerings);
        model.addAttribute("selectedTerm", term == null ? "" : term);
        model.addAttribute("completedCourses", completedCourses);

        return "catalog";
    }

    @GetMapping("/courses/eligible")
    public String eligibleCourses(@RequestParam(name = "term", required = false) String term, Model model) {
        return catalog(term, model);
    }
}
