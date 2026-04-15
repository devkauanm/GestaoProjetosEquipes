package com.gestao.model;

// Classe que representa um usuário do sistema
// Todo mundo que usa o sistema é um Usuario: admin, gerente ou colaborador
public class Usuario {

    // Atributos privados — só são acessados via getters e setters
    private int id;              // identificador único gerado pelo banco de dados
    private String nomeCompleto; // nome completo do usuário
    private String cpf;          // CPF único por usuário
    private String email;        // e-mail de contato
    private String cargo;        // cargo na empresa (ex: desenvolvedor, analista)
    private String login;        // nome de usuário para entrar no sistema
    private String senha;        // senha de acesso
    private String perfil;       // nível de acesso: "administrador", "gerente" ou "colaborador"

    // Construtor vazio — usado quando queremos criar um usuário e preencher campo por campo
    public Usuario() {}

    // Construtor completo — usado quando já temos todos os dados (ex: ao buscar do banco)
    public Usuario(int id, String nomeCompleto, String cpf, String email,
                   String cargo, String login, String senha, String perfil) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    // --- GETTERS E SETTERS ---
    // Getters leem o valor do atributo
    // Setters alteram o valor do atributo

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    // toString — define como o objeto aparece quando exibido como texto
    // Ex: numa lista de usuários na tela vai aparecer "João Silva (gerente)"
    @Override
    public String toString() {
        return nomeCompleto + " (" + perfil + ")";
    }
}