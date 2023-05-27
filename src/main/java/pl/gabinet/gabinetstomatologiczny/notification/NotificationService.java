package pl.gabinet.gabinetstomatologiczny.notification;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pl.gabinet.gabinetstomatologiczny.user.User;
import pl.gabinet.gabinetstomatologiczny.user.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(NotificationRepository notificationRepository, @Lazy UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public List<Notification> findAllNotificationsForUser(String email) {
        User user = userService.findUserByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("User with email " + email + " not found"));
        return notificationRepository.findAllByRecipient(user);
    }

    public List<Notification> findAllUnreadNotificationsForUser(String email) {
        User user = userService.findUserByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("User with email " + email + " not found"));
        return notificationRepository.findAllByRecipientAndIsReadEquals(user, false);
    }

    public void notify(String message, User recipient) {
        notificationRepository.save(new Notification(message, recipient));
    }

    @Transactional
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification with id " + notificationId + " not found"));
        notification.setIsRead(true);
    }
}
