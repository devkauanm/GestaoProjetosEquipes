package com.gestao.view;

import com.gestao.util.Sessao;

import javax.swing.*;
import java.awt.*;

// Tela principal do sistema — exibida após o login
// Funciona como um menu central que dá acesso a todas as funcionalidades
public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        configurarJanela();
        construirTela();
    }

    private void configurarJanela() {
        setTitle("Construtora São Judas Ltda. — Sistema de Gestão");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(22, 24, 31));
    }

    private void construirTela() {
        setLayout(new BorderLayout());

        // --- TOPO ---
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(30, 32, 41));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel labelTitulo = new JLabel("Sistema de Gestão de Demandas");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        labelTitulo.setForeground(new Color(220, 220, 240));

        // Mostra o nome e perfil do usuário logado
        String nomeUsuario = Sessao.getUsuarioLogado().getNomeCompleto();
        String perfil = Sessao.getUsuarioLogado().getPerfil();
        JLabel labelUsuario = new JLabel(nomeUsuario + "  |  " + perfil);
        labelUsuario.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelUsuario.setForeground(new Color(120, 120, 150));

        JButton botaoSair = new JButton("Sair");
        botaoSair.setFont(new Font("SansSerif", Font.BOLD, 12));
        botaoSair.setBackground(new Color(239, 68, 68));
        botaoSair.setForeground(Color.WHITE);
        botaoSair.setFocusPainted(false);
        botaoSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoSair.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        botaoSair.addActionListener(e -> sair());

        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelDireito.setBackground(new Color(30, 32, 41));
        painelDireito.add(labelUsuario);
        painelDireito.add(botaoSair);

        painelTopo.add(labelTitulo, BorderLayout.WEST);
        painelTopo.add(painelDireito, BorderLayout.EAST);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(22, 24, 31));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Painel de conteúdo — onde as telas são abertas
        JPanel painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(new Color(22, 24, 31));

        // Label inicial
        JLabel labelBemVindo = new JLabel("Selecione uma opção no menu", SwingConstants.CENTER);
        labelBemVindo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelBemVindo.setForeground(new Color(100, 100, 130));
        painelConteudo.add(labelBemVindo, BorderLayout.CENTER);

        // --- BOTÕES DO MENU ---
        adicionarSecao(sidebar, "PRINCIPAL");
        adicionarBotaoMenu(sidebar, "Dashboard", painelConteudo, "dashboard");

        adicionarSecao(sidebar, "GESTÃO");
        adicionarBotaoMenu(sidebar, "Projetos", painelConteudo, "projetos");
        adicionarBotaoMenu(sidebar, "Tarefas", painelConteudo, "tarefas");
        adicionarBotaoMenu(sidebar, "Equipes", painelConteudo, "equipes");

        // Só admin vê o menu de usuários
        if (Sessao.isAdmin()) {
            adicionarBotaoMenu(sidebar, "Usuários", painelConteudo, "usuarios");
        }

        adicionarSecao(sidebar, "ANÁLISE");
        adicionarBotaoMenu(sidebar, "Relatórios", painelConteudo, "relatorios");

        add(painelTopo, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(painelConteudo, BorderLayout.CENTER);
    }

    // Adiciona um título de seção na sidebar
    private void adicionarSecao(JPanel sidebar, String titulo) {
        JLabel label = new JLabel(titulo);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(new Color(80, 80, 110));
        label.setBorder(BorderFactory.createEmptyBorder(16, 10, 4, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(label);
    }

    // Adiciona um botão de navegação na sidebar
    private void adicionarBotaoMenu(JPanel sidebar, String nome, JPanel painelConteudo, String tela) {
        JButton botao = new JButton(nome);
        botao.setFont(new Font("SansSerif", Font.PLAIN, 13));
        botao.setForeground(new Color(180, 180, 210));
        botao.setBackground(new Color(22, 24, 31));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botao.setBackground(new Color(35, 38, 55));
                botao.setForeground(new Color(220, 220, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setBackground(new Color(22, 24, 31));
                botao.setForeground(new Color(180, 180, 210));
            }
        });

        botao.addActionListener(e -> navegarPara(tela, painelConteudo));
        sidebar.add(botao);
    }

    // Navega para a tela selecionada no painel de conteúdo
    private void navegarPara(String tela, JPanel painelConteudo) {
        painelConteudo.removeAll();

        JPanel novaTela = switch (tela) {
            case "usuarios" -> new TelaUsuarios();
            case "projetos" -> new TelaProjetos();
            case "equipes" -> new TelaEquipes();
            case "tarefas" -> new TelaTarefas();
            case "relatorios" -> new TelaRelatorios();
            default -> {
                JLabel placeholder = new JLabel("Tela de " + tela + " — em construção", SwingConstants.CENTER);
                placeholder.setFont(new Font("SansSerif", Font.PLAIN, 14));
                placeholder.setForeground(new Color(100, 100, 130));
                JPanel p = new JPanel(new BorderLayout());
                p.setBackground(new Color(22, 24, 31));
                p.add(placeholder, BorderLayout.CENTER);
                yield p;
            }
        };

        painelConteudo.add(novaTela, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    // Faz logout e volta para a tela de login
    private void sair() {
        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair?",
                "Confirmar saída",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmacao == JOptionPane.YES_OPTION) {
            Sessao.encerrar();
            new TelaLogin().setVisible(true);
            dispose();
        }
    }
}