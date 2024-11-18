package com.example.orders.dto.input;

import com.example.orders.models.Address;
import com.example.orders.models.OrderLine;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InputOrder {
    @NotNull(message = "El pedido debe tener al menos una línea de pedido")
    private List<OrderLine> orderLines;
    @NotNull(message = "El nombre de usuario no puede ser nulo")
    private Long userId;
    @NotNull
    private Address address;
}