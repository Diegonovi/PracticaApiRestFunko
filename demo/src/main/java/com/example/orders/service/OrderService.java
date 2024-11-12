package com.example.orders.service;


import com.example.orders.models.Order;
import org.bson.types.ObjectId;

import java.util.List;

interface OrderService {
    List<Order> findAll();
    Order findById(ObjectId id);
    Order save(Order order);
    Order update(ObjectId id, Order updatedOrder);
    void deleteById(ObjectId id);

}
