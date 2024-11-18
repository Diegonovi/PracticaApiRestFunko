package com.example.orders.service;

import com.example.funko.funko.mapper.FunkoMapper;
import com.example.funko.funko.model.Funko;
import com.example.funko.funko.services.FunkoService;
import com.example.orders.dto.input.InputOrder;
import com.example.orders.exceptions.InvalidOrderException;
import com.example.orders.exceptions.OrderNotFoundException;
import com.example.orders.mappers.OrderMapper;
import com.example.orders.models.Order;
import com.example.orders.models.OrderLine;
import com.example.orders.repository.OrderRepository;
import com.example.users.exceptions.UserDoesntExistException;
import com.example.users.models.User;
import com.example.users.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"orders"})
public class OrderServiceImpl implements OrderService{

    private final OrderRepository repository;
    private final FunkoService funkoService;
    private final UserService userService;

    @Autowired
    public OrderServiceImpl(
            OrderRepository repository,
            FunkoService funkoService,
            UserService userService
    ){
        this.repository = repository;
        this.funkoService = funkoService;
        this.userService = userService;
    }

    @Override
    public List<Order> findAll() {
        log.info("Buscando todos los pedidos");
        return repository.findAll();
    }

    @Override
    @Cacheable
    public Order findById(ObjectId id) {
        log.info("Buscando el pedido con id: " + id);
        return repository.findById(id).orElseThrow(
                () -> new OrderNotFoundException("El pedido con id: " + id + " no existe")
        );
    }

    @Override
    @CachePut(key = "#result.id")
    public Order save(InputOrder order) {
        log.info("Guardando el pedido: " + order);
        User user = userService.getUserById(order.getUserId()); // El usuario e el contexto de seguridad de Spring Boot
        Order newOrder = setUpOrder(order, user);
        return repository.save(newOrder);
    }

    private Order setUpOrder(InputOrder order, User user) {
        Double total = 0.0;
        Integer numItems = 0;
        // Valída que los Funkos estén disponibles para el pedido
        for (OrderLine line : order.getOrderLines()){
            Funko funko = funkoService.findById(line.getFunkoId()); // Tirará una excepción si no lo encuentra
            if (funko.getStock() < line.getAmount()) throw new InvalidOrderException("No hay suficientes funkos");
            funko.setStock(funko.getStock() - line.getAmount());
            funkoService.update(funko.getId(), FunkoMapper.toInputFunko(funko));
            total += line.getAmount() * funko.getPrice();
            numItems += line.getAmount();
        }
        return OrderMapper.toOrder(order, total, numItems);
    }

    @Override
    @CachePut(key = "#result.id")
    public Order update(ObjectId id, InputOrder updatedOrder) {
        log.info("Actualizando el pedido con id: " + id);
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            Order existingOrder = order.get();

            return repository.save(existingOrder);
        }else throw new OrderNotFoundException("El pedido con id: " + id + " no existe existe");
    }

    @Override
    @CacheEvict(key = "#id")
    public Order deleteById(ObjectId id) {
        log.info("Eliminando el pedido con id: " + id);
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            repository.deleteById(id);
        }else throw new OrderNotFoundException("El pedido con id: " + id + " no existe existe");
        Order deletedOrder = order.get();
        deletedOrder.setIsDeleted(true);
        return deletedOrder;
    }
}
