package com.gestao.util;

import java.sql.Connection;

// Classe temporária apenas para testar se a conexão com o banco está funcionando
// Pode ser deletada depois que confirmarmos que tudo está ok
public class TesteConexao {

    public static void main(String[] args) {
        System.out.println("Tentando conectar ao banco...");

        Connection conexao = ConexaoBanco.getConexao();

        if (conexao != null) {
            System.out.println("Conexão bem sucedida!");
            ConexaoBanco.fecharConexao(conexao);
        } else {
            System.out.println("Falha na conexão!");
        }
    }
}
