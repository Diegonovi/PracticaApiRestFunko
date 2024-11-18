package com.example.users.dto;

import com.example.users.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutputUser {
    private Long id;
    private String name;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Boolean isDeleted = false;
    private List<Role> roles;
}
