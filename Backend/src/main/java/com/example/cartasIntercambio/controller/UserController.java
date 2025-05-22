package com.example.cartasIntercambio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cartasIntercambio.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/user")
public class UserController {

    // Suponemos que esta función busca al usuario en la base de datos
    private boolean buscarUsuario(String username, String password) {
        return "user".equals(username) && "password".equals(password);
    }

    // Genera el JWT y lo guarda en una cookie
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        if (buscarUsuario(username, password)) {
            String token = JwtUtil.generateToken(username);

            // Crear y configurar la cookie con el JWT
            /*Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);  
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);  // 1 hora

            response.addCookie(cookie);*/

            // hago esta bosta para poder configurar lo de SameSite, que la clase Cookie no lo permite nativamente, gracias java
            response.setHeader("Set-Cookie", "jwt=" + token + "; HttpOnly; Secure; Path=/; Max-Age=3600; SameSite=None");

            return new ResponseEntity<>("Login exitoso", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }
    }

    // Verifica si el usuario está autenticado mediante la cookie
    @GetMapping("/check")
    public ResponseEntity<String> checkLogin(@CookieValue(name = "jwt", required = false) String token) {
        if (token != null && JwtUtil.isValidToken(token, JwtUtil.extractUsername(token))) {
            // A futuro hay que cambiarlo que retorne los datos básicos del usuario, es decir, confirma que está logueado y el
            // server te dice amablemente datos básicos tuyos para que el front lo maneje
            return new ResponseEntity<>("Estás logueado como " + JwtUtil.extractUsername(token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No estás logueado", HttpStatus.UNAUTHORIZED);
        }
    }
}
