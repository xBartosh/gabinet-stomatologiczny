package pl.gabinet.gabinetstomatologiczny.user;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.gabinet.gabinetstomatologiczny.message.Message;
import pl.gabinet.gabinetstomatologiczny.notification.NotificationService;
import pl.gabinet.gabinetstomatologiczny.role.Role;
import pl.gabinet.gabinetstomatologiczny.role.RoleType;
import pl.gabinet.gabinetstomatologiczny.role.RoleRepository;
import pl.gabinet.gabinetstomatologiczny.surgery.Surgery;
import pl.gabinet.gabinetstomatologiczny.surgery.SurgeryService;
import pl.gabinet.gabinetstomatologiczny.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SurgeryService surgeryService;
    private final NotificationService notificationService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       SurgeryService surgeryService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.surgeryService = surgeryService;
        this.notificationService = notificationService;
    }

    public void saveUser(UserDto userDto) {
        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .balance(0.0)
                .build();

        Role role = roleRepository.findByRoleType(userDto.getRoleType());
        if (role == null) {
            role = createRoleIfDoesNotExist(userDto.getRoleType());
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isUserDoctor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("User with email " + email + " not found"));
        List<Role> roles = user.getRoles();
        return roles.stream().anyMatch(role -> role.getRoleType().equals(RoleType.ROLE_DOCTOR));
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
        double addedBalance = user.addBalanceAndGet(value);
        notificationService.notify(String.format(Message.ADD_BALANCE.getMessage(), value), user);
        return addedBalance;
    }

    @Transactional
    public synchronized double pay(String email, List<Surgery> surgeries) throws IllegalArgumentException, IllegalStateException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
        return user.payAndGet(surgeryService.getPriceForSurgeries(surgeries));
    }

    private Role createRoleIfDoesNotExist(RoleType roleType) {
        Role role = new Role();
        role.setRoleType(roleType);
        return roleRepository.save(role);
    }
}
