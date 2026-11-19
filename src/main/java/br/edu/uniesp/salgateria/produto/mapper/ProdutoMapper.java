
package br.edu.uniesp.salgateria.produto.mapper;

import br.edu.uniesp.salgateria.produto.dto.CriarProdutoRequest;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.produto.response.ProdutoResponse;

public class ProdutoMapper {

    private ProdutoMapper() {
    }

    public static ProdutoEntity converteParaEntidade(CriarProdutoRequest request) {
        return new ProdutoEntity(
                request.nome(),
                request.descricao(),
                request.preco()
        );
    }

    public static ProdutoResponse converterParaResposta(ProdutoEntity produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.isAtivo()
        );
    }
}
