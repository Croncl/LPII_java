package model;

import java.util.ArrayList;
import java.util.List;

public class Agencia {
    private int numeroAgencia; // Número da agência
    private String nome;       // Nome da agência
    private List<Conta> contas; // Lista de contas associadas à agência

    // Construtor da classe
    public Agencia(int numeroAgencia, String nome) {
        this.numeroAgencia = numeroAgencia;
        this.nome = nome;
        this.contas = new ArrayList<>();
    }

    // Getter para o número da agência
    public int getNumeroAgencia() {
        return numeroAgencia;
    }

    // Getter para o nome da agência
    public String getNome() {
        return nome;
    }

    // Método para adicionar uma conta à agência
    public void adicionarConta(Conta conta) {
        contas.add(conta);
    }

    // Método para obter a lista de contas da agência
    public List<Conta> getContas() {
        return contas;
    }

    // Método para obter o código da agência (para compatibilidade com o código existente)
    public String getCodigo() {
        return String.valueOf(numeroAgencia);
    }

    @Override
    public String toString() {
        return "Agencia{" +
                "numeroAgencia=" + numeroAgencia +
                ", nome='" + nome + '\'' +
                '}';
    }
}