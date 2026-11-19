package br.edu.uniesp.salgateria.pedido;

import br.edu.uniesp.salgateria.cliente.entity.ClienteEntity;
import br.edu.uniesp.salgateria.pagamento.FormaPagamento;
import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.pedido.entity.StatusPedido;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.shared.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoEntityTest {

    private PedidoEntity pedido;

    @BeforeEach
    void setUp() {
        ClienteEntity cliente = new ClienteEntity("Maria", "12345678901", "83999999999", null, null);
        ProdutoEntity coxinha = new ProdutoEntity("Coxinha (cento)", null, new BigDecimal("100.00"));
        pedido = new PedidoEntity(cliente, LocalDate.now().plusDays(3), null, "Atendente");
        pedido.adicionarItem(coxinha, 2);
    }

    private static BigDecimal v(String valor) {
        return new BigDecimal(valor);
    }

    @Test
    void pedidoNovoAguardaPagamentoESemCodigoDeRastreio() {
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertThat(pedido.getCodigoRastreio()).isNull();
        assertThat(pedido.valorTotal()).isEqualByComparingTo("200.00");
        assertThat(pedido.valorEntradaMinima()).isEqualByComparingTo("100.00");
    }

    @Test
    void pagamentoAbaixoDoMinimoNaoConfirmaPedido() {
        pedido.registrarPagamento(v("99.99"), FormaPagamento.PIX, "Atendente");

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertThat(pedido.getCodigoRastreio()).isNull();
    }

    @Test
    void pagamentoDeCinquentaPorCentoConfirmaPedidoEGeraCodigo() {
        pedido.registrarPagamento(v("100.00"), FormaPagamento.PIX, "Atendente");

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.EM_ANDAMENTO);
        assertThat(pedido.getCodigoRastreio()).startsWith("PED-");
        assertThat(pedido.saldoRestante()).isEqualByComparingTo("100.00");
    }

    @Test
    void pagamentoAntecipadoTotalTambemConfirmaPedido() {
        pedido.registrarPagamento(v("200.00"), FormaPagamento.PIX, "Atendente");

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.EM_ANDAMENTO);
        assertThat(pedido.saldoRestante()).isEqualByComparingTo("0.00");
    }

    @Test
    void pagamentosParciaisSomadosAtingemOMinimo() {
        pedido.registrarPagamento(v("60.00"), FormaPagamento.DINHEIRO, "Atendente");
        pedido.registrarPagamento(v("40.00"), FormaPagamento.DINHEIRO, "Atendente");

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.EM_ANDAMENTO);
    }

    @Test
    void naoAceitaPagamentoAcimaDoSaldo() {
        assertThatThrownBy(() -> pedido.registrarPagamento(v("200.01"), FormaPagamento.PIX, "Atendente"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void naoConcluiSemQuitarOSaldo() {
        pedido.registrarPagamento(v("100.00"), FormaPagamento.PIX, "Atendente");

        assertThatThrownBy(() -> pedido.concluir("Entregador"))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("saldo restante");
    }

    @Test
    void concluiAposQuitarOSaldoNaEntrega() {
        pedido.registrarPagamento(v("100.00"), FormaPagamento.PIX, "Atendente");
        pedido.registrarPagamento(v("100.00"), FormaPagamento.DINHEIRO, "Entregador");
        pedido.concluir("Entregador");

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(pedido.getDataConclusao()).isNotNull();
    }

    @Test
    void naoConcluiPedidoQueNaoFoiConfirmado() {
        assertThatThrownBy(() -> pedido.concluir("Entregador")).isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void itensSoSaoAlteradosAntesDaConfirmacao() {
        pedido.registrarPagamento(v("100.00"), FormaPagamento.PIX, "Atendente");

        assertThatThrownBy(() -> pedido.adicionarItem(new ProdutoEntity("Kibe", null, v("50.00")), 1))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void naoPermiteCancelarPedidoConcluido() {
        pedido.registrarPagamento(v("200.00"), FormaPagamento.PIX, "Atendente");
        pedido.concluir("Entregador");

        assertThatThrownBy(() -> pedido.cancelar("Atendente", "desistência"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void cancelamentoRegistraHistoricoENaoAceitaNovosPagamentos() {
        pedido.cancelar("Atendente", "cliente desistiu");

        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
        assertThat(pedido.getHistorico()).hasSize(2);
        assertThatThrownBy(() -> pedido.registrarPagamento(v("10.00"), FormaPagamento.PIX, "Atendente"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void naoAbrePedidoParaClienteInativo() {
        ClienteEntity inativo = new ClienteEntity("João", "10987654321", "83988888888", null, null);
        inativo.inativar();

        assertThatThrownBy(() -> new PedidoEntity(inativo, LocalDate.now().plusDays(1), null, "Atendente"))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void historicoRegistraAberturaEConfirmacao() {
        pedido.registrarPagamento(v("100.00"), FormaPagamento.PIX, "Atendente");

        assertThat(pedido.getHistorico()).hasSize(2);
    }
}
