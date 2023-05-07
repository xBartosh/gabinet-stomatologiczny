package pl.gabinet.gabinetstomatologiczny.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.gabinet.gabinetstomatologiczny.role.RoleType;
import pl.gabinet.gabinetstomatologiczny.surgery.Surgery;
import pl.gabinet.gabinetstomatologiczny.surgery.SurgeryService;

import java.util.List;

@Controller
@RequestMapping("/surgery")
public class SurgeryController {
    private final SurgeryService surgeryService;

    public SurgeryController(SurgeryService surgeryService) {
        this.surgeryService = surgeryService;
    }

    @GetMapping("/all")
    public String surgeries(Model model) {
        List<Surgery> surgeries = surgeryService.findAllSurgeries();
        model.addAttribute("surgeries", surgeries);
        return "surgeries";
    }

    @GetMapping("/add")
    public String surgeries(SecurityContextHolderAwareRequestWrapper requestWrapper) {
        boolean userInRole = requestWrapper.isUserInRole(RoleType.ROLE_DOCTOR.name());
        return  userInRole ? "add-surgery" : "error";
    }
}
