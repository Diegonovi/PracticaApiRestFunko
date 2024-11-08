package com.example.funko.funko.repository;

import com.example.funko.funko.model.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface FunkosRepository extends JpaRepository<Funko,Long> {

    List<Funko> findByName(String name);

}
