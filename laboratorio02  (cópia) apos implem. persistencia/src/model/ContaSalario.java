package model;

import exceptions.LimiteDeSaquesException;
import exceptions.SaldoInsuficienteException;

public class ContaSalario extends Conta {
    private int limiteSaques;
    private int saquesRealizados;

    public ContaSalario(Cliente cliente, Agencia agencia, int numeroConta, double saldo, int limiteSaques) {
        super(cliente, agencia, numeroConta, saldo);
        this.limiteSaques = limiteSaques;
    }

    @Override
    public void sacar(double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        if (saquesRealizados >= limiteSaques) {
            throw new LimiteDeSaquesException("Limite de saques atingido.");
        }
        super.sacar(quantia); // Chama o método sacar da classe Conta
        saquesRealizados++;
    }
}