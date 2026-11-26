package br.edu.uniesp.salgateria.cliente.controller;

import br.edu.uniesp.salgateria.cliente.dto.ClienteRequest;
import br.edu.uniesp.salgateria.cliente.dto.ClienteResponse;
import br.edu.uniesp.salgateria.cliente.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/clientes")
@RestController
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteServices;

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrarCliente(@RequestBody ClienteRequest clienteRequest) {
        ClienteResponse clienteResponse = clienteServices.cadastrarCliente(clienteRequest);
        return ResponseEntity.ok(clienteResponse);
    }
}
