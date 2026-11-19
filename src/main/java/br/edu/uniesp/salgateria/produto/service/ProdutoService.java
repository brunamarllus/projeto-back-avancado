
package br.edu.uniesp.salgateria.produto.service;

import br.edu.uniesp.salgateria.produto.dto.CriarProdutoRequest;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.produto.mapper.ProdutoMapper;
import br.edu.uniesp.salgateria.produto.repository.ProdutoRepository;
import br.edu.uniesp.salgateria.produto.response.ProdutoResponse;
import br.edu.uniesp.salgateria.shared.exception.RecursoNaoEncontradoException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Transactional
    public ProdutoResponse criarProduto(CriarProdutoRequest request) {
        ProdutoEntity produto = ProdutoMapper.converteParaEntidade(request);
        produtoRepository.save(produto);
        return ProdutoMapper.converterParaResposta(produto);
    }

}
