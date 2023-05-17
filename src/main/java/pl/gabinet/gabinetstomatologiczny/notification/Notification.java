package pl.gabinet.gabinetstomatologiczny.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pl.gabinet.gabinetstomatologiczny.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    @JsonIgnore
    private User recipient;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public Notification(String message, User recipient) {
        this.message = message;
        this.recipient = recipient;
        this.timestamp = LocalDateTime.now();
    }
}
