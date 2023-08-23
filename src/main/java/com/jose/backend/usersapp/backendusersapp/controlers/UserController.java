package com.jose.backend.usersapp.backendusersapp.controlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jose.backend.usersapp.backendusersapp.auth.filters.dto.UserDto;
import com.jose.backend.usersapp.backendusersapp.models.entities.User;
import com.jose.backend.usersapp.backendusersapp.models.request.UserRequest;
import com.jose.backend.usersapp.backendusersapp.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<UserDto> list() {
        return service.findAll();
    }

    // Se debe llamar igual el path variable
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<UserDto> userOptional = service.findById(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    /* Va invocar este mapping cuando sea llamado el mapeo principal */
    /*
     * @valid - valida el usuario BindingResult result va a lado del cual se valida
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody UserRequest user, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validation(result);
        }
        // Verificamos si existe en la base de datos
        Optional<UserDto> o = service.update(user, id);
        if (o.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(o.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        // Verificamos si existe en la base de datos
        Optional<UserDto> o = service.findById(id);
        if (o.isPresent()) {
            service.remove(id);
            return ResponseEntity.noContent().build(); // status 204
        }
        return ResponseEntity.notFound().build();
    }

    /* Metodo utilitario para mostrar lista de errores en caso de que no adquiera los datos del post o put de la validación de user.java*/
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        /* Manejo de errores */
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
