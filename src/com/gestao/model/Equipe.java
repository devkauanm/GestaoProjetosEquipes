package com.gestao.model;

import java.util.ArrayList; // implementação de lista dinâmica
import java.util.List;      // interface de lista do Java

// Classe que representa uma equipe de trabalho
// Uma equipe tem vários membros e pode atuar em vários projetos
public class Equipe {

    private int id;                   // identificador único no banco
    private String nome;              // nome da equipe (ex: "Equipe Alpha")
    private String descricao;         // descrição da equipe
    private List<Usuario> membros;    // lista de usuários que fazem parte da equipe

    // Construtor vazio — já inicializa a lista de membros vazia
    public Equipe() {
        this.membros = new ArrayList<>(); // cria uma lista vazia pronta para receber membros
    }

    // Construtor com dados básicos — a lista de membros começa vazia e é preenchida depois
    public Equipe(int id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    // --- GETTERS E SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Usuario> getMembros() { return membros; }
    public void setMembros(List<Usuario> membros) { this.membros = membros; }

    // Adiciona um membro à equipe
    // O "if" garante que o mesmo usuário não seja adicionado duas vezes
    public void adicionarMembro(Usuario usuario) {
        if (!membros.contains(usuario)) {
            membros.add(usuario);
        }
    }

    // Remove um membro da equipe
    public void removerMembro(Usuario usuario) {
        membros.remove(usuario);
    }

    // Retorna quantos membros a equipe tem no momento
    public int getTotalMembros() {
        return membros.size();
    }

    // Exibe a equipe como texto — ex: "Equipe Alpha (4 membros)"
    @Override
    public String toString() {
        return nome + " (" + getTotalMembros() + " membros)";
    }
}