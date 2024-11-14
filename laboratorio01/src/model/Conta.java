package model;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class Conta {
    // Atributos que definem o cliente dono da conta, agência, número da conta, tipo de conta e saldo
    private Cliente cliente;
    private int agencia;
    private int numeroConta;
    private String tipo; // Tipos possíveis: poupanca, corrente, investimento
    private double saldo;
    private List<Movimentacao> movimentacoes; // Lista de movimentações registradas para a conta
    private static final DecimalFormat df = new DecimalFormat("#.00"); // Formatação para exibir saldo com duas casas decimais
    public Conta(Cliente cliente, int agencia, int numeroConta, String tipo, double saldoInicial) {
        this.cliente = cliente;
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.tipo = tipo;
        this.saldo = saldoInicial;
        this.movimentacoes = new ArrayList<>();
    }
    // Métodos Getters para acessar os atributos da conta
    public Cliente getCliente() {
        return cliente;
    }
    public int getAgencia() {
        return agencia;
    }
    public int getNumeroConta() {
        return numeroConta;
    }
    public String getTipo() {
        return tipo;
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
    public boolean sacar(double quantia) {
        if (quantia <= saldo) {
            this.saldo -= quantia;
            registrarMovimentacao("Saque", quantia);
            return true;
        }
        return false; // Retorna falso se o saldo for insuficiente
    }
    public boolean transferir(Conta destino, double quantia) {
        if (sacar(quantia)) { // Realiza o saque na conta de origem
            destino.depositar(quantia); // Realiza o depósito na conta de destino
            registrarTransferencia(this, destino, quantia); // Registra a transferência
            return true;
        }
        return false;
    }
    private void registrarMovimentacao(String tipo, double valor) {
        Movimentacao movimentacao = new Movimentacao(tipo, valor, this.cliente);
        movimentacoes.add(movimentacao);
    }
    private void registrarTransferencia(Conta origem, Conta destino, double valor) {
        Transferencia transferencia = new Transferencia(origem, destino, valor);
        movimentacoes.add(transferencia);
    }
}