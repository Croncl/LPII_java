package model;

import exceptions.SaldoInsuficienteException;
import exceptions.LimiteDeSaquesException;

public class ContaCorrente extends Conta {
    private double taxaManutencao;

    public ContaCorrente(Cliente cliente, Agencia agencia, int numeroConta, double saldo, double taxaManutencao) {
        super(cliente, agencia, numeroConta, saldo);
        this.taxaManutencao = taxaManutencao;
    }

    public void cobrarTaxaManutencao() throws SaldoInsuficienteException {
        if (getSaldo() < taxaManutencao) {
            throw new SaldoInsuficienteException("Saldo insuficiente para cobrança da taxa de manutenção.");
        }
        try {
            sacar(taxaManutencao);
        } catch (LimiteDeSaquesException e) {
            // Trata a exceção (por exemplo, exibe uma mensagem de erro)
            System.err.println("Erro ao cobrar taxa de manutenção: " + e.getMessage());
        }
    }
}