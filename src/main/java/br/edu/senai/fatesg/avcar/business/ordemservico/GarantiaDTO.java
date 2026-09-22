package br.edu.senai.fatesg.avcar.business.ordemservico;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
public class GarantiaDTO {
    private String tipo;
    private String item;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime dataVencimento;
    private int diasRestantes;
    private boolean vencida;
    private String colaboradorNome;

    public GarantiaDTO() {}

    public GarantiaDTO(String tipo, String item, LocalDateTime dataFinalizacao, int prazoDias) {
        this(tipo, item, dataFinalizacao, prazoDias, null);
    }

    public GarantiaDTO(String tipo, String item, LocalDateTime dataFinalizacao, int prazoDias, String colaboradorNome) {
        this.tipo = tipo;
        this.item = item;
        this.dataFinalizacao = dataFinalizacao;
        this.colaboradorNome = colaboradorNome;
        if (dataFinalizacao != null) {
            this.dataVencimento = dataFinalizacao.plusDays(prazoDias);
            this.diasRestantes = (int) ChronoUnit.DAYS.between(LocalDateTime.now(), dataVencimento);
            this.vencida = diasRestantes <= 0;
        }
    }
}
