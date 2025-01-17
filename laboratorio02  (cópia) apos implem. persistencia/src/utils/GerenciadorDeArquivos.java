package utils;

import model.Agencia;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.ContaSalario;
import model.Cliente;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorDeArquivos {

    /**
     * Salva os dados das agências e contas em um arquivo CSV.
     *
     * @param clientes Lista de clientes a serem salvos.
     * @param agencias Lista de agências a serem salvas.
     * @param arquivo  Caminho do arquivo onde os dados serão salvos.
     * @throws IOException Se ocorrer um erro de escrita no arquivo.
     */
    public static void salvarDados(List<Cliente> clientes, List<Agencia> agencias, String arquivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            // Salvar agências
            for (Agencia agencia : agencias) {
                writer.write("AGENCIA," + agencia.getNumeroAgencia() + "," + agencia.getNome());
                writer.newLine();
            }

            // Salvar clientes e contas
            for (Cliente cliente : clientes) {
                writer.write("CLIENTE," + cliente.getNome() + "," + cliente.getCpf() + "," + cliente.getSenha() + "," + cliente.getEmail() + "," + cliente.getTelefone());
                writer.newLine();
                for (Conta conta : cliente.getContas()) {
                    String tipoConta = conta.getClass().getSimpleName();
                    writer.write("CONTA," + tipoConta + "," + conta.getNumeroConta() + "," + conta.getSaldo() + "," + conta.getAgencia().getNumeroAgencia());
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Carrega os dados de um arquivo CSV e recria as agências e contas.
     *
     * @param clientes Lista de clientes a ser preenchida.
     * @param agencias Lista de agências a ser preenchida.
     * @param arquivo  Caminho do arquivo de onde os dados serão carregados.
     * @throws IOException Se ocorrer um erro de leitura no arquivo.
     */
    public static void carregarDados(List<Cliente> clientes, List<Agencia> agencias, String arquivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(",");

                switch (partes[0]) {
                    case "AGENCIA":
                        agencias.add(new Agencia(Integer.parseInt(partes[1]), partes[2]));
                        break;
                    case "CLIENTE":
                        Cliente cliente = new Cliente(partes[1], partes[2], partes[3], partes[4], partes[5]);
                        clientes.add(cliente);
                        break;
                    case "CONTA":
                        Cliente clienteConta = buscarClientePorCpf(clientes, partes[2]);
                        Agencia agenciaConta = buscarAgenciaPorNumero(agencias, Integer.parseInt(partes[4]));
                        Conta conta = criarContaPorTipo(partes[1], clienteConta, agenciaConta, Integer.parseInt(partes[2]), Double.parseDouble(partes[3]));
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