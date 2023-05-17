package pl.gabinet.gabinetstomatologiczny.notification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.gabinet.gabinetstomatologiczny.user.User;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findAllByRecipient(User recipient);
    List<Notification> findAllByRecipientAndIsReadEquals(User recipient, boolean isRead);
}
