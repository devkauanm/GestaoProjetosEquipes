package com.gestao.dao;

import com.gestao.model.Projeto;
import com.gestao.model.Tarefa;
import com.gestao.model.Usuario;
import com.gestao.util.ConexaoBanco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe responsável por todas as operações do banco relacionadas a tarefas
public class TarefaDAO {

    // =====================================
    // INSERIR — salva uma nova tarefa no banco
    // =====================================
    public boolean inserir(Tarefa tarefa) {
        String sql = "INSERT INTO tarefas (titulo, descricao, id_projeto, id_responsavel, status, " +
                "data_inicio_prevista, data_fim_prevista) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjeto().getId());
            stmt.setInt(4, tarefa.getResponsavel().getId());
            stmt.setString(5, tarefa.getStatus());
            stmt.setDate(6, tarefa.getDataInicioPrevista() != null ? Date.valueOf(tarefa.getDataInicioPrevista()) : null);
            stmt.setDate(7, tarefa.getDataFimPrevista() != null ? Date.valueOf(tarefa.getDataFimPrevista()) : null);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir tarefa: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // LISTAR TODAS — retorna todas as tarefas com projeto e responsável
    // =====================================
    public List<Tarefa> listarTodas() {
        List<Tarefa> tarefas = new ArrayList<>();

        // JOIN com projetos e usuarios para trazer os dados completos
        String sql = "SELECT t.*, " +
                "p.nome as nome_projeto, " +
                "u.nome_completo as nome_responsavel, u.perfil " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.id_projeto = p.id " +
                "LEFT JOIN usuarios u ON t.id_responsavel = u.id";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tarefas.add(montarTarefa(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return tarefas;
    }

    // =====================================
    // LISTAR POR PROJETO — retorna tarefas de um projeto específico
    // =====================================
    public List<Tarefa> listarPorProjeto(int idProjeto) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT t.*, " +
                "p.nome as nome_projeto, " +
                "u.nome_completo as nome_responsavel, u.perfil " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.id_projeto = p.id " +
                "LEFT JOIN usuarios u ON t.id_responsavel = u.id " +
                "WHERE t.id_projeto = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idProjeto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tarefas.add(montarTarefa(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas do projeto: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return tarefas;
    }

    // =====================================
    // LISTAR POR RESPONSÁVEL — retorna tarefas de um usuário específico
    // =====================================
    public List<Tarefa> listarPorResponsavel(int idUsuario) {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = "SELECT t.*, " +
                "p.nome as nome_projeto, " +
                "u.nome_completo as nome_responsavel, u.perfil " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.id_projeto = p.id " +
                "LEFT JOIN usuarios u ON t.id_responsavel = u.id " +
                "WHERE t.id_responsavel = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tarefas.add(montarTarefa(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar tarefas do responsável: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return tarefas;
    }

    // =====================================
    // BUSCAR POR ID — retorna uma tarefa específica
    // =====================================
    public Tarefa buscarPorId(int id) {
        String sql = "SELECT t.*, " +
                "p.nome as nome_projeto, " +
                "u.nome_completo as nome_responsavel, u.perfil " +
                "FROM tarefas t " +
                "LEFT JOIN projetos p ON t.id_projeto = p.id " +
                "LEFT JOIN usuarios u ON t.id_responsavel = u.id " +
                "WHERE t.id = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return montarTarefa(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar tarefa: " + e.getMessage());
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
        return null;
    }

    // =====================================
    // ATUALIZAR STATUS — atualiza só o status e as datas reais
    // =====================================
    public boolean atualizarStatus(Tarefa tarefa) {
        String sql = "UPDATE tarefas SET status=?, data_inicio_real=?, data_fim_real=? WHERE id=?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, tarefa.getStatus());
            stmt.setDate(2, tarefa.getDataInicioReal() != null ? Date.valueOf(tarefa.getDataInicioReal()) : null);
            stmt.setDate(3, tarefa.getDataFimReal() != null ? Date.valueOf(tarefa.getDataFimReal()) : null);
            stmt.setInt(4, tarefa.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // ATUALIZAR — edita todos os dados de uma tarefa
    // =====================================
    public boolean atualizar(Tarefa tarefa) {
        String sql = "UPDATE tarefas SET titulo=?, descricao=?, id_projeto=?, id_responsavel=?, " +
                "status=?, data_inicio_prevista=?, data_fim_prevista=?, " +
                "data_inicio_real=?, data_fim_real=? WHERE id=?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjeto().getId());
            stmt.setInt(4, tarefa.getResponsavel().getId());
            stmt.setString(5, tarefa.getStatus());
            stmt.setDate(6, tarefa.getDataInicioPrevista() != null ? Date.valueOf(tarefa.getDataInicioPrevista()) : null);
            stmt.setDate(7, tarefa.getDataFimPrevista() != null ? Date.valueOf(tarefa.getDataFimPrevista()) : null);
            stmt.setDate(8, tarefa.getDataInicioReal() != null ? Date.valueOf(tarefa.getDataInicioReal()) : null);
            stmt.setDate(9, tarefa.getDataFimReal() != null ? Date.valueOf(tarefa.getDataFimReal()) : null);
            stmt.setInt(10, tarefa.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar tarefa: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // DELETAR — remove uma tarefa do banco
    // =====================================
    public boolean deletar(int id) {
        String sql = "DELETE FROM tarefas WHERE id = ?";

        Connection conexao = ConexaoBanco.getConexao();
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar tarefa: " + e.getMessage());
            return false;
        } finally {
            ConexaoBanco.fecharConexao(conexao);
        }
    }

    // =====================================
    // MÉTODO AUXILIAR — monta um objeto Tarefa a partir do ResultSet
    // Evita repetição de código nos métodos de listagem
    // =====================================
    private Tarefa montarTarefa(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getInt("id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescricao(rs.getString("descricao"));
        t.setStatus(rs.getString("status"));
        t.setDataInicioPrevista(rs.getDate("data_inicio_prevista") != null ? rs.getDate("data_inicio_prevista").toLocalDate() : null);
        t.setDataFimPrevista(rs.getDate("data_fim_prevista") != null ? rs.getDate("data_fim_prevista").toLocalDate() : null);
        t.setDataInicioReal(rs.getDate("data_inicio_real") != null ? rs.getDate("data_inicio_real").toLocalDate() : null);
        t.setDataFimReal(rs.getDate("data_fim_real") != null ? rs.getDate("data_fim_real").toLocalDate() : null);

        // Monta o objeto Projeto vinculado
        Projeto p = new Projeto();
        p.setId(rs.getInt("id_projeto"));
        p.setNome(rs.getString("nome_projeto"));
        t.setProjeto(p);

        // Monta o objeto Usuario responsável
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_responsavel"));
        u.setNomeCompleto(rs.getString("nome_responsavel"));
        u.setPerfil(rs.getString("perfil"));
        t.setResponsavel(u);

        return t;
    }
}