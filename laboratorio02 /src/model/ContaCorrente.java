package model;

import exceptions.SaldoInsuficienteException;
import exceptions.LimiteDeSaquesException;

public class ContaCorrente extends Conta {
    private double limiteChequeEspecial;

    public ContaCorrente(Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial, double limiteChequeEspecial) {
        super(cliente, agencia, numeroConta, saldoInicial);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public void setLimiteChequeEspecial(double limiteChequeEspecial) {
        this.limiteChequeEspecial = limiteChequeEspecial;
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