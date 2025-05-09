package br.com.farmacia.farmacia_em_dia.repository;

import br.com.farmacia.farmacia_em_dia.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf); // Método para buscar cliente pelo CPF

}