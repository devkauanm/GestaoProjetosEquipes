package com.gestao.dao;

import com.gestao.model.Usuario;
import com.gestao.util.ConexaoBanco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe responsável por todas as operações do banco relacionadas a usuários
// CRUD completo: Create, Read, Update, Delete + autenticação
public class UsuarioDAO {

    // =====================================
    // INSERIR — salva um novo usuário no banco
    // =====================================
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome_completo, cpf, email, cargo, login, senha, perfil) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            // Os "?" são preenchidos em ordem com os dados do objeto usuario
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getCargo());
            stmt.setString(5, usuario.getLogin());
            stmt.setString(6, usuario.getSenha());
            stmt.setString(7, usuario.getPerfil());
            stmt.executeUpdate(); // executa o INSERT
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuário: " + e.getMessage());
            return false;
        } finally {
            // O bloco finally sempre executa, garantindo que a conexão seja fechada
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // LISTAR TODOS — retorna todos os usuários do banco
    // =====================================
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(); // executa o SELECT e retorna os resultados

            // Percorre cada linha retornada pelo banco
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNomeCompleto(rs.getString("nome_completo"));
                u.setCpf(rs.getString("cpf"));
                u.setEmail(rs.getString("email"));
                u.setCargo(rs.getString("cargo"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha"));
                u.setPerfil(rs.getString("perfil"));
                usuarios.add(u); // adiciona o usuário na lista
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return usuarios;
    }

    // =====================================
    // BUSCAR POR ID — retorna um usuário específico
    // =====================================
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNomeCompleto(rs.getString("nome_completo"));
                u.setCpf(rs.getString("cpf"));
                u.setEmail(rs.getString("email"));
                u.setCargo(rs.getString("cargo"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha"));
                u.setPerfil(rs.getString("perfil"));
                return u;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return null; // retorna null se não encontrar
    }

    // =====================================
    // ATUALIZAR — edita os dados de um usuário existente
    // =====================================
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome_completo=?, cpf=?, email=?, cargo=?, login=?, senha=?, perfil=? WHERE id=?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getCargo());
            stmt.setString(5, usuario.getLogin());
            stmt.setString(6, usuario.getSenha());
            stmt.setString(7, usuario.getPerfil());
            stmt.setInt(8, usuario.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // DELETAR — remove um usuário do banco
    // =====================================
    public boolean deletar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar usuário: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // AUTENTICAR — verifica login e senha para o acesso ao sistema
    // =====================================
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Se encontrou um usuário com esse login e senha, retorna ele
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNomeCompleto(rs.getString("nome_completo"));
                u.setCpf(rs.getString("cpf"));
                u.setEmail(rs.getString("email"));
                u.setCargo(rs.getString("cargo"));
                u.setLogin(rs.getString("log