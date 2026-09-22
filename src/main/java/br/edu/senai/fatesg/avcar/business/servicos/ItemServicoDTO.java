package br.edu.senai.fatesg.avcar.business.servicos;

import br.edu.senai.fatesg.avcar.core.dtos.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemServicoDTO extends BaseDTO {
    private Long servicoId;
    private String servicoNome;
    private int quantidade;
    private double valorUnitario;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private String status;
    private String colaboradorNome;

    public ItemServicoDTO() {}

    public static ItemServicoDTO from(ItemServico item) {
        ItemServicoDTO dto = new ItemServicoDTO();
        dto.setId(item.getId());
        dto.setServicoId(item.getServico().getId());
        dto.setServicoNome(item.getServico().getNomeServico());
        dto.setQuantidade(item.getQuantidade());
        dto.setValorUnitario(item.getValorUnitario());
        dto.setHoraInicio(item.getHoraInicio());
        dto.setHoraFim(item.getHoraFim());
        dto.setStatus(item.getStatus());
        dto.setColaboradorNome(item.getColaboradorNome());
        return dto;
    }

    public double getSubtotal() { return quantidade * valorUnitario; }
}
