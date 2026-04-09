package edu.iu.registration.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.iu.registration.data.entities.AppUser;
import edu.iu.registration.services.StudentService;

@Controller
public class HomeController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal AppUser user) {
        /*
            Order is:
            deg: degree, min: minor, maj: major, ah: arts and humanities, sh: science and history
            with each having 'have' and 'need', so 0 is degHave, 1 is degNeed, 2 is majHave, etc
        */
        int progress[] = studentService.checkProgress(user);
        model.addAttribute("degHave", progress[0]); model.addAttribute("degNeed", progress[1]);
        model.addAttribute("majHave", progress[2]); model.addAttribute("majNeed", progress[3]);
        model.addAttribute("minHave", progress[4]); model.addAttribute("minNeed", progress[5]);
        model.addAttribute("ahHave", progress[6]); model.addAttribute("ahNeed", progress[7]);
        model.addAttribute("shHave", progress[8]); model.addAttribute("shNeed", progress[9]);
        model.addAttribute("username", user.getUsername());
        return "dashboard";
    }
}