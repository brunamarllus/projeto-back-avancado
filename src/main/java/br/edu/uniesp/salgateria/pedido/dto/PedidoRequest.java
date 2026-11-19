package br.edu.uniesp.salgateria.pedido.dto;

import java.time.LocalDate;

public record PedidoRequest(
        Long clienteId,
        Long produtoId,
        Integer quantidade,
        LocalDate dataEntregaPrevista,
        String observacao,
        String responsavel
) {
}