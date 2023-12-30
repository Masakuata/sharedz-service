package xatal.sharedz.controllers;

import io.jsonwebtoken.Claims;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xatal.sharedz.entities.Venta;
import xatal.sharedz.security.TokenUtils;
import xatal.sharedz.services.VentaService;
import xatal.sharedz.structures.PublicVenta;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/venta")
public class VentaController {
    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity getVentas(
            @RequestHeader("Token") String token,
            @RequestParam(name = "cliente", required = false) Optional<String> clienteNombre,
            @RequestParam(name = "fecha", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Optional<Date> fecha
    ) {
        Claims claims = TokenUtils.getTokenClaims(token);
        if (claims == null || TokenUtils.isExpired(claims)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<Venta> ventas = this.ventaService.searchVentas(
                clienteNombre.orElse(null),
                fecha.orElse(null));
        if (ventas.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ventas);
    }

    @PostMapping
    public ResponseEntity newVenta(
            @RequestHeader("Token") String token,
            @RequestBody PublicVenta venta
    ) {
        Claims claims = TokenUtils.getTokenClaims(token);
        if (claims == null || TokenUtils.isExpired(claims)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Venta savedVenta = this.ventaService.newVenta(venta);
        if (savedVenta == null) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(new PublicVenta(savedVenta), HttpStatus.CREATED);
    }

//    @GetMapping("/{id_venta}")
//    public ResponseEntity getVenta(
//        @RequestHeader("")
//    ) {
//
//    }
}
