package com.example.cartasIntercambio.service;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CookieServiceTest {

    private CookieService cookieService;
    private HttpServletResponse response;

    @BeforeEach
    void init() {
        cookieService = new CookieService();
        response = mock(HttpServletResponse.class);
    }

    // --------------------------------------- TESTS --------------------------------------- //

    @Test
    void testAddHttpCookie() {
        String name = "jwt";
        String value = "testdetoken";
        int maxAge = 3600;

        cookieService.addHttpCookie(name, value, maxAge, response);

        // Verifica que se haya agregado una cookie
        verify(response).addCookie(argThat(cookie ->
                cookie.getName().equals(name) &&
                        cookie.getValue().equals(value) &&
                        cookie.isHttpOnly() &&
                        cookie.getSecure() &&
                        cookie.getPath().equals("/") &&
                        cookie.getMaxAge() == maxAge
        ));
    }

    @Test
    void testDeleteHttpCookie() {
        String name = "jwt";

        cookieService.deleteHttpCookie(name, response);

        // Verifica que se haya agregado una cookie con maxAge 0
        verify(response).addCookie(argThat(cookie ->
                cookie.getName().equals(name) &&
                        cookie.getValue() == null &&
                        cookie.isHttpOnly() &&
                        cookie.getSecure() &&
                        cookie.getPath().equals("/") &&
                        cookie.getMaxAge() == 0
        ));
    }
}