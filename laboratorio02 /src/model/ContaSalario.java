package model;

import exceptions.SaldoInsuficienteException;
import exceptions.LimiteDeSaquesException;

public class ContaSalario extends Conta {
    private int limiteSaques;
    private int saquesRealizados;

    public ContaSalario(Cliente cliente, Agencia agencia, int numeroConta, double saldo, int limiteSaques) {
        super(cliente, agencia, numeroConta, saldo);
        this.limiteSaques = limiteSaques;
        this.saquesRealizados = 0;
    }

    @Override
    public void depositar(double quantia) {
        super.depositar(quantia);
        registrarTransacao("Depósito de " + quantia + " realizado.");
    }

    @Override
    public void sacar(double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        if (saquesRealizados >= limiteSaques) {
            throw new LimiteDeSaquesException("Limite de saques atingido.");
        }
        super.sacar(quantia); // Chama o método sacar da classe Conta
        saquesRealizados++;
        registrarTransacao("Saque de " + quantia + " realizado.");
    }

    @Override
    public void transferir(Conta destino, double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        super.transferir(destino, quantia);
        registrarTransacao("Transferência de " + quantia + " para conta " + destino.getNumeroConta() + " realizada.");
        destino.registrarTransacao("Recebimento de transferência de " + quantia + " da conta " + this.getNumeroConta() + " realizada.");
    }
}