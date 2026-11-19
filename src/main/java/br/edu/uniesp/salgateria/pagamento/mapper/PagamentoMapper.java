package br.edu.uniesp.salgateria.pagamento.mapper;

import br.edu.uniesp.salgateria.pagamento.dto.CriarPagamentoResponse;
import br.edu.uniesp.salgateria.pagamento.entity.PagamentoEntity;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {

    public CriarPagamentoResponse paraResponse(PagamentoEntity entity) {
        return new CriarPagamentoResponse(
                entity.getId(),
                entity.getPedido().getId(),
                entity.getValor(),
                entity.getForma(),
                entity.getDataPagamento(),
                entity.getResponsavel()
        );
    }
}
