package br.com.farmacia.farmacia_em_dia.controller;

import br.com.farmacia.farmacia_em_dia.ProdutoDTO.ProdutoDTO;
import br.com.farmacia.farmacia_em_dia.model.Produto;
import br.com.farmacia.farmacia_em_dia.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // POST /produtos — cadastra um produto
    @PostMapping
    public ResponseEntity<ProdutoDTO> criarProduto(@RequestBody ProdutoDTO produtoDTO) {
        Produto produto = new Produto();
        atualizarProdutoFromDTO(produto, produtoDTO);
        Produto produtoSalvo = produtoRepository.save(produto);
        return ResponseEntity.ok(converterParaDTO(produtoSalvo));
    }

    // GET /produtos — lista todos os produtos
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarProdutos() {
        List<ProdutoDTO> produtos = produtoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    // GET /produtos/{id} — busca um produto específico
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarProduto(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(produto -> ResponseEntity.ok(converterParaDTO(produto)))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /produtos/{id} — atualiza um produto
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizarProduto(@PathVariable Long id, @RequestBody ProdutoDTO produtoDTO) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    atualizarProdutoFromDTO(produto, produtoDTO);
                    Produto produtoAtualizado = produtoRepository.save(produto);
                    return ResponseEntity.ok(converterParaDTO(produtoAtualizado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produtoRepository.delete(produto);
                    return new ResponseEntity<>("Produto deletado com sucesso.", HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>("Produto não encontrado.", HttpStatus.NOT_FOUND));
    }

    // Métodos auxiliares para converter entre DTO e entidade
    private ProdutoDTO converterParaDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setFornecedor(produto.getFornecedor());
        dto.setCategoria(produto.getCategoria());
        dto.setDataValidade(produto.getDataValidade());
        dto.setEstoqueMinimo(produto.getEstoqueMinimo());
        dto.setEstoqueAtual(produto.getEstoqueAtual());
        dto.setControlado(produto.isControlado());
        dto.setQuantidadePorEmbalagem(produto.getQuantidadePorEmbalagem());
        dto.setReceitaObrigatoria(produto.isReceitaObrigatoria());
        dto.setObservacoes(produto.getObservacoes());
        return dto;
    }

    private void atualizarProdutoFromDTO(Produto produto, ProdutoDTO dto) {
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setFornecedor(dto.getFornecedor());
        produto.setCategoria(dto.getCategoria());
        produto.setDataValidade(dto.getDataValidade());
        produto.setEstoqueMinimo(dto.getEstoqueMinimo());
        produto.setEstoqueAtual(dto.getEstoqueAtual());
        produto.setControlado(dto.isControlado());
        produto.setQuantidadePorEmbalagem(dto.getQuantidadePorEmbalagem());
        produto.setReceitaObrigatoria(dto.isReceitaObrigatoria());
        produto.setObservacoes(dto.getObservacoes());
    }
}