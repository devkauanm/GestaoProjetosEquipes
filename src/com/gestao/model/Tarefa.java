package com.gestao.model;

import java.time.LocalDate;

// Classe que representa uma tarefa dentro de um projeto
// Cada tarefa pertence a um único projeto e tem um responsável
public class Tarefa {

    private int id;                        // identificador único no banco
    private String titulo;                 // nome curto da tarefa
    private String descricao;             // detalhamento do que deve ser feito
    private Projeto projeto;              // qual projeto essa tarefa pertence
    private Usuario responsavel;          // quem vai executar a tarefa
    private String status;                // "pendente", "em execucao", "concluida"
    private LocalDate dataInicioPrevista; // quando era para começar
    private LocalDate dataFimPrevista;    // prazo de entrega
    private LocalDate dataInicioReal;     // quando realmente começou (preenchido automaticamente)
    private LocalDate dataFimReal;        // quando realmente terminou (preenchido automaticamente)

    // Construtor vazio
    public Tarefa() {}

    // Construtor com dados principais — datas reais ficam nulas até o status mudar
    public Tarefa(int id, String titulo, String descricao, Projeto projeto, Usuario responsavel,
                  String status, LocalDate dataInicioPrevista, LocalDate dataFimPrevista) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.projeto = projeto;
        this.responsavel = responsavel;
        this.status = status;
        this.dataInicioPrevista = dataInicioPrevista;
        this.dataFimPrevista = dataFimPrevista;
    }

    // --- GETTERS E SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }

    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }

    // Setter especial — ao mudar o status, registra automaticamente as datas reais
    public void setStatus(String status) {
        this.status = status;

        // Se mudou para "em execucao" e ainda não tinha data de início real, registra hoje
        if (status.equals("em execucao") && this.dataInicioReal == null) {
            this.dataInicioReal = LocalDate.now();
        }

        // Se mudou para "concluida" e ainda não tinha data de fim real, registra hoje
        if (status.equals("concluida") && this.dataFimReal == null) {
            this.dataFimReal = LocalDate.now();
        }
    }

    public String getStatus() { return status; }

    public LocalDate getDataInicioPrevista() { return dataInicioPrevista; }
    public void setDataInicioPrevista(LocalDate dataInicioPrevista) { this.dataInicioPrevista = dataInicioPrevista; }

    public LocalDate getDataFimPrevista() { return dataFimPrevista; }
    public void setDataFimPrevista(LocalDate dataFimPrevista) { this.dataFimPrevista = dataFimPrevista; }

    public LocalDate getDataInicioReal() { return dataInicioReal; }
    public void setDataInicioReal(LocalDate dataInicioReal) { this.dataInicioReal = dataInicioReal; }

    public LocalDate getDataFimReal() { return dataFimReal; }
    public void setDataFimReal(LocalDate dataFimReal) { this.dataFimReal = dataFimReal; }

    // Verifica se a tarefa está atrasada
    // Retorna true se: passou do prazo E a tarefa ainda não foi concluída
    public boolean isAtrasada() {
        return dataFimPrevista != null
                && LocalDate.now().isAfter(dataFimPrevista)
                && !status.equals("concluida");
    }

    // Exibe a tarefa como texto — ex: "Implementar login [em execucao]"
    @Override
    public String toString() {
        return titulo + " [" + status + "]";
    }
}