package com.jose.backend.usersapp.backendusersapp.auth.filters;

import static com.jose.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jose.backend.usersapp.backendusersapp.models.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /* Intentar hacer la autenticacion (CONTROLADOR) */
    /* Request obtiene los datos del cuerpo del request */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        User user = null;
        String username = null;
        String password = null;

        try {
            /*
             * Leemos los datos del cuerpo con getInputStream, readvalue toma los datos y
             * los pobla en User class
             */
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();

            /*
             * Por efectos de curso para mostrar en consola
             * logger.info("Username desde request InputStream (raw)" + username);
             * logger.info("Password desde request InputStream (raw)" + password);
             */

            /* Manejo de excepciones */
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Se autentica el usuario y regresa un token */
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        /* Se hace un cast de getPrincipal al user */
        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
                .getUsername();
        /* Obtener los roles de la BD */
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        // Buscar si es admin
        boolean isAdmin = roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        /* Guardamos los datos en claims para mandar en el token */
        Claims claims = Jwts.claims();
        claims.put("authorities", new ObjectMapper().writeValueAsString(roles));   
        claims.put("isAdmin", isAdmin);     
        claims.put("username", username);    
        /*
         * Codificamos con la llave secreta junto con la vida util de 1 hora y pasamos
         * al payload el username
         */
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .signWith(SECRET_KEY)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)).compact();
        /* Se pasa a la cabecera el token */
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);
        /* Se genera un JSON */
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s, has iniciado sesion con exito!", username));
        body.put("Username", username);
        /* Se guarda en el cuerpo de la respuesta le JSON */
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Error en la autenticación username o password no validos!");
        body.put("error", failed.getMessage());
        /* Regresa un JSON */
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }

}
