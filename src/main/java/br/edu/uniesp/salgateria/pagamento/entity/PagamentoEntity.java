package br.edu.uniesp.salgateria.pagamento.entity;

import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "pagamentos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormaPagamento forma;

    @Column(name = "data_pagamento", nullable = false, updatable = false)
    private LocalDateTime dataPagamento;

    @Column(nullable = false, length = 150)
    private String responsavel;

    public PagamentoEntity(PedidoEntity pedido, BigDecimal valor, FormaPagamento forma, String responsavel) {
        this.pedido = pedido;
        this.valor = valor;
        this.forma = forma;
        this.responsavel = responsavel;
        this.dataPagamento = LocalDateTime.now();
    }
}
