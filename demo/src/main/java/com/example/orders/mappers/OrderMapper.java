package com.example.orders.mappers;

import com.example.orders.dto.input.InputOrder;
import com.example.orders.dto.output.OutputOrder;
import com.example.orders.models.Order;

public class OrderMapper {
    // Constructor privado para evitar la instancia de la clase
    private OrderMapper(){}

    public static Order toOrder(
        InputOrder input,
        Double total,
        Integer totalItems
    ){
        Order order = new Order();
        order.setUserId(input.getUserId());
        order.setAddress(input.getAddress());
        order.setOrderLines(input.getOrderLines());
        order.setTotal(total);
        order.setTotalItems(totalItems);
        return order;
    }

    public static OutputOrder toOutputOrder(Order order) {
        // Agregando la fecha de creación y modificación
        return OutputOrder.builder()
               .id(order.getId())
               .userId(order.getUserId())
               .orderLines(order.getOrderLines())
               .totalItems(order.getTotalItems())
               .total(order.getTotal())
               .createdAt(order.getCreatedAt())
               .updatedAt(order.getUpdatedAt())
               .isDeleted(order.getIsDeleted())
               .build();
    }
}
