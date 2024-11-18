package com.example.users.dto;

import com.example.users.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpUser {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String lastName;
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;
    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email no puede estar vacío")
    private String email;
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Length(min = 5, message = "La contraseña debe tener al menos 5 caracteres")
    private String password;
    private String repeatedPassword;
    private List<Role> roles = List.of(Role.USER);
}
