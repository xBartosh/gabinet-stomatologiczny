package pl.gabinet.gabinetstomatologiczny.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pl.gabinet.gabinetstomatologiczny.role.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private Double balance = 0.0;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();

    public User(Long id, String firstName, String lastName, String email, String password, Double balance, List<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.balance = 0.0;
        this.roles = roles;
    }

    public double addBalanceAndGet(double value) {
        balance += value;
        return balance;
    }

    public double payAndGet(double value) throws IllegalStateException {
        if (balance - value < 0){
            throw new IllegalStateException("Not enough balance to pay for the surgery!");
        }
        balance -= value;
        return balance;
    }
}
