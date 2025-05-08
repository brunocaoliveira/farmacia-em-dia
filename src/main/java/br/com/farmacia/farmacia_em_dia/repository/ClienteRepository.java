package br.com.farmacia.farmacia_em_dia.repository;

import br.com.farmacia.farmacia_em_dia.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}