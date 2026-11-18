package br.edu.uniesp.salgateria.pedido.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "historico_status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoricoStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusPedido statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "novo_status", nullable = false, length = 30)
    private StatusPedido novoStatus;

    @Column(name = "data_alteracao", nullable = false, updatable = false)
    private LocalDateTime dataAlteracao;

    @Column(nullable = false, length = 150)
    private String responsavel;

    @Column(length = 500)
    private String observacao;

    HistoricoStatusEntity(PedidoEntity pedido, StatusPedido statusAnterior, StatusPedido novoStatus,
                          String responsavel, String observacao) {
        this.pedido = pedido;
        this.statusAnterior = statusAnterior;
        this.novoStatus = novoStatus;
        this.responsavel = responsavel;
        this.observacao = observacao;
        this.dataAlteracao = LocalDateTime.now();
    }
}
