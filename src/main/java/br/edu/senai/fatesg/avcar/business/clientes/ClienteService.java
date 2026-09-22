package br.edu.senai.fatesg.avcar.business.clientes;

import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("businessClienteService")
public class ClienteService extends GenericService<ClienteModel, ClienteDTO, IClienteRepository>
        implements IClienteService {

    private final ClienteMapper mapper;
    private final IClienteValidation validation;

    public ClienteService(IClienteRepository repository, ClienteMapper mapper, IClienteValidation validation) {
        super(repository, "Cliente");
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected ClienteDTO toDTO(ClienteModel model) {
        return mapper.toDto(model);
    }

    @Override
    public List<ClienteDTO> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome).stream().map(mapper::toDto).toList();
    }

    @Override
    public ClienteDTO salvar(ClienteModel model) {
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    public void atualizar(ClienteModel model) {
        validation.validar(model);
        executarComTratamentoDuplicidade(() -> repository.atualizar(model));
    }

    public ClienteDTO atualizarPorRequest(Long id, ClienteController.AtualizarClienteRequest req) {
        ClienteModel atual = repository.buscarPorId(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente", id));

        if (req.nome() != null) atual.setNome(req.nome());
        if (req.endereco() != null) atual.setEndereco(req.endereco());
        if (req.bairro() != null) atual.setBairro(req.bairro());
        if (req.cidade() != null) atual.setCidade(req.cidade());
        if (req.estado() != null) atual.setEstado(req.estado());
        if (req.cep() != null) atual.setCep(req.cep());
        if (req.telefone() != null) atual.setTelefone(req.telefone());
        if (req.email() != null) atual.setEmail(req.email());
        if (req.inscricaoEstadual() != null) atual.setInscricaoEstadual(req.inscricaoEstadual());
        if (req.rg() != null) atual.setRg(req.rg());
        if (req.razaoSocial() != null) atual.setRazaoSocial(req.razaoSocial());
        if (req.observacoes() != null) atual.setObservacoes(req.observacoes());

        // Documento pode vir como cpf/cnpj explícito ou como campo genérico "documento"
        if ("PF".equals(atual.getTipo())) {
            String cpf = req.cpf() != null ? req.cpf() : req.documento();
            if (cpf != null) atual.setCpf(cpf);
        } else if ("PJ".equals(atual.getTipo())) {
            String cnpj = req.cnpj() != null ? req.cnpj() : req.documento();
            if (cnpj != null) atual.setCnpj(cnpj);
        }

        validation.validar(atual);
        executarComTratamentoDuplicidade(() -> repository.atualizar(atual));
        return mapper.toDto(repository.buscarPorId(id).orElseThrow());
    }
}
