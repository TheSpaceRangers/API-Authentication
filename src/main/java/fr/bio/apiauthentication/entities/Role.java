package fr.bio.apiauthentication.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "users")
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private long idRole;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @Builder.Default
    private boolean enabled = true;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
}
