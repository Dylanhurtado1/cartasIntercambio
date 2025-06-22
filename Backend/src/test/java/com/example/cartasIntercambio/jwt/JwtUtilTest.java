package com.example.cartasIntercambio.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void init() {
        jwtUtil = new JwtUtil();

        // Simulamos la clave desde application.properties
        String secret = "estaEsUnaClaveMuySecretaYSegura123456789012";
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        jwtUtil.init(); // fuerza el @PostConstruct
    }

    // --------------------------------------- TESTS --------------------------------------- //

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("1", "usuario");

        assertNotNull(token);

        Claims claims = jwtUtil.validateToken(token);

        assertEquals("1", claims.getSubject());
        assertEquals("usuario", claims.get("username"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testValidateToken_tokenInvalido_lanzaExcepcion() {
        String tokenInvalido = "estoNoEsUnTokenValido";

        assertThrows(Exception.class, () -> jwtUtil.validateToken(tokenInvalido));
    }

}
