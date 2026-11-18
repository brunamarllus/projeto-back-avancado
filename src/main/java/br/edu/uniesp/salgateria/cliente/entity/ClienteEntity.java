package br.edu.uniesp.salgateria.cliente.entity;

import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.shared.exception.RegraNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Table(name = "clientes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(length = 150)
    private String email;

    @Embedded
    private EnderecoEmbeddable endereco;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "cliente")
    private List<PedidoEntity> pedidos = new ArrayList<>();

    public ClienteEntity(String nome, String cpf, String telefone, String email, EnderecoEmbeddable endereco) {
        exigirTexto(nome, "Nome do cliente é obrigatório.");
        exigirTexto(cpf, "CPF do cliente é obrigatório.");
        exigirTexto(telefone, "Telefone do cliente é obrigatório.");
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public List<PedidoEntity> getPedidos() {
        return Collections.unmodifiableList(pedidos);
    }

    public void alterarNome(String nome) {
        exigirTexto(nome, "Nome do cliente é obrigatório.");
        this.nome = nome;
    }

    public void alterarTelefone(String telefone) {
        exigirTexto(telefone, "Telefone do cliente é obrigatório.");
        this.telefone = telefone;
    }

    public void alterarEmail(String email) {
        this.email = email;
    }

    public void alterarEndereco(EnderecoEmbeddable endereco) {
        this.endereco = endereco;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void inativar() {
        this.ativo = false;
    }

    private static void exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException(mensagem);
        }
    }
}
