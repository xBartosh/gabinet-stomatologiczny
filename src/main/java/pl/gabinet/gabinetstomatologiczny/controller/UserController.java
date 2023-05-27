package pl.gabinet.gabinetstomatologiczny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.gabinet.gabinetstomatologiczny.notification.Notification;
import pl.gabinet.gabinetstomatologiczny.notification.NotificationService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final NotificationService notificationService;

    public UserController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/add")
    public String add() {
        return "add-balance";
    }

    @GetMapping("/notifications")
    public String notifications(Principal principal, Model model) {
        List<Notification> notifications = notificationService.findAllNotificationsForUser(principal.getName());
        model.addAttribute("notifications", notifications);
        return "notifications";
    }
}
