package com.jose.backend.usersapp.backendusersapp.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jose.backend.usersapp.backendusersapp.models.entities.Role;

/* Las interfaces se pueden heredar unas a otras */
/* ORM mapeo relacional de objetos */
public interface RolRepository extends CrudRepository<Role, Long> {

    // Primera forma de consulta
    Optional<Role> findByname(String name);
}
