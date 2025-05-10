package br.com.farmacia.farmacia_em_dia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Produto produto;

    private LocalDate dataCompra;
    private Integer quantidade;  // Mudado de int para Integer
    private Integer dosagemPorDia;

    // Construtor padrão
    public Compra() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public Integer getQuantidade() {  // Mudado de int para Integer
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {  // Mudado de int para Integer
        this.quantidade = quantidade;
    }

    public Integer getDosagemPorDia() {  // Mudado de int para Integer
        return dosagemPorDia;
    }

    public void setDosagemPorDia(Integer dosagemPorDia) {  // Mudado de int para Integer
        this.dosagemPorDia = dosagemPorDia;
    }
}