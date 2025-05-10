package br.com.farmacia.farmacia_em_dia.controller;

import br.com.farmacia.farmacia_em_dia.CompraDTO.CompraDTO;
import br.com.farmacia.farmacia_em_dia.Alerta.AlertaDTO;
import br.com.farmacia.farmacia_em_dia.model.Cliente;
import br.com.farmacia.farmacia_em_dia.model.Compra;
import br.com.farmacia.farmacia_em_dia.model.Produto;
import br.com.farmacia.farmacia_em_dia.repository.ClienteRepository;
import br.com.farmacia.farmacia_em_dia.repository.CompraRepository;
import br.com.farmacia.farmacia_em_dia.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<Compra> criarCompra(@RequestBody CompraDTO dto) {
        // Validação dos campos obrigatórios
        if (dto.getClienteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do cliente é obrigatório");
        }
        if (dto.getProdutoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do produto é obrigatório");
        }
        if (dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        // Validação específica para produtos controlados
        if (produto.isControlado()) {
            if (dto.getDosagemPorDia() == null || dto.getDosagemPorDia() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Dosagem por dia deve ser maior que zero para medicamentos de uso contínuo");
            }
        }

        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setProduto(produto);
        compra.setQuantidade(dto.getQuantidade());
        compra.setDosagemPorDia(dto.getDosagemPorDia());
        compra.setDataCompra(dto.getDataCompra() != null ? dto.getDataCompra() : LocalDate.now());

        Compra salva = compraRepository.save(compra);
        return new ResponseEntity<>(salva, HttpStatus.CREATED);
    }

    // GET /compras — lista todas as compras ou compras de um cliente específico
    @GetMapping
    public List<Compra> listarCompras(@RequestParam(required = false) Long clienteId) {
        if (clienteId != null) {
            return compraRepository.findByClienteId(clienteId);
        }
        return compraRepository.findAll();
    }

    // GET /compras/alerta/{clienteId} — alertas de um único cliente
    @GetMapping("/alerta/{clienteId}")
    public List<AlertaDTO> alertaPorCliente(@PathVariable Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        return compraRepository.findByClienteId(clienteId).stream()
                .filter(c -> c.getProduto() != null && c.getProduto().isControlado())
                .filter(c -> c.getDosagemPorDia() != null && c.getDosagemPorDia() > 0)
                .map(c -> {
                    Integer diasRestantes = c.getQuantidade() / c.getDosagemPorDia();
                    LocalDate dataFim = c.getDataCompra().plusDays(diasRestantes);
                    return new AlertaDTO(
                            c.getProduto().getNome(),
                            cliente.getId(),
                            cliente.getNome(),
                            diasRestantes,
                            dataFim
                    );
                })
                .filter(alerta -> alerta.getDiasRestantes() <= 7)
                .toList();
    }

    // GET /compras/alertas — alertas gerais de todos os clientes
    @GetMapping("/alertas")
    public List<AlertaDTO> listarAlertasGerais() {
        return compraRepository.findAll().stream()
                .filter(c -> c.getProduto() != null && c.getCliente() != null && c.getProduto().isControlado())
                .filter(c -> c.getDosagemPorDia() != null && c.getDosagemPorDia() > 0)
                .map(c -> {
                    Integer diasRestantes = c.getQuantidade() / c.getDosagemPorDia();
                    LocalDate dataFim = c.getDataCompra().plusDays(diasRestantes);
                    return new AlertaDTO(
                            c.getProduto().getNome(),
                            c.getCliente().getId(),
                            c.getCliente().getNome(),
                            diasRestantes,
                            dataFim
                    );
                })
                .filter(alerta -> alerta.getDiasRestantes() <= 7)
                .toList();
    }
}