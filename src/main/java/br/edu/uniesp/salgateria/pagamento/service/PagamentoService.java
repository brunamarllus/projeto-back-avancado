package br.edu.uniesp.salgateria.pagamento.service;

import br.edu.uniesp.salgateria.pagamento.dto.CriarPagamentoRequest;
import br.edu.uniesp.salgateria.pagamento.dto.CriarPagamentoResponse;
import br.edu.uniesp.salgateria.pagamento.entity.PagamentoEntity;
import br.edu.uniesp.salgateria.pagamento.mapper.PagamentoMapper;
import br.edu.uniesp.salgateria.pagamento.repository.PagamentoRepository;
import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import br.edu.uniesp.salgateria.pedido.repository.PedidoRepository;
import br.edu.uniesp.salgateria.shared.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoMapper pagamentoMapper;

    @Transactional
    public CriarPagamentoResponse criarPagamento(CriarPagamentoRequest request) {
        if (request == null || request.pedidoId() == null) {
            throw new IllegalArgumentException("Os dados e o identificador do pedido são obrigatórios.");
        }

        PedidoEntity pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", request.pedidoId()));

        PagamentoEntity pagamento = pedido.registrarPagamento(
                request.valor(),
                request.formaPagamento(),
                request.responsavel()
        );
        pagamentoRepository.save(pagamento);

        return pagamentoMapper.paraResponse(pagamento);
    }
}