package com.gestao.util;

import com.gestao.model.Usuario;

// Classe que armazena o usuário logado durante o uso do sistema
// É estática — ou seja, pode ser acessada de qualquer tela sem precisar passar o usuário por parâmetro
public class Sessao {

    // Usuário que está logado no momento
    private static Usuario usuarioLogado;

    // Salva o usuário logado ao fazer login
    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    // Retorna o usuário logado
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    // Limpa a sessão ao fazer logout
    public static void encerrar() {
        usuarioLogado = null;
    }

    // Verifica se tem alguém logado
    public static boolean estaLogado() {
        return usuarioLogado != null;
    }

    // Atalhos para verificar o perfil do usuário logado
    // Usados nas telas para mostrar ou esconder funcionalidades
    public static boolean isAdmin() {
        return estaLogado() && usuarioLogado.getPerfil().equals("administrador");
    }

    public static boolean isGerente() {
        return estaLogado() && usuarioLogado.getPerfil().equals("gerente");
    }

    public static boolean isColaborador() {
        return estaLogado() && usuarioLogado.getPerfil().equals("colaborador");
    }
}