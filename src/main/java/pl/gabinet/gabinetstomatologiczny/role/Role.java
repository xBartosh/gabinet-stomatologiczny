package pl.gabinet.gabinetstomatologiczny.role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.gabinet.gabinetstomatologiczny.user.User;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="roles")
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @ManyToMany(mappedBy="roles")
    private List<User> users;
}
