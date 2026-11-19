package br.edu.uniesp.salgateria.pagamento.service;

import br.edu.uniesp.salgateria.cliente.entity.ClienteEntity;
import br.edu.uniesp.salgateria.pagamento.FormaPagamento;
import br.edu.uniesp.salgateria.pagamento.dto.CriarPagamentoRequest;
import br.edu.uniesp.salgateria.pagamento.dto.CriarPagamentoResponse;
import br.edu.uniesp.salgateria.pagamento.entity.PagamentoEntity;
import br.edu.uniesp.salgateria.pagamento.mapper.PagamentoMapper;
import br.edu.uniesp.salgateria.pagamento.repository.PagamentoRepository;
import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.pedido.entity.StatusPedido;
import br.edu.uniesp.salgateria.pedido.repository.PedidoRepository;
import br.edu.uniesp.salgateria.produto.entity.ProdutoEntity;
import br.edu.uniesp.salgateria.shared.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    private PagamentoService pagamentoService;
    private PedidoEntity pedido;

    @BeforeEach
    void setUp() {
        ClienteEntity cliente = new ClienteEntity("Maria", "12345678901", "83999999999", null, null);
        pedido = new PedidoEntity(cliente, LocalDate.now().plusDays(3), null, "Atendente");
        pedido.adicionarItem(new ProdutoEntity("Coxinha", null, new BigDecimal("100.00")), 2);
        pagamentoService = new PagamentoService(
                pagamentoRepository,
                pedidoRepository,
                new PagamentoMapper()
        );
    }

    @Test
    void registraPagamentoPeloPedidoESalvaResposta() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.save(any(PagamentoEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CriarPagamentoResponse response = pagamentoService.criarPagamento(
                new CriarPagamentoRequest(1L, new BigDecimal("100.00"), FormaPagamento.PIX, "Atendente")
        );

        assertThat(response.valor()).isEqualByComparingTo("100.00");
        assertThat(response.formaPagamento()).isEqualTo(FormaPagamento.PIX);
        assertThat(response.dataPagamento()).isNotNull();
        assertThat(pedido.getStatus()).isEqualTo(StatusPedido.EM_ANDAMENTO);
        verify(pagamentoRepository).save(any(PagamentoEntity.class));
    }

    @Test
    void pedidoInexistenteNaoRegistraPagamento() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagamentoService.criarPagamento(
                new CriarPagamentoRequest(99L, new BigDecimal("100.00"), FormaPagamento.PIX, "Atendente")
        )).isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
