package br.edu.uniesp.salgateria.cliente.dto;

import br.edu.uniesp.salgateria.cliente.entity.EnderecoEmbeddable;

public record ClienteRequest(String nome, String cpf, String telefone, String email, EnderecoEmbeddable endereco) {
}
