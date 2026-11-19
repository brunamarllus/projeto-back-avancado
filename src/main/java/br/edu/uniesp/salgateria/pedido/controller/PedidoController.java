package br.edu.uniesp.salgateria.pedido.controller;

import br.edu.uniesp.salgateria.pedido.dto.PedidoRequest;
import br.edu.uniesp.salgateria.pedido.dto.PedidoResponse;
import br.edu.uniesp.salgateria.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> criarPedido(@RequestBody PedidoRequest pedidoRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.criarPedido(pedidoRequest));
    }
}