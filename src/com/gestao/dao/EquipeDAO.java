package com.gestao.dao;

import com.gestao.model.Equipe;
import com.gestao.model.Usuario;
import com.gestao.util.ConexaoBanco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe responsável por todas as operações do banco relacionadas a equipes
public class EquipeDAO {

    // =====================================
    // INSERIR — salva uma nova equipe no banco
    // =====================================
    public boolean inserir(Equipe equipe) {
        String sql = "INSERT INTO equipes (nome, descricao) VALUES (?, ?)";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            // RETURN_GENERATED_KEYS permite recuperar o ID gerado automaticamente pelo banco
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.executeUpdate();

            // Recupera o ID gerado e salva nos membros
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idGerado = rs.getInt(1);
                equipe.setId(idGerado);
                // Insere os membros da equipe na tabela intermediária
                inserirMembros(conexao, idGerado, equipe.getMembros());
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir equipe: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // Insere os membros na tabela intermediária equipe_usuarios
    private void inserirMembros(Connection conexao, int idEquipe, List<Usuario> membros) throws SQLException {
        String sql = "INSERT INTO equipe_usuarios (id_equipe, id_usuario) VALUES (?, ?)";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        for (Usuario membro : membros) {
            stmt.setInt(1, idEquipe);
            stmt.setInt(2, membro.getId());
            stmt.executeUpdate();
        }
    }

    // =====================================
    // LISTAR TODOS — retorna todas as equipes
    // =====================================
    public List<Equipe> listarTodos() {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT * FROM equipes";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Equipe e = new Equipe();
                e.setId(rs.getInt("id"));
                e.setNome(rs.getString("nome"));
                e.setDescricao(rs.getString("descricao"));
                // Busca os membros dessa equipe separadamente
                e.setMembros(buscarMembros(conexao, e.getId()));
                equipes.add(e);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar equipes: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return equipes;
    }

    // =====================================
    // BUSCAR POR ID — retorna uma equipe específica com seus membros
    // =====================================
    public Equipe buscarPorId(int id) {
        String sql = "SELECT * FROM equipes WHERE id = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Equipe e = new Equipe();
                e.setId(rs.getInt("id"));
                e.setNome(rs.getString("nome"));
                e.setDescricao(rs.getString("descricao"));
                e.setMembros(buscarMembros(conexao, e.getId()));
                return e;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar equipe: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return null;
    }

    // Busca os membros de uma equipe na tabela intermediária equipe_usuarios
    private List<Usuario> buscarMembros(Connection conexao, int idEquipe) {
        List<Usuario> membros = new ArrayList<>();
        String sql = "SELECT u.* FROM usuarios u " +
                "INNER JOIN equipe_usuarios eu ON u.id = eu.id_usuario " +
                "WHERE eu.id_equipe = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idEquipe);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNomeCompleto(rs.getString("nome_completo"));
                u.setCpf(rs.getString("cpf"));
                u.setEmail(rs.getString("email"));
                u.setCargo(rs.getString("cargo"));
                u.setLogin(rs.getString("login"));
                u.setPerfil(rs.getString("perfil"));
                membros.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar membros: " + e.getMessage());
        }
        return membros;
    }

    // =====================================
    // ATUALIZAR — edita os dados de uma equipe
    // =====================================
    public boolean atualizar(Equipe equipe) {
        String sql = "UPDATE equipes SET nome=?, descricao=? WHERE id=?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.setInt(3, equipe.getId());
            stmt.executeUpdate();

            // Remove os membros antigos e insere os novos
            removerMembros(conexao, equipe.getId());
            inserirMembros(conexao, equipe.getId(), equipe.getMembros());
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar equipe: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // Remove todos os membros de uma equipe da tabela intermediária
    private void removerMembros(Connection conexao, int idEquipe) throws SQLException {
        String sql = "DELETE FROM equipe_usuarios WHERE id_equipe = ?";
        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setInt(1, idEquipe);
        stmt.executeUpdate();
    }

    // =====================================
    // DELETAR — remove uma equipe e seus vínculos
    // =====================================
    public boolean deletar(int id) {
        Connection conexao = ConexaoBanco.getConexao();
        try {
            // Primeiro remove os vínculos da tabela intermediária
            removerMembros(conexao, id);

            // Depois remove a equipe
            String sql = "DELETE FROM equipes WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar equipe: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // VINCULAR EQUIPE A PROJETO
    // =====================================
    public boolean vincularProjeto(int idEquipe, int idProjeto) {
        String sql = "INSERT INTO equipe_projetos (id_equipe, id_projeto) VALUES (?, ?)";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idEquipe);
            stmt.setInt(2, idProjeto);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao vincular equipe ao projeto: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // DESVINCULAR EQUIPE DE PROJETO
    // =====================================
    public boolean desvincularProjeto(int idEquipe, int idProjeto) {
        String sql = "DELETE FROM equipe_projetos WHERE id_equipe = ? AND id_projeto = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idEquipe);
            stmt.setInt(2, idProjeto);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao desvincular equipe do projeto: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // LISTAR EQUIPES DE UM PROJETO
    // =====================================
    public List<Equipe> listarPorProjeto(int idProjeto) {
        List<Equipe> equipes = new ArrayList<>();
        String sql = "SELECT e.* FROM equipes e " +
                "INNER JOIN equipe_projetos ep ON e.id = ep.id_equipe " +
                "WHERE ep.id_projeto = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idProjeto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Equipe e = new Equipe();
                e.setId(rs.getInt("id"));
                e.setNome(rs.getString("nome"));
                e.setDescricao(rs.getString("descricao"));
                e.setMembros(buscarMembros(conexao, e.getId()));
                equipes.add(e);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar equipes do projeto: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return equipes;
    }
}