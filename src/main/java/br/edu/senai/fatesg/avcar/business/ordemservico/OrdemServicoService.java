package br.edu.senai.fatesg.avcar.business.ordemservico;

import br.edu.senai.fatesg.avcar.business.fornecedores.IFornecedorRepository;
import br.edu.senai.fatesg.avcar.business.pecas.IPecaRepository;
import br.edu.senai.fatesg.avcar.business.servicos.IServicoRepository;
import br.edu.senai.fatesg.avcar.business.veiculos.IVeiculoRepository;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServico;
import br.edu.senai.fatesg.avcar.business.ordemservico.StatusOrdemServico;
import br.edu.senai.fatesg.avcar.business.pecas.ItemPecaDTO;
import br.edu.senai.fatesg.avcar.business.servicos.ItemServicoDTO;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoExternoDTO;
import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.exceptions.NegocioException;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdemServicoService {

    private final IOrdemServicoRepository repository;
    private final IVeiculoRepository veiculoRepository;
    private final IServicoRepository servicoRepository;
    private final IPecaRepository pecaRepository;
    private final IFornecedorRepository fornecedorRepository;
    private final br.edu.senai.fatesg.avcar.business.parceiros.IParceiroRepository parceiroRepository;

    public OrdemServicoService(IOrdemServicoRepository repository,
                               IVeiculoRepository veiculoRepository,
                               IServicoRepository servicoRepository,
                               IPecaRepository pecaRepository,
                               IFornecedorRepository fornecedorRepository,
                               br.edu.senai.fatesg.avcar.business.parceiros.IParceiroRepository parceiroRepository) {
        this.repository = repository;
        this.veiculoRepository = veiculoRepository;
        this.servicoRepository = servicoRepository;
        this.pecaRepository = pecaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.parceiroRepository = parceiroRepository;
    }

    public List<OrdemServicoDTO> listarTodos() {
        return repository.listarTodos().stream()
            .map(OrdemServicoDTO::from).toList();
    }

    public OrdemServicoDTO buscarPorId(Long id) {
        OrdemServico os = repository.buscarPorId(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Ordem de Serviço", id));
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO avancarOrcamento(Long id) {
        OrdemServico os = carregar(id);
        if (os.getStatus() != StatusOrdemServico.ABERTA) {
            throw new NegocioException("OS deve estar em Aberta para gerar orçamento");
        }
        double total = repository.somarValorItens(id);
        if (total <= 0) {
            throw new NegocioException("OS deve ter ao menos um serviço ou peça para orçamento");
        }
        os.setValorTotal(total);
        os.setStatus(StatusOrdemServico.EM_ORCAMENTO);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO avancarExecucao(Long id) {
        OrdemServico os = carregar(id);
        if (os.getStatus() != StatusOrdemServico.EM_ORCAMENTO
            && os.getStatus() != StatusOrdemServico.AGUARDANDO_PECA) {
            throw new NegocioException("OS deve estar em Orçamento ou Aguardando Peça para iniciar Execução");
        }
        os.setStatus(StatusOrdemServico.EM_EXECUCAO);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO avancarPagamento(Long id) {
        OrdemServico os = carregar(id);
        if (os.getStatus() != StatusOrdemServico.EM_EXECUCAO) {
            throw new NegocioException("OS deve estar em Execução para finalizar");
        }
        if (os.getValorTotal() <= 0) {
            throw new NegocioException("Valor total da OS deve ser positivo");
        }
        double servicos = repository.listarItensServico(id).stream().mapToDouble(s -> s.getQuantidade() * s.getValorUnitario()).sum();
        double pecas = repository.listarItensPeca(id).stream().mapToDouble(p -> p.getQuantidade() * p.getValorUnitario()).sum();
        double externos = repository.listarServicosExternos(id).stream().mapToDouble(br.edu.senai.fatesg.avcar.business.servicos.ServicoExterno::getValor).sum();
        
        os.setValorMaoObra(servicos);
        os.setValorTotalPecas(pecas);
        os.setValorServicoExterno(externos);
        
        double novoTotal = (servicos + pecas + externos) - os.getValorDesconto();
        os.setValorTotal(novoTotal);

        os.setStatus(StatusOrdemServico.FINALIZADA);
        os.setDataFinalizacao(LocalDateTime.now());
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO finalizar(Long id) {
        return avancarPagamento(id);
    }

    public OrdemServicoDTO cancelar(Long id) {
        OrdemServico os = carregar(id);
        OrdemServicoTemplate.performCancel(os);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO pausar(Long id) {
        OrdemServico os = carregar(id);
        OrdemServicoTemplate.performPause(os);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO retornar(Long id) {
        OrdemServico os = carregar(id);
        OrdemServicoTemplate.performReturn(os);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO aplicarGarantiaEstendida(Long id, int diasAdicionais) {
        OrdemServico os = carregar(id);
        os.setGarantia(os.getGarantia() + diasAdicionais);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO aplicarDesconto(Long id, double valorDesconto) {
        OrdemServico os = carregar(id);
        os.setValorDesconto(valorDesconto);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public List<OrdemServicoDTO> buscarPorStatus(String status) {
        return repository.buscarPorStatus(status).stream()
            .map(OrdemServicoDTO::from).toList();
    }

    public DashboardDTO obterResumoDashboard() {
        java.util.List<OrdemServicoDTO> ordens = this.listarTodos();
        int totalOS = 0;
        int osAbertas = 0;
        double faturamentoTotal = 0.0;
        double descontosTotal = 0.0;
        
        if (ordens != null) {
            totalOS = ordens.size();
            for (OrdemServicoDTO os : ordens) {
                if (os.getStatus() != null) {
                    String statusStr = os.getStatus().toUpperCase();
                    if (statusStr.equals("ABERTA") || statusStr.equals("EM_ORCAMENTO") || statusStr.equals("EM ORÇAMENTO") ||
                        statusStr.equals("EM_EXECUCAO") || statusStr.equals("EM EXECUÇÃO") || 
                        statusStr.equals("AGUARDANDO_PECA") || statusStr.equals("AGUARDANDO PEÇA") ||
                        statusStr.equals("EM_ANDAMENTO") || statusStr.equals("EM ANDAMENTO")) {
                        osAbertas++;
                    } else if (statusStr.equals("FINALIZADA") || statusStr.equals("FINALIZADO") || statusStr.equals("PAGA")) {
                        double somaBruta = os.getValorMaoObra() + os.getValorTotalPecas() + os.getValorServicoExterno();
                        faturamentoTotal += (somaBruta - os.getValorDesconto());
                        descontosTotal += os.getValorDesconto();
                    }
                }
            }
        }
        return new DashboardDTO(totalOS, osAbertas, faturamentoTotal, descontosTotal);
    }

    private OrdemServico carregar(Long id) {
        return repository.buscarPorId(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Ordem de Serviço", id));
    }

    public OrdemServicoDTO criar(Long veiculoId, Long responsavelId,
                                  String entradaVeiculo, String defeitoRelatado,
                                  String formaPagamento) {
        var veiculoModel = veiculoRepository.buscarPorId(veiculoId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Veículo", veiculoId));
        // Converte VeiculoModel para o domain Veiculo necessário pela OS
        br.edu.senai.fatesg.avcar.business.veiculos.Veiculo veiculo = toVeiculoDomain(veiculoModel);
        OrdemServico os = new OrdemServico();
        os.setVeiculo(veiculo);
        os.setColaboradorId(responsavelId);
        if (entradaVeiculo != null && !entradaVeiculo.isBlank())
            os.setEntradaVeiculo(LocalDate.parse(entradaVeiculo));
        os.setDefeitoRelatado(defeitoRelatado);
        os.setFormaPagamento(formaPagamento);
        repository.salvar(os);
        return OrdemServicoDTO.from(os);
    }

    public OrdemServicoDTO atualizarCampos(Long id, String defeitoRelatado,
                                            String formaPagamento) {
        OrdemServico os = carregar(id);
        if (defeitoRelatado != null) os.setDefeitoRelatado(defeitoRelatado);
        if (formaPagamento != null) os.setFormaPagamento(formaPagamento);
        repository.atualizar(os);
        return OrdemServicoDTO.from(os);
    }

    public ItemServicoDTO adicionarItemServico(Long osId, Long servicoId, int quantidade,
                                                double valorUnitario, String horaInicio,
                                                String horaFim, String status, Long colaboradorId) {
        carregar(osId);
        var servicoModel = servicoRepository.buscarPorId(servicoId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço", servicoId));
        var item = repository.adicionarItemServico(osId, servicoId, quantidade, valorUnitario,
            servicoModel.getGarantiaDias(), colaboradorId);
        br.edu.senai.fatesg.avcar.business.servicos.Servico servico = toServicoDomain(servicoModel);
        item.setServico(servico);
        if (horaInicio != null && !horaInicio.isBlank())
            item.setHoraInicio(LocalDateTime.parse(horaInicio));
        if (horaFim != null && !horaFim.isBlank())
            item.setHoraFim(LocalDateTime.parse(horaFim));
        if (status != null && !status.isBlank())
            item.setStatus(status);
        return ItemServicoDTO.from(item);
    }

    public void removerItemServico(Long itemId) {
        repository.removerItemServico(itemId);
    }

    public List<ItemServicoDTO> listarItensServico(Long osId) {
        return repository.listarItensServico(osId).stream().map(ItemServicoDTO::from).toList();
    }

    public ItemPecaDTO adicionarItemPeca(Long osId, Long pecaId, int quantidade, double valorUnitario) {
        carregar(osId);
        var pecaModel = pecaRepository.buscarPorId(pecaId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Peça", pecaId));
        var item = repository.adicionarItemPeca(osId, pecaId, quantidade, valorUnitario, pecaModel.getGarantiaPeca());
        br.edu.senai.fatesg.avcar.business.pecas.Peca peca = toPecaDomain(pecaModel);
        item.setPeca(peca);
        return ItemPecaDTO.from(item);
    }

    public void removerItemPeca(Long itemId) {
        repository.removerItemPeca(itemId);
    }

    public List<ItemPecaDTO> listarItensPeca(Long osId) {
        return repository.listarItensPeca(osId).stream().map(ItemPecaDTO::from).toList();
    }

    public ServicoExternoDTO adicionarServicoExterno(Long osId, Long parceiroId, String descricao, double valor, int garantiaDias) {
        carregar(osId);
        parceiroRepository.buscarPorId(parceiroId)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Parceiro Externo", parceiroId));
        var se = repository.adicionarServicoExterno(osId, parceiroId, descricao, valor, garantiaDias);
        return ServicoExternoDTO.from(se);
    }

    public void removerServicoExterno(Long itemId) {
        repository.removerServicoExterno(itemId);
    }

    public List<ServicoExternoDTO> listarServicosExternos(Long osId) {
        return repository.listarServicosExternos(osId).stream().map(ServicoExternoDTO::from).toList();
    }

    public void deletar(Long id) {
        if (repository.buscarPorId(id).isEmpty()) {
            throw new EntidadeNaoEncontradaException("Ordem de Serviço", id);
        }
        repository.deletar(id);
    }

    public List<GarantiaDTO> calcularGarantia(Long id) {
        OrdemServico os = carregar(id);
        java.time.LocalDateTime dataFim = os.getDataFinalizacao();
        List<GarantiaDTO> lista = new ArrayList<>();

        for (var item : repository.listarItensServico(id)) {
            var servicoOpt = servicoRepository.buscarPorId(item.getServico().getId());
            int prazo = servicoOpt.map(s -> s.getGarantiaDias()).orElse(90);
            lista.add(new GarantiaDTO("Serviço", item.getServico().getNomeServico(), dataFim, prazo,
                item.getColaboradorNome()));
        }
        for (var item : repository.listarItensPeca(id)) {
            var pecaOpt = pecaRepository.buscarPorId(item.getPeca().getId());
            int prazo = pecaOpt.map(p -> p.getGarantiaPeca()).orElse(180);
            lista.add(new GarantiaDTO("Peça", item.getPeca().getNome(), dataFim, prazo));
        }
        for (var item : repository.listarServicosExternos(id)) {
            String nomeParceiro = item.getParceiro() != null && item.getParceiro().getNome() != null ? 
                                  item.getParceiro().getNome() : "Parceiro Externo";
            lista.add(new GarantiaDTO("Serv. Externo", item.getDescricao(), dataFim, item.getGarantiaDias(), "Parceiro: " + nomeParceiro));
        }
        return lista;
    }

    // Métodos auxiliares de conversão de Model para Domain
    private br.edu.senai.fatesg.avcar.business.veiculos.Veiculo toVeiculoDomain(
            br.edu.senai.fatesg.avcar.business.veiculos.VeiculoModel model) {
        br.edu.senai.fatesg.avcar.business.veiculos.Modelo modelo =
            new br.edu.senai.fatesg.avcar.business.veiculos.Modelo();
        modelo.setIdModelo(model.getModeloId());
        modelo.setNomeModelo(model.getModeloNome());
        br.edu.senai.fatesg.avcar.business.veiculos.Marca marca =
            new br.edu.senai.fatesg.avcar.business.veiculos.Marca();
        marca.setIdMarca(model.getMarcaId());
        marca.setNomeMarca(model.getMarcaNome());
        modelo.setMarca(marca);
        return new br.edu.senai.fatesg.avcar.business.veiculos.Veiculo(
            model.getId(), model.getPlaca(), model.getChassi(),
            model.getAnoFabricacao(), model.getAnoModelo(), modelo);
    }

    private br.edu.senai.fatesg.avcar.business.servicos.Servico toServicoDomain(
            br.edu.senai.fatesg.avcar.business.servicos.ServicoModel model) {
        br.edu.senai.fatesg.avcar.business.servicos.Servico s =
            new br.edu.senai.fatesg.avcar.business.servicos.Servico();
        s.setId(model.getId());
        s.setNomeServico(model.getNomeServico());
        s.setGarantiaDias(model.getGarantiaDias());
        return s;
    }

    private br.edu.senai.fatesg.avcar.business.pecas.Peca toPecaDomain(
            br.edu.senai.fatesg.avcar.business.pecas.PecaModel model) {
        br.edu.senai.fatesg.avcar.business.pecas.Peca p =
            new br.edu.senai.fatesg.avcar.business.pecas.Peca();
        p.setId(model.getId());
        p.setNome(model.getNome());
        p.setGarantiaPeca(model.getGarantiaPeca());
        return p;
    }
}
