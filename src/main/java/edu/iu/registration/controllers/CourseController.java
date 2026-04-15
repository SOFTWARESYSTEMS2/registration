package edu.iu.registration.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.entities.Course;
import edu.iu.registration.data.entities.CourseOffering;
import edu.iu.registration.data.entities.Term;
import edu.iu.registration.data.repositories.AppUserRepository;
import edu.iu.registration.data.repositories.TermRepository;
import edu.iu.registration.services.CourseService;
import edu.iu.registration.services.DegreeMapService;
import edu.iu.registration.services.PlanService;

@Controller
public class CourseController {

    private final CourseService courseService;
    private final DegreeMapService degreeMapService;
    private final PlanService planService;
    private final AppUserRepository appUserRepository;
    private final TermRepository termRepository;

    public CourseController(
            CourseService courseService,
            DegreeMapService degreeMapService,
            PlanService planService,
            AppUserRepository appUserRepository,
            TermRepository termRepository) {
        this.courseService = courseService;
        this.degreeMapService = degreeMapService;
        this.planService = planService;
        this.appUserRepository = appUserRepository;
        this.termRepository = termRepository;
    }

    @GetMapping("/courses")
    public String catalog(
            @RequestParam(name = "term", required = false) String termLabel,
            Model model,
            Principal principal) {

        AppUser user = resolveUser(principal);
        if (user == null) return "redirect:/login";

        Set<String> completedCodes = planService.getCompletedCourseCodes(user);
        Set<Course> relevantCourses = degreeMapService.getRelevantCourses(user);

        // Pick the active term if none is selected
        List<CourseOffering> offerings;
        if (termLabel != null && !termLabel.isBlank()) {
            offerings = courseService.getOfferingsForTermLabel(termLabel);
        } else {
            offerings = courseService.getOfferingsForActiveTerm();
            termLabel = termRepository.findByActiveTrue()
                    .map(Term::getLabel)
                    .orElse("");
        }

        // Apply all three filters, then restrict to degree-relevant courses
        List<CourseOffering> filtered = courseService.getAvailableOfferingsForStudent(offerings, completedCodes)
                .stream()
                .filter(o -> relevantCourses.contains(o.getCourse()))
                .collect(Collectors.toList());

        model.addAttribute("offerings", filtered);
        model.addAttribute("terms", termRepository.findAll());
        model.addAttribute("selectedTerm", termLabel);
        model.addAttribute("completedCodes", completedCodes);
        return "catalog";
    }

    private AppUser resolveUser(Principal principal) {
        if (principal == null) return null;
        return appUserRepository.findByUsernameWithCourses(principal.getName()).orElse(null);
    }
}
