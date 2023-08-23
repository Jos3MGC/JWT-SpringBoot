package com.jose.backend.usersapp.backendusersapp.auth.filters.dto.mapper;

import com.jose.backend.usersapp.backendusersapp.auth.filters.dto.UserDto;
import com.jose.backend.usersapp.backendusersapp.models.entities.User;

public class DtoMapperUser {
    
    private User user;

    // Constructor privado
    private DtoMapperUser() {
    }

    // Constuctor estatico que regresa un constructor privado
    public static DtoMapperUser builder() {
        return new DtoMapperUser();
    }

    public DtoMapperUser setUser(User user) {
        this.user = user;
        return this;
    }

    public UserDto build() {
        if (user == null) {
            throw new RuntimeException("Debe pasar el entity user");
        }
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
        return new UserDto(this.user.getId(), user.getUsername(), user.getEmail(), isAdmin);
    }
}
