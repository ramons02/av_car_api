package br.edu.senai.fatesg.avcar.business.ordemservico;

import br.edu.senai.fatesg.avcar.core.exceptions.NegocioException;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

// PADRÃO TEMPLATE METHOD: Define o fluxo rígido de transição do status de uma OrdemServico
// no método executar(os): primeiro valida, depois executa a transição e por fim notifica.
// As subclasses implementam apenas os passos mutáveis: como validar as regras específicas
// de cada etapa e qual é o próximo status, evitando duplicação de código.
public abstract class OrdemServicoTemplate {

    private static final Logger LOGGER = Logger.getLogger(OrdemServicoTemplate.class.getName());

    public final OrdemServico executar(OrdemServico os) {
        validarTransicao(os);
        executarTransicao(os);
        notificar(os);
        return os;
    }

    protected abstract void validarTransicao(OrdemServico os);

    protected void executarTransicao(OrdemServico os) {
        StatusOrdemServico proximo = proximoStatus(os.getStatus());
        os.setStatus(proximo);
        if (proximo == StatusOrdemServico.FINALIZADA) {
            os.setDataFinalizacao(LocalDateTime.now());
        }
        recalcularValor(os);
    }

    protected void notificar(OrdemServico os) {
        LOGGER.log(Level.INFO, "[NOTIFICAÇÃO] OS {0} alterada para: {1}", 
            new Object[]{os.getNumeroOs(), os.getStatus().getDescricao()});
    }

    protected abstract StatusOrdemServico proximoStatus(StatusOrdemServico atual);

    protected void recalcularValor(OrdemServico os) {
        double totalServicos = os.getItensServico().stream()
            .mapToDouble(i -> i.getQuantidade() * i.getValorUnitario()).sum();
        double totalPecas = os.getItensPeca().stream()
            .mapToDouble(i -> i.getQuantidade() * i.getValorUnitario()).sum();
        os.setValorTotal(totalServicos + totalPecas);
    }

    public static void performCancel(OrdemServico os) {
        if (os.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new NegocioException("OS finalizada não pode ser cancelada");
        }
        os.setStatus(StatusOrdemServico.CANCELADA);
        LOGGER.log(Level.INFO, "[NOTIFICAÇÃO] OS {0} cancelada.", os.getNumeroOs());
    }

    public static void performPause(OrdemServico os) {
        if (os.getStatus() != StatusOrdemServico.EM_ORCAMENTO
            && os.getStatus() != StatusOrdemServico.EM_EXECUCAO) {
            throw new NegocioException("OS deve estar em Orçamento ou Execução para pausar");
        }
        os.setStatus(StatusOrdemServico.AGUARDANDO_PECA);
        LOGGER.log(Level.INFO, "[NOTIFICAÇÃO] OS {0} pausada para aguardar peça.", os.getNumeroOs());
    }

    public static void performReturn(OrdemServico os) {
        if (os.getStatus() != StatusOrdemServico.AGUARDANDO_PECA) {
            throw new NegocioException("OS deve estar em Aguardando Peça para retornar");
        }
        os.setStatus(StatusOrdemServico.EM_ORCAMENTO);
        LOGGER.log(Level.INFO, "[NOTIFICAÇÃO] OS {0} retornada para Orçamento.", os.getNumeroOs());
    }
}
