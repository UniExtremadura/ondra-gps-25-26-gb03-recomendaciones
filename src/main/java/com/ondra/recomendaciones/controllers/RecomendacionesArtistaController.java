package com.ondra.recomendaciones.controllers;

import com.ondra.recomendaciones.dto.RecomendacionesResponseDTO;
import com.ondra.recomendaciones.exceptions.ForbiddenAccessException;
import com.ondra.recomendaciones.services.RecomendacionesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para recomendaciones personalizadas de artistas.
 *
 * <p>
 * Genera recomendaciones de canciones y álbumes basadas en las preferencias
 * musicales del artista, excluyendo su propio contenido.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/artistas/recomendaciones")
public class RecomendacionesArtistaController {

    @Autowired
    private RecomendacionesService recomendacionesService;

    /**
     * Obtiene recomendaciones personalizadas para el artista autenticado.
     * El artistId se extrae del token JWT, disponible solo para usuarios tipo
     * ARTISTA.
     *
     * @param tipo    tipo de contenido a recomendar: "cancion", "album" o "ambos"
     * @param limite  número máximo de recomendaciones (1-50)
     * @param request contexto de la petición HTTP con datos de autenticación
     * @return respuesta con las recomendaciones generadas
     * @throws ForbiddenAccessException si el token no contiene artistId
     */
    @GetMapping
    public ResponseEntity<RecomendacionesResponseDTO> obtenerRecomendacionesArtista(
            @RequestParam(defaultValue = "ambos") String tipo,
            @RequestParam(defaultValue = "20") int limite,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long artistId = (Long) request.getAttribute("artistId");
        boolean isServiceRequest = Boolean.TRUE.equals(request.getAttribute("isServiceRequest"));

        log.info("📨 GET /artistas/recomendaciones - userId: {}, artistId: {}, tipo: {}, limite: {}",
                userId, artistId, tipo, limite);

        if (!isServiceRequest && artistId == null) {
            log.warn("⚠️ Acceso denegado: token sin artistId");
            throw new ForbiddenAccessException("Este endpoint solo es accesible para usuarios tipo ARTISTA");
        }

        RecomendacionesResponseDTO recomendaciones = recomendacionesService.obtenerRecomendaciones(userId, artistId,
                tipo, limite);

        return ResponseEntity.ok(recomendaciones);
    }
}