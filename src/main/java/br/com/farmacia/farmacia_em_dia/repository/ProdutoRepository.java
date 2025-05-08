package br.com.farmacia.farmacia_em_dia.repository;

import br.com.farmacia.farmacia_em_dia.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
