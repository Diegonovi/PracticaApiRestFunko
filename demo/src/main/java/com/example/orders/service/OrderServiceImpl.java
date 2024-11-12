package com.example.orders.service;

import com.example.funko.funko.exceptions.FunkoNotFoundException;
import com.example.funko.funko.model.Funko;
import com.example.funko.funko.repository.FunkosRepository;
import com.example.orders.exceptions.OrderNotFoundException;
import com.example.orders.models.Order;
import com.example.orders.models.OrderLine;
import com.example.orders.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Cacheable("orders")
public class OrderServiceImpl implements OrderService{

    private final OrderRepository repository;
    private final FunkosRepository funkosRepository;

    @Autowired
    public OrderServiceImpl(
            OrderRepository repository,
            FunkosRepository funkosRepository
    ){
        this.repository = repository;
        this.funkosRepository = funkosRepository;
    }

    @Override
    public List<Order> findAll() {
        log.info("Buscando todos los pedidos");
        return repository.findAll();
    }

    @Override
    public Order findById(ObjectId id) {
        log.info("Buscando el pedido con id: " + id);
        return repository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("El pedido con id: " + id + " no existe")
        );
    }

    @Override
    public Order save(Order order) {
        log.info("Guardando el pedido: " + order);
        return repository.save(order);
    }

    @Override
    public Order update(ObjectId id, Order updatedOrder) {
        log.info("Actualizando el pedido con id: " + id);
        Order order = findById(id);
        // Asegurando que las líneas son válidas
        for (OrderLine line : order.getOrderLines()){
            Optional<Funko> funko = funkosRepository.findById(line.getIdProducto());
            if (funko.isEmpty()) throw new FunkoNotFoundException("El funko con id: " + line.getIdProducto() + " no existe");
            else line.set(funko.get());
        }
        return repository.save(order);
    }

    @Override
    public void deleteById(ObjectId id) {

    }
}
