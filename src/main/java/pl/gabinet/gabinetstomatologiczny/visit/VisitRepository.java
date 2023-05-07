package pl.gabinet.gabinetstomatologiczny.visit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.gabinet.gabinetstomatologiczny.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends CrudRepository<Visit, Long> {
    List<Visit> findByDoctorAndStartGreaterThanEqualAndEndLessThanEqual(User doctor, LocalDateTime start, LocalDateTime end);
    List<Visit> findByDoctorAndStartLessThanAndEndGreaterThan(User doctor, LocalDateTime start, LocalDateTime end);
    List<Visit> findByDoctorAndStartLessThanEqualAndEndGreaterThanEqual(User doctor, LocalDateTime start, LocalDateTime end);
    List<Visit> findByDoctor(User doctor);
    List<Visit> findByPatient(User patient);
    List<Visit> findByDoctorOrPatient(User doctor, User patient);
}
