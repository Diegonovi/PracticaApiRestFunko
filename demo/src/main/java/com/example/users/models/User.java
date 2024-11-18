package com.example.users.models;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "nombre no puede estar vacío")
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @NotBlank(message = "apellidos no puede estar vacío")
    private String lastName;
    @Column(unique = true, nullable = false)
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Length(min = 5, message = "La contraseña debe tener al menos 5 caracteres")
    @Column(nullable = false)
    private String password;
    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Builder.Default
    private Boolean isDeleted = false;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}