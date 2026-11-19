package br.edu.uniesp.salgateria.pagamento.mapper;

import br.edu.uniesp.salgateria.pagamento.entity.PagamentoEntity;
import br.edu.uniesp.salgateria.pagamento.response.PagamentoResponse;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMepper {

    public PagamentoEntity paraEntidade(CriarPagamentoRequest request) {
        if (request == null) {
            return null;
        }
        PagamentoEntity entity = new PagamentoEntity();
        // Ajuste os setters conforme os campos existentes em PagamentoEntity
        entity.setPedidoId(request.pedidoId());
        entity.setValor(request.valor());
        entity.setFormaPagamento(request.formaPagamento());
        entity.setAtivo(true);
        return entity;
    }

    public PagamentoResponse paraResponse(PagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new PagamentoResponse(
                entity.getId(),
                entity.getPedidoId(),
                entity.getValor(),
                entity.getFormaPagamento(),
                entity.getAtivo()
        );
    }

    public PagamentoDto paraDto(PagamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new PagamentoDto(
                entity.getId(),
                entity.getPedidoId(),
                entity.getValor(),
                entity.getFormaPagamento(),
                entity.getAtivo()
        );
    }
}
