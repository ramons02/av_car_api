package br.edu.senai.fatesg.avcar.business.ordemservico;

import br.edu.senai.fatesg.avcar.business.pecas.ItemPeca;
import br.edu.senai.fatesg.avcar.business.servicos.ItemServico;
import br.edu.senai.fatesg.avcar.business.veiculos.Veiculo;
import br.edu.senai.fatesg.avcar.core.domains.BaseModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OrdemServico extends BaseModel {
    private Integer numeroOs;
    private Veiculo veiculo;
    private StatusOrdemServico status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFinalizacao;
    private LocalDate entradaVeiculo;
    private String defeitoRelatado;
    private int quantidadePecas;
    private double valorTotalPecas;
    private double valorMaoObra;
    private double valorServicoExterno;
    private String formaPagamento;
    private double valorDesconto;
    private double valorTotal;
    private int garantia;
    private Long colaboradorId;
    private String colaboradorNome;
    private List<ItemServico> itensServico;
    private List<ItemPeca> itensPeca;

    public OrdemServico() {
        this.itensServico = new ArrayList<>();
        this.itensPeca = new ArrayList<>();
        this.status = StatusOrdemServico.ABERTA;
        this.dataAbertura = LocalDateTime.now();
    }

    public OrdemServico(Long id, Integer numeroOs, Veiculo veiculo) {
        setId(id);
        this.numeroOs = numeroOs;
        this.veiculo = veiculo;
        this.status = StatusOrdemServico.ABERTA;
        this.dataAbertura = LocalDateTime.now();
        this.itensServico = new ArrayList<>();
        this.itensPeca = new ArrayList<>();
    }

    public void addItemServico(ItemServico item) { this.itensServico.add(item); }
    public void addItemPeca(ItemPeca item) { this.itensPeca.add(item); }
}
