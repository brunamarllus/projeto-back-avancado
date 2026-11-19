package br.edu.uniesp.salgateria.pagamento.service;

import br.edu.uniesp.salgateria.pagamento.dto.CriarPagamentoRequest;
import br.edu.uniesp.salgateria.pagamento.dto.CriarPagamentoResponse;
import br.edu.uniesp.salgateria.pagamento.entity.PagamentoEntity;
import br.edu.uniesp.salgateria.pagamento.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    public CriarPagamentoResponse criarPagamento (CriarPagamentoRequest criarPagamentoRequest){
        PagamentoEntity pagamentoEntity = new PagamentoEntity(criarPagamentoRequest);
    }

}