package edu.iu.registration.controllers;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.data.repositories.AppUserRepository;
import edu.iu.registration.models.DegreeProgress;
import edu.iu.registration.models.RequirementStatus;
import edu.iu.registration.services.DegreeMapService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private final AppUserRepository appUserRepository;
    private final DegreeMapService degreeMapService;

    public HomeController(AppUserRepository appUserRepository, DegreeMapService degreeMapService) {
        this.appUserRepository = appUserRepository;
        this.degreeMapService = degreeMapService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        AppUser user = resolveUser(principal);
        if (user == null) return "redirect:/login";

        DegreeProgress progress = degreeMapService.getProgress(user);
        List<RequirementStatus> requirements = degreeMapService.getRequirementStatuses(user);

        model.addAttribute("user", user);
        model.addAttribute("progress", progress);
        model.addAttribute("requirements", requirements);
        return "dashboard";
    }

    private AppUser resolveUser(Principal principal) {
        if (principal == null) return null;
        return appUserRepository.findByUsernameWithCourses(principal.getName()).orElse(null);
    }
}
