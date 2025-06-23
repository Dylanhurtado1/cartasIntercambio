package com.example.cartasIntercambio.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter { // Bucket4j + Blacklist temporal

    // Bucket (limite de rate)
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Timestamp de blacklist (milisegundos hasta que puede volver)
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    // Config
    private final int MAX_REQUESTS_PER_MINUTE = 50;
    private final int BLACKLIST_DURATION_MINUTES = 2;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = request.getRemoteAddr();
        String path = req.getRequestURI();

        // Limitamos los endpoints del backend
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        // Verificamos si la IP está en la blacklist
        if (blacklist.containsKey(ip)) {
            long tiempoRestante = blacklist.get(ip) - System.currentTimeMillis();
            if (tiempoRestante > 0) {
                res.setStatus(429);
                res.getWriter().write("Tu IP esta bloqueada temporalmente. Espera " + (tiempoRestante / 1000) + " segundos.");
                return;
            } else {
                blacklist.remove(ip); // Se vencio la penalización
            }
        }

        // Obtenemos el bucket para la IP
        Bucket bucket = buckets.computeIfAbsent(ip, k -> crearBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response); // Puede continuar
        } else {
            // Como supero el limite lo blacklisteamos
            blacklist.put(ip, System.currentTimeMillis() + Duration.ofMinutes(BLACKLIST_DURATION_MINUTES).toMillis());
            res.setStatus(429);
            res.getWriter().write("Demasiadas peticiones. Tu IP ha sido bloqueada por " + BLACKLIST_DURATION_MINUTES + " minutos.");
        }
    }

    private Bucket crearBucket() {
        Refill refill = Refill.greedy(MAX_REQUESTS_PER_MINUTE, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(MAX_REQUESTS_PER_MINUTE, refill);
        return Bucket.builder().addLimit(limit).build();
    }

}
