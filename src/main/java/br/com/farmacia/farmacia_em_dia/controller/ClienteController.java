package br.com.farmacia.farmacia_em_dia.controller;

import br.com.farmacia.farmacia_em_dia.CpfValidator;
import br.com.farmacia.farmacia_em_dia.model.Cliente;
import br.com.farmacia.farmacia_em_dia.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // POST /clientes — cadastra um novo cliente
    @PostMapping
    public ResponseEntity<?> criarCliente(@RequestBody Cliente cliente) {
        if (!CpfValidator.isValidCPF(cliente.getCpf())) {
            return new ResponseEntity<>("CPF inválido.", HttpStatus.BAD_REQUEST);
        }

        // 2. Verifica se já existe cliente com o mesmo CPF
        if (clienteRepository.findByCpf(cliente.getCpf()).isPresent()) {
            return new ResponseEntity<>("Cliente com esse CPF já está cadastrado.", HttpStatus.BAD_REQUEST);
        }

        // 3. Salva o cliente
        Cliente novoCliente = clienteRepository.save(cliente);
        return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
    }

    // GET /clientes — lista todos os clientes
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }
    // dentro de ClienteController:

    // GET /clientes/{id} — busca um cliente específico
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }


    // DELETE /clientes/{id} — remove um cliente pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    clienteRepository.delete(cliente);
                    return new ResponseEntity<>("Cliente deletado com sucesso.", HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>("Cliente não encontrado.", HttpStatus.NOT_FOUND));

    }
    // PUT /clientes/{id} — atualiza os dados de um cliente pelo ID
    // PUT /clientes/{id} — atualiza os dados de um cliente pelo ID
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    // Atualiza os campos do cliente
                    clienteExistente.setNome(clienteAtualizado.getNome());
                    clienteExistente.setTelefone(clienteAtualizado.getTelefone());
                    clienteExistente.setCpf(clienteAtualizado.getCpf());
                    clienteExistente.setNascimento(clienteAtualizado.getNascimento());

                    // Salva as alterações
                    Cliente clienteSalvo = clienteRepository.save(clienteExistente);
                    return new ResponseEntity<>(clienteSalvo, HttpStatus.OK); // Retorna o cliente atualizado
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Caso o cliente não seja encontrado
    }






}
