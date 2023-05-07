package pl.gabinet.gabinetstomatologiczny.surgery;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurgeryRepository extends CrudRepository<Surgery, Long> {
    Optional<Surgery> findByName(String name);
}
