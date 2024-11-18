package com.example.funko.category.model;

import com.example.funko.funko.model.Funko;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    private UUID id = UUID.randomUUID();
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "createdAt", column = @Column(name = "created")),
            @AttributeOverride( name = "updatedAt", column = @Column(name = "last_updated")),
    })
    @NotBlank(message = "La categoría debe tener una descripción")
    private Description description;
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Funko> funkos = Collections.emptyList();
    @CreatedBy
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedBy
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
