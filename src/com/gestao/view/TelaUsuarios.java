package com.gestao.view;

import com.gestao.dao.UsuarioDAO;
import com.gestao.model.Usuario;
import com.gestao.util.Sessao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Tela de gerenciamento de usuários — acessível apenas pelo administrador
public class TelaUsuarios extends JPanel {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private UsuarioDAO dao;

    public TelaUsuarios() {
        dao = new UsuarioDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(22, 24, 31));
        construirTela();
        carregarUsuarios();
    }

    private void construirTela() {

        // --- TOPO ---
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(22, 24, 31));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));

        JLabel labelTitulo = new JLabel("Usuários");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelTitulo.setForeground(new Color(220, 220, 240));

        JButton botaoNovo = new JButton("+ Novo usuário");
        botaoNovo.setFont(new Font("SansSerif", Font.BOLD, 12));
        botaoNovo.setBackground(new Color(79, 110, 247));
        botaoNovo.setForeground(Color.WHITE);
        botaoNovo.setFocusPainted(false);
        botaoNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoNovo.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        botaoNovo.addActionListener(e -> abrirFormulario(null));

        painelTopo.add(labelTitulo, BorderLayout.WEST);
        painelTopo.add(botaoNovo, BorderLayout.EAST);

        // --- TABELA ---
        String[] colunas = {"ID", "Nome completo", "CPF", "Email", "Cargo", "Login", "Perfil"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            // Impede edição direta nas células da tabela
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

        // Esconde a coluna ID da visualização mas mantém no modelo
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBackground(new Color(22, 24, 31));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(30, 32, 41));

        // --- BOTÕES DE AÇÃO ---
        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelAcoes.setBackground(new Color(22, 24, 31));
        painelAcoes.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

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

        add(painelTopo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(painelAcoes, BorderLayout.SOUTH);
    }

    // Carrega os usuários do banco e preenche a tabela
    private void carregarUsuarios() {
        modeloTabela.setRowCount(0); // limpa a tabela antes de carregar
        List<Usuario> usuarios = dao.listarTodos();
        for (Usuario u : usuarios) {
            modeloTabela.addRow(new Object[]{
                    u.getId(),
                    u.getNomeCompleto(),
                    u.getCpf(),
                    u.getEmail(),
                    u.getCargo(),
                    u.getLogin(),
                    u.getPerfil()
            });
        }
    }

    // Abre o formulário para cadastrar ou editar um usuário
    private void abrirFormulario(Usuario usuario) {
        // Define o título baseado na operação
        String titulo = usuario == null ? "Novo usuário" : "Editar usuário";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), titulo, true);
        dialog.setSize(400, 420);
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

        // Campos do formulário
        JTextField campoNome = criarCampo(painelForm, gbc, "Nome completo", 0);
        JTextField campoCpf = criarCampo(painelForm, gbc, "CPF", 2);
        JTextField campoEmail = criarCampo(painelForm, gbc, "Email", 4);
        JTextField campoCargo = criarCampo(painelForm, gbc, "Cargo", 6);
        JTextField campoLogin = criarCampo(painelForm, gbc, "Login", 8);
        JTextField campoSenha = criarCampo(painelForm, gbc, "Senha", 10);

        // ComboBox de perfil
        JLabel labelPerfil = new JLabel("Perfil");
        labelPerfil.setForeground(new Color(160, 160, 190));
        labelPerfil.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 12;
        painelForm.add(labelPerfil, gbc);

        JComboBox<String> comboPerfil = new JComboBox<>(new String[]{"colaborador", "gerente", "administrador"});
        comboPerfil.setBackground(new Color(45, 45, 60));
        comboPerfil.setForeground(Color.WHITE);
        comboPerfil.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 13;
        painelForm.add(comboPerfil, gbc);

        // Preenche os campos se for edição
        if (usuario != null) {
            campoNome.setText(usuario.getNomeCompleto());
            campoCpf.setText(usuario.getCpf());
            campoEmail.setText(usuario.getEmail());
            campoCargo.setText(usuario.getCargo());
            campoLogin.setText(usuario.getLogin());
            campoSenha.setText(usuario.getSenha());
            comboPerfil.setSelectedItem(usuario.getPerfil());
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
            // Validação básica
            if (campoNome.getText().trim().isEmpty() || campoLogin.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nome e login são obrigatórios!", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario u = usuario != null ? usuario : new Usuario();
            u.setNomeCompleto(campoNome.getText().trim());
            u.setCpf(campoCpf.getText().trim());
            u.setEmail(campoEmail.getText().trim());
            u.setCargo(campoCargo.getText().trim());
            u.setLogin(campoLogin.getText().trim());
            u.setSenha(campoSenha.getText().trim());
            u.setPerfil((String) comboPerfil.getSelectedItem());

            boolean sucesso = usuario == null ? dao.inserir(u) : dao.atualizar(u);

            if (sucesso) {
                carregarUsuarios(); // atualiza a tabela
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
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

    // Método auxiliar para criar campos do formulário com label
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

    // Abre o formulário de edição com o usuário selecionado na tabela
    private void editarSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        Usuario usuario = dao.buscarPorId(id);
        abrirFormulario(usuario);
    }

    // Exclui o usuário selecionado na tabela
    private void excluirSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir!", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir este usuário?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            if (dao.deletar(id)) {
                carregarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir usuário!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}