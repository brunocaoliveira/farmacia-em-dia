package br.com.farmacia.farmacia_em_dia.repository;

import br.com.farmacia.farmacia_em_dia.model.Compra;
import br.com.farmacia.farmacia_em_dia.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    // busca por objeto Cliente
    List<Compra> findByCliente(Cliente cliente);

    // opcional: busca direto pelo ID do cliente
    List<Compra> findByClienteId(Long clienteId);
}
