package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

    // Construtor da classe Conta
    public Conta(Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial) {
        this.cliente = cliente;
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.saldo = saldoInicial;
        this.movimentacoes = new ArrayList<>();
        // Registra a transação de criação da conta
        registrarTransacao("Conta criada com saldo inicial de " + saldoInicial);
    }

    // Retorna o cliente associado à conta
    public Cliente getCliente() {
        return cliente;
    }

    // Retorna a agência associada à conta
    public Agencia getAgencia() {
        return agencia;
    }

    // Retorna o número da conta
    public int getNumeroConta() {
        return numeroConta;
    }

    // Retorna o saldo da conta
    public double getSaldo() {
        return saldo;
    }

    // Retorna o saldo formatado
    public String getSaldoFormatado() {
        return df.format(saldo);
    }

    // Retorna a lista de movimentações da conta
    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    // Realiza um depósito na conta
    public void depositar(double quantia) {
        this.saldo += quantia;
        registrarTransacao("Depósito de " + quantia + " realizado.");
    }

    // Realiza um saque da conta
    public void sacar(double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        if (saldo < quantia) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }
        saldo -= quantia;
        registrarTransacao("Saque de " + quantia + " realizado.");
    }

    // Realiza uma transferência para outra conta
    public void transferir(Conta destino, double quantia) throws SaldoInsuficienteException, LimiteDeSaquesException {
        this.sacar(quantia); // Pode lançar SaldoInsuficienteException ou LimiteDeSaquesException
        destino.depositar(quantia);
        registrarTransacao("Transferência de " + quantia + " para a conta " + destino.getNumeroConta() + " realizada.");
        destino.registrarTransacao("Recebimento de transferência de " + quantia + " da conta " + this.getNumeroConta() + " realizada.");
    }

    // Exibe o extrato de transações da conta
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

    // Registra uma transação no arquivo CSV
    public void registrarTransacao(String descricao) {
        String tipoConta = this.getClass().getSimpleName(); // Obtém o nome da classe (tipo de conta)
        String nomeArquivo = "transacoes_" + tipoConta + "_" + this.agencia.getNumeroAgencia() + "_" + this.numeroConta + ".csv";
        boolean arquivoExiste = new File(nomeArquivo).exists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            if (!arquivoExiste) {
                // Escrever o cabeçalho
                writer.write("ID,DataHora,Descricao");
                writer.newLine();
            }
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String idTransacao = UUID.randomUUID().toString(); // Gera um identificador único para a transação
            writer.write(idTransacao + "," + dataHora + "," + descricao);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carrega as transações do arquivo CSV
    public List<String> carregarTransacoes() {
        List<String> transacoes = new ArrayList<>();
        String tipoConta = this.getClass().getSimpleName();
        String nomeArquivo = "transacoes_" + tipoConta + "_" + this.agencia.getNumeroAgencia() + "_" + this.numeroConta + ".csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            boolean isFirstLine = true;
            while ((linha = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Ignorar o cabeçalho
                }
                if (!transacoes.contains(linha)) {
                    transacoes.add(linha);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transacoes;
    }
}