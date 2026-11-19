package br.edu.uniesp.salgateria.produto.controller;

import br.edu.uniesp.salgateria.produto.dto.CriarProdutoRequest;
import br.edu.uniesp.salgateria.produto.response.ProdutoResponse;
import br.edu.uniesp.salgateria.produto.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<ProdutoResponse> criar (@RequestBody CriarProdutoRequest criarProdutoRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criarProduto(criarProdutoRequest));
    }
}
