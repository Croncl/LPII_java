package model;

import exceptions.SaldoInsuficienteException;
import exceptions.LimiteDeSaquesException;

public class ContaPoupanca extends Conta {
    private double taxaRendimento;

    public ContaPoupanca(Cliente cliente, Agencia agencia, int numeroConta, double saldo, double taxaRendimento) {
        super(cliente, agencia, numeroConta, saldo);
        this.taxaRendimento = taxaRendimento;
    }

    public void aplicarRendimento() {
        double rendimento = getSaldo() * taxaRendimento;
        depositar(rendimento);
        registrarTransacao("Rendimento de " + rendimento + " aplicado.");
    }

    @Override
    public void depositar(double quantia) {
        super.depositar(quantia);
        registrarTransacao("Depósito de " + quantia + " realizado.");
    }

    @Override
    public void sacar(double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        super.sacar(quantia);
        registrarTransacao("Saque de " + quantia + " realizado.");
    }

    @Override
    public void transferir(Conta destino, double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        super.transferir(destino, quantia);
        registrarTransacao("Transferência de " + quantia + " para conta " + destino.getNumeroConta() + " realizada.");
        destino.registrarTransacao("Recebimento de transferência de " + quantia + " da conta " + this.getNumeroConta() + " realizada.");
    }
}