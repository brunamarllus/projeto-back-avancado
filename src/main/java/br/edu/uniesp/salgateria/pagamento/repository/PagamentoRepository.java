package br.edu.uniesp.salgateria.pagamento.repository;

import br.edu.uniesp.salgateria.pagamento.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<PagamentoEntity, Long> {
}
