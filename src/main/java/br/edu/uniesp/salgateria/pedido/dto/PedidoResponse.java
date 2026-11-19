package br.edu.uniesp.salgateria.pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoResponse(
        Long id,
        Long clienteId,
        Long produtoId,
        Integer quantidade,
        BigDecimal precoTotal,
        String status,
        String codigoRastreio,
        LocalDate dataEntregaPrevista,
        LocalDateTime dataPedido
) {
}