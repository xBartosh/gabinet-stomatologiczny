package pl.gabinet.gabinetstomatologiczny.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.gabinet.gabinetstomatologiczny.role.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRolesIn(List<Role> roles);
}
