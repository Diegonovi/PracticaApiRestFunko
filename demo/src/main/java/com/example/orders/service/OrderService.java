package com.example.orders.service;


import com.example.orders.dto.input.InputOrder;
import com.example.orders.models.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.List;

interface OrderService {
    List<Order> findAll();
    Order findById(ObjectId id);
    Order save(InputOrder order);
    Order update(ObjectId id, InputOrder updatedOrder);
    Order deleteById(ObjectId id);

}
