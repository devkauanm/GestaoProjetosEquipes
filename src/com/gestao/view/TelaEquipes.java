package com.gestao.view;

import com.gestao.dao.EquipeDAO;
import com.gestao.dao.UsuarioDAO;
import com.gestao.model.Equipe;
import com.gestao.model.Usuario;
import com.gestao.util.Sessao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Tela de gerenciamento de equipes
public class TelaEquipes extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private EquipeDAO dao;
    private UsuarioDAO usuarioDAO;

    public TelaEquipes() {
        dao = new EquipeDAO();
        usuarioDAO = new UsuarioDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(22, 24, 31));
        construirTela();
        carregarEquipes();
    }

    private void construirTela() {

        // --- TOPO ---
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(22, 24, 31));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JLabel labelTitulo = new JLabel("Equipes");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelTitulo.setForeground(new Color(220, 220, 240));

        if (Sessao.isAdmin() || Sessao.isGerente()) {
            JButton botaoNovo = new JButton("+ Nova equipe");
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
        String[] colunas = {"ID", "Nome", "Descrição", "Total de membros"};
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

    private void carregarEquipes() {
        modeloTabela.setRowCount(0);
        List<Equipe> equipes = dao.listarTodos();
        for (Equipe e : equipes) {
            modeloTabela.addRow(new Object[]{
                    e.getId(),
                    e.getNome(),
                    e.getDescricao(),
                    e.getTotalMembros() + " membro(s)"
            });
        }
    }

    private void abrirFormulario(Equipe equipe) {
        String titulo = equipe == null ? "Nova equipe" : "Editar equipe";

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

        // Campo nome
        JLabel labelNome = new JLabel("Nome da equipe");
        labelNome.setForeground(new Color(160, 160, 190));
        labelNome.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 0;
        painelForm.add(labelNome, gbc);

        JTextField campoNome = new JTextField();
        campoNome.setBackground(new Color(45, 45, 60));
        campoNome.setForeground(Color.WHITE);
        campoNome.setCaretColor(Color.WHITE);
        campoNome.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoNome.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridy = 1;
        painelForm.add(campoNome, gbc);

        // Campo descrição
        JLabel labelDesc = new JLabel("Descrição");
        labelDesc.setForeground(new Color(160, 160, 190));
        labelDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 2;
        painelForm.add(labelDesc, gbc);

        JTextField campoDesc = new JTextField();
        campoDesc.setBackground(new Color(45, 45, 60));
        campoDesc.setForeground(Color.WHITE);
        campoDesc.setCaretColor(Color.WHITE);
        campoDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoDesc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridy = 3;
        painelForm.add(campoDesc, gbc);

        // Lista de membros
        JLabel labelMembros = new JLabel("Membros da equipe");
        labelMembros.setForeground(new Color(160, 160, 190));
        labelMembros.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 4;
        painelForm.add(labelMembros, gbc);

        // Carrega todos os usuários para seleção
        List<Usuario> todosUsuarios = usuarioDAO.listarTodos();
        JList<Usuario> listaUsuarios = new JList<>(todosUsuarios.toArray(new Usuario[0]));
        listaUsuarios.setBackground(new Color(45, 45, 60));
        listaUsuarios.setForeground(Color.WHITE);
        listaUsuarios.setFont(new Font("SansSerif", Font.PLAIN, 13));
        listaUsuarios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Pré-seleciona membros se for edição
        if (equipe != null) {
            campoNome.setText(equipe.getNome());
            campoDesc.setText(equipe.getDescricao());
            // Marca os membros já existentes na lista
            List<Usuario> membrosAtuais = equipe.getMembros();
            int[] indices = new int[membrosAtuais.size()];
            for (int i = 0; i < todosUsuarios.size(); i++) {
                for (Usuario m : membrosAtuais) {
                    if (todosUsuarios.get(i).getId() == m.getId()) {
                        listaUsuarios.addSelectionInterval(i, i);
                    }
                }
            }
        }

        JScrollPane scrollLista = new JScrollPane(listaUsuarios);
        scrollLista.setPreferredSize(new Dimension(0, 120));
        scrollLista.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 110)));
        gbc.gridy = 5;
        painelForm.add(scrollLista, gbc);

        JLabel labelDica = new JLabel("Segure Ctrl para selecionar múltiplos");
        labelDica.setForeground(new Color(100, 100, 130));
        labelDica.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridy = 6;
        painelForm.add(labelDica, gbc);

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
                JOptionPane.showMessageDialog(dialog, "Nome da equipe é obrigatório!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Equipe eq = equipe != null ? equipe : new Equipe();
            eq.setNome(campoNome.getText().trim());
            eq.setDescricao(campoDesc.getText().trim());

            // Adiciona os membros selecionados
            List<Usuario> selecionados = listaUsuarios.getSelectedValuesList();
            eq.getMembros().clear();
            for (Usuario u : selecionados) {
                eq.adicionarMembro(u);
            }

            boolean sucesso = equipe == null ? dao.inserir(eq) : dao.atualizar(eq);

            if (sucesso) {
                carregarEquipes();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar equipe!", "Erro", JOptionPane.ERROR_MESSAGE);
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

    private void editarSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe para editar!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        Equipe equipe = dao.buscarPorId(id);
        abrirFormulario(equipe);
    }

    private void excluirSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe para excluir!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir esta equipe?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            if (dao.deletar(id)) {
                carregarEquipes();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir equipe!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
