package fr.bio.apiauthentication.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private long idRole;

    @Column(name = "authority", unique = true, nullable = false)
    private String authority;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDate modifiedAt;

    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;

    @Builder.Default
    private boolean enabled = true;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
}