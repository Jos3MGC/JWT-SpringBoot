package com.jose.backend.usersapp.backendusersapp.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jose.backend.usersapp.backendusersapp.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busqueda del usuario por username
        Optional<com.jose.backend.usersapp.backendusersapp.models.entities.User> o = repository
                .findByUsername(username);

        if (!o.isPresent()) {
            throw new UsernameNotFoundException(String.format("Username no existe en el sistema!", username));
        }

        com.jose.backend.usersapp.backendusersapp.models.entities.User user = o.orElseThrow();
        // PASAR LOS ROLES DESDE LA BD
        List<GrantedAuthority> authorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());

        return new User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }

}
