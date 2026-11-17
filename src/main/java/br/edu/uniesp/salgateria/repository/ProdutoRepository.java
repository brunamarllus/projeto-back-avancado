package br.edu.uniesp.salgateria.repository;

import br.edu.uniesp.salgateria.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
