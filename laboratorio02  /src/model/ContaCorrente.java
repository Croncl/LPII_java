package model;

import exceptions.SaldoInsuficienteException;
import exceptions.LimiteDeSaquesException;

public class ContaCorrente extends Conta {
    private double limiteChequeEspecial;
    private double taxaManutencao;

    public ContaCorrente(Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial, double limiteChequeEspecial, double taxaManutencao) {
        super(cliente, agencia, numeroConta, saldoInicial);
        this.limiteChequeEspecial = limiteChequeEspecial;
        this.taxaManutencao = taxaManutencao;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public void setLimiteChequeEspecial(double limiteChequeEspecial) {
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getTaxaManutencao() {
        return taxaManutencao;
    }

    public void setTaxaManutencao(double taxaManutencao) {
        this.taxaManutencao = taxaManutencao;
    }

    @Override
    public void depositar(double quantia) {
        super.depositar(quantia);
    }

    @Override
    public void sacar(double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        if (quantia > getSaldo() + limiteChequeEspecial) {
            throw new SaldoInsuficienteException("Saldo insuficiente, incluindo limite do cheque especial.");
        }
        super.sacar(quantia);
    }

    @Override
    public void transferir(Conta destino, double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        if (quantia > getSaldo() + limiteChequeEspecial) {
            throw new SaldoInsuficienteException("Saldo insuficiente, incluindo limite do cheque especial.");
        }
        super.transferir(destino, quantia);
    }

    public void aplicarTaxaManutencao() throws SaldoInsuficienteException {
        try {
            if (taxaManutencao > getSaldo() + limiteChequeEspecial) {
                throw new SaldoInsuficienteException("Saldo insuficiente para aplicar a taxa de manutenção, incluindo limite do cheque especial.");
            }
            super.sacar(taxaManutencao);
            registrarTransacao("Taxa de manutenção de " + taxaManutencao + " aplicada.");
        } catch (LimiteDeSaquesException e) {
            // Tratar a exceção LimiteDeSaquesException, se necessário
            e.printStackTrace();
        }
    }
}