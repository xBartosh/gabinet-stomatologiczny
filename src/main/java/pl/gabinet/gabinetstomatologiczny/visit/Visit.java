package pl.gabinet.gabinetstomatologiczny.visit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.gabinet.gabinetstomatologiczny.surgery.Surgery;
import pl.gabinet.gabinetstomatologiczny.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToMany
    @JoinTable(
            name = "visit_surgeries",
            joinColumns = {@JoinColumn(name = "visit_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "surgery_id", referencedColumnName = "id")}
    )
    private List<Surgery> surgeries;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime end;

    private boolean paid = false;

    public Visit(User patient, User doctor, List<Surgery> surgeries, LocalDateTime start, LocalDateTime end) {
        this.patient = patient;
        this.doctor = doctor;
        this.surgeries = surgeries;
        this.start = start;
        this.end = end;
    }
}
