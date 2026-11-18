package br.edu.uniesp.salgateria.produto.entity;

import br.edu.uniesp.salgateria.shared.exception.RegraNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "produtos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private boolean ativo = true;

    public ProdutoEntity(String nome, String descricao, BigDecimal preco) {
        exigirNome(nome);
        exigirPrecoValido(preco);
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.ativo = true;
    }

    public void alterarNome(String nome) {
        exigirNome(nome);
        this.nome = nome;
    }

    public void alterarDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void atualizarPreco(BigDecimal novoPreco) {
        exigirPrecoValido(novoPreco);
        this.preco = novoPreco;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void inativar() {
        this.ativo = false;
    }

    private static void exigirNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome do produto é obrigatório.");
        }
    }

    private static void exigirPrecoValido(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Preço do produto deve ser maior que zero.");
        }
    }
}
