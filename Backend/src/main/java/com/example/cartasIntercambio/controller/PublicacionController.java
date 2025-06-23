package com.example.cartasIntercambio.controller;

import com.example.cartasIntercambio.dto.*;
import com.example.cartasIntercambio.jwt.JwtUtil;
import com.example.cartasIntercambio.model.Mercado.EstadoOferta;
import com.example.cartasIntercambio.model.Mercado.Oferta;
import com.example.cartasIntercambio.model.Mercado.Publicacion;
import com.example.cartasIntercambio.model.Usuario.Usuario;
import com.example.cartasIntercambio.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController{
    private final PublicacionServiceImpl publicacionService;
    private final OfertaServiceImpl ofertaService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    public PublicacionController(PublicacionServiceImpl publicacionService, OfertaServiceImpl ofertaService) {
        this.publicacionService = publicacionService;
        this.ofertaService = ofertaService;
    }

    // Buscar todas las publicaciones con o sin filtros
    @GetMapping
    public ResponseEntity<PublicacionResponse> listarPublicaciones(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String juego,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double preciomin,
            @RequestParam(required = false) Double preciomax,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize

    ) {
        return ResponseEntity.ok(
                publicacionService.buscarPublicacionesFiltradas(nombre, juego, estado, preciomin,
                        preciomax, pageNo, pageSize)
        );
    }

    @GetMapping("/{idPublicacion}")
    public ResponseEntity<PublicacionDto> buscarPublicacion(@PathVariable("idPublicacion") String idPublicacion) {
        PublicacionDto publicacionDto = publicacionService.buscarPublicacionDTOPorId(idPublicacion);
        return new ResponseEntity<>(publicacionDto, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<PublicacionDto> crearPublicacion(
            @RequestBody PublicacionDto publicacionDto,
            @CookieValue("jwt") String token) {
        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();
        publicacionDto.getPublicador().setId(userId);
        PublicacionDto guardada = publicacionService.guardarPublicacion(publicacionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // Crear una oferta para una publicacion
    @PostMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<?> crearOferta(
            @PathVariable("idPublicacion") String idPublicacion,
            @RequestBody OfertaDto ofertaDto,
            @CookieValue("jwt") String token) {
        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();
        ofertaDto.getOfertante().setId(userId);

        Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);

        if(userId.equals(publicacion.getPublicador().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: No podes ofertar sobre tu propia publicacion");
        }

        OfertaDto dtoGuardado = ofertaService.crearOferta(ofertaDto, publicacion);

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoGuardado);
    }

    //Buscar todas las ofertas de una publicacion
    @GetMapping("/{idPublicacion}/ofertas")
    public ResponseEntity<?> buscarOfertasDeUnaPublicacion(@PathVariable("idPublicacion") String idPublicacion,
                                                                         @CookieValue("jwt") String token) {
        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();

        //Comentar estas lineas si necesitas que un usuario cualquiera puede ver todas las ofertas
        PublicacionDto publicacionDto = publicacionService.buscarPublicacionDTOPorId(idPublicacion);
        if(!userId.equals(publicacionDto.getPublicador().getId())){

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Acceso denegado");
        }

        List<OfertaDto> ofertas = ofertaService.buscarOfertasPorPublicacion(idPublicacion);
        return ResponseEntity.ok(ofertas);
    }

    // Buscar una oferta de una publicacion
    @GetMapping("/ofertas/{idOferta}")
    public ResponseEntity<?> buscarOferta(@PathVariable("idOferta") String idOferta,
                                          @CookieValue("jwt") String token) {
        //Comentar si esta mal la logica
        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();
        OfertaDto oferta = ofertaService.buscarOfertaDto(idOferta);//Esta no
        PublicacionDto publicacionDto = publicacionService.buscarPublicacionDTOPorId(oferta.getIdPublicacion());
        //Si no sos el duenio de la oferta ni de la publicacion => denegado
        if(!publicacionDto.getPublicador().getId().equals(userId) && !oferta.getOfertante().getId().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Acceso denegado");
        }

        return new ResponseEntity<>(oferta, HttpStatus.OK);
    }


    // Aceptar o rechazar una oferta
    @PatchMapping(path = "/ofertas/{idOferta}", consumes = "application/json-patch+json")
    public ResponseEntity<OfertaDto> responderOferta(
            @PathVariable("idOferta") String idOferta,
            @RequestBody JsonPatch patch,
            @CookieValue("jwt") String token) {

        // 1. Validar token y obtener userId
        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();

        // 2. Traer la oferta y la publicación asociada
        Oferta oferta = ofertaService.buscarOfertaPorId(idOferta);
        Publicacion publicacion = publicacionService.buscarPublicacionPorId(oferta.getIdPublicacion());
        // 3. Chequear que el user actual sea el dueño de la publicación
        if (!publicacion.getPublicador().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 4. Proceso normal
        try {
            Oferta ofertaActualizada = patchOferta(patch, oferta);
            ofertaService.guardarOferta(ofertaActualizada);
            if(ofertaActualizada.getEstado().equals(EstadoOferta.ACEPTADO)){
                ofertaService.rechazarOtrasOfertas(idOferta, ofertaActualizada.getIdPublicacion());
                publicacionService.finalizarPublicacion(ofertaActualizada.getIdPublicacion(), publicacion.getVersion());
            }
            return ResponseEntity.ok(new OfertaDto(ofertaActualizada));
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Oferta patchOferta (JsonPatch patch, Oferta oferta) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(mapper.convertValue(oferta, JsonNode.class));
        return mapper.treeToValue(patched, Oferta.class);
    }

    // Mis publicaciones
    @GetMapping("/usuario/{idUsuario}") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<PublicacionResponse> listarPublicacionesPorUsuario(
            @PathVariable("idUsuario") String idUsuario,
            @CookieValue("jwt") String token,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) int pageSize) {

        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();

        if(!userId.equals(idUsuario)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(publicacionService.buscarPublicacionesPorUsuario(idUsuario, pageNo, pageSize));
    }

    // Mis ofertas recibidas
    @GetMapping("/usuario/{idUsuario}/ofertas/recibidas") // TODO: El ID del usuario no lo vamos a pasar por URL
    public ResponseEntity<List<OfertaDto>> listarOfertasRecibidas(@PathVariable("idUsuario") String idUsuario,
                                                                  @CookieValue("jwt") String token) {
        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();
        if(!userId.equals(idUsuario)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<PublicacionDto> publicacionesUsuario = publicacionService.buscarPublicacionesPorUsuario(idUsuario);
        List<OfertaDto> ofertasRecibidas = new ArrayList<>();
        for(PublicacionDto publicacion : publicacionesUsuario){
            ofertasRecibidas.addAll(ofertaService.buscarOfertasPorPublicacion(publicacion.getId()));
        }
        return ResponseEntity.ok(ofertasRecibidas);
    }

    // Mis ofertas realizadas
    @GetMapping("/usuario/{idUsuario}/ofertas/realizadas")
    public ResponseEntity<List<OfertaDto>> listarOfertasRealizadas(@PathVariable("idUsuario") String idUsuario,
                                                                   @CookieValue("jwt") String token) {

        Claims claims = jwtUtil.validateToken(token);
        String userId = claims.getSubject();
        if(!userId.equals(idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<OfertaDto> ofertasRealizadas = ofertaService.buscarOfertasRealizadas(idUsuario);
        return ResponseEntity.ok(ofertasRealizadas);
    }

    public Map<String, List<MultipartFile>> extraerImagenesPorCarta(HttpServletRequest request) {
        // Si, si mandás algo que no sea el formato cartaInteres[i], lo bocha básicamente
        Map<String, List<MultipartFile>> imagenesPorCarta = new HashMap<>();

        // Tengo entendido que el if valida si hay multiples archivos acá, si no saltea
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();
            
            for (Map.Entry<String, List<MultipartFile>> entry : multiFileMap.entrySet()) {
                String key = entry.getKey(); 
                // Solo procesamos las claves que empiecen con "cartaInteres[", el resto no lo apreciamos
                if (key.startsWith("cartaInteres[")) {
                    imagenesPorCarta
                        .computeIfAbsent(key, k -> new ArrayList<>())
                        .addAll(entry.getValue());
                }
            }
        }
        return imagenesPorCarta;
    }

    
    private List<String> getUrlsFromS3(List<MultipartFile> imgDeCTActual) {
        return imgDeCTActual.stream()
                            .map(img -> {
                                // un try-catch por si s3service falla, el ide me rompiá los huevos para agregarlo
                                try {
                                    return s3Service.uploadFile(img);
                                } catch (IOException e) {
                                    // si llegó acá, estamos jodidos. por suerte el poderoso front ya maneja cuando la imagen no tiene un url funcionando
                                    System.out.println("Error guardando archivo: " + e);
                                    e.printStackTrace(); // no tiene sentido porque esta función lo llama solo cuando hago un post de publicación, pero queda lindo
                                    throw new RuntimeException("No se pudo guardar la imagen: " + img.getOriginalFilename());
                                }
                            })
                            .toList();
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearPublicacionMultipart(
            @RequestPart("publicacion") PublicacionDto publicacionDto,
            @RequestPart(value = "publicacionImagenes", required = true) MultipartFile[] publicacionImagenes,
            @CookieValue("jwt") String token,
            HttpServletRequest restoDeInformacion // acá estará el "cartaInteres[i]" por cada carta de interés, en orden 
    ) {
        try {
            // --- USER DEL JWT ---
            Claims claims = jwtUtil.validateToken(token);
            String userId = claims.getSubject();

            // Buscar usuario real desde tu service
            UsuarioResponseDto usuarioDto = usuarioService.buscarUsuarioPorId(userId);
            // Conversión manual, o implementá un método .toModel() si tienes en UsuarioResponseDto
            Usuario usuario = new Usuario();
            usuario.setId(usuarioDto.getId());
            usuario.setUser(usuarioDto.getUser());
            usuario.setNombre(usuarioDto.getNombre());

            publicacionDto.setPublicador(usuario);

            // --- Imagenes carta principal ---
            List<String> urlsPrincipal = new ArrayList<>();
            if (publicacionImagenes != null) {
                for (MultipartFile img : publicacionImagenes) {
                    urlsPrincipal.add(s3Service.uploadFile(img));
                }
            }
            if (publicacionDto.getCartaOfrecida() != null) {
                publicacionDto.getCartaOfrecida().setImagenes(urlsPrincipal);
            }

            // --- Imagenes de cartas de interés (ahora si acepta N de una vez) ---
            Map<String, List<MultipartFile>> cartaInteresImagenes = extraerImagenesPorCarta(restoDeInformacion);
            List<CartaDto> cartasInteres = publicacionDto.getCartasInteres();
            int cantidadDeCTs = cartasInteres.size();

            // Itero por cada carta de interés
            System.out.println("Llegó " + cantidadDeCTs + " cartas de interés");
            for (int i = 0; i < cantidadDeCTs; i++) {
                // Obtengo las imágenes asociadas a "cartaInteres[i]"
                List<MultipartFile> imgDeCTActual = cartaInteresImagenes.get("cartaInteres[" + i + "]");
                
                System.out.println("La carta " + i + " tiene " + imgDeCTActual.size() + " fotos");
                
                // No puede haber cartas de interés sin imágenes, es ridículo
                if (imgDeCTActual == null || imgDeCTActual.size() == 0) {
                    return ResponseEntity.badRequest().body("La oferta #" + (i + 1) + " debe tener al menos una imagen.");
                }

                // Guardar imágenes de la oferta y asignar URLs
                CartaDto CTActual = cartasInteres.get(i);
                List<String> urls = getUrlsFromS3(imgDeCTActual);

                // Al final, guardo las imágenes en la carta de interés que estoy parado en la iteración
                CTActual.setImagenes(urls);
                System.out.println(CTActual.getImagenes());
            }

            // --- Guardar publicación en BD ---
            PublicacionDto guardada = publicacionService.guardarPublicacion(publicacionDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error subiendo publicación o imágenes: " + e.getMessage());
        }
    }

}