package com.example.funko.funko.model;

import com.example.funko.category.model.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "funkos")
@NoArgsConstructor
@AllArgsConstructor
public class Funko {
    public static final Long DEFAULT_ID = 0L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funko_id")
    private Long id = DEFAULT_ID;
    @Column(name = "nombre", nullable = false)
    private String name;
    @Column(name = "precio", nullable = false)
    private Double price;
    @Column(name = "stock", nullable = false)
    private Integer stock;
    @Column(name = "fecha_lanzamiento", nullable = false)
    private LocalDate releaseDate;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @CreatedBy
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedBy
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
