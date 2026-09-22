package br.edu.senai.fatesg.avcar.business.ordemservico;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// PADRÃO ITERATOR: Permite percorrer uma lista de OrdemServico aplicando automaticamente um 
// filtro de StatusOrdemServico. Isso encapsula a lógica de navegação e as regras do filtro
// (loops e if's) dentro do iterador. O cliente que for percorrer a lista só precisa utilizar
// os métodos simples hasNext() e next().
public class OrdemServicoIterator implements Iterator<OrdemServico> {
    private final List<OrdemServico> ordens;
    private final StatusOrdemServico filtroStatus;
    private int posicaoAtual;
    private int proximoIndex;

    public OrdemServicoIterator(List<OrdemServico> ordens, StatusOrdemServico filtroStatus) {
        this.ordens = ordens;
        this.filtroStatus = filtroStatus;
        this.posicaoAtual = 0;
        this.proximoIndex = -1;
        avancarParaProximo();
    }

    private void avancarParaProximo() {
        proximoIndex = -1;
        for (int i = posicaoAtual; i < ordens.size(); i++) {
            if (ordens.get(i).getStatus() == filtroStatus) {
                proximoIndex = i;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return proximoIndex >= 0;
    }

    @Override
    public OrdemServico next() {
        if (!hasNext()) throw new NoSuchElementException();
        OrdemServico atual = ordens.get(proximoIndex);
        posicaoAtual = proximoIndex + 1;
        avancarParaProximo();
        return atual;
    }
}
