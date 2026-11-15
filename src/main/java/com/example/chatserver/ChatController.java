package com.example.chatserver;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate broker;
    private final UsuariosConectados usuarios;
    private final ChatMetrics metrics;

    public ChatController(SimpMessagingTemplate broker, UsuariosConectados usuarios, ChatMetrics metrics) {
        this.broker = broker;
        this.usuarios = usuarios;
        this.metrics = metrics;
    }

    @MessageMapping("/chat.unirse")
    @SendTo("/topic/sala")
    public ChatMessage unirse(ChatMessage mensaje, SimpMessageHeaderAccessor headers) {
        headers.getSessionAttributes().put("usuario", mensaje.autor());
        usuarios.agregar(headers.getSessionId(), mensaje.autor(), mensaje.contenido());
        broker.convertAndSend("/topic/usuarios", usuarios.listar());
        metrics.entra();
        log.info("Se unió {}", mensaje.autor());
        return new ChatMessage("UNION", mensaje.autor(), mensaje.autor() + " entró al chat", null).conFecha();
    }

    @MessageMapping("/chat.enviar")
    @SendTo("/topic/sala")
    public ChatMessage enviar(ChatMessage mensaje) {
        metrics.mensaje(mensaje.tipo());
        log.info("Mensaje {} de {}", mensaje.tipo(), mensaje.autor());
        return mensaje.conFecha();
    }

    @GetMapping("/api/usuarios")
    @CrossOrigin(origins = "http://localhost:5173")
    @ResponseBody
    public List<UsuariosConectados.UsuarioEnLinea> listarUsuarios() {
        return usuarios.listar();
    }
}
