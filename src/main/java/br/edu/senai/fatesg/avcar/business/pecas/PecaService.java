package br.edu.senai.fatesg.avcar.business.pecas;

import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("businessPecaService")
public class PecaService extends GenericService<PecaModel, PecaDTO, IPecaRepository>
        implements IPecaService {

    private final PecaMapper mapper;
    private final IPecaValidation validation;

    public PecaService(IPecaRepository repository, PecaMapper mapper, IPecaValidation validation) {
        super(repository, "Peça");
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected PecaDTO toDTO(PecaModel model) {
        return mapper.toDto(model);
    }

    @Override
    public PecaDTO salvar(PecaModel model) {
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    public void atualizar(PecaModel model) {
        repository.buscarPorId(model.getId())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Peça", model.getId()));
        validation.validar(model);
        executarComTratamentoDuplicidade(() -> repository.atualizar(model));
    }

    @Override
    public List<PecaDTO> buscarEstoqueBaixo(int quantidadeMinima) {
        return repository.buscarEstoqueBaixo(quantidadeMinima).stream().map(mapper::toDto).toList();
    }

    @Override
    public List<PecaDTO> buscarPorCodigo(long codigo) {
        return repository.buscarPorCodigoNacional(codigo).stream().map(mapper::toDto).toList();
    }
}
