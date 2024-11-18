package com.example.orders.models;

import com.example.users.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@TypeAlias("Order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("orders")
public class Order {
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();
    @NotNull(message = "El id del cliente no puede ser nulo")
    private Long userId;
    @NotNull(message = "El pedido debe tener al menos una línea de pedido")
    private List<OrderLine> orderLines;
    @Builder.Default()
    private Integer totalItems = 0;
    @Builder.Default()
    private Double total = 0.0;
    @Builder.Default()
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default()
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Builder.Default()
    private Boolean isDeleted = false;
    @NotNull
    private Address address;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }
}
