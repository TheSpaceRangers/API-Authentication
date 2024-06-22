package fr.bio.apiauthentication.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login_history")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_login_history")
    private long idLoginHistory;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "date_login")
    private LocalDateTime dateLogin;
}
