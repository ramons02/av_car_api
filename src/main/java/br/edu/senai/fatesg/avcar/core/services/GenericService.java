package br.edu.senai.fatesg.avcar.core.services;

import br.edu.senai.fatesg.avcar.core.dtos.BaseDTO;
import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.domains.BaseModel;
import br.edu.senai.fatesg.avcar.core.exceptions.BusinessException;
import br.edu.senai.fatesg.avcar.core.repositories.Repository;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.function.Supplier;

public abstract class GenericService<T extends BaseModel, D extends BaseDTO, R extends Repository<T>> implements IGenericService<D> {

    protected final R repository;
    private final String entityName;

    public GenericService(R repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    public List<D> listarTodos() {
        return listarTodos(false);
    }

    public List<D> listarTodos(boolean incluirInativos) {
        var lista = incluirInativos ? repository.listarTodosIncluindoInativos() : repository.listarTodos();
        return lista.stream().map(this::toDTO).toList();
    }

    public D buscarPorId(Long id) {
        T entity = repository.buscarPorId(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException(entityName, id));
        return toDTO(entity);
    }

    public void toggleStatus(Long id) {
        if (repository.buscarPorId(id).isEmpty()) {
            throw new EntidadeNaoEncontradaException(entityName, id);
        }
        repository.toggleStatus(id);
    }

    public void deletar(Long id) {
        if (repository.buscarPorId(id).isEmpty()) {
            throw new EntidadeNaoEncontradaException(entityName, id);
        }
        repository.deletar(id);
    }

    protected <X> X executarComTratamentoDuplicidade(Supplier<X> acao) {
        try {
            return acao.get();
        } catch (DataIntegrityViolationException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("placa") || msg.contains("unique_placa")) {
                throw new BusinessException("Já existe um registro com esta Placa.");
            } else if (msg.contains("cpf") || msg.contains("unique_cpf")) {
                throw new BusinessException("Já existe um registro com este CPF.");
            } else if (msg.contains("cnpj") || msg.contains("unique_cnpj")) {
                throw new BusinessException("Já existe um registro com este CNPJ.");
            } else if (msg.contains("codigo") || msg.contains("unique_codigo")) {
                throw new BusinessException("Já existe um registro com este Código.");
            }
            throw new BusinessException("Não foi possível salvar pois este registro já existe (Duplicidade detectada).");
        }
    }

    protected void executarComTratamentoDuplicidade(Runnable acao) {
        executarComTratamentoDuplicidade(() -> {
            acao.run();
            return null;
        });
    }

    protected abstract D toDTO(T entity);
}
