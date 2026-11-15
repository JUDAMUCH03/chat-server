package com.example.chatserver;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class PresenciaListener {

    private static final Logger log = LoggerFactory.getLogger(PresenciaListener.class);
    private final SimpMessagingTemplate broker;
    private final UsuariosConectados usuarios;

    public PresenciaListener(SimpMessagingTemplate broker, UsuariosConectados usuarios) {
        this.broker = broker;
        this.usuarios = usuarios;
    }

    @EventListener
    public void alDesconectar(SessionDisconnectEvent evento) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(evento.getMessage());
        Map<String, Object> sesion = accessor.getSessionAttributes();
        String usuario = sesion == null ? null : (String) sesion.get("usuario");
        if (usuario == null) return;
        usuarios.quitar(accessor.getSessionId());
        broker.convertAndSend("/topic/usuarios", usuarios.listar());
        log.info("Salió {}", usuario);
        broker.convertAndSend("/topic/sala",
                new ChatMessage("SALIDA", usuario, usuario + " salió del chat", null).conFecha());
    }
}
