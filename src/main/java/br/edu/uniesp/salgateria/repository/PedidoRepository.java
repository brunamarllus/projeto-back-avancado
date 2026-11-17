
package br.edu.uniesp.salgateria.repository;

import br.edu.uniesp.salgateria.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
