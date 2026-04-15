package com.gestao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Classe responsável por criar e gerenciar a conexão com o banco de dados
// É usada por todas as classes DAO para se comunicar com o MySQL
public class ConexaoBanco {

    // Credenciais do banco no Railway — não altere esses valores
    private static final String HOST = "mainline.proxy.rlwy.net";
    private static final String PORTA = "11859";
    private static final String BANCO = "railway";
    private static final String USUARIO = "root";
    private static final String SENHA = "QvApaXEaRdLVWtQnOlorwScRkeCKEIYs";

    // Monta a URL de conexão no formato que o JDBC (Java Database Connectivity) entende
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORTA + "/" + BANCO
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";

    // Retorna uma conexão ativa com o banco de dados
    // É chamado toda vez que precisamos executar um SQL
    public static Connection getConexao() {
        try {
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            return conexao;
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco: " + e.getMessage());
            return null;
        }
    }

    // Fecha a conexão com o banco de dados com segurança
    // Sempre deve ser chamado após terminar de usar a conexão
    public static void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
