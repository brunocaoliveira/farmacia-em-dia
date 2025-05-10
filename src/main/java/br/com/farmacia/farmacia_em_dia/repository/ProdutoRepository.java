package br.com.farmacia.farmacia_em_dia.repository;

import br.com.farmacia.farmacia_em_dia.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Busca produtos por nome (ignorando maiúsculas/minúsculas)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca produtos por categoria
    List<Produto> findByCategoria(String categoria);

    // Busca produtos com estoque baixo
    List<Produto> findByEstoqueAtualLessThanEqual(Integer estoqueMinimo);

    // Busca produtos próximos do vencimento
    List<Produto> findByDataValidadeBefore(LocalDate data);

    // Busca produtos de uso contínuo
    List<Produto> findByControladoTrue();

    // Busca produtos por fornecedor
    List<Produto> findByFornecedor(String fornecedor);

    // Busca produtos com preço entre dois valores
    List<Produto> findByPrecoBetween(Double precoMinimo, Double precoMaximo);

    // Busca produtos com estoque abaixo do mínimo
    List<Produto> findByEstoqueAtualLessThanAndEstoqueMinimoGreaterThan(
            Integer estoqueAtual, Integer estoqueMinimo);
}