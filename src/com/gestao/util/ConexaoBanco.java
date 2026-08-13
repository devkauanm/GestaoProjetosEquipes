package com.gestao.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Classe responsável por criar e gerenciar a conexão com o banco de dados
// As credenciais são lidas do arquivo config.properties — nunca ficam expostas no código
public class ConexaoBanco {

    private static String URL;
    private static String USUARIO;
    private static String SENHA;

    // Bloco estático — executa uma única vez quando a classe é carregada
    // Lê as credenciais do arquivo config.properties
    static {
        try {
            Properties props = new Properties();

            // Busca o arquivo config.properties dentro da pasta src
            InputStream input = ConexaoBanco.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (input == null) {
                System.out.println("Arquivo config.properties não encontrado!");
            } else {
                props.load(input);
                String host = props.getProperty("db.host");
                String porta = props.getProperty("db.porta");
                String banco = props.getProperty("db.banco");
                USUARIO = props.getProperty("db.usuario");
                SENHA = props.getProperty("db.senha");

                URL = "jdbc:mysql://" + host + ":" + porta + "/" + banco
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar configurações: " + e.getMessage());
        }
    }

    // Retorna uma conexão ativa com o banco de dados
    public static Connection getConexao() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco: " + e.getMessage());
            return null;
        }
    }

    // Fecha a conexão com segurança
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