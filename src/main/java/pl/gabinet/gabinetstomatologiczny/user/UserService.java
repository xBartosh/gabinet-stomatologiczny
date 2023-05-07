package pl.gabinet.gabinetstomatologiczny.user;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.gabinet.gabinetstomatologiczny.role.Role;
import pl.gabinet.gabinetstomatologiczny.role.RoleType;
import pl.gabinet.gabinetstomatologiczny.role.RoleRepository;
import pl.gabinet.gabinetstomatologiczny.surgery.Surgery;
import pl.gabinet.gabinetstomatologiczny.surgery.SurgeryRepository;
import pl.gabinet.gabinetstomatologiczny.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SurgeryRepository surgeryRepository;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       SurgeryRepository surgeryRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.surgeryRepository = surgeryRepository;
    }

    public void saveUser(UserDto userDto) {
        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();

        Role role = roleRepository.findByRoleType(userDto.getRoleType());
        if (role == null) {
            role = createRoleIfDoesNotExist(userDto.getRoleType());
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new IllegalArgumentException("User with email " + email + " not found");
        }
    }

    public List<User> findUsersByRoleName(String roleName) {
        RoleType roleType = RoleType.getRoleType(roleName);
        Role role = roleRepository.findByRoleType(roleType);
        return userRepository.findByRolesIn(List.of(role));
    }

    public double getBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("User with email " + email + " not found"));
        return user.getBalance();
    }

    @Transactional
    public double addBalance(String email, double value) throws IllegalArgumentException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("User with email " + email + " not found"));
        return user.addBalanceAndGet(value);
    }

    @Transactional
    public synchronized double pay(String email, List<String> surgeries) throws IllegalArgumentException, IllegalStateException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));

        int sumToPay = 0;
        for (String surgeryName: surgeries) {
            Surgery srg = surgeryRepository.findByName(surgeryName)
                    .orElseThrow(() -> new IllegalArgumentException("There is no surgery with name=" + surgeryName));
            sumToPay += srg.getPrice();
        }
        return user.payAndGet(sumToPay);
    }

    private Role createRoleIfDoesNotExist(RoleType roleType) {
        Role role = new Role();
        role.setRoleType(roleType);
        return roleRepository.save(role);
    }
}
