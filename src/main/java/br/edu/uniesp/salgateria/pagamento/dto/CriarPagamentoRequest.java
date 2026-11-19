package br.edu.uniesp.salgateria.pagamento.dto;

import br.edu.uniesp.salgateria.pagamento.FormaPagamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarPagamentoRequest(
        @NotNull @Positive Long pedidoId,
        @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal valor,
        @NotNull FormaPagamento formaPagamento,
        @NotBlank @Size(max = 150) String responsavel
) {
}
