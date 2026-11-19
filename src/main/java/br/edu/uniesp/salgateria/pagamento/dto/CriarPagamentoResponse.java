package br.edu.uniesp.salgateria.pagamento.dto;

import br.edu.uniesp.salgateria.pagamento.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CriarPagamentoResponse(
        Long id,
        Long pedidoId,
        BigDecimal valor,
        FormaPagamento formaPagamento,
        LocalDateTime dataPagamento,
        String responsavel
) {
}
