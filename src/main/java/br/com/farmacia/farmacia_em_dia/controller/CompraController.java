package br.com.farmacia.farmacia_em_dia.controller;




import br.com.farmacia.farmacia_em_dia.Alerta.AlertaDTO;
import br.com.farmacia.farmacia_em_dia.model.Cliente;
import br.com.farmacia.farmacia_em_dia.model.Compra;
import br.com.farmacia.farmacia_em_dia.repository.ClienteRepository;
import br.com.farmacia.farmacia_em_dia.repository.CompraRepository;
import br.com.farmacia.farmacia_em_dia.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private final CompraRepository compraRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    @Autowired
    public CompraController(CompraRepository compraRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.compraRepository = compraRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    // POST /compras — registra uma compra
    @PostMapping
    public Compra criarCompra(@RequestBody Compra compra) {
        // garante que a data de compra seja hoje se não for enviada
        if (compra.getDataCompra() == null) {
            compra.setDataCompra(LocalDate.now());
        }
        return compraRepository.save(compra);
    }

    // GET /compras — lista todas as compras
    @GetMapping
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    // GET /compras/alerta/{clienteId} — retorna dias restantes para o próximo medicamento controlado
    @GetMapping("/alerta/{clienteId}")
    public String alertaRemedio(@PathVariable Long clienteId) {
        List<Compra> compras = compraRepository.findByClienteId(clienteId);

        StringBuilder sb = new StringBuilder();
        for (Compra compra : compras) {
            if (compra.getProduto().isControlado()) {
                int diasRestantes = (int) (compra.getQuantidade() / (double) compra.getDosagemPorDia());
                LocalDate dataFim = compra.getDataCompra().plusDays(diasRestantes);
                sb.append("• ")
                        .append(compra.getProduto().getNome())
                        .append(" — falta(m) ")
                        .append(diasRestantes)
                        .append(" dia(s) (até ")
                        .append(dataFim)
                        .append(")\n");
            }

        }
        if (sb.isEmpty()) {
            return "Nenhum medicamento controlado encontrado para o cliente.";
        }
        return sb.toString();
    }
    @GetMapping("/alertas")
    public List<AlertaDTO> listarAlertasGerais() {
        List<Compra> compras = compraRepository.findAll();

        return compras.stream()
                .filter(c -> c.getProduto().isControlado())
                .map(c -> {
                    int diasRestantes = (int) (c.getQuantidade() / (double) c.getDosagemPorDia());
                    LocalDate dataFim = c.getDataCompra().plusDays(diasRestantes);
                    long diasParaFim = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dataFim);

                    return new AlertaDTO(
                            c.getProduto().getNome(),
                            c.getCliente().getId(),
                            c.getCliente().getNome(),
                            diasParaFim,
                            dataFim
                    );
                })
                .filter(alerta -> alerta.getDiasRestantes() <= 7)
                .toList();
    }


}