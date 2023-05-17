package pl.gabinet.gabinetstomatologiczny.visit;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.gabinet.gabinetstomatologiczny.message.Message;
import pl.gabinet.gabinetstomatologiczny.notification.NotificationService;
import pl.gabinet.gabinetstomatologiczny.surgery.Surgery;
import pl.gabinet.gabinetstomatologiczny.surgery.SurgeryRepository;
import pl.gabinet.gabinetstomatologiczny.surgery.SurgeryService;
import pl.gabinet.gabinetstomatologiczny.user.User;
import pl.gabinet.gabinetstomatologiczny.user.UserRepository;
import pl.gabinet.gabinetstomatologiczny.user.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Service
public class VisitService {
    private final VisitRepository visitRepository;
    private final UserRepository userRepository;
    private final SurgeryRepository surgeryRepository;
    private final NotificationService notificationService;
    private final UserService userService;
    private final SurgeryService surgeryService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VisitService(VisitRepository visitRepository, UserRepository userRepository, SurgeryRepository surgeryRepository, NotificationService notificationService, UserService userService, SurgeryService surgeryService) {
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
        this.surgeryRepository = surgeryRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.surgeryService = surgeryService;
    }

    public Visit findVisitById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visit with id " + id + " not found"));
    }

    public synchronized Visit scheduleVisit(String email, Long userId, List<String> surgeries, LocalDateTime start, boolean isDoctor) {
        try (Stream<String> stream = surgeries.stream()) {
            if (stream.anyMatch(StringUtils::isBlank)) {
                throw new IllegalArgumentException("Surgery name cannot be null or empty.");
            }
        }

        User user;
        User otherUser;
        String userType;
        if (isDoctor) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
            otherUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
            userType = "doctor";
        } else {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
            otherUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
            userType = "patient";
        }

        LocalDateTime end = start.plusHours(1);
        boolean doctorBusy = isDoctorBusy(user, start, start);
        if (!doctorBusy) {
            List<Surgery> surgeriesList = surgeries.stream()
                    .map(surgeryRepository::findByName)
                    .map(optSurgery -> optSurgery.orElseThrow(() -> new IllegalArgumentException("There is no surgery with the given name.")))
                    .collect(Collectors.toList());
            notificationService.notify(format(Message.NEW_VISIT.getMessage(), start.format(DATE_TIME_FORMATTER), surgeriesList.stream().map(Surgery::getName).collect(Collectors.joining(", "))), user);
            notificationService.notify(format(Message.NEW_VISIT.getMessage(), start.format(DATE_TIME_FORMATTER), surgeriesList.stream().map(Surgery::getName).collect(Collectors.joining(", "))), otherUser);
            return visitRepository.save(new Visit(otherUser, user, surgeriesList, start, end));
        }

        throw new IllegalArgumentException(format("Cannot schedule a visit for %s=%s %s, at=%s", userType, user.getFirstName(), user.getLastName(), start));
    }

    public List<Visit> findVisitsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
        return visitRepository.findByDoctorOrPatient(user, user);
    }

    public List<Visit> findVisitsForDoctor(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor with id " + doctorId + " not found"));
        return visitRepository.findByDoctor(doctor);
    }

    public List<Visit> findVisitsForPatient(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient with id " + patientId + " not found"));
        return visitRepository.findByDoctor(patient);
    }

    public List<Visit> findVisitsForDoctorWithin(Long doctorId, LocalDateTime start, LocalDateTime end) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + doctorId + " not found"));
        return visitRepository.findByDoctorAndStartGreaterThanEqualAndEndLessThanEqual(doctor, start, end);
    }

    @Transactional
    public Visit rescheduleVisit(Long visitId, LocalDateTime newStart) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit with id " + visitId + " not found"));

        boolean doctorBusy = isDoctorBusy(visit.getDoctor(), newStart, newStart);
        LocalDateTime actualStart = visit.getStart();

        if (!doctorBusy) {
            visit.setStart(newStart);
            visit.setEnd(newStart.plusHours(1));
            notificationService.notify(format(Message.RESCHEDULE_VISIT.getMessage(), actualStart.format(DATE_TIME_FORMATTER), newStart.format(DATE_TIME_FORMATTER)), visit.getPatient());
            notificationService.notify(format(Message.RESCHEDULE_VISIT.getMessage(), actualStart.format(DATE_TIME_FORMATTER), newStart.format(DATE_TIME_FORMATTER)), visit.getDoctor());
            return visit;
        }

        throw new IllegalArgumentException(format("Cannot reschedule a visit with id=%d, at=%s", visitId, newStart));
    }

    @Transactional
    public void payForVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit with id " + visitId + " not found"));
        if (!visit.isPaid()) {
            userService.pay(visit.getPatient().getEmail(),
                    visit.getSurgeries());
            visit.setPaid(true);
            notificationService.notify(format(Message.PAY_VISIT.getMessage(), visit.getStart().format(DATE_TIME_FORMATTER), surgeryService.getPriceForSurgeries(visit.getSurgeries())), visit.getPatient());
        } else {
            throw new IllegalStateException("Visit has already been paid!");
        }

    }

    public void cancelVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit with id " + visitId + " not found"));
        notificationService.notify(format(Message.CANCEL_VISIT.getMessage(), visit.getStart().format(DATE_TIME_FORMATTER)), visit.getPatient());
        notificationService.notify(format(Message.CANCEL_VISIT.getMessage(), visit.getStart().format(DATE_TIME_FORMATTER)), visit.getDoctor());
        visitRepository.deleteById(visitId);
    }

    private boolean isDoctorBusy(User doctor, LocalDateTime start, LocalDateTime end) {
        List<Visit> visits = visitRepository.findByDoctorAndStartLessThanEqualAndEndGreaterThanEqual(doctor, start, end);
        return !visits.isEmpty();
    }
}
