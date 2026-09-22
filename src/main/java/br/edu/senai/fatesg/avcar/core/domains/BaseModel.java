package br.edu.senai.fatesg.avcar.core.domains;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_hora_criacao")
    private Date dataHoraCriacao;

    @Column(name = "ativo")
    private boolean ativo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDataHoraCriacao() { return dataHoraCriacao; }
    public void setDataHoraCriacao(Date dataHoraCriacao) { this.dataHoraCriacao = dataHoraCriacao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @PrePersist
    protected void onCreate() {
        if (this.dataHoraCriacao == null) {
            this.dataHoraCriacao = new Date();
        }
        this.ativo = true;
    }
}
