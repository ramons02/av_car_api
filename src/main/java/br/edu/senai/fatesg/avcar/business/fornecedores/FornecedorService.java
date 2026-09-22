package br.edu.senai.fatesg.avcar.business.fornecedores;

import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("businessFornecedorService")
public class FornecedorService extends GenericService<FornecedorModel, FornecedorDTO, IFornecedorRepository>
        implements IFornecedorService {

    private final FornecedorMapper mapper;
    private final IFornecedorValidation validation;

    public FornecedorService(IFornecedorRepository repository, FornecedorMapper mapper, IFornecedorValidation validation) {
        super(repository, "Fornecedor");
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected FornecedorDTO toDTO(FornecedorModel model) {
        return mapper.toDto(model);
    }

    @Override
    public List<FornecedorDTO> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome).stream().map(mapper::toDto).toList();
    }

    @Override
    public FornecedorDTO salvar(FornecedorModel model) {
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    public void atualizar(FornecedorModel model) {
        repository.buscarPorId(model.getId())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Fornecedor", model.getId()));
        validation.validar(model);
        executarComTratamentoDuplicidade(() -> repository.atualizar(model));
    }
}
