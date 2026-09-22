package br.edu.senai.fatesg.avcar.business.servicos;

import br.edu.senai.fatesg.avcar.core.dtos.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServicoExternoDTO extends BaseDTO {
    private Long parceiroId;
    private String parceiroNome;
    private String descricao;
    private double valor;
    private int garantiaDias;

    public ServicoExternoDTO() {}

    public static ServicoExternoDTO from(ServicoExterno se) {
        ServicoExternoDTO dto = new ServicoExternoDTO();
        dto.setId(se.getId());
        dto.setParceiroId(se.getParceiro().getId());
        dto.setParceiroNome(se.getParceiro().getNome());
        dto.setDescricao(se.getDescricao());
        dto.setValor(se.getValor());
        dto.setGarantiaDias(se.getGarantiaDias());
        return dto;
    }
}
