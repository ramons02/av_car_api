package br.edu.senai.fatesg.avcar.business.veiculos;

import br.edu.senai.fatesg.avcar.core.repositories.AbstractRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository("businessVeiculoRepository")
public class VeiculoRepositoryImpl extends AbstractRepository<VeiculoModel> implements IVeiculoRepository {

    private static final String SQL_JOIN = """
        SELECT v.idveiculo, v.placa, v.chassi, v.anofabricacao, v.anomodelo,
               v.cor, v.quilometragem, v.acessorios, v.ativo,
               m.idmarca, m.nomemarca, m.logo_url,
               mo.idmodelo, mo.nomemodelo,
               c.idcliente, p.nome AS clientenome
        FROM veiculo v
        JOIN modelo mo ON v.idmodelo = mo.idmodelo
        JOIN marca m ON mo.idmarca = m.idmarca
        LEFT JOIN historicocliente hc ON hc.idveiculo = v.idveiculo AND hc.datafim IS NULL
        LEFT JOIN cliente c ON c.idcliente = hc.idcliente
        LEFT JOIN pessoafisica pf ON pf.idpessoafisica = c.idpessoafisica
        LEFT JOIN pessoajuridica pj ON pj.idpessoajuridica = c.idpessoajuridica
        LEFT JOIN pessoa p ON p.idpessoa = COALESCE(pf.idpessoa, pj.idpessoa)
        """;

    private final RowMapper<VeiculoModel> mapper = (rs, rowNum) -> {
        VeiculoModel m = new VeiculoModel();
        m.setId(rs.getLong("idveiculo"));
        m.setPlaca(rs.getString("placa"));
        m.setChassi(rs.getString("chassi"));
        m.setAnoFabricacao(rs.getInt("anofabricacao"));
        m.setAnoModelo(rs.getInt("anomodelo"));
        m.setCor(rs.getString("cor"));
        m.setQuilometragem(rs.getInt("quilometragem"));
        m.setAcessorios(rs.getString("acessorios"));
        m.setModeloId(rs.getLong("idmodelo"));
        m.setModeloNome(rs.getString("nomemodelo"));
        m.setMarcaId(rs.getLong("idmarca"));
        m.setMarcaNome(rs.getString("nomemarca"));
        m.setMarcaLogoUrl(rs.getString("logo_url"));
        long clienteId = rs.getLong("idcliente");
        if (!rs.wasNull()) m.setClienteId(clienteId);
        m.setClienteNome(rs.getString("clientenome"));
        m.setAtivo(rs.getBoolean("ativo"));
        return m;
    };

    public VeiculoRepositoryImpl(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected String getTableName() { return "veiculo"; }

    @Override
    protected RowMapper<VeiculoModel> getRowMapper() { return mapper; }

    @Override
    public Optional<VeiculoModel> buscarPorId(Long id) {
        List<VeiculoModel> result = jdbc.query(SQL_JOIN + " WHERE v.idveiculo = ?", mapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<VeiculoModel> listarTodos() {
        return jdbc.query(SQL_JOIN + " WHERE v.ativo = TRUE ORDER BY v.placa", mapper);
    }

    @Override
    public List<VeiculoModel> listarTodosIncluindoInativos() {
        return jdbc.query(SQL_JOIN + " ORDER BY v.placa", mapper);
    }

    @Override
    public VeiculoModel salvar(VeiculoModel model) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO veiculo (placa, chassi, anofabricacao, anomodelo, cor, quilometragem, acessorios, idmodelo) VALUES (?,?,?,?,?,?,?,?)",
                new String[]{"idveiculo"});
            ps.setString(1, model.getPlaca());
            ps.setString(2, model.getChassi());
            ps.setInt(3, model.getAnoFabricacao());
            ps.setInt(4, model.getAnoModelo());
            ps.setString(5, model.getCor());
            ps.setInt(6, model.getQuilometragem());
            ps.setString(7, model.getAcessorios());
            ps.setLong(8, model.getModeloId());
            return ps;
        }, keyHolder);
        model.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        
        // NOVO: Se foi selecionado um cliente, insere na tabela de histórico
        if (model.getClienteId() != null) {
            jdbc.update("INSERT INTO historicocliente (idcliente, idveiculo, datainicio) VALUES (?, ?, CURRENT_TIMESTAMP)", 
                        model.getClienteId(), model.getId());
        }
        
        return model;
    }

    @Override
    public void atualizar(VeiculoModel model) {
        jdbc.update(
            "UPDATE veiculo SET placa=?, chassi=?, anofabricacao=?, anomodelo=?, cor=?, quilometragem=?, acessorios=?, idmodelo=? WHERE idveiculo=?",
            model.getPlaca(), model.getChassi(), model.getAnoFabricacao(), model.getAnoModelo(),
            model.getCor(), model.getQuilometragem(), model.getAcessorios(), model.getModeloId(), model.getId());
            
        // NOVO: Gerencia a mudança de dono no Histórico do Cliente
        if (model.getClienteId() != null) {
            // Verifica quem é o cliente atual ativo
            java.util.List<Long> donoAtual = jdbc.queryForList(
                "SELECT idcliente FROM historicocliente WHERE idveiculo = ? AND datafim IS NULL", 
                Long.class, model.getId());
                
            // Se não tem dono, ou o dono atual é diferente do novo dono selecionado
            if (donoAtual.isEmpty() || !donoAtual.get(0).equals(model.getClienteId())) {
                // Encerra o vínculo antigo (se existir)
                jdbc.update("UPDATE historicocliente SET datafim = CURRENT_TIMESTAMP WHERE idveiculo = ? AND datafim IS NULL", model.getId());
                // Inicia o novo vínculo
                jdbc.update("INSERT INTO historicocliente (idcliente, idveiculo, datainicio) VALUES (?, ?, CURRENT_TIMESTAMP)", 
                            model.getClienteId(), model.getId());
            }
        } else {
            // Se desmarcou o cliente, apenas encerra o vínculo atual
            jdbc.update("UPDATE historicocliente SET datafim = CURRENT_TIMESTAMP WHERE idveiculo = ? AND datafim IS NULL", model.getId());
        }
    }

    @Override
    public void deletar(Long id) {
        jdbc.update("DELETE FROM veiculo WHERE idveiculo = ?", id);
    }

    @Override
    public void toggleStatus(Long id) {
        jdbc.update("UPDATE veiculo SET ativo = NOT ativo WHERE idveiculo = ?", id);
    }

    @Override
    public List<VeiculoModel> buscarPorPlaca(String placa) {
        return jdbc.query(SQL_JOIN + " WHERE v.placa ILIKE ? ORDER BY v.placa", mapper, "%" + placa + "%");
    }

    @Override
    public List<VeiculoModel> buscarPorCliente(Long clienteId) {
        return jdbc.query(SQL_JOIN + """
            JOIN historicocliente hcv ON hcv.idveiculo = v.idveiculo
            WHERE hcv.idcliente = ? AND hcv.datafim IS NULL
            ORDER BY v.placa
            """, mapper, clienteId);
    }

    @Override
    public List<MarcaDTO> listarMarcas() {
        return jdbc.query("SELECT idmarca, nomemarca, logo_url FROM marca ORDER BY nomemarca",
            (rs, i) -> new MarcaDTO(rs.getLong("idmarca"), rs.getString("nomemarca"), rs.getString("logo_url")));
    }

    @Override
    public List<ModeloDTO> listarModelosPorMarca(Long marcaId) {
        return jdbc.query("""
            SELECT mo.idmodelo, mo.nomemodelo, m.idmarca, m.nomemarca
            FROM modelo mo JOIN marca m ON mo.idmarca = m.idmarca
            WHERE mo.idmarca = ? ORDER BY mo.nomemodelo
            """, (rs, i) -> new ModeloDTO(
                rs.getLong("idmodelo"), rs.getString("nomemodelo"),
                rs.getLong("idmarca"), rs.getString("nomemarca")
            ), marcaId);
    }
}
