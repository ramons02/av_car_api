package br.edu.senai.fatesg.avcar.business.parceiros;

import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("businessParceiroService")
public class ParceiroService extends GenericService<ParceiroModel, ParceiroDTO, IParceiroRepository>
        implements IParceiroService {

    private final ParceiroMapper mapper;
    private final IParceiroValidation validation;

    public ParceiroService(IParceiroRepository repository, ParceiroMapper mapper, IParceiroValidation validation) {
        super(repository, "Parceiro");
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected ParceiroDTO toDTO(ParceiroModel model) {
        return mapper.toDto(model);
    }

    @Override
    public List<ParceiroDTO> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome).stream().map(mapper::toDto).toList();
    }

    @Override
    public ParceiroDTO salvar(ParceiroModel model) {
        if (!model.isAtivo()) model.setAtivo(true);
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    public void atualizar(ParceiroModel model) {
        repository.buscarPorId(model.getId())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Parceiro", model.getId()));
        validation.validar(model);
        executarComTratamentoDuplicidade(() -> repository.atualizar(model));
    }
}
