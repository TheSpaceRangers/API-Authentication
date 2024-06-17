package fr.bio.apiauthentication.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private long idUser;

    private String email;

    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_nam")
    private String lastName;

    private boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "id_user_fk", referencedColumnName = "id_user"
            ), inverseJoinColumns = @JoinColumn(
                    name = "id_role_fk", referencedColumnName = "id_role"
            )
    )
    private Collection<Role> roles;
}