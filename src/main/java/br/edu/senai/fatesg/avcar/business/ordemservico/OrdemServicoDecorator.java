package br.edu.senai.fatesg.avcar.business.ordemservico;

// PADRÃO DECORATOR: Atua como a classe base abstrata que envolve uma entidade OrdemServico real.
// As subclasses (como os decoradores de Garantia ou Desconto) sobrescrevem apenas os métodos
// pertinentes ao cálculo de valores para injetar regras adicionais no ciclo de vida da OS,
// mantendo a interface original intacta e evitando dezenas de subclasses combinadas.
public abstract class OrdemServicoDecorator extends OrdemServico {
    protected OrdemServico wrappee;

    public OrdemServicoDecorator(OrdemServico wrappee) {
        this.wrappee = wrappee;
        this.setId(wrappee.getId());
        this.setNumeroOs(wrappee.getNumeroOs());
        this.setVeiculo(wrappee.getVeiculo());
        this.setStatus(wrappee.getStatus());
        this.setDataAbertura(wrappee.getDataAbertura());
        this.setDataFinalizacao(wrappee.getDataFinalizacao());
        this.setEntradaVeiculo(wrappee.getEntradaVeiculo());
        this.setDefeitoRelatado(wrappee.getDefeitoRelatado());
        this.setQuantidadePecas(wrappee.getQuantidadePecas());
        this.setValorTotalPecas(wrappee.getValorTotalPecas());
        this.setValorMaoObra(wrappee.getValorMaoObra());
        this.setValorServicoExterno(wrappee.getValorServicoExterno());
        this.setFormaPagamento(wrappee.getFormaPagamento());
        this.setValorDesconto(wrappee.getValorDesconto());
        this.setValorTotal(wrappee.getValorTotal());
        this.setGarantia(wrappee.getGarantia());
        this.setColaboradorNome(wrappee.getColaboradorNome());
        this.setItensServico(wrappee.getItensServico());
        this.setItensPeca(wrappee.getItensPeca());
    }

    public OrdemServico getWrappee() { return wrappee; }
}
