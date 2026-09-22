package br.edu.senai.fatesg.avcar.business.ordemservico;

import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServico;
import br.edu.senai.fatesg.avcar.core.dtos.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrdemServicoDTO extends BaseDTO {

    // Campos do modelo de negócio
    private Long idVeiculo;
    private Long idCliente;
    private Long idColaboradorResponsavel;
    private String status;
    private String defeito;
    private String solucao;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private LocalDate dataPrevisao;
    private double valorTotal;
    private String observacoes;

    // Campos legados (compatibilidade com OrdemServicoService existente)
    private Integer numeroOs;
    private String veiculo;

    private LocalDateTime dataFinalizacao;
    private LocalDate entradaVeiculo;
    private String defeitoRelatado;
    private int quantidadePecas;
    private double valorTotalPecas;
    private double valorMaoObra;
    private double valorServicoExterno;
    private String formaPagamento;
    private double valorDesconto;
    private int garantia;
    private String colaboradorNome;

    public OrdemServicoDTO() {}

    public static OrdemServicoDTO from(OrdemServico os) {
        OrdemServicoDTO dto = new OrdemServicoDTO();
        dto.setId(os.getId());
        dto.setNumeroOs(os.getNumeroOs());
        dto.setVeiculo(os.getVeiculo().getPlaca() + " - " + os.getVeiculo().getModelo().getNomeModelo());
        dto.setStatus(os.getStatus().getDescricao());
        dto.setDataAbertura(os.getDataAbertura());
        dto.setDataFinalizacao(os.getDataFinalizacao());
        dto.setEntradaVeiculo(os.getEntradaVeiculo());
        dto.setDefeitoRelatado(os.getDefeitoRelatado());
        dto.setQuantidadePecas(os.getQuantidadePecas());
        dto.setValorTotalPecas(os.getValorTotalPecas());
        dto.setValorMaoObra(os.getValorMaoObra());
        dto.setValorServicoExterno(os.getValorServicoExterno());
        dto.setFormaPagamento(os.getFormaPagamento());
        dto.setValorDesconto(os.getValorDesconto());
        dto.setValorTotal(os.getValorTotal());
        dto.setGarantia(os.getGarantia());
        dto.setColaboradorNome(os.getColaboradorNome());
        return dto;
    }
}
