package com.sid.pokegiciel.repository;

import com.sid.pokegiciel.model.Caracter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CaracterRepository extends JpaRepository<Caracter, Long> {
    List<Caracter> findAllByUser_Username(String username);
    Caracter findById(Long id);
}
