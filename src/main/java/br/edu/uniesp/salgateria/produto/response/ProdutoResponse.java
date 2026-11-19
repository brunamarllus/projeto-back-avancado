
package br.edu.uniesp.salgateria.produto.response;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        boolean ativo
) {
}
