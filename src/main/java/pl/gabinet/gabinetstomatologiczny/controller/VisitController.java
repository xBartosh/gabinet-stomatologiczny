package pl.gabinet.gabinetstomatologiczny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.gabinet.gabinetstomatologiczny.visit.Visit;
import pl.gabinet.gabinetstomatologiczny.visit.VisitService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/visit")
public class VisitController {
    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/all")
    public String visits(Principal principal, Model model) {
        String email = principal.getName();
        List<Visit> visits = visitService.findVisitsForUser(email);
        model.addAttribute("visits", visits);
        return "visits";
    }
}
