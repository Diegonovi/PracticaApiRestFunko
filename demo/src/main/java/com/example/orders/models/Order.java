package com.example.orders.models;

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
// Nombre de la colección en MongoDB
@Document("orders")
// Para que sepa con qué clase recuperarlo al traerlo con MongoDB y aplicar polimorfismo
public class Order {
    // Id de mongo
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();
    @NotNull(message = "El id del usuario no puede ser nulo")
    private Long idUsuario;
    @NotNull(message = "El id del cliente no puede ser nulo")
    private User user;
    @NotNull(message = "El pedido debe tener al menos una línea de pedido")
    private List<OrderLine> orderLines;
    @Builder.Default()
    private Integer totalItems = 0;
    @Builder.Default()
    private Double total = 0.0;
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    @Builder.Default()
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default()
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    private LocalDateTime updatedAt = LocalDateTime.now();
    // No hace falta pasarlo, lo calculamos, pero si lo pasamos lo usamos
    @Builder.Default()
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }

    public void setLineasPedido(List<OrderLine> lineasPedido) {
        this.orderLine = lineasPedido;
        this.totalItems = lineasPedido != null ? lineasPedido.size() : 0;
        this.total = lineasPedido != null ? lineasPedido.stream().mapToDouble(OrderLine::getTotal).sum() : 0.0;
    }
}
