package com.example.chatserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "http://localhost:5173")
public class ArchivoController {

    private static final Logger log = LoggerFactory.getLogger(ArchivoController.class);
    private final Path carpeta = Path.of("uploads").toAbsolutePath();

    @PostMapping
    public Map<String, String> subir(@RequestParam("archivo") MultipartFile archivo) throws IOException {
        Files.createDirectories(carpeta);
        String ext = StringUtils.getFilenameExtension(archivo.getOriginalFilename());
        String nombre = UUID.randomUUID() + (ext == null ? "" : "." + ext);
        Files.copy(archivo.getInputStream(), carpeta.resolve(nombre));
        log.info("Archivo subido {} ({} bytes)", nombre, archivo.getSize());
        return Map.of("url", "/api/archivos/" + nombre);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<Resource> ver(@PathVariable String nombre) throws IOException {
        Path ruta = carpeta.resolve(nombre).normalize();
        if (!ruta.startsWith(carpeta) || !Files.exists(ruta)) return ResponseEntity.notFound().build();
        MediaType tipo = MediaTypeFactory.getMediaType(nombre).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(tipo).body(new UrlResource(ruta.toUri()));
    }
}
