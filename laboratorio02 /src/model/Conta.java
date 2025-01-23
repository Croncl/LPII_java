package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        if (saldo < quantia) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }
        saldo -= quantia;
        registrarTransacao("Saque de " + quantia + " realizado.");
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

    public void exibirExtrato() {
        List<String> transacoes = carregarTransacoes();
        if (transacoes.isEmpty()) {
            System.out.println("Nenhuma transação encontrada para esta conta.");
        } else {
            System.out.println("Extrato de Transações:");
            for (String transacao : transacoes) {
                System.out.println(transacao);
            }
        }
    }

    public void registrarTransacao(String descricao) {
        String tipoConta = this.getClass().getSimpleName(); // Obtém o nome da classe (tipo de conta)
        String nomeArquivo = "log_" + tipoConta + "_" + this.agencia.getNumeroAgencia() + "_" + this.numeroConta + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String idTransacao = UUID.randomUUID().toString(); // Gera um identificador único para a transação
            writer.write(idTransacao + " - " + dataHora + " - " + descricao);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> carregarTransacoes() {
        List<String> transacoes = new ArrayList<>();
        String tipoConta = this.getClass().getSimpleName();
        String nomeArquivo = "log_" + tipoConta + "_" + this.agencia.getNumeroAgencia() + "_" + this.numeroConta + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                transacoes.add(linha);
            }
        } catch (IOException e) {
            // Se o arquivo não existir, cria um novo arquivo vazio
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
                // Arquivo criado vazio
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return transacoes;
    }
}