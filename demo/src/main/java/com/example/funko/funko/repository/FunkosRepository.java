package com.example.funko.funko.repository;

import com.example.funko.funko.model.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FunkosRepository extends JpaRepository<Funko,Long>, JpaSpecificationExecutor<Funko> {

    List<Funko> findByName(String name);

}
