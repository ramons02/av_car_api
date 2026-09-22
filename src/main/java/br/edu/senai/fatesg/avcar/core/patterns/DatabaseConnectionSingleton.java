package br.edu.senai.fatesg.avcar.core.patterns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

// PADRÃO SINGLETON: Gerencia uma única conexão com o banco de dados JDBC PostgreSQL para
// ser reaproveitada em todo o cliente Swing. Ao invés de o sistema abrir e fechar conexões
// repetidas vezes, todos os repositórios requisitam a instância estática central, prevenindo
// o esgotamento do pool e potenciais vazamentos de conexões.
public final class DatabaseConnectionSingleton {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionSingleton.class.getName());
    private static DatabaseConnectionSingleton instancia;
    private Connection conexao;

    private static final String URL = "jdbc:postgresql://localhost:5432/avcar";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "postgres";

    private DatabaseConnectionSingleton() {
    }

    public static synchronized DatabaseConnectionSingleton getInstance() {
        if (instancia == null) {
            instancia = new DatabaseConnectionSingleton();
        }
        return instancia;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
        }
        return conexao;
    }

    public synchronized void closeConnection() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erro ao fechar conexão", e);
            }
        }
    }

    public synchronized boolean isConnected() {
        try {
            return conexao != null && !conexao.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
