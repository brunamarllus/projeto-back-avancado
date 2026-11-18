# Diagrama de classes

Entidades do domínio, com atributos, métodos modificadores de estado, relacionamentos e multiplicidades.
O diagrama está em [Mermaid](https://mermaid.js.org/) e é renderizado automaticamente pelo GitHub.
Os getters (gerados com Lombok) foram omitidos para manter o diagrama legível.

```mermaid
classDiagram
    direction LR

    class ClienteEntity {
        -Long id
        -String nome
        -String cpf
        -String telefone
        -String email
        -EnderecoEmbeddable endereco
        -boolean ativo
        -LocalDateTime dataCadastro
        +alterarNome(String)
        +alterarTelefone(String)
        +alterarEmail(String)
        +alterarEndereco(EnderecoEmbeddable)
        +ativar()
        +inativar()
    }

    class EnderecoEmbeddable {
        <<Embeddable>>
        -String logradouro
        -String numero
        -String complemento
        -String bairro
        -String cidade
        -String uf
        -String cep
    }

    class ProdutoEntity {
        -Long id
        -String nome
        -String descricao
        -BigDecimal preco
        -boolean ativo
        +alterarNome(String)
        +alterarDescricao(String)
        +atualizarPreco(BigDecimal)
        +ativar()
        +inativar()
    }

    class PedidoEntity {
        -Long id
        -String codigoRastreio
        -StatusPedido status
        -LocalDateTime dataPedido
        -LocalDate dataEntregaPrevista
        -LocalDateTime dataConclusao
        -String observacao
        +adicionarItem(ProdutoEntity, int)
        +removerItem(ItemPedidoEntity)
        +alterarDataEntrega(LocalDate)
        +alterarObservacao(String)
        +registrarPagamento(BigDecimal, FormaPagamento, String) PagamentoEntity
        +concluir(String)
        +cancelar(String, String)
        +valorTotal() BigDecimal
        +valorPago() BigDecimal
        +saldoRestante() BigDecimal
        +valorEntradaMinima() BigDecimal
        +entradaMinimaPaga() boolean
    }

    class ItemPedidoEntity {
        -Long id
        -Integer quantidade
        -BigDecimal precoUnitario
        +subtotal() BigDecimal
    }

    class PagamentoEntity {
        -Long id
        -BigDecimal valor
        -FormaPagamento forma
        -LocalDateTime dataPagamento
        -String responsavel
    }

    class HistoricoStatusEntity {
        -Long id
        -StatusPedido statusAnterior
        -StatusPedido novoStatus
        -LocalDateTime dataAlteracao
        -String responsavel
        -String observacao
    }

    class StatusPedido {
        <<enumeration>>
        AGUARDANDO_PAGAMENTO
        EM_ANDAMENTO
        CONCLUIDO
        CANCELADO
    }

    class FormaPagamento {
        <<enumeration>>
        PIX
        DINHEIRO
        CARTAO_DEBITO
        CARTAO_CREDITO
    }

    ClienteEntity "1" *-- "0..1" EnderecoEmbeddable : endereco
    ClienteEntity "1" --> "0..*" PedidoEntity : pedidos
    PedidoEntity "1" *-- "1..*" ItemPedidoEntity : itens
    PedidoEntity "1" *-- "0..*" PagamentoEntity : pagamentos
    PedidoEntity "1" *-- "1..*" HistoricoStatusEntity : historico
    ItemPedidoEntity "0..*" --> "1" ProdutoEntity : produto
    PedidoEntity ..> StatusPedido
    HistoricoStatusEntity ..> StatusPedido
    PagamentoEntity ..> FormaPagamento
```

## Observações

- **Pedido × Produto (N—N):** resolvido por `ItemPedidoEntity`, que guarda a quantidade e o preço unitário no momento da venda.
- **Pagamento:** um pedido pode ter vários pagamentos (entrada de 50%, quitação na entrega, ou um único pagamento antecipado de 100%). O pedido só passa a `EM_ANDAMENTO` (e recebe o `codigoRastreio`) quando o total pago atinge 50% do valor total, e só pode ser `CONCLUIDO` com o saldo restante igual a zero.
- **Cardinalidade `1..*` em itens:** o pedido só aceita pagamento se tiver ao menos um item. O banco não impõe isso, é regra do método `registrarPagamento`.
- **Responsável:** as ações de pagamento e de mudança de status recebem o nome do funcionário (texto), já que o sistema é de uso interno e não tem cadastro de usuários nesta versão.
