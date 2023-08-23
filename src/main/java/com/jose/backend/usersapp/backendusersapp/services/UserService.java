package com.jose.backend.usersapp.backendusersapp.services;

import java.util.List;
import java.util.Optional;

import com.jose.backend.usersapp.backendusersapp.auth.filters.dto.UserDto;
import com.jose.backend.usersapp.backendusersapp.models.entities.User;
import com.jose.backend.usersapp.backendusersapp.models.request.UserRequest;

public interface UserService {

    // Ordena
    List<UserDto> findAll();
    //Buscar
    Optional<UserDto> findById(Long id);
    // Insertar y/o actualizar el user
    UserDto save(User user);
    Optional<UserDto> update(UserRequest user, Long id);
    // Delete
    void remove(Long id);
}
