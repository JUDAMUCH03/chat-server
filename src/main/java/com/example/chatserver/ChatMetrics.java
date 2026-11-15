package com.example.chatserver;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class ChatMetrics {

    private final MeterRegistry registry;
    private final AtomicInteger conectados;

    public ChatMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.conectados = registry.gauge("chat.usuarios.conectados", new AtomicInteger(0));
    }

    public void mensaje(String tipo) {
        registry.counter("chat.mensajes", "tipo", tipo).increment();
    }

    public void entra() { conectados.incrementAndGet(); }

    public void sale()  { conectados.decrementAndGet(); }
}
