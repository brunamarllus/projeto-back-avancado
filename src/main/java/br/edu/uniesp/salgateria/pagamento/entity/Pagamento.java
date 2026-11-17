package br.edu.uniesp.salgateria.pagamento.entity;

import br.edu.uniesp.salgateria.pedido.entity.Pedido;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    @Column(nullable = false)
    private LocalDateTime dataPagamento;

    protected Pagamento() {
    }

    public Pagamento(Pedido pedido, BigDecimal valor,
                     FormaPagamento formaPagamento) {

        if (pedido == null) {
            throw new IllegalArgumentException("O pedido é obrigatório.");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        if (formaPagamento == null) {
            throw new IllegalArgumentException("A forma de pagamento é obrigatória.");
        }

        this.pedido = pedido;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.dataPagamento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }
}
