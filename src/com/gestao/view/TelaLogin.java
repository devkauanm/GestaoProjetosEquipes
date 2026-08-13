package com.gestao.view;

import com.gestao.dao.UsuarioDAO;
import com.gestao.model.Usuario;
import com.gestao.util.Sessao;

import javax.swing.*;
import java.awt.*;

// Tela de login — primeira tela que o usuário vê ao abrir o sistema
public class TelaLogin extends JFrame {

    // Componentes da tela
    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;
    private JLabel labelErro;

    public TelaLogin() {
        configurarJanela();
        construirTela();
    }

    // Configurações gerais da janela
    private void configurarJanela() {
        setTitle("Construtora São Judas Ltda. — Sistema de Gestão");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centraliza na tela
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 40));
    }

    // Constrói todos os componentes visuais da tela
    private void construirTela() {
        setLayout(new BorderLayout());

        // --- PAINEL DO TOPO com o nome da empresa ---
        JPanel painelTopo = new JPanel();
        painelTopo.setBackground(new Color(30, 30, 40));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        painelTopo.setLayout(new BoxLayout(painelTopo, BoxLayout.Y_AXIS));

        JLabel labelEmpresa = new JLabel("Construtora São Judas Ltda.");
        labelEmpresa.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelEmpresa.setForeground(new Color(200, 200, 220));
        labelEmpresa.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelSistema = new JLabel("Sistema de Gestão de Demandas");
        labelSistema.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelSistema.setForeground(new Color(120, 120, 150));
        labelSistema.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelTopo.add(labelEmpresa);
        painelTopo.add(Box.createVerticalStrut(4));
        painelTopo.add(labelSistema);

        // --- PAINEL CENTRAL com os campos de login ---
        JPanel painelCentro = new JPanel(new GridBagLayout());
        painelCentro.setBackground(new Color(30, 30, 40));
        painelCentro.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Campo login
        JLabel labelLogin = new JLabel("Login");
        labelLogin.setForeground(new Color(180, 180, 200));
        labelLogin.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 0;
        painelCentro.add(labelLogin, gbc);

        campoLogin = new JTextField();
        campoLogin.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoLogin.setBackground(new Color(45, 45, 60));
        campoLogin.setForeground(Color.WHITE);
        campoLogin.setCaretColor(Color.WHITE);
        campoLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridy = 1;
        painelCentro.add(campoLogin, gbc);

        // Campo senha
        JLabel labelSenha = new JLabel("Senha");
        labelSenha.setForeground(new Color(180, 180, 200));
        labelSenha.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 2;
        painelCentro.add(labelSenha, gbc);

        campoSenha = new JPasswordField();
        campoSenha.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campoSenha.setBackground(new Color(45, 45, 60));
        campoSenha.setForeground(Color.WHITE);
        campoSenha.setCaretColor(Color.WHITE);
        campoSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 110)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        gbc.gridy = 3;
        painelCentro.add(campoSenha, gbc);

        // Label de erro — aparece só quando login falha
        labelErro = new JLabel("");
        labelErro.setForeground(new Color(239, 68, 68));
        labelErro.setFont(new Font("SansSerif", Font.PLAIN, 11));
        labelErro.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        painelCentro.add(labelErro, gbc);

        // Botão entrar
        botaoEntrar = new JButton("Entrar");
        botaoEntrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        botaoEntrar.setBackground(new Color(79, 110, 247));
        botaoEntrar.setForeground(Color.WHITE);
        botaoEntrar.setFocusPainted(false);
        botaoEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoEntrar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        botaoEntrar.addActionListener(e -> realizarLogin());
        gbc.gridy = 5;
        painelCentro.add(botaoEntrar, gbc);

        // Permite pressionar Enter para logar
        campoSenha.addActionListener(e -> realizarLogin());
        campoLogin.addActionListener(e -> realizarLogin());

        add(painelTopo, BorderLayout.NORTH);
        add(painelCentro, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JLabel rodape = new JLabel("© 2025 Construtora São Judas Ltda. — CNPJ 47.382.910/0001-53");
        rodape.setFont(new Font("SansSerif", Font.PLAIN, 10));
        rodape.setForeground(new Color(80, 80, 100));
        rodape.setHorizontalAlignment(SwingConstants.CENTER);
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(rodape, BorderLayout.SOUTH);
    }

    // Lógica de autenticação
    private void realizarLogin() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        // Validação básica
        if (login.isEmpty() || senha.isEmpty()) {
            labelErro.setText("Preencha login e senha.");
            return;
        }

        // Consulta o banco via DAO
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.autenticar(login, senha);

        if (usuario != null) {
            // Login bem sucedido — salva na sessão e abre a tela principal
            Sessao.setUsuarioLogado(usuario);
            new TelaPrincipal().setVisible(true);
            dispose(); // fecha a tela de login
        } else {
            labelErro.setText("Login ou senha incorretos.");
            campoSenha.setText("");
        }
    }

    // Método main — ponto de entrada do sistema
    public static void main(String[] args) {
        // Garante que a interface rode na thread correta do Swing
        SwingUtilities.invokeLater(() -> {
            new TelaLogin().setVisible(true);
        });
    }
}