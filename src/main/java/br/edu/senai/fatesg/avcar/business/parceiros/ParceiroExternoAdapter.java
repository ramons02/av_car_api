package br.edu.senai.fatesg.avcar.business.parceiros;

import br.edu.senai.fatesg.avcar.business.fornecedores.Fornecedor;
import br.edu.senai.fatesg.avcar.business.servicos.Servico;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoExterno;

// PADRÃO ADAPTER: Adapta um Fornecedor e um ServicoExterno (parceria com terceiros) à interface
// padrão de um Servico interno. Isso permite que a Ordem de Serviço trate os serviços
// terceirizados da mesma forma que os serviços próprios, sem alterar o código que manipula a OS.
public class ParceiroExternoAdapter extends Servico {
    private final Fornecedor fornecedor;
    private final ServicoExterno servicoExterno;

    public ParceiroExternoAdapter(Fornecedor fornecedor, ServicoExterno servicoExterno) {
        super(null, servicoExterno.getDescricao(), "Serviço terceirizado: " + fornecedor.getRazaoSocial(),
              servicoExterno.getValor(), servicoExterno.getGarantiaDias(), null);
        this.fornecedor = fornecedor;
        this.servicoExterno = servicoExterno;
    }

    public Fornecedor getFornecedor() { return fornecedor; }
    public ServicoExterno getServicoExterno() { return servicoExterno; }
}
