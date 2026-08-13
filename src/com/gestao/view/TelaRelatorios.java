package com.gestao.view;

import com.gestao.dao.ProjetoDAO;
import com.gestao.dao.TarefaDAO;
import com.gestao.dao.UsuarioDAO;
import com.gestao.model.Projeto;
import com.gestao.model.Tarefa;
import com.gestao.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Tela de relatórios e dashboard do sistema
public class TelaRelatorios extends JPanel {

    private ProjetoDAO projetoDAO;
    private TarefaDAO tarefaDAO;
    private UsuarioDAO usuarioDAO;

    public TelaRelatorios() {
        projetoDAO = new ProjetoDAO();
        tarefaDAO = new TarefaDAO();
        usuarioDAO = new UsuarioDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(22, 24, 31));
        construirTela();
    }

    private void construirTela() {

        // --- TOPO ---
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(22, 24, 31));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JLabel labelTitulo = new JLabel("Relatórios");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelTitulo.setForeground(new Color(220, 220, 240));
        painelTopo.add(labelTitulo, BorderLayout.WEST);

        // --- CONTEÚDO COM ABAS ---
        JTabbedPane abas = new JTabbedPane();
        abas.setBackground(new Color(22, 24, 31));
        abas.setForeground(new Color(200, 200, 220));
        abas.setFont(new Font("SansSerif", Font.PLAIN, 13));

        abas.addTab("Resumo geral", criarAbaResumo());
        abas.addTab("Projetos em atraso", criarAbaAtraso());
        abas.addTab("Desempenho por colaborador", criarAbaDesempenho());

        add(painelTopo, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
    }

    // =====================================
    // ABA 1 — RESUMO GERAL
    // =====================================
    private JPanel criarAbaResumo() {
        JPanel painel = new JPanel();
        painel.setBackground(new Color(22, 24, 31));
        painel.setLayout(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Carrega dados
        List<Projeto> projetos = projetoDAO.listarTodos();
        List<Tarefa> tarefas = tarefaDAO.listarTodas();

        // Conta por status
        long projetosAndamento = projetos.stream().filter(p -> p.getStatus().equals("em andamento")).count();
        long projetosConcluidos = projetos.stream().filter(p -> p.getStatus().equals("concluido")).count();
        long projetosPlanejados = projetos.stream().filter(p -> p.getStatus().equals("planejado")).count();
        long projetosCancelados = projetos.stream().filter(p -> p.getStatus().equals("cancelado")).count();

        long tarefasPendentes = tarefas.stream().filter(t -> t.getStatus().equals("pendente")).count();
        long tarefasExecucao = tarefas.stream().filter(t -> t.getStatus().equals("em execucao")).count();
        long tarefasConcluidas = tarefas.stream().filter(t -> t.getStatus().equals("concluida")).count();
        long projetosAtraso = projetos.stream().filter(Projeto::isAtrasado).count();

        // Grid de cards
        JPanel gridCards = new JPanel(new GridLayout(2, 4, 14, 14));
        gridCards.setBackground(new Color(22, 24, 31));

        gridCards.add(criarCard("Projetos ativos", String.valueOf(projetosAndamento), new Color(79, 110, 247)));
        gridCards.add(criarCard("Concluídos", String.valueOf(projetosConcluidos), new Color(34, 197, 94)));
        gridCards.add(criarCard("Planejados", String.valueOf(projetosPlanejados), new Color(56, 189, 248)));
        gridCards.add(criarCard("Em atraso", String.valueOf(projetosAtraso), new Color(239, 68, 68)));
        gridCards.add(criarCard("Tarefas pendentes", String.valueOf(tarefasPendentes), new Color(245, 158, 11)));
        gridCards.add(criarCard("Em execução", String.valueOf(tarefasExecucao), new Color(79, 110, 247)));
        gridCards.add(criarCard("Tarefas concluídas", String.valueOf(tarefasConcluidas), new Color(34, 197, 94)));
        gridCards.add(criarCard("Total de projetos", String.valueOf(projetos.size()), new Color(124, 58, 237)));

        painel.add(gridCards, BorderLayout.NORTH);
        return painel;
    }

    // Card individual do resumo
    private JPanel criarCard(String label, String valor, Color cor) {
        JPanel card = new JPanel();
        card.setBackground(new Color(30, 32, 41));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, cor),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("SansSerif", Font.BOLD, 28));
        labelValor.setForeground(cor);

        JLabel labelNome = new JLabel(label);
        labelNome.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelNome.setForeground(new Color(130, 130, 160));

        card.add(labelValor, BorderLayout.CENTER);
        card.add(labelNome, BorderLayout.SOUTH);
        return card;
    }

    // =====================================
    // ABA 2 — PROJETOS EM ATRASO
    // =====================================
    private JPanel criarAbaAtraso() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(22, 24, 31));
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] colunas = {"Nome do projeto", "Gerente", "Término previsto", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<Projeto> atrasados = projetoDAO.listarEmAtraso();
        for (Projeto p : atrasados) {
            modelo.addRow(new Object[]{
                    p.getNome(),
                    p.getGerente() != null ? p.getGerente().getNomeCompleto() : "-",
                    p.getDataTerminoPrevista() != null ? p.getDataTerminoPrevista().toString() : "-",
                    p.getStatus()
            });
        }

        JTable tabela = criarTabela(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(30, 32, 41));

        if (atrasados.isEmpty()) {
            JLabel labelOk = new JLabel("Nenhum projeto em atraso! ✓", SwingConstants.CENTER);
            labelOk.setFont(new Font("SansSerif", Font.PLAIN, 14));
            labelOk.setForeground(new Color(34, 197, 94));
            painel.add(labelOk, BorderLayout.CENTER);
        } else {
            painel.add(scroll, BorderLayout.CENTER);
        }

        return painel;
    }

    // =====================================
    // ABA 3 — DESEMPENHO POR COLABORADOR
    // =====================================
    private JPanel criarAbaDesempenho() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(22, 24, 31));
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        String[] colunas = {"Colaborador", "Perfil", "Tarefas atribuídas", "Concluídas", "Em execução", "Pendentes"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        List<Usuario> usuarios = usuarioDAO.listarTodos();
        List<Tarefa> todasTarefas = tarefaDAO.listarTodas();

        for (Usuario u : usuarios) {
            // Filtra as tarefas desse usuário
            List<Tarefa> tarefasUsuario = todasTarefas.stream()
                    .filter(t -> t.getResponsavel() != null && t.getResponsavel().getId() == u.getId())
                    .toList();

            long concluidas = tarefasUsuario.stream().filter(t -> t.getStatus().equals("concluida")).count();
            long execucao = tarefasUsuario.stream().filter(t -> t.getStatus().equals("em execucao")).count();
            long pendentes = tarefasUsuario.stream().filter(t -> t.getStatus().equals("pendente")).count();

            modelo.addRow(new Object[]{
                    u.getNomeCompleto(),
                    u.getPerfil(),
                    tarefasUsuario.size(),
                    concluidas,
                    execucao,
                    pendentes
            });
        }

        JTable tabela = criarTabela(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(30, 32, 41));

        painel.add(scroll, BorderLayout.CENTER);
        return painel;
    }

    // Método auxiliar para criar tabelas com estilo padrão
    private JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setBackground(new Color(30, 32, 41));
        tabela.setForeground(new Color(200, 200, 220));
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.getTableHeader().setBackground(new Color(22, 24, 31));
        tabela.getTableHeader().setForeground(new Color(120, 120, 150));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabela.setSelectionBackground(new Color(50, 55, 80));
        tabela.setSelectionForeground(Color.WHITE);
        return tabela;
    }
}