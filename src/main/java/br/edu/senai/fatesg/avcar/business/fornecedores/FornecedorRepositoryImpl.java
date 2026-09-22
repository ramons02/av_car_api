package br.edu.senai.fatesg.avcar.business.fornecedores;

import br.edu.senai.fatesg.avcar.core.repositories.AbstractRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("businessFornecedorRepository")
public class FornecedorRepositoryImpl extends AbstractRepository<FornecedorModel> implements IFornecedorRepository {

    private final RowMapper<FornecedorModel> mapper = (rs, rowNum) -> {
        FornecedorModel m = new FornecedorModel();
        m.setId(rs.getLong("idfornecedor"));
        m.setRazaoSocial(rs.getString("razaosocial"));
        m.setCnpj(rs.getString("cnpj"));
        m.setDdi(rs.getString("ddi"));
        m.setDdd(rs.getString("ddd"));
        m.setNumeroFornecedor(rs.getString("numerofornecedor"));
        m.setEmail(rs.getString("email"));
        m.setEnderecoFornecedor(rs.getString("enderecofornecedor"));
        m.setBairroFornecedor(rs.getString("bairrofornecedor"));
        m.setCidadeFornecedor(rs.getString("cidadefornecedor"));
        m.setEstadoFornecedor(rs.getString("estadofornecedor"));
        m.setCepFornecedor(rs.getInt("cepfornecedor"));
        m.setAtivo(rs.getBoolean("ativo"));
        return m;
    };

    public FornecedorRepositoryImpl(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected String getTableName() { return "fornecedor"; }

    @Override
    protected RowMapper<FornecedorModel> getRowMapper() { return mapper; }

    @Override
    public Optional<FornecedorModel> buscarPorId(Long id) {
        List<FornecedorModel> result = jdbc.query(getSelectSql() + " WHERE idfornecedor = ?", mapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<FornecedorModel> listarTodos() {
        return jdbc.query(getSelectSql() + " WHERE ativo = true ORDER BY razaosocial", mapper);
    }

    @Override
    public List<FornecedorModel> listarTodosIncluindoInativos() {
        return jdbc.query(getSelectSql() + " ORDER BY razaosocial", mapper);
    }

    @Override
    public FornecedorModel salvar(FornecedorModel model) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            var ps = connection.prepareStatement("""
                INSERT INTO fornecedor (razaosocial, cnpj, ddi, ddd, numerofornecedor, email,
                    enderecofornecedor, bairrofornecedor, cidadefornecedor, estadofornecedor, cepfornecedor)
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
                """, new String[]{"idfornecedor"});
            ps.setString(1, model.getRazaoSocial());
            ps.setString(2, model.getCnpj());
            ps.setString(3, model.getDdi());
            ps.setString(4, model.getDdd());
            ps.setString(5, model.getNumeroFornecedor());
            ps.setString(6, model.getEmail());
            ps.setString(7, model.getEnderecoFornecedor());
            ps.setString(8, model.getBairroFornecedor());
            ps.setString(9, model.getCidadeFornecedor());
            ps.setString(10, model.getEstadoFornecedor());
            ps.setInt(11, model.getCepFornecedor());
            return ps;
        }, kh);
        model.setId(kh.getKey().longValue());
        return model;
    }

    @Override
    public void atualizar(FornecedorModel model) {
        jdbc.update("""
            UPDATE fornecedor SET razaosocial=?, cnpj=?, ddi=?, ddd=?, numerofornecedor=?, email=?,
                enderecofornecedor=?, bairrofornecedor=?, cidadefornecedor=?, estadofornecedor=?, cepfornecedor=?
            WHERE idfornecedor=?
            """,
            model.getRazaoSocial(), model.getCnpj(), model.getDdi(), model.getDdd(),
            model.getNumeroFornecedor(), model.getEmail(), model.getEnderecoFornecedor(),
            model.getBairroFornecedor(), model.getCidadeFornecedor(), model.getEstadoFornecedor(),
            model.getCepFornecedor(), model.getId());
    }

    @Override
    public void deletar(Long id) {
        jdbc.update("UPDATE fornecedor SET ativo = false WHERE idfornecedor = ?", id);
    }

    @Override
    public void toggleStatus(Long id) {
        jdbc.update("UPDATE fornecedor SET ativo = NOT ativo WHERE idfornecedor = ?", id);
    }

    @Override
    public List<FornecedorModel> buscarPorNome(String nome) {
        return jdbc.query(getSelectSql() + " WHERE razaosocial ILIKE ? ORDER BY razaosocial",
            mapper, "%" + nome + "%");
    }
}
