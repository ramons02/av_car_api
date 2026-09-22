package br.edu.senai.fatesg.avcar.core.dtos;

import lombok.Data;

@Data
public abstract class BaseDTO {
    private Long id;
    private boolean ativo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
