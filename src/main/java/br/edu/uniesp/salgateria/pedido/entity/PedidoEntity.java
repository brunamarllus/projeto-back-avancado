package br.edu.uniesp.salgateria.pedido.entity;

import br.edu.uniesp.salgateria.cliente.entity.ClienteEntity;
import br.edu.uniesp.salgateria.pagamento.entity.FormaPagamento;
import br.edu.uniesp.salgateria.pagamento.entity.PagamentoEntity;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.shared.exception.RegraNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "pedidos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PedidoEntity {

    public static final BigDecimal PERCENTUAL_MINIMO_ENTRADA = new BigDecimal("0.50");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_rastreio", unique = true, length = 30)
    private String codigoRastreio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;

    @Column(name = "data_pedido", nullable = false, updatable = false)
    private LocalDateTime dataPedido;

    @Column(name = "data_entrega_prevista", nullable = false)
    private LocalDate dataEntregaPrevista;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(length = 500)
    private String observacao;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedidoEntity> itens = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagamentoEntity> pagamentos = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoStatusEntity> historico = new ArrayList<>();

    public PedidoEntity(ClienteEntity cliente, LocalDate dataEntregaPrevista, String observacao, String responsavel) {
        if (cliente == null || !cliente.isAtivo()) {
            throw new RegraNegocioException("Não é permitido abrir pedido para um cliente inativo.");
        }
        exigirDataEntregaValida(dataEntregaPrevista);
        this.cliente = cliente;
        this.dataEntregaPrevista = dataEntregaPrevista;
        this.observacao = observacao;
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.dataPedido = LocalDateTime.now();
        registrarHistorico(null, StatusPedido.AGUARDANDO_PAGAMENTO, responsavel, "Pedido registrado.");
    }

    public List<ItemPedidoEntity> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public List<PagamentoEntity> getPagamentos() {
        return Collections.unmodifiableList(pagamentos);
    }

    public List<HistoricoStatusEntity> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public void adicionarItem(ProdutoEntity produto, int quantidade) {
        exigirAguardandoPagamento("Itens só podem ser alterados enquanto o pedido aguarda pagamento.");
        if (produto == null || !produto.isAtivo()) {
            throw new RegraNegocioException("Somente produtos ativos podem ser adicionados ao pedido.");
        }
        if (quantidade <= 0) {
            throw new RegraNegocioException("A quantidade do item deve ser maior que zero.");
        }
        for (ItemPedidoEntity existente : itens) {
            if (existente.getProduto().getId() != null && existente.getProduto().getId().equals(produto.getId())) {
                existente.aumentarQuantidade(quantidade);
                return;
            }
        }
        itens.add(new ItemPedidoEntity(this, produto, quantidade));
    }

    public void removerItem(ItemPedidoEntity item) {
        exigirAguardandoPagamento("Itens só podem ser alterados enquanto o pedido aguarda pagamento.");
        if (!itens.remove(item)) {
            throw new RegraNegocioException("O item informado não pertence a este pedido.");
        }
    }

    public void alterarDataEntrega(LocalDate novaData) {
        exigirEmAberto("Não é possível alterar a data de entrega de um pedido concluído ou cancelado.");
        exigirDataEntregaValida(novaData);
        this.dataEntregaPrevista = novaData;
    }

    public void alterarObservacao(String observacao) {
        exigirEmAberto("Não é possível alterar um pedido concluído ou cancelado.");
        this.observacao = observacao;
    }

    public PagamentoEntity registrarPagamento(BigDecimal valor, FormaPagamento forma, String responsavel) {
        if (status != StatusPedido.AGUARDANDO_PAGAMENTO && status != StatusPedido.EM_ANDAMENTO) {
            throw new RegraNegocioException("Não é possível registrar pagamento em pedido concluído ou cancelado.");
        }
        if (itens.isEmpty()) {
            throw new RegraNegocioException("Não é possível registrar pagamento em um pedido sem itens.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor do pagamento deve ser maior que zero.");
        }
        if (valor.compareTo(saldoRestante()) > 0) {
            throw new RegraNegocioException("O pagamento (" + valor + ") excede o saldo restante do pedido (" + saldoRestante() + ").");
        }

        PagamentoEntity pagamento = new PagamentoEntity(this, valor, forma, responsavel);
        pagamentos.add(pagamento);

        if (status == StatusPedido.AGUARDANDO_PAGAMENTO && entradaMinimaPaga()) {
            this.codigoRastreio = gerarCodigoRastreio();
            alterarStatus(StatusPedido.EM_ANDAMENTO, responsavel,
                    "Pagamento mínimo recebido. Pedido confirmado. Código de rastreio: " + codigoRastreio + ".");
        }
        return pagamento;
    }

    public BigDecimal valorTotal() {
        return itens.stream()
                .map(ItemPedidoEntity::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal valorPago() {
        return pagamentos.stream()
                .map(PagamentoEntity::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal saldoRestante() {
        return valorTotal().subtract(valorPago());
    }

    public BigDecimal valorEntradaMinima() {
        return valorTotal().multiply(PERCENTUAL_MINIMO_ENTRADA).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean entradaMinimaPaga() {
        return !itens.isEmpty() && valorPago().compareTo(valorEntradaMinima()) >= 0;
    }

    public void concluir(String responsavel) {
        if (status != StatusPedido.EM_ANDAMENTO) {
            throw new RegraNegocioException("Somente um pedido em andamento pode ser concluído.");
        }
        if (saldoRestante().compareTo(BigDecimal.ZERO) > 0) {
            throw new RegraNegocioException("O pedido só pode ser concluído após a quitação do saldo restante (" + saldoRestante() + ").");
        }
        this.dataConclusao = LocalDateTime.now();
        alterarStatus(StatusPedido.CONCLUIDO, responsavel, "Pedido entregue e quitado.");
    }

    public void cancelar(String responsavel, String motivo) {
        if (status == StatusPedido.CONCLUIDO || status == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Não é possível cancelar um pedido concluído ou já cancelado.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new RegraNegocioException("O motivo do cancelamento é obrigatório.");
        }
        alterarStatus(StatusPedido.CANCELADO, responsavel, "Pedido cancelado: " + motivo);
    }

    private void alterarStatus(StatusPedido novoStatus, String responsavel, String observacao) {
        StatusPedido anterior = this.status;
        this.status = novoStatus;
        registrarHistorico(anterior, novoStatus, responsavel, observacao);
    }

    private void registrarHistorico(StatusPedido anterior, StatusPedido novo, String responsavel, String observacao) {
        historico.add(new HistoricoStatusEntity(this, anterior, novo, responsavel, observacao));
    }

    private void exigirAguardandoPagamento(String mensagem) {
        if (status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new RegraNegocioException(mensagem);
        }
    }

    private void exigirEmAberto(String mensagem) {
        if (status == StatusPedido.CONCLUIDO || status == StatusPedido.CANCELADO) {
            throw new RegraNegocioException(mensagem);
        }
    }

    private static void exigirDataEntregaValida(LocalDate data) {
        if (data == null || data.isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data de entrega deve ser informada e não pode estar no passado.");
        }
    }

    private static String gerarCodigoRastreio() {
        return "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
