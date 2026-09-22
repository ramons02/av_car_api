package br.edu.senai.fatesg.avcar.business.ordemservico;

import br.edu.senai.fatesg.avcar.core.services.IGenericService;

import java.util.List;

public interface IOrdemServicoService extends IGenericService<OrdemServicoDTO> {
    List<OrdemServicoDTO> buscarPorStatus(String status);
    DashboardDTO obterResumoDashboard();
}
