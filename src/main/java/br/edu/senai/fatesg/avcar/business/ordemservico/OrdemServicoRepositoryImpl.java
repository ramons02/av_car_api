package br.edu.senai.fatesg.avcar.business.ordemservico;

import br.edu.senai.fatesg.avcar.business.fornecedores.Fornecedor;
import br.edu.senai.fatesg.avcar.business.pecas.ItemPeca;
import br.edu.senai.fatesg.avcar.business.pecas.Peca;
import br.edu.senai.fatesg.avcar.business.servicos.ItemServico;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServico;
import br.edu.senai.fatesg.avcar.business.servicos.Servico;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoExterno;
import br.edu.senai.fatesg.avcar.business.ordemservico.StatusOrdemServico;
import br.edu.senai.fatesg.avcar.business.veiculos.Marca;
import br.edu.senai.fatesg.avcar.business.veiculos.Modelo;
import br.edu.senai.fatesg.avcar.business.veiculos.Veiculo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class OrdemServicoRepositoryImpl implements IOrdemServicoRepository {

    private final JdbcTemplate jdbc;

    public OrdemServicoRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<OrdemServico> mapper = (rs, rowNum) -> {
        Marca marca = new Marca(rs.getLong("idmarca"), rs.getString("nomemarca"), rs.getString("logo_url"));
        Modelo modelo = new Modelo(rs.getLong("idmodelo"), rs.getString("nomemodelo"), marca);
        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getLong("idveiculo"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setChassi(rs.getString("chassi"));
        veiculo.setAnoFabricacao(rs.getInt("anofabricacao"));
        veiculo.setAnoModelo(rs.getInt("anomodelo"));
        veiculo.setCor(rs.getString("cor"));
        veiculo.setQuilometragem(rs.getInt("quilometragem"));
        veiculo.setAcessorios(rs.getString("acessorios"));
        veiculo.setModelo(modelo);

        OrdemServico os = new OrdemServico();
        os.setId(rs.getLong("idordemservico"));
        os.setNumeroOs(rs.getInt("numeroos"));
        os.setVeiculo(veiculo);
        os.setEntradaVeiculo(rs.getDate("entradaveiculo") != null ? rs.getDate("entradaveiculo").toLocalDate() : null);
        os.setDataAbertura(rs.getTimestamp("dataabertura") != null ? rs.getTimestamp("dataabertura").toLocalDateTime() : null);
        if (rs.getTimestamp("datafechamento") != null)
            os.setDataFinalizacao(rs.getTimestamp("datafechamento").toLocalDateTime());
        os.setDefeitoRelatado(rs.getString("defeitorelatado"));
        os.setQuantidadePecas(rs.getInt("quantidadepecas"));
        os.setValorTotalPecas(rs.getDouble("valortotalpecas"));
        os.setValorMaoObra(rs.getDouble("valormaodeobra"));
        os.setValorServicoExterno(rs.getDouble("valorservicoexterno"));
        os.setFormaPagamento(rs.getString("formadepagamento"));
        os.setValorDesconto(rs.getDouble("valordesconto"));
        os.setValorTotal(rs.getDouble("valortotal"));
        os.setGarantia(rs.getInt("garantia"));
        os.setColaboradorNome(rs.getString("colaborador_nome"));
        os.setStatus(StatusOrdemServico.fromRotulo(rs.getString("status")));
        return os;
    };

    private static final String SELECT_SQL = """
        SELECT os.idordemservico, os.numeroos, os.entradaveiculo, os.dataabertura,
               os.datafechamento, os.defeitorelatado, os.quantidadepecas,
               os.valortotalpecas, os.valormaodeobra, os.valorservicoexterno,
               os.valordesconto, os.valortotal, os.formadepagamento, os.garantia, os.status,
               v.idveiculo, v.placa, v.chassi, v.anofabricacao, v.anomodelo,
               v.cor, v.quilometragem, v.acessorios,
               mo.idmodelo, mo.nomemodelo,
               m.idmarca, m.nomemarca, m.logo_url,
               pe.nome as colaborador_nome
        FROM ordemservico os
        JOIN veiculo v ON v.idveiculo = os.idveiculo
        JOIN modelo mo ON mo.idmodelo = v.idmodelo
        JOIN marca m ON m.idmarca = mo.idmarca
        LEFT JOIN colaborador c ON c.idcolaborador = os.idcolaborador
        LEFT JOIN pessoa pe ON pe.idpessoa = c.idpessoa
        """;

    @Override
    public Optional<OrdemServico> buscarPorId(Long id) {
        List<OrdemServico> result = jdbc.query(SELECT_SQL + " WHERE os.idordemservico = ?", mapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<OrdemServico> listarTodos() {
        return jdbc.query(SELECT_SQL + " ORDER BY os.dataabertura DESC", mapper);
    }

    @Override
    public OrdemServico salvar(OrdemServico os) {
        if (os.getStatus() == null)
            os.setStatus(StatusOrdemServico.ABERTA);
        if (os.getDataAbertura() == null)
            os.setDataAbertura(java.time.LocalDateTime.now());
        Integer tempNumero = os.getNumeroOs() != null ? os.getNumeroOs() : 0;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO ordemservico (numeroos, entradaveiculo, dataabertura, defeitorelatado, quantidadepecas, valortotalpecas, valormaodeobra, valorservicoexterno, valordesconto, valortotal, formadepagamento, garantia, status, idveiculo, idcolaborador) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new String[]{"idordemservico"});
            ps.setInt(1, tempNumero);
            if (os.getEntradaVeiculo() != null)
                ps.setDate(2, java.sql.Date.valueOf(os.getEntradaVeiculo()));
            else
                ps.setNull(2, Types.DATE);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(os.getDataAbertura()));
            ps.setString(4, os.getDefeitoRelatado());
            ps.setInt(5, os.getQuantidadePecas());
            ps.setDouble(6, os.getValorTotalPecas());
            ps.setDouble(7, os.getValorMaoObra());
            ps.setDouble(8, os.getValorServicoExterno());
            ps.setDouble(9, os.getValorDesconto());
            ps.setDouble(10, os.getValorTotal());
            ps.setString(11, os.getFormaPagamento());
            ps.setInt(12, os.getGarantia());
            ps.setString(13, os.getStatus().getRotulo());
            ps.setLong(14, os.getVeiculo().getId());
            if (os.getColaboradorId() != null)
                ps.setLong(15, os.getColaboradorId());
            else
                ps.setNull(15, Types.INTEGER);
            return ps;
        }, keyHolder);
        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        os.setId(generatedId);
        os.setNumeroOs(generatedId.intValue());
        jdbc.update("UPDATE ordemservico SET numeroos = ? WHERE idordemservico = ?", os.getNumeroOs(), generatedId);
        return os;
    }

    @Override
    public void atualizar(OrdemServico os) {
        jdbc.update("""
            UPDATE ordemservico SET status=?, datafechamento=?, entradaveiculo=?, defeitorelatado=?,
            quantidadepecas=?, valortotalpecas=?, valormaodeobra=?, valorservicoexterno=?,
            formadepagamento=?, valordesconto=?, valortotal=?, garantia=?
            WHERE idordemservico=?
            """, os.getStatus().getRotulo(), os.getDataFinalizacao(),
            os.getEntradaVeiculo(), os.getDefeitoRelatado(),
            os.getQuantidadePecas(), os.getValorTotalPecas(),
            os.getValorMaoObra(), os.getValorServicoExterno(),
            os.getFormaPagamento(), os.getValorDesconto(),
            os.getValorTotal(), os.getGarantia(), os.getId());
    }

    @Override
    public void deletar(Long id) {
        jdbc.update("DELETE FROM ordemservico WHERE idordemservico = ?", id);
    }

    @Override
    public List<OrdemServico> listarTodosIncluindoInativos() {
        return listarTodos();
    }

    @Override
    public void toggleStatus(Long id) {
        // OS não suporta toggle simples — use os métodos de avanço de status
    }

    @Override
    public List<OrdemServico> buscarPorStatus(String status) {
        return jdbc.query(SELECT_SQL + " WHERE os.status = ? ORDER BY os.dataabertura DESC", mapper, status);
    }

    @Override
    public double somarValorItens(Long osId) {
        Double servicos = jdbc.queryForObject(
            "SELECT COALESCE(SUM(ise.quantidadeitenservico * ise.valorunitarioitenservico), 0) FROM itensservicooficina ise WHERE ise.idordemservico = ?",
            (rs, rowNum) -> {
                Object val = rs.getObject(1);
                if (val == null) return 0.0;
                return ((Number) val).doubleValue();
            }, osId);
        Double pecas = jdbc.queryForObject(
            "SELECT COALESCE(SUM(ip.quantidade * ip.valorunitario), 0) FROM itempecas ip WHERE ip.idordemservico = ?",
            (rs, rowNum) -> {
                Object val = rs.getObject(1);
                if (val == null) return 0.0;
                return ((Number) val).doubleValue();
            }, osId);
        Double externos = jdbc.queryForObject(
            "SELECT COALESCE(SUM(ise.quantidade * ise.valorunitario), 0) FROM itensservicoexterno ise WHERE ise.idordemservico = ?",
            (rs, rowNum) -> {
                Object val = rs.getObject(1);
                if (val == null) return 0.0;
                return ((Number) val).doubleValue();
            }, osId);
        return servicos + pecas + externos;
    }

    @Override
    public ItemServico adicionarItemServico(Long osId, Long servicoId, int quantidade,
                                            double valorUnitario, int garantiaDias, Long colaboradorId) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO itensservicooficina (quantidadeitenservico, valorunitarioitenservico, valortotalitenservico, garantiadias, idcolaborador, idservico, idordemservico) VALUES (?,?,?,?,?,?,?)",
                new String[]{"iditensservicooficina"});
            ps.setInt(1, quantidade);
            ps.setDouble(2, valorUnitario);
            ps.setDouble(3, quantidade * valorUnitario);
            ps.setInt(4, garantiaDias);
            if (colaboradorId != null) ps.setLong(5, colaboradorId);
            else ps.setNull(5, Types.BIGINT);
            ps.setLong(6, servicoId);
            ps.setLong(7, osId);
            return ps;
        }, kh);
        Long id = Objects.requireNonNull(kh.getKey()).longValue();
        Servico servico = new Servico();
        servico.setId(servicoId);
        return new ItemServico(id, null, servico, quantidade, valorUnitario);
    }

    @Override
    public void removerItemServico(Long itemId) {
        jdbc.update("DELETE FROM itensservicooficina WHERE iditensservicooficina = ?", itemId);
    }

    @Override
    public List<ItemServico> listarItensServico(Long osId) {
        return jdbc.query("""
            SELECT ise.*, s.nomeservico as servico_nome,
                   pe.nome as colaborador_nome
            FROM itensservicooficina ise
            JOIN servico s ON s.idservico = ise.idservico
            LEFT JOIN colaborador c ON c.idcolaborador = ise.idcolaborador
            LEFT JOIN pessoa pe ON pe.idpessoa = c.idpessoa
            WHERE ise.idordemservico = ?
            ORDER BY s.nomeservico
            """, (rs, row) -> {
            Servico s = new Servico();
            s.setId(rs.getLong("idservico"));
            s.setNomeServico(rs.getString("servico_nome"));
            ItemServico item = new ItemServico(rs.getLong("iditensservicooficina"), null, s,
                rs.getInt("quantidadeitenservico"), rs.getDouble("valorunitarioitenservico"));
            if (rs.getTimestamp("horainicio") != null)
                item.setHoraInicio(rs.getTimestamp("horainicio").toLocalDateTime());
            if (rs.getTimestamp("horafim") != null)
                item.setHoraFim(rs.getTimestamp("horafim").toLocalDateTime());
            item.setStatus(rs.getString("statusitenservico"));
            item.setColaboradorNome(rs.getString("colaborador_nome"));
            return item;
        }, osId);
    }

    @Override
    public ItemPeca adicionarItemPeca(Long osId, Long pecaId, int quantidade,
                                      double valorUnitario, int garantiaDias) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO itempecas (quantidade, valorunitario, valortotal, garantia, idpecas, idordemservico) VALUES (?,?,?,?,?,?)",
                new String[]{"iditempecas"});
            ps.setInt(1, quantidade);
            ps.setDouble(2, valorUnitario);
            ps.setDouble(3, quantidade * valorUnitario);
            ps.setInt(4, garantiaDias);
            ps.setLong(5, pecaId);
            ps.setLong(6, osId);
            return ps;
        }, kh);
        Long id = Objects.requireNonNull(kh.getKey()).longValue();
        Peca peca = new Peca();
        peca.setId(pecaId);
        return new ItemPeca(id, peca, null, quantidade, valorUnitario);
    }

    @Override
    public void removerItemPeca(Long itemId) {
        jdbc.update("DELETE FROM itempecas WHERE iditempecas = ?", itemId);
    }

    @Override
    public List<ItemPeca> listarItensPeca(Long osId) {
        return jdbc.query("""
            SELECT ip.*, p.nomepeca as peca_nome
            FROM itempecas ip
            JOIN pecas p ON p.idpecas = ip.idpecas
            WHERE ip.idordemservico = ?
            ORDER BY p.nomepeca
            """, (rs, row) -> {
            Peca p = new Peca();
            p.setId(rs.getLong("idpecas"));
            p.setNome(rs.getString("peca_nome"));
            ItemPeca item = new ItemPeca(rs.getLong("iditempecas"), p, null,
                rs.getInt("quantidade"), rs.getDouble("valorunitario"));
            item.setValorTotal(rs.getDouble("valortotal"));
            item.setGarantia(rs.getInt("garantia"));
            return item;
        }, osId);
    }

    @Override
    public ServicoExterno adicionarServicoExterno(Long osId, Long parceiroId, String descricao,
                                                  double valor, int garantiaDias) {
        // 1. Inserir em servicoexterno
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            var ps = con.prepareStatement(
                "INSERT INTO servicoexterno (descricao, valor, prazo, garantiadias, idparceiro) VALUES (?,?,CURRENT_DATE,?,?)",
                new String[]{"idservicoexterno"});
            ps.setString(1, descricao);
            ps.setDouble(2, valor);
            ps.setInt(3, garantiaDias);
            ps.setLong(4, parceiroId);
            return ps;
        }, keyHolder);
        Long seId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO itensservicoexterno (quantidade, valorunitario, valortotal, garantiadias, idservicoexterno, idordemservico) VALUES (?,?,?,?,?,?)",
                new String[]{"iditensservicoexterno"});
            ps.setInt(1, 1);
            ps.setDouble(2, valor);
            ps.setDouble(3, valor);
            ps.setInt(4, garantiaDias);
            ps.setLong(5, seId);
            ps.setLong(6, osId);
            return ps;
        }, kh);
        Long id = Objects.requireNonNull(kh.getKey()).longValue();
        br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExterno f = new br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExterno();
        f.setId(parceiroId);
        return new ServicoExterno(id, null, f, descricao, valor, garantiaDias);
    }

    @Override
    public void removerServicoExterno(Long itemId) {
        jdbc.update("DELETE FROM itensservicoexterno WHERE iditensservicoexterno = ?", itemId);
    }

    @Override
    public List<ServicoExterno> listarServicosExternos(Long osId) {
        return jdbc.query("""
            SELECT ise.*, se.descricao as servico_descricao, f.nome as parceiro_nome
            FROM itensservicoexterno ise
            JOIN servicoexterno se ON se.idservicoexterno = ise.idservicoexterno
            JOIN parceiro_externo f ON f.id = se.idparceiro
            WHERE ise.idordemservico = ?
            ORDER BY ise.iditensservicoexterno
            """, (rs, row) -> {
            br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExterno f = new br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExterno();
            f.setId(rs.getLong("idservicoexterno"));
            f.setNome(rs.getString("parceiro_nome"));
            return new ServicoExterno(rs.getLong("iditensservicoexterno"), null, f,
                rs.getString("servico_descricao"),
                rs.getDouble("valorunitario"),
                rs.getInt("garantiadias"));
        }, osId);
    }
}
