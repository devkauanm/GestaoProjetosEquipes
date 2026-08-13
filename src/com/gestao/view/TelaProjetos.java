package com.gestao.view;

import com.gestao.dao.ProjetoDAO;
import com.gestao.dao.UsuarioDAO;
import com.gestao.model.Projeto;
import com.gestao.model.Usuario;
import com.gestao.util.Sessao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Tela de gerenciamento de projetos
public class TelaProjetos extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private ProjetoDAO dao;
    private UsuarioDAO usuarioDAO;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaProjetos() {
        dao = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(22, 24, 31));
        construirTela();
        carregarProjetos();
    }

    private void construirTela() {

        // --- TOPO ---
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(22, 24, 31));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JLabel labelTitulo = new JLabel("Projetos");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelTitulo.setForeground(new Color(220, 220, 240));

        // Só admin e gerente podem criar projetos
        if (Sessao.isAdmin() || Sessao.isGerente()) {
            JButton botaoNovo = new JButton("+ Novo projeto");
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
        String[] colunas = {"ID", "Nome", "Gerente", "Início", "Término previsto", "Status"};
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

            painelAcoes.add(botaoEditar);
            painelAcoes.add(botaoExcluir);
        }

        add(painelTopo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(painelAcoes, BorderLayout.SOUTH);
    }

    private void carregarProjetos() {
        modeloTabela.setRowCount(0);
        List<Projeto> projetos = dao.listarTodos();
        for (Projeto p : projetos) {
            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getGerente() != null ? p.getGerente().getNomeCompleto() : "-",
                    p.getDataInicio() != null ? p.getDataInicio().format(formatter) : "-",
                    p.getDataTerminoPrevista() != null ? p.getDataTerminoPrevista().format(formatter) : "-",
                    p.getStatus()
            });
        }
    }

    private void abrirFormulario(Projeto projeto) {
        String titulo = projeto == null ? "Novo projeto" : "Editar projeto";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), titulo, true);
        dialog.setSize(440, 480);
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
        JTextField campoNome = criarCampo(painelForm, gbc, "Nome do projeto", 0);
        JTextField campoDescricao = criarCampo(painelForm, gbc, "Descrição", 2);
        JTextField campoInicio = criarCampo(painelForm, gbc, "Data de início (dd/MM/yyyy)", 4);
        JTextField campoTermino = criarCampo(painelForm, gbc, "Data de término prevista (dd/MM/yyyy)", 6);

        // Status
        JLabel labelStatus = new JLabel("Status");
        labelStatus.setForeground(new Color(160, 160, 190));
        labelStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 8;
        painelForm.add(labelStatus, gbc);

        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"planejado", "em andamento", "concluido", "cancelado"});
        comboStatus.setBackground(new Color(45, 45, 60));
        comboStatus.setForeground(Color.WHITE);
        comboStatus.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 9;
        painelForm.add(comboStatus, gbc);

        // Gerente
        JLabel labelGerente = new JLabel("Gerente responsável");
        labelGerente.setForeground(new Color(160, 160, 190));
        labelGerente.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 10;
        painelForm.add(labelGerente, gbc);

        // Carrega só gerentes e admins para o combo
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        JComboBox<Usuario> comboGerente = new JComboBox<>();
        for (Usuario u : usuarios) {
            if (u.getPerfil().equals("gerente") || u.getPerfil().equals("administrador")) {
                comboGerente.addItem(u);
            }
        }
        comboGerente.setBackground(new Color(45, 45, 60));
        comboGerente.setForeground(Color.WHITE);
        comboGerente.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 11;
        painelForm.add(comboGerente, gbc);

        // Preenche se for edição
        if (projeto != null) {
            campoNome.setText(projeto.getNome());
            campoDescricao.setText(projeto.getDescricao());
            campoInicio.setText(projeto.getDataInicio() != null ? projeto.getDataInicio().format(formatter) : "");
            campoTermino.setText(projeto.getDataTerminoPrevista() != null ? projeto.getDataTerminoPrevista().format(formatter) : "");
            comboStatus.setSelectedItem(projeto.getStatus());
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
            if (campoNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nome do projeto é obrigatório!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Projeto p = projeto != null ? projeto : new Projeto();
                p.setNome(campoNome.getText().trim());
                p.setDescricao(campoDescricao.getText().trim());
                p.setDataInicio(!campoInicio.getText().trim().isEmpty() ? LocalDate.parse(campoInicio.getText().trim(), formatter) : null);
                p.setDataTerminoPrevista(!campoTermino.getText().trim().isEmpty() ? LocalDate.parse(campoTermino.getText().trim(), formatter) : null);
                p.setStatus((String) comboStatus.getSelectedItem());
                p.setGerente((Usuario) comboGerente.getSelectedItem());

                boolean sucesso = projeto == null ? dao.inserir(p) : dao.atualizar(p);

                if (sucesso) {
                    carregarProjetos();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erro ao salvar projeto!", "Erro", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Selecione um projeto para editar!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        Projeto projeto = dao.buscarPorId(id);
        abrirFormulario(projeto);
    }

    private void excluirSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto para excluir!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este projeto?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            if (dao.deletar(id)) {
                carregarProjetos();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir projeto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}