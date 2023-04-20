package pl.gabinet.gabinetstomatologiczny.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.gabinet.gabinetstomatologiczny.role.Role;
import pl.gabinet.gabinetstomatologiczny.role.RoleType;
import pl.gabinet.gabinetstomatologiczny.role.RoleRepository;
import pl.gabinet.gabinetstomatologiczny.user.dto.UserDto;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByRoleType(userDto.getRoleType());
        if (role == null) {
            role = createRoleIfDoesNotExist(userDto.getRoleType());
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private Role createRoleIfDoesNotExist(RoleType roleType) {
        Role role = new Role();
        role.setRoleType(roleType);
        return roleRepository.save(role);
    }
}
