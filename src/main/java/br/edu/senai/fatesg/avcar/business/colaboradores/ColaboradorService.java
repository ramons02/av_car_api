package br.edu.senai.fatesg.avcar.business.colaboradores;

import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service("businessColaboradorService")
public class ColaboradorService extends GenericService<ColaboradorModel, ColaboradorDTO, IColaboradorRepository>
        implements IColaboradorService {

    private final ColaboradorMapper mapper;
    private final IColaboradorValidation validation;

    public ColaboradorService(IColaboradorRepository repository, ColaboradorMapper mapper, IColaboradorValidation validation) {
        super(repository, "Colaborador");
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected ColaboradorDTO toDTO(ColaboradorModel model) {
        return mapper.toDto(model);
    }

    @Override
    public List<ColaboradorDTO> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome).stream().map(mapper::toDto).toList();
    }

    @Override
    public ColaboradorDTO salvar(ColaboradorModel model) {
        if (model.getDataAdmissao() == null) model.setDataAdmissao(LocalDate.now());
        validation.validar(model);
        ColaboradorModel salvo = executarComTratamentoDuplicidade(() -> repository.salvar(model));
        if (model.getFuncaoIds() != null && !model.getFuncaoIds().isEmpty()) {
            repository.salvarFuncoes(salvo.getId(), model.getFuncaoIds());
        }
        return mapper.toDto(repository.buscarPorId(salvo.getId()).orElseThrow());
    }

    @Override
    public void atualizar(ColaboradorModel model) {
        ColaboradorModel atual = repository.buscarPorId(model.getId())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Colaborador", model.getId()));
        model.setIdPessoa(atual.getIdPessoa());
        if (model.getMatricula() == null) model.setMatricula(atual.getMatricula());
        if (model.getDataAdmissao() == null) model.setDataAdmissao(atual.getDataAdmissao());
        if (model.getDataDemissao() == null) model.setDataDemissao(atual.getDataDemissao());
        if (model.getSalario() == null) model.setSalario(atual.getSalario());
        if (model.getObservacoes() == null) model.setObservacoes(atual.getObservacoes());
        validation.validar(model);
        executarComTratamentoDuplicidade(() -> {
            repository.atualizar(model);
            repository.salvarFuncoes(model.getId(), model.getFuncaoIds());
        });
    }

    @Override
    public List<FuncaoDTO> listarFuncoes() {
        return repository.listarFuncoes();
    }
}
