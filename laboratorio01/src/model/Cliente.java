package model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    // Atributos para armazenar as informações pessoais e as contas do cliente
    private String nome;
    private String cpf;
    private String senha;
    private String email;
    private String telefone;
    private List<Conta> contas;

    public Cliente(String nome, String cpf, String senha, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.contas = new ArrayList<>(); // Inicializa a lista de contas como vazia
    }

    // Métodos Getters e Setters para acessar e modificar os atributos do cliente

    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }


    public String getSenha() {
        return senha;
    }


    public void setSenha(String senha) {
        this.senha = senha;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }


    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }


    public List<Conta> getContas() {
        return contas;
    }

    public void adicionarConta(Conta conta) {
        this.contas.add(conta);
    }
}
