package com.gestao.view;

import com.gestao.dao.ProjetoDAO;
import com.gestao.dao.TarefaDAO;
import com.gestao.dao.UsuarioDAO;
import com.gestao.model.Projeto;
import com.gestao.model.Tarefa;
import com.gestao.model.Usuario;
import com.gestao.util.Sessao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Tela de gerenciamento de tarefas
public class TelaTarefas extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private TarefaDAO dao;
    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaTarefas() {
        dao = new TarefaDAO();
        projetoDAO = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(22, 24, 31));
        construirTela();
        carregarTarefas();
    }

    private void construirTela() {

        // --- TOPO ---
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(22, 24, 31));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JLabel labelTitulo = new JLabel("Tarefas");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelTitulo.setForeground(new Color(220, 220, 240));

        if (Sessao.isAdmin() || Sessao.isGerente()) {
            JButton botaoNovo = new JButton("+ Nova tarefa");
            botaoNovo.setFont(new Font("SansSerif", Font.BOLD, 12));
            botaoNovo.setBackground(new Color(79, 110, 247));
            botaoNovo.setForeground(Color.WHITE);
            botaoNovo.setFocusPainted(false);
            botaoNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botaoNovo.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            botaoNovo.addActionListener(e -> abrirFormulario(null));
            painelTopo.add(botaoNovo, BorderLayout.EAST);
        }

        painelTopo.add(labelTitulo, BorderLayout.WEST);

        // --- TABELA ---
        String[] colunas = {"ID", "Título", "Projeto", "Responsável", "Status", "Início previsto", "Fim previsto"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(modeloTabela);
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

        // Esconde coluna ID
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(30, 32, 41));

        // --- BOTÕES DE AÇÃO ---
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelAcoes.setBackground(new Color(22, 24, 31));
        painelAcoes.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        // Colaborador só pode atualizar status das suas tarefas
        if (Sessao.isColaborador()) {
            JButton botaoStatus = new JButton("Atualizar status");
            botaoStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
            botaoStatus.setBackground(new Color(34, 197, 94));
            botaoStatus.setForeground(Color.WHITE);
            botaoStatus.setFocusPainted(false);
            botaoStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botaoStatus.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
            botaoStatus.addActionListener(e -> atualizarStatus());
            painelAcoes.add(botaoStatus);
        }

        if (Sessao.isAdmin() || Sessao.isGerente()) {
            JButton botaoEditar = new JButton("Editar");
            botaoEditar.setFont(new Font("SansSerif", Font.BOLD, 12));
            botaoEditar.setBackground(new Color(245, 158, 11));
            botaoEditar.setForeground(Color.WHITE);
            botaoEditar.setFocusPainted(false);
            botaoEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botaoEditar.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
            botaoEditar.addActionListener(e -> editarSelecionado());

            JButton botaoExcluir = new JButton("Excluir");
            botaoExcluir.setFont(new Font("SansSerif", Font.BOLD, 12));
            botaoExcluir.setBackground(new Color(239, 68, 68));
            botaoExcluir.setForeground(Color.WHITE);
            botaoExcluir.setFocusPainted(false);
            botaoExcluir.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botaoExcluir.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
            botaoExcluir.addActionListener(e -> excluirSelecionado());

            JButton botaoStatus = new JButton("Atualizar status");
            botaoStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
            botaoStatus.setBackground(new Color(34, 197, 94));
            botaoStatus.setForeground(Color.WHITE);
            botaoStatus.setFocusPainted(false);
            botaoStatus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            botaoStatus.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
            botaoStatus.addActionListener(e -> atualizarStatus());

            painelAcoes.add(botaoStatus);
            painelAcoes.add(botaoEditar);
            painelAcoes.add(botaoExcluir);
        }

        add(painelTopo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(painelAcoes, BorderLayout.SOUTH);
    }

    private void carregarTarefas() {
        modeloTabela.setRowCount(0);
        List<Tarefa> tarefas;

        // Colaborador vê só suas próprias tarefas
        if (Sessao.isColaborador()) {
            tarefas = dao.listarPorResponsavel(Sessao.getUsuarioLogado().getId());
        } else {
            tarefas = dao.listarTodas();
        }

        for (Tarefa t : tarefas) {
            modeloTabela.addRow(new Object[]{
                    t.getId(),
                    t.getTitulo(),
                    t.getProjeto() != null ? t.getProjeto().getNome() : "-",
                    t.getResponsavel() != null ? t.getResponsavel().getNomeCompleto() : "-",
                    t.getStatus(),
                    t.getDataInicioPrevista() != null ? t.getDataInicioPrevista().format(formatter) : "-",
                    t.getDataFimPrevista() != null ? t.getDataFimPrevista().format(formatter) : "-"
            });
        }
    }

    private void abrirFormulario(Tarefa tarefa) {
        String titulo = tarefa == null ? "Nova tarefa" : "Editar tarefa";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), titulo, true);
        dialog.setSize(440, 540);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(30, 32, 41));
        dialog.setLayout(new BorderLayout());

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(new Color(30, 32, 41));
        painelForm.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

        // Campos
        JTextField campoTitulo = criarCampo(painelForm, gbc, "Título", 0);
        JTextField campoDescricao = criarCampo(painelForm, gbc, "Descrição", 2);
        JTextField campoInicio = criarCampo(painelForm, gbc, "Início previsto (dd/MM/yyyy)", 4);
        JTextField campoFim = criarCampo(painelForm, gbc, "Fim previsto (dd/MM/yyyy)", 6);

        // Combo projeto
        JLabel labelProjeto = new JLabel("Projeto");
        labelProjeto.setForeground(new Color(160, 160, 190));
        labelProjeto.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 8;
        painelForm.add(labelProjeto, gbc);

        List<Projeto> projetos = projetoDAO.listarTodos();
        JComboBox<Projeto> comboProjeto = new JComboBox<>();
        for (Projeto p : projetos) comboProjeto.addItem(p);
        comboProjeto.setBackground(new Color(45, 45, 60));
        comboProjeto.setForeground(Color.WHITE);
        comboProjeto.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 9;
        painelForm.add(comboProjeto, gbc);

        // Combo responsável
        JLabel labelResp = new JLabel("Responsável");
        labelResp.setForeground(new Color(160, 160, 190));
        labelResp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 10;
        painelForm.add(labelResp, gbc);

        List<Usuario> usuarios = usuarioDAO.listarTodos();
        JComboBox<Usuario> comboResp = new JComboBox<>();
        for (Usuario u : usuarios) comboResp.addItem(u);
        comboResp.setBackground(new Color(45, 45, 60));
        comboResp.setForeground(Color.WHITE);
        comboResp.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 11;
        painelForm.add(comboResp, gbc);

        // Combo status
        JLabel labelStatus = new JLabel("Status");
        labelStatus.setForeground(new Color(160, 160, 190));
        labelStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 12;
        painelForm.add(labelStatus, gbc);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"pendente", "em execucao", "concluida"});
        comboStatus.setBackground(new Color(45, 45, 60));
        comboStatus.setForeground(Color.WHITE);
        comboStatus.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 13;
        painelForm.add(comboStatus, gbc);

        // Preenche se for edição
        if (tarefa != null) {
            campoTitulo.setText(tarefa.getTitulo());
            campoDescricao.setText(tarefa.getDescricao());
            campoInicio.setText(tarefa.getDataInicioPrevista() != null ? tarefa.getDataInicioPrevista().format(formatter) : "");
            campoFim.setText(tarefa.getDataFimPrevista() != null ? tarefa.getDataFimPrevista().format(formatter) : "");
            comboStatus.setSelectedItem(tarefa.getStatus());
        }

        // Botão salvar
        JButton botaoSalvar = new JButton("Salvar");
        botaoSalvar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botaoSalvar.setBackground(new Color(79, 110, 247));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFocusPainted(false);
        botaoSalvar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        botaoSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botaoSalvar.addActionListener(e -> {
            if (campoTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Título é obrigatório!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Tarefa t = tarefa != null ? tarefa : new Tarefa();
                t.setTitulo(campoTitulo.getText().trim());
                t.setDescricao(campoDescricao.getText().trim());
                t.setDataInicioPrevista(!campoInicio.getText().trim().isEmpty() ? LocalDate.parse(campoInicio.getText().trim(), formatter) : null);
                t.setDataFimPrevista(!campoFim.getText().trim().isEmpty() ? LocalDate.parse(campoFim.getText().trim(), formatter) : null);
                t.setProjeto((Projeto) comboProjeto.getSelectedItem());
                t.setResponsavel((Usuario) comboResp.getSelectedItem());
                t.setStatus((String) comboStatus.getSelectedItem());

                boolean sucesso = tarefa == null ? dao.inserir(t) : dao.atualizar(t);

                if (sucesso) {
                    carregarTarefas();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erro ao salvar tarefa!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Data inválida! Use o formato dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel painelBotao = new JPanel(new BorderLayout());
        painelBotao.setBackground(new Color(30, 32, 41));
        painelBotao.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
        painelBotao.add(botaoSalvar, BorderLayout.CENTER);

        dialog.add(painelForm, BorderLayout.CENTER);
        dialog.add(painelBotao, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Atualiza apenas o status da tarefa selecionada
    private void atualizarStatus() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        Tarefa tarefa = dao.buscarPorId(id);

        String[] opcoes = {"pendente", "em execucao", "concluida"};
        String novoStatus = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o novo status:",
                "Atualizar status",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                tarefa.getStatus()
        );

        if (novoStatus != null) {
            tarefa.setStatus(novoStatus);
            if (dao.atualizarStatus(tarefa)) {
                carregarTarefas();
            }
        }
    }

    private JTextField criarCampo(JPanel painel, GridBagConstraints gbc, String labelText, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(160, 160, 190));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = gridy;
        painel.add(label, gbc);

        JTextField campo = new JTextField();
        campo.setBackground(new Color(45, 45, 60));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridy = gridy + 1;
        painel.add(campo, gbc);
        return campo;
    }

    private void editarSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para editar!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        Tarefa tarefa = dao.buscarPorId(id);
        abrirFormulario(tarefa);
    }

    private void excluirSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para excluir!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir esta tarefa?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            if (dao.deletar(id)) {
                carregarTarefas();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir tarefa!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}