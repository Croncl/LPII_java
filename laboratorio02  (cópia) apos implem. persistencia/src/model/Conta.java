package model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import exceptions.LimiteDeSaquesException;
import exceptions.SaldoInsuficienteException;

public abstract class Conta {
    private Cliente cliente;
    private Agencia agencia;
    private int numeroConta;
    private double saldo;
    private List<Movimentacao> movimentacoes;
    private static final DecimalFormat df = new DecimalFormat("#.00");

    public Conta(Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial) {
        this.cliente = cliente;
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.saldo = saldoInicial;
        this.movimentacoes = new ArrayList<>();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public int getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getSaldoFormatado() {
        return df.format(saldo);
    }

    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    public void depositar(double quantia) {
        this.saldo += quantia;
        registrarMovimentacao("Depósito", quantia);
    }

    public void sacar(double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        if (quantia > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
        }
        saldo -= quantia;
        registrarMovimentacao("Saque", quantia);
    }

    public void transferir(Conta destino, double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        this.sacar(quantia); // Pode lançar SaldoInsuficienteException ou LimiteDeSaquesException
        destino.depositar(quantia);
        registrarMovimentacao("Transferência para " + destino.getNumeroConta(), quantia);
    }

    private void registrarMovimentacao(String tipo, double valor) {
        Movimentacao movimentacao = new Movimentacao(tipo, valor, this.cliente);
        movimentacoes.add(movimentacao);
    }
}