package br.edu.uniesp.salgateria.pagamento.response;

import br.edu.uniesp.salgateria.pagamento.FormaPagamento;
import java.math.BigDecimal;

public record PagamentoResponse(
        Long id,
        Long pedidoId,
        BigDecimal valor,
        FormaPagamento formaPagamento,
        Boolean ativo
) {
}