
package br.edu.uniesp.salgateria.produto.dto;

import java.math.BigDecimal;

public record ProdutoDto(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        boolean ativo
) {
}
