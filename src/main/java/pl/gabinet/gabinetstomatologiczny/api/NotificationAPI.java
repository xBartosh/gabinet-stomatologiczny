package pl.gabinet.gabinetstomatologiczny.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.gabinet.gabinetstomatologiczny.notification.NotificationService;

import java.security.Principal;

@RestController
@RequestMapping("/api/notification")
@Tag(name = "Notification API")
public class NotificationAPI {
    private final NotificationService notificationService;

    public NotificationAPI(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all notifications for logged-in user")
    public ResponseEntity<?> findNotificationsForLoggedInUser(Principal principal) {
        String email = principal.getName();
        try {
            return ResponseEntity.ok(notificationService.findAllNotificationsForUser(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all-unread")
    @Operation(summary = "Get all unread notifications for logged-in user")
    public ResponseEntity<?> findUnreadNotificationsForLoggedInUser(Principal principal) {
        String email = principal.getName();
        try {
            return ResponseEntity.ok(notificationService.findAllUnreadNotificationsForUser(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/read")
    @Operation(summary = "Set a notification as read")
    public ResponseEntity<?> readNotification(@RequestParam Long notificationId) {
        try {
            notificationService.readNotification(notificationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
