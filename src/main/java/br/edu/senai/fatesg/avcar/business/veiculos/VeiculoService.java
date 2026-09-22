package br.edu.senai.fatesg.avcar.business.veiculos;

import br.edu.senai.fatesg.avcar.core.exceptions.EntidadeNaoEncontradaException;
import br.edu.senai.fatesg.avcar.core.services.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("businessVeiculoService")
public class VeiculoService extends GenericService<VeiculoModel, VeiculoDTO, IVeiculoRepository>
        implements IVeiculoService {

    private final VeiculoMapper mapper;
    private final IVeiculoValidation validation;

    public VeiculoService(IVeiculoRepository repository, VeiculoMapper mapper, IVeiculoValidation validation) {
        super(repository, "Veiculo");
        this.mapper = mapper;
        this.validation = validation;
    }

    @Override
    protected VeiculoDTO toDTO(VeiculoModel model) {
        return mapper.toDto(model);
    }

    @Override
    public List<VeiculoDTO> buscarPorPlaca(String placa) {
        return repository.buscarPorPlaca(placa).stream().map(mapper::toDto).toList();
    }

    @Override
    public List<VeiculoDTO> buscarPorCliente(Long clienteId) {
        return repository.buscarPorCliente(clienteId).stream().map(mapper::toDto).toList();
    }

    @Override
    public VeiculoDTO salvar(VeiculoModel model) {
        validation.validar(model);
        return executarComTratamentoDuplicidade(() -> mapper.toDto(repository.salvar(model)));
    }

    @Override
    public void atualizar(VeiculoModel model) {
        repository.buscarPorId(model.getId())
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Veiculo", model.getId()));
        validation.validar(model);
        executarComTratamentoDuplicidade(() -> repository.atualizar(model));
    }

    @Override
    public List<MarcaDTO> listarMarcas() {
        return repository.listarMarcas();
    }

    @Override
    public List<ModeloDTO> listarModelosPorMarca(Long marcaId) {
        return repository.listarModelosPorMarca(marcaId);
    }
}
