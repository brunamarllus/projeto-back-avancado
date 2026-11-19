package br.edu.uniesp.salgateria.pagamento.entity;

import br.edu.uniesp.salgateria.pagamento.FormaPagamento;
import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.shared.exception.RegraNegocioException;
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
        if (pedido == null) {
            throw new RegraNegocioException("O pedido do pagamento é obrigatório.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor do pagamento deve ser maior que zero.");
        }
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new RegraNegocioException("O valor do pagamento deve ter no máximo duas casas decimais.");
        }
        if (forma == null) {
            throw new RegraNegocioException("A forma de pagamento é obrigatória.");
        }
        if (responsavel == null || responsavel.isBlank()) {
            throw new RegraNegocioException("O responsável pelo pagamento é obrigatório.");
        }
        if (responsavel.length() > 150) {
            throw new RegraNegocioException("O responsável pelo pagamento deve ter no máximo 150 caracteres.");
        }

        this.pedido = pedido;
        this.valor = valor;
        this.forma = forma;
        this.responsavel = responsavel;
        this.dataPagamento = LocalDateTime.now();
    }
}
