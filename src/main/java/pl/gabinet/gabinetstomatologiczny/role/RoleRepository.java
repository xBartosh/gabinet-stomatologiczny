package pl.gabinet.gabinetstomatologiczny.role;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRoleType(RoleType roleType);
}
