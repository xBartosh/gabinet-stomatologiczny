package pl.gabinet.gabinetstomatologiczny.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.gabinet.gabinetstomatologiczny.role.RoleType;
import pl.gabinet.gabinetstomatologiczny.user.User;
import pl.gabinet.gabinetstomatologiczny.user.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User API")
public class UserAPI {
    private final UserService userService;

    public UserAPI(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/doctors")
    @Operation(summary = "Get all doctors")
    public ResponseEntity<List<User>> findDoctors() {
        return ResponseEntity.ok()
                .body(userService.findUsersByRoleName(RoleType.ROLE_DOCTOR.name()));
    }

    @GetMapping("/balance")
    @Operation(summary = "Get balance for logged in user")
    public ResponseEntity<?> getBalance(Principal principal) {
        String email = principal.getName();
        try {
            return ResponseEntity.ok(userService.getBalance(email));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Add balance to account")
    @PostMapping("/add-balance")
    public ResponseEntity<?> addBalance(@RequestParam double value,
                                        Principal principal,
                                        SecurityContextHolderAwareRequestWrapper requestWrapper) {
        String email = principal.getName();
        boolean isPatient = requestWrapper.isUserInRole(RoleType.ROLE_PATIENT.name());
        if (isPatient) {
            try {
                return ResponseEntity.ok(userService.addBalance(email, value));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied, only patients can add balance.");
        }
    }
}
