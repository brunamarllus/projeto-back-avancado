package br.edu.uniesp.salgateria.pedido.repository;

import br.edu.uniesp.salgateria.pedido.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    Optional<PedidoEntity> findByCodigoRastreio(String codigoRastreio);
}
