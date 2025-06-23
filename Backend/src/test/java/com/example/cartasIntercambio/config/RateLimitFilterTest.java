package com.example.cartasIntercambio.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RateLimitFilterTest {
    private RateLimitFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private StringWriter responseWriter;

    @BeforeEach
    void init() throws Exception {
        filter = new RateLimitFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // ----------------------------------------- TESTS ----------------------------------------- //

    @Test
    void testRequestWithinLimit_passesThrough() throws Exception {
        // Simulamos una sola request que deberia pasar
        filter.doFilter(request, response, chain);

        // Verifica que el filtro no fue bloqueado
        verify(chain, times(1)).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        verify(response, never()).setStatus(429);
    }

    @Test
    void testRequestOverLimit_getsBlocked() throws Exception {
        // Simulamos mas de 50 requests para exceder el límite
        for (int i = 0; i < 55; i++) {
            filter.doFilter(request, response, chain);
        }

        // En las ultimas deberia ser bloqueado
        verify(response, atLeastOnce()).setStatus(429);
        assertTrue(responseWriter.toString().contains("bloqueada"));
    }

    @Test
    void testNonApiRoute_isIgnored() throws Exception {
        when(request.getRequestURI()).thenReturn("/");

        filter.doFilter(request, response, chain);

        // Verifica que no fue bloqueado
        verify(chain, times(1)).doFilter(any(), any());
        verify(response, never()).setStatus(anyInt());
    }

}
