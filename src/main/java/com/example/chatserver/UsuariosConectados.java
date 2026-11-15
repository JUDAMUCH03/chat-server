package com.example.chatserver;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class UsuariosConectados {

    public record UsuarioEnLinea(String nombre, String agente) {}

    private final Map<String, UsuarioEnLinea> sesiones = new ConcurrentHashMap<>();

    public void agregar(String sessionId, String nombre, String agente) {
        if (sessionId != null && nombre != null) {
            String agenteValido = (agente == null || agente.isBlank()) ? "Jett" : agente;
            sesiones.put(sessionId, new UsuarioEnLinea(nombre, agenteValido));
        }
    }

    public void quitar(String sessionId) {
        if (sessionId != null) {
            sesiones.remove(sessionId);
        }
    }

    public List<UsuarioEnLinea> listar() {
        return sesiones.values().stream()
                .sorted(Comparator.comparing(UsuarioEnLinea::nombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
