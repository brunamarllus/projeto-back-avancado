package br.edu.uniesp.salgateria.pedido;

import br.edu.uniesp.salgateria.cliente.entity.ClienteEntity;
import br.edu.uniesp.salgateria.cliente.entity.EnderecoEmbeddable;
import br.edu.uniesp.salgateria.cliente.repository.ClienteRepository;
import br.edu.uniesp.salgateria.pagamento.FormaPagamento;
import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.pedido.entity.StatusPedido;
import br.edu.uniesp.salgateria.pedido.repository.PedidoRepository;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.produto.repository.ProdutoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
class PedidoPersistenciaIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired ClienteRepository clienteRepository;
    @Autowired ProdutoRepository produtoRepository;
    @Autowired PedidoRepository pedidoRepository;
    @Autowired EntityManager em;

    @Test
    void persisteEReleOPedidoComItensPagamentosEHistorico() {
        ClienteEntity cliente = clienteRepository.save(new ClienteEntity("Maria", "12345678901", "83999999999",
                "maria@email.com", new EnderecoEmbeddable("Rua A", "10", null, "Centro", "Campina Grande", "PB", "58400000")));
        ProdutoEntity coxinha = produtoRepository.save(new ProdutoEntity("Coxinha (cento)", null, new BigDecimal("100.00")));
        ProdutoEntity kibe = produtoRepository.save(new ProdutoEntity("Kibe (cento)", null, new BigDecimal("80.00")));

        PedidoEntity pedido = new PedidoEntity(cliente, LocalDate.now().plusDays(3), "Sem pimenta", "Atendente");
        pedido.adicionarItem(coxinha, 2);
        pedido.adicionarItem(kibe, 1);
        pedido.adicionarItem(coxinha, 1);
        pedido.registrarPagamento(new BigDecimal("190.00"), FormaPagamento.PIX, "Atendente");
        pedidoRepository.saveAndFlush(pedido);

        em.clear();
        PedidoEntity lido = pedidoRepository.findById(pedido.getId()).orElseThrow();

        assertThat(lido.getStatus()).isEqualTo(StatusPedido.EM_ANDAMENTO);
        assertThat(lido.getCodigoRastreio()).startsWith("PED-");
        assertThat(pedidoRepository.findByCodigoRastreio(lido.getCodigoRastreio())).isPresent();
        assertThat(lido.getItens()).hasSize(2);
        assertThat(lido.valorTotal()).isEqualByComparingTo("380.00");
        assertThat(lido.getPagamentos()).hasSize(1);
        assertThat(lido.saldoRestante()).isEqualByComparingTo("190.00");
        assertThat(lido.getHistorico()).hasSize(2);
        assertThat(lido.getCliente().getEndereco().getCidade()).isEqualTo("Campina Grande");

        lido.registrarPagamento(new BigDecimal("190.00"), FormaPagamento.DINHEIRO, "Entregador");
        lido.concluir("Entregador");
        pedidoRepository.saveAndFlush(lido);
        em.clear();

        PedidoEntity concluido = pedidoRepository.findById(pedido.getId()).orElseThrow();
        assertThat(concluido.getStatus()).isEqualTo(StatusPedido.CONCLUIDO);
        assertThat(concluido.getPagamentos()).hasSize(2);
        assertThat(clienteRepository.findById(cliente.getId()).orElseThrow().getPedidos()).hasSize(1);
    }
}
