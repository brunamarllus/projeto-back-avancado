package br.edu.uniesp.salgateria.pagamento.entity;

import br.edu.uniesp.salgateria.cliente.entity.ClienteEntity;
import br.edu.uniesp.salgateria.pagamento.FormaPagamento;
import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.shared.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PagamentoEntityTest {

    private PedidoEntity pedido;

    @BeforeEach
    void setUp() {
        ClienteEntity cliente = new ClienteEntity("Maria", "12345678901", "83999999999", null, null);
        pedido = new PedidoEntity(cliente, LocalDate.now().plusDays(1), null, "Atendente");
        pedido.adicionarItem(new ProdutoEntity("Coxinha", null, new BigDecimal("10.00")), 1);
    }

    @Test
    void exigeFormaDePagamento() {
        assertThatThrownBy(() -> new PagamentoEntity(
                pedido, new BigDecimal("5.00"), null, "Atendente"
        )).isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("forma de pagamento");
    }

    @Test
    void exigeResponsavel() {
        assertThatThrownBy(() -> new PagamentoEntity(
                pedido, new BigDecimal("5.00"), FormaPagamento.PIX, " "
        )).isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("responsável");
    }

    @Test
    void rejeitaValorComMaisDeDuasCasasDecimais() {
        assertThatThrownBy(() -> new PagamentoEntity(
                pedido, new BigDecimal("5.001"), FormaPagamento.PIX, "Atendente"
        )).isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("duas casas decimais");
    }
}
