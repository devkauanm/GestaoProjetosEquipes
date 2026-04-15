package com.gestao.dao;

import com.gestao.model.Projeto;
import com.gestao.model.Usuario;
import com.gestao.util.ConexaoBanco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe responsável por todas as operações do banco relacionadas a projetos
public class ProjetoDAO {

    // =====================================
    // INSERIR — salva um novo projeto no banco
    // =====================================
    public boolean inserir(Projeto projeto) {
        String sql = "INSERT INTO projetos (nome, descricao, data_inicio, data_termino_prevista, status, id_gerente) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            // Converte LocalDate para Date do SQL
            stmt.setDate(3, projeto.getDataInicio() != null ? Date.valueOf(projeto.getDataInicio()) : null);
            stmt.setDate(4, projeto.getDataTerminoPrevista() != null ? Date.valueOf(projeto.getDataTerminoPrevista()) : null);
            stmt.setString(5, projeto.getStatus());
            // Salva apenas o ID do gerente, não o objeto inteiro
            stmt.setInt(6, projeto.getGerente().getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir projeto: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // LISTAR TODOS — retorna todos os projetos com o gerente vinculado
    // =====================================
    public List<Projeto> listarTodos() {
        List<Projeto> projetos = new ArrayList<>();

        // JOIN com a tabela usuarios para trazer os dados do gerente junto
        String sql = "SELECT p.*, u.nome_completo, u.perfil FROM projetos p " +
                "LEFT JOIN usuarios u ON p.id_gerente = u.id";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Projeto p = new Projeto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                // Converte Date do SQL para LocalDate do Java
                p.setDataInicio(rs.getDate("data_inicio") != null ? rs.getDate("data_inicio").toLocalDate() : null);
                p.setDataTerminoPrevista(rs.getDate("data_termino_prevista") != null ? rs.getDate("data_termino_prevista").toLocalDate() : null);
                p.setStatus(rs.getString("status"));

                // Monta o objeto gerente com os dados do JOIN
                Usuario gerente = new Usuario();
                gerente.setId(rs.getInt("id_gerente"));
                gerente.setNomeCompleto(rs.getString("nome_completo"));
                gerente.setPerfil(rs.getString("perfil"));
                p.setGerente(gerente);

                projetos.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar projetos: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return projetos;
    }

    // =====================================
    // BUSCAR POR ID — retorna um projeto específico
    // =====================================
    public Projeto buscarPorId(int id) {
        String sql = "SELECT p.*, u.nome_completo, u.perfil FROM projetos p " +
                "LEFT JOIN usuarios u ON p.id_gerente = u.id WHERE p.id = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Projeto p = new Projeto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setDataInicio(rs.getDate("data_inicio") != null ? rs.getDate("data_inicio").toLocalDate() : null);
                p.setDataTerminoPrevista(rs.getDate("data_termino_prevista") != null ? rs.getDate("data_termino_prevista").toLocalDate() : null);
                p.setStatus(rs.getString("status"));

                Usuario gerente = new Usuario();
                gerente.setId(rs.getInt("id_gerente"));
                gerente.setNomeCompleto(rs.getString("nome_completo"));
                gerente.setPerfil(rs.getString("perfil"));
                p.setGerente(gerente);

                return p;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar projeto: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return null;
    }

    // =====================================
    // LISTAR EM ATRASO — retorna projetos que passaram do prazo
    // =====================================
    public List<Projeto> listarEmAtraso() {
        List<Projeto> projetos = new ArrayList<>();

        // Busca projetos onde a data prevista já passou e ainda não foram concluídos ou cancelados
        String sql = "SELECT p.*, u.nome_completo, u.perfil FROM projetos p " +
                "LEFT JOIN usuarios u ON p.id_gerente = u.id " +
                "WHERE p.data_termino_prevista < CURDATE() " +
                "AND p.status NOT IN ('concluido', 'cancelado')";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Projeto p = new Projeto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setDataInicio(rs.getDate("data_inicio") != null ? rs.getDate("data_inicio").toLocalDate() : null);
                p.setDataTerminoPrevista(rs.getDate("data_termino_prevista") != null ? rs.getDate("data_termino_prevista").toLocalDate() : null);
                p.setStatus(rs.getString("status"));

                Usuario gerente = new Usuario();
                gerente.setId(rs.getInt("id_gerente"));
                gerente.setNomeCompleto(rs.getString("nome_completo"));
                gerente.setPerfil(rs.getString("perfil"));
                p.setGerente(gerente);

                projetos.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar projetos em atraso: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return projetos;
    }

    // =====================================
    // ATUALIZAR — edita os dados de um projeto existente
    // =====================================
    public boolean atualizar(Projeto projeto) {
        String sql = "UPDATE projetos SET nome=?, descricao=?, data_inicio=?, data_termino_prevista=?, status=?, id_gerente=? WHERE id=?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, projeto.getDataInicio() != null ? Date.valueOf(projeto.getDataInicio()) : null);
            stmt.setDate(4, projeto.getDataTerminoPrevista() != null ? Date.valueOf(projeto.getDataTerminoPrevista()) : null);
            stmt.setString(5, projeto.getStatus());
            stmt.setInt(6, projeto.getGerente().getId());
            stmt.setInt(7, projeto.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar projeto: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // DELETAR — remove um projeto do banco
    // =====================================
    public boolean deletar(int id) {
        String sql = "DELETE FROM projetos WHERE id = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar projeto: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }
}