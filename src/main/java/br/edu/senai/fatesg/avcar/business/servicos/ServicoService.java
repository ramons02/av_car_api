package br.edu.senai.fatesg.avcar.business.servicos;

import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("businessServicoService")
public class ServicoService extends GenericService<ServicoModel, ServicoDTO, IServicoRepository>
        implements IServicoService {

    private final ServicoMapper mapper;
    private final IServicoValidation validation;

    public ServicoService(IServicoRepository repository, ServicoMapper mapper, IServicoValidation validation) {
        super(repository, "Serviço");
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected ServicoDTO toDTO(ServicoModel model) {
        return mapper.toDto(model);
    }

    @Override
    public List<ServicoDTO> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome).stream().map(mapper::toDto).toList();
    }

    @Override
    public ServicoDTO salvar(ServicoModel model) {
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    public void atualizar(ServicoModel model) {
        repository.buscarPorId(model.getId())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Serviço", model.getId()));
        validation.validar(model);
        executarComTratamentoDuplicidade(() -> repository.atualizar(model));
    }
}
