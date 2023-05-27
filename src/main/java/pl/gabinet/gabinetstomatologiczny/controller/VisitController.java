package pl.gabinet.gabinetstomatologiczny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.gabinet.gabinetstomatologiczny.role.RoleType;
import pl.gabinet.gabinetstomatologiczny.surgery.Surgery;
import pl.gabinet.gabinetstomatologiczny.surgery.SurgeryService;
import pl.gabinet.gabinetstomatologiczny.user.User;
import pl.gabinet.gabinetstomatologiczny.user.UserService;
import pl.gabinet.gabinetstomatologiczny.visit.Visit;
import pl.gabinet.gabinetstomatologiczny.visit.VisitService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/visit")
public class VisitController {
    private final VisitService visitService;
    private final SurgeryService surgeryService;
    private final UserService userService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VisitController(VisitService visitService, SurgeryService surgeryService, UserService userService) {
        this.visitService = visitService;
        this.surgeryService = surgeryService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String visits(Principal principal, Model model) {
        String email = principal.getName();
        List<Visit> visits = visitService.findVisitsForUser(email);
        double balance = userService.getBalance(principal.getName());
        model.addAttribute("visits", visits);
        model.addAttribute("balance", balance);
        model.addAttribute("user", userService.findUserByEmail(principal.getName()).get());
        return "visits";
    }

    @GetMapping("/book")
    public String book(Principal principal, Model model) {
        List<String> surgeries = surgeryService.findAllSurgeries().stream().map(Surgery::getName).toList();
        List<User> doctors = userService.findUsersByRoleName(RoleType.ROLE_DOCTOR.name());
        List<User> patients = userService.findUsersByRoleName(RoleType.ROLE_PATIENT.name());
        model.addAttribute("surgeries", surgeries);
        model.addAttribute("doctors", doctors);
        model.addAttribute("isDoctor", userService.isUserDoctor(principal.getName()));
        model.addAttribute("patients", patients);
        model.addAttribute("start", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return "book";
    }
}
