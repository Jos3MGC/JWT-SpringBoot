package com.jose.backend.usersapp.backendusersapp.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jose.backend.usersapp.backendusersapp.models.entities.User;

/* Las interfaces se pueden heredar unas a otras */
/* ORM mapeo relacional de objetos */
public interface UserRepository extends CrudRepository<User, Long> {

    // Primera forma de consulta
    Optional<User> findByUsername(String username);
    // Segunda forma consulta personalizada
    /* @Query("select u from User u where u.username=?1")
    Optional<User> getUserByUsername(String username); */
}
