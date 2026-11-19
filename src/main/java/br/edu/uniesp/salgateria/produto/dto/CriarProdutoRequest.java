
package br.edu.uniesp.salgateria.produto.dto;

import java.math.BigDecimal;

public record CriarProdutoRequest(
        String nome,
        String descricao,
        BigDecimal preco
) {
}
