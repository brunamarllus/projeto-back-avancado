
package br.edu.uniesp.salgateria.pedido.entity;

import br.edu.uniesp.salgateria.cliente.entity.Cliente;
import br.edu.uniesp.salgateria.pagamento.entity.Pagamento;
import br.edu.uniesp.salgateria.pagamento.entity.FormaPagamento;
import br.edu.uniesp.salgateria.shared.exception.RegraNegocioException;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    private String codigoRastreio;

    @OneToMany(mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Pagamento> pagamentos = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(Cliente cliente, BigDecimal valorTotal) {

        if (cliente == null) {
            throw new IllegalArgumentException("O cliente é obrigatório.");
        }

        if (valorTotal == null ||
                valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor total deve ser maior que zero.");
        }

        this.cliente = cliente;
        this.valorTotal = valorTotal;
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
    }

    public void registrarPagamento(
            BigDecimal valor,
            FormaPagamento formaPagamento) {

        if (status == StatusPedido.CANCELADO ||
                status == StatusPedido.CONCLUIDO) {
            throw new RegraNegocioException(
                    "Não é possível pagar um pedido cancelado ou concluído.");
        }

        if (valor == null ||
                valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(
                    "O pagamento deve ser maior que zero.");
        }

        BigDecimal totalPago = getTotalPago();
        BigDecimal novoTotal = totalPago.add(valor);

        if (novoTotal.compareTo(valorTotal) > 0) {
            throw new RegraNegocioException(
                    "O pagamento não pode ultrapassar o valor do pedido.");
        }

        Pagamento pagamento =
                new Pagamento(this, valor, formaPagamento);

        pagamentos.add(pagamento);

        BigDecimal minimo = valorTotal.multiply(
                new BigDecimal("0.50"));

        if (novoTotal.compareTo(minimo) >= 0 &&
                status == StatusPedido.AGUARDANDO_PAGAMENTO) {

            this.status = StatusPedido.EM_ANDAMENTO;
            this.codigoRastreio = UUID.randomUUID()
                    .toString().substring(0, 8).toUpperCase();
        }
    }

    public BigDecimal getTotalPago() {
        return pagamentos.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSaldoRestante() {
        return valorTotal.subtract(getTotalPago());
    }

    public void concluir() {

        if (status == StatusPedido.CANCELADO) {
            throw new RegraNegocioException(
                    "Pedido cancelado não pode ser concluído.");
        }

        if (getSaldoRestante().compareTo(BigDecimal.ZERO) > 0) {
            throw new RegraNegocioException(
                    "O pedido só pode ser concluído após a quitação.");
        }

        if (status != StatusPedido.EM_ANDAMENTO) {
            throw new RegraNegocioException(
                    "Somente pedidos em andamento podem ser concluídos.");
        }

        this.status = StatusPedido.CONCLUIDO;
    }

    public void cancelar() {

        if (status == StatusPedido.CONCLUIDO) {
            throw new RegraNegocioException(
                    "Pedido concluído não pode ser cancelado.");
        }

        if (status == StatusPedido.CANCELADO) {
            throw new RegraNegocioException(
                    "Pedido já está cancelado.");
        }

        this.status = StatusPedido.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public String getCodigoRastreio() {
        return codigoRastreio;
    }

    public List<Pagamento> getPagamentos() {
        return List.copyOf(pagamentos);
    }
}
