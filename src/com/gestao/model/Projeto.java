package com.gestao.model;

import java.time.LocalDate; // classe do Java para trabalhar com datas

// Classe que representa um projeto da empresa
// Um projeto tem prazo, status e um gerente responsável
public class Projeto {

    private int id;                          // identificador único no banco
    private String nome;                     // nome do projeto
    private String descricao;               // descrição do que o projeto faz
    private LocalDate dataInicio;           // quando o projeto começou
    private LocalDate dataTerminoPrevista;  // prazo de entrega
    private String status;                  // "planejado", "em andamento", "concluido", "cancelado"
    private Usuario gerente;                // objeto Usuario que representa o gerente responsável

    // Construtor vazio
    public Projeto() {}

    // Construtor completo — usado ao buscar projetos do banco de dados
    public Projeto(int id, String nome, String descricao, LocalDate dataInicio,
                   LocalDate dataTerminoPrevista, String status, Usuario gerente) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.status = status;
        this.gerente = gerente;
    }

    // --- GETTERS E SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataTerminoPrevista() { return dataTerminoPrevista; }
    public void setDataTerminoPrevista(LocalDate dataTerminoPrevista) { this.dataTerminoPrevista = dataTerminoPrevista; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Usuario getGerente() { return gerente; }
    public void setGerente(Usuario gerente) { this.gerente = gerente; }

    // Verifica automaticamente se o projeto está atrasado
    // Retorna true se: a data atual passou do prazo E o projeto não foi concluído nem cancelado
    public boolean isAtrasado() {
        return dataTerminoPrevista != null
                && LocalDate.now().isAfter(dataTerminoPrevista)
                && !status.equals("concluido")
                && !status.equals("cancelado");
    }

    // Exibe o projeto como texto — ex: "Portal do Cliente [em andamento]"
    @Override
    public String toString() {
        return nome + " [" + status + "]";
    }
}