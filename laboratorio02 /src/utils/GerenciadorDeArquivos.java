package utils;

import model.Agencia;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.ContaSalario;
import model.Cliente;
import java.io.*;
import java.util.List;

public class GerenciadorDeArquivos {

    public static void salvarDados(List<Cliente> clientes, String arquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            // Salvar clientes e contas
            for (Cliente cliente : clientes) {
                writer.write("CLIENTE," + cliente.getNome() + "," + cliente.getCpf() + "," + cliente.getSenha() + "," + cliente.getEmail() + "," + cliente.getTelefone());
                writer.newLine();
                for (Conta conta : cliente.getContas()) {
                    String tipoConta = conta.getClass().getSimpleName();
                    writer.write("CONTA," + tipoConta + "," + cliente.getCpf() + "," + conta.getNumeroConta() + "," + conta.getSaldo() + "," + conta.getAgencia().getNumeroAgencia());
                    writer.newLine();
                }
            }
        }
    }

    public static void carregarDados(List<Cliente> clientes, List<Agencia> agencias, String arquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(",");

                switch (partes[0]) {
                    case "CLIENTE":
                        Cliente cliente = new Cliente(partes[1], partes[2], partes[3], partes[4], partes[5]);
                        clientes.add(cliente);
                        break;
                    case "CONTA":
                        String tipoConta = partes[1];
                        String cpfCliente = partes[2];
                        Cliente clienteConta = buscarClientePorCpf(clientes, cpfCliente);
                        if (clienteConta == null) {
                            throw new IllegalArgumentException("Cliente não encontrado para CPF: " + cpfCliente);
                        }
                        int numeroConta = Integer.parseInt(partes[3]);
                        double saldoInicial = Double.parseDouble(partes[4]);
                        int numeroAgencia = Integer.parseInt(partes[5]);
                        Agencia agenciaConta = buscarAgenciaPorNumero(agencias, numeroAgencia);
                        if (agenciaConta == null) {
                            agenciaConta = new Agencia(numeroAgencia, "Agencia " + numeroAgencia);
                            agencias.add(agenciaConta);
                        }
                        Conta conta = criarContaPorTipo(tipoConta, clienteConta, agenciaConta, numeroConta, saldoInicial);
                        clienteConta.adicionarConta(conta);
                        agenciaConta.adicionarConta(conta);
                        break;
                }
            }
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

    private static Conta criarContaPorTipo(String tipoConta, Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial) {
        switch (tipoConta) {
            case "ContaCorrente":
                return new ContaCorrente(cliente, agencia, numeroConta, saldoInicial, 10.0); // Exemplo de taxa de manutenção
            case "ContaPoupanca":
                return new ContaPoupanca(cliente, agencia, numeroConta, saldoInicial, 0.05); // Exemplo de taxa de rendimento
            case "ContaSalario":
                return new ContaSalario(cliente, agencia, numeroConta, saldoInicial, 3); // Exemplo de limite de saques
            default:
                throw new IllegalArgumentException("Tipo de conta desconhecido: " + tipoConta);
        }
    }
} 