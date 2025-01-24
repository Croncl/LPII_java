package utils;

import model.Agencia;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.ContaSalario;
import model.Movimentacao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GerenciadorDeArquivos {
    private static final String ARQUIVO_DADOS = "dados.csv";

    public static void salvarDados(List<Cliente> clientes) {
        try (FileWriter writer = new FileWriter(ARQUIVO_DADOS)) {
            // Escrever o cabeçalho
            writer.write("CPF,Nome,Senha,NumeroAgencia,NumeroConta,TipoConta,Saldo\n");
            
            for (Cliente cliente : clientes) {
                for (Conta conta : cliente.getContas()) {
                    writer.write(cliente.getCpf() + "," +
                                 cliente.getNome() + "," +
                                 cliente.getSenha() + "," +
                                 conta.getAgencia().getNumeroAgencia() + "," +
                                 conta.getNumeroConta() + "," +
                                 conta.getClass().getSimpleName() + "," +
                                 conta.getSaldo() + "\n");
                    // Salvar transações da conta
                    salvarTransacoes(conta);
                }
            }
            System.out.println("Dados salvos com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private static void salvarTransacoes(Conta conta) {
        String tipoConta = conta.getClass().getSimpleName();
        String nomeArquivo = "transacoes_" + tipoConta + "_" + conta.getAgencia().getNumeroAgencia() + "_" + conta.getNumeroConta() + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            List<String> transacoes = conta.carregarTransacoes();
            for (String transacao : transacoes) {
                writer.write(transacao);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void carregarDados(List<Cliente> clientes, List<Agencia> agencias) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_DADOS))) {
            String linha;
            boolean isFirstLine = true;
            while ((linha = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Ignorar o cabeçalho
                }
                String[] dados = linha.split(",");
                String cpf = dados[0];
                String nome = dados[1];
                String senha = dados[2]; // Carregar a senha do arquivo CSV
                int numeroAgencia = Integer.parseInt(dados[3]);
                int numeroConta = Integer.parseInt(dados[4]);
                String tipoConta = dados[5];
                double saldo = Double.parseDouble(dados[6]);
    
                Cliente cliente = buscarClientePorCpf(clientes, cpf);
                if (cliente == null) {
                    cliente = new Cliente(nome, cpf, senha, "", ""); // Adicionar a senha ao cliente
                    clientes.add(cliente);
                }
    
                Agencia agencia = buscarAgenciaPorNumero(agencias, numeroAgencia);
                if (agencia == null) {
                    agencia = new Agencia(numeroAgencia, "Agencia " + numeroAgencia);
                    agencias.add(agencia);
                }
    
                Conta conta = criarContaPorTipo(tipoConta, cliente, agencia, numeroConta, saldo);
                cliente.adicionarConta(conta);
    
                // Carregar transações da conta
                carregarTransacoes(conta);
            }
            System.out.println("Dados carregados com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private static Conta criarContaPorTipo(String tipoConta, Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial) {
        switch (tipoConta.toLowerCase()) {
            case "contapoupanca":
                return new ContaPoupanca(cliente, agencia, numeroConta, saldoInicial, 0.05);
            case "contacorrente":
                return new ContaCorrente(cliente, agencia, numeroConta, saldoInicial, 1000.0, 10.0); // Incluindo limiteChequeEspecial e taxaManutencao
            case "contasalario":
                return new ContaSalario(cliente, agencia, numeroConta, saldoInicial, 3);
            default:
                throw new IllegalArgumentException("Tipo de conta desconhecido: " + tipoConta);
        }
    }

    private static Cliente buscarClientePorCpf(List<Cliente> clientes, String cpf) {
        for (Cliente cliente : clientes) {
            if (cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }
        return null;
    }

    private static Agencia buscarAgenciaPorNumero(List<Agencia> agencias, int numero) {
        for (Agencia agencia : agencias) {
            if (agencia.getNumeroAgencia() == numero) {
                return agencia;
            }
        }
        return null;
    }

    private static void carregarTransacoes(Conta conta) {
        String tipoConta = conta.getClass().getSimpleName();
        String nomeArquivo = "transacoes_" + tipoConta + "_" + conta.getAgencia().getNumeroAgencia() + "_" + conta.getNumeroConta() + ".csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            boolean isFirstLine = true;
            while ((linha = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Ignorar o cabeçalho
                }
                String[] dados = linha.split(",");
                if (dados.length < 4) {
                    continue; // Ignorar linhas malformadas
                }
                String descricao = dados[2];
                double valor = Double.parseDouble(dados[3]);
                Movimentacao movimentacao = new Movimentacao(descricao, valor, conta.getCliente());
                conta.getMovimentacoes().add(movimentacao); // Adicionar a transação à lista de movimentações
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}