package controller;

import model.Agencia;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.ContaSalario;
import model.Arquivador;
import utils.GerenciadorDeArquivos;
import view.Visao;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import exceptions.LimiteDeSaquesException;
import exceptions.SaldoInsuficienteException;

public class Controlador {
    private List<Cliente> clientes;
    private List<Agencia> agencias;
    private Visao visao;
    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static final String ARQUIVO_DADOS = "dados.csv";

    public Controlador() {
        this.clientes = new ArrayList<>();
        this.agencias = new ArrayList<>();
        this.visao = new Visao();
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists()) {
            carregarDados(); // Carregar dados se o arquivo existir
        }
    }

    public void iniciar() {
        while (true) {
            visao.exibirMensagem("\n===============================");
            visao.exibirMensagem("       MENU PRINCIPAL");
            visao.exibirMensagem("===============================\n");
            visao.exibirMensagem("1. Abrir sua Conta");
            visao.exibirMensagem("2. Operações Bancárias");
            visao.exibirMensagem("3. Gerenciar Contas");
            visao.exibirMensagem("4. Sair");
            visao.exibirMensagem("\n===============================\n");
            int opcao = (int) visao.lerDouble("Escolha uma opção: ");
    
            switch (opcao) {
                case 1:
                    abrirConta();
                    break;
                case 2:
                    operacoesBancarias();
                    break;
                case 3:
                    gerenciarConta();
                    break;
                case 4:
                    salvarDados(); // Salvar dados automaticamente ao sair do programa
                    return;
                default:
                    visao.exibirMensagem("Opção inválida!");
            }
        }
    }

    private Conta criarContaPorTipo(String tipoConta, Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial) {
        switch (tipoConta.toLowerCase()) {
            case "poupanca":
                return new ContaPoupanca(cliente, agencia, numeroConta, saldoInicial, 0.05);
            case "corrente":
                return new ContaCorrente(cliente, agencia, numeroConta, saldoInicial, 10.0);
            case "salario":
                return new ContaSalario(cliente, agencia, numeroConta, saldoInicial, 3);
            default:
                throw new IllegalArgumentException("Tipo de conta desconhecido: " + tipoConta);
        }
    }

    private String obterTipoConta() {
        visao.exibirMensagem("Tipo de conta:");
        visao.exibirMensagem("1. Poupança");
        visao.exibirMensagem("2. Corrente");
        visao.exibirMensagem("3. Salário");
        int opcaoTipoConta = (int) visao.lerDouble("Escolha uma opção: ");
        
        switch (opcaoTipoConta) {
            case 1:
                return "poupanca";
            case 2:
                return "corrente";
            case 3:
                return "salario";
            default:
                visao.exibirMensagem("Opção inválida!");
                return null;
        }
    }

    private void abrirConta() {
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       ABERTURA DE CONTA");
        visao.exibirMensagem("===============================\n");
        String cpf = visao.lerString("CPF: ");
        Cliente cliente = buscarClientePorCpf(cpf);
        if (cliente == null) {
            visao.exibirMensagem("Usuário não encontrado. Deseja se cadastrar?");
            visao.exibirMensagem("1. Sim");
            visao.exibirMensagem("2. Não");
            int opcao = (int) visao.lerDouble("Escolha uma opção: ");
            if (opcao == 1) {
                String nome = visao.lerString("Nome: ");
                String senha = visao.lerString("Senha: ");
                String email = visao.lerString("Email: ");
                String telefone = visao.lerString("Telefone: ");
                cliente = new Cliente(nome, cpf, senha, email, telefone);
                clientes.add(cliente);
                visao.exibirMensagem("Usuário cadastrado com sucesso!");
            } else {
                visao.exibirMensagem("Operação cancelada.");
                return;
            }
        } else {
            visao.exibirMensagem("Usuário encontrado. Deseja abrir uma nova conta?");
            visao.exibirMensagem("1. Sim");
            visao.exibirMensagem("2. Não");
            int opcao = (int) visao.lerDouble("Escolha uma opção: ");
            if (opcao != 1) {
                visao.exibirMensagem("Operação cancelada.");
                return;
            }
        }

        int numeroAgencia = (int) visao.lerDouble("Número da agência (001-999): ");
        if (numeroAgencia < 1 || numeroAgencia > 999) {
            visao.exibirMensagem("Número da agência inválido!");
            return;
        }
        Agencia agencia = buscarAgenciaPorNumero(numeroAgencia);
        if (agencia == null) {
            agencia = new Agencia(numeroAgencia, "Agencia " + numeroAgencia);
            agencias.add(agencia);
        }
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        String tipoConta = obterTipoConta();
        if (tipoConta == null) {
            return;
        }
        if (existeContaNaAgencia(agencia, numeroConta, tipoConta)) {
            visao.exibirMensagem("Já existe uma conta com esse número e tipo na mesma agência!");
            return;
        }
        double saldoInicial = visao.lerDouble("Saldo inicial: ");
        Conta conta = criarContaPorTipo(tipoConta, cliente, agencia, numeroConta, saldoInicial);
        cliente.adicionarConta(conta);
        salvarDados(); // Salvar dados automaticamente após cadastrar conta
        visao.exibirMensagem("Conta cadastrada com sucesso!");
    }

    private boolean existeContaNaAgencia(Agencia agencia, int numeroConta, String tipoConta) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_DADOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                int numeroAgenciaArquivo = Integer.parseInt(dados[2]);
                int numeroContaArquivo = Integer.parseInt(dados[3]);
                String tipoContaArquivo = dados[4];
                if (numeroAgenciaArquivo == agencia.getNumeroAgencia() &&
                    numeroContaArquivo == numeroConta &&
                    tipoContaArquivo.equalsIgnoreCase(tipoConta)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Agencia buscarAgenciaPorNumero(int numero) {
        for (Agencia agencia : agencias) {
            if (agencia.getNumeroAgencia() == numero) {
                return agencia;
            }
        }
        return null;
    }

    private void operacoesBancarias() {
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       OPERAÇÕES BANCÁRIAS");
        visao.exibirMensagem("===============================\n");
        String cpf = visao.lerString("CPF do usuário: ");
        Cliente cliente = buscarClientePorCpf(cpf);
        if (cliente == null) {
            visao.exibirMensagem("Usuário não encontrado!");
            return;
        }
        String senha = visao.lerString("Senha: ");
        if (!cliente.getSenha().equals(senha)) {
            visao.exibirMensagem("Senha incorreta!");
            return;
        }
    
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       CONTAS DO USUÁRIO");
        visao.exibirMensagem("===============================\n");
        List<Conta> contas = cliente.getContas();
        for (Conta conta : contas) {
            visao.exibirMensagem("Agência: " + conta.getAgencia().getCodigo() + ", Número da Conta: " + conta.getNumeroConta() + ", Tipo: " + conta.getClass().getSimpleName() + ", Saldo: " + df.format(conta.getSaldo()));
        }
        visao.exibirMensagem("\n===============================\n");
    
        while (true) {
            visao.exibirMensagem("\n===============================");
            visao.exibirMensagem("       MENU DE OPERAÇÕES");
            visao.exibirMensagem("===============================\n");
            visao.exibirMensagem("1. Consultar Saldo");
            visao.exibirMensagem("2. Depositar");
            visao.exibirMensagem("3. Sacar");
            visao.exibirMensagem("4. Transferir");
            visao.exibirMensagem("5. Pesquisar Contas");
            visao.exibirMensagem("6. Exibir Extrato");
            visao.exibirMensagem("7. Sair da Conta");
            visao.exibirMensagem("\n===============================\n");
            int opcao = (int) visao.lerDouble("Escolha uma opção: ");
    
            switch (opcao) {
                case 1:
                    consultarSaldo(cliente);
                    break;
                case 2:
                    depositar(cliente);
                    break;
                case 3:
                    sacar(cliente);
                    break;
                case 4:
                    transferir(cliente);
                    break;
                case 5:
                    pesquisarContas(cliente);
                    break;
                case 6:
                    exibirExtrato(cliente);
                    break;
                case 7:
                    return;
                default:
                    visao.exibirMensagem("Opção inválida!");
            }
        }
    }

    private void exibirExtrato(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        String tipoConta = obterTipoConta();
        if (tipoConta == null) {
            return;
        }
        Conta conta = buscarContaPorNumeroAgenciaETipo(cliente, numeroConta, numeroAgencia, tipoConta);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        conta.exibirExtrato();
    }

    private void consultarSaldo(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        String tipoConta = obterTipoConta();
        if (tipoConta == null) {
            return;
        }
        Conta conta = buscarContaPorNumeroAgenciaETipo(cliente, numeroConta, numeroAgencia, tipoConta);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        visao.exibirMensagem("Saldo: " + df.format(conta.getSaldo()));
    }

    private void depositar(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        String tipoConta = obterTipoConta();
        if (tipoConta == null) {
            return;
        }
        Conta conta = buscarContaPorNumeroAgenciaETipo(cliente, numeroConta, numeroAgencia, tipoConta);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        double quantia = visao.lerDouble("Quantia a depositar: ");
        conta.depositar(quantia);
        visao.exibirMensagem("Depósito realizado com sucesso!");
        salvarDados(); // Salvar dados automaticamente após depósito
    }

    private void sacar(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        String tipoConta = obterTipoConta();
        if (tipoConta == null) {
            return;
        }
        Conta conta = buscarContaPorNumeroAgenciaETipo(cliente, numeroConta, numeroAgencia, tipoConta);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        double quantia = visao.lerDouble("Quantia a sacar: ");
        try {
            conta.sacar(quantia);
            visao.exibirMensagem("Saque realizado com sucesso!");
            salvarDados(); // Salvar dados automaticamente após saque
        } catch (SaldoInsuficienteException e) {
            visao.exibirMensagem(e.getMessage());
        } catch (LimiteDeSaquesException e) {
            visao.exibirMensagem(e.getMessage());
        }
    }

    private void transferir(Cliente cliente) {
        int numeroAgenciaOrigem = (int) visao.lerDouble("Número da agência de origem: ");
        int numeroContaOrigem = (int) visao.lerDouble("Número da conta de origem: ");
        String tipoContaOrigem = obterTipoConta();
        if (tipoContaOrigem == null) {
            return;
        }
        Conta contaOrigem = buscarContaPorNumeroAgenciaETipo(cliente, numeroContaOrigem, numeroAgenciaOrigem, tipoContaOrigem);
        if (contaOrigem == null) {
            visao.exibirMensagem("Conta de origem não encontrada!");
            return;
        }
        String cpfDestino = visao.lerString("CPF do destinatário: ");
        Cliente clienteDestino = buscarClientePorCpf(cpfDestino);
        if (clienteDestino == null) {
            visao.exibirMensagem("Destinatário não encontrado!");
            return;
        }
        int numeroAgenciaDestino = (int) visao.lerDouble("Número da agência de destino: ");
        int numeroContaDestino = (int) visao.lerDouble("Número da conta de destino: ");
        String tipoContaDestino = obterTipoConta();
        if (tipoContaDestino == null) {
            return;
        }
        Conta contaDestino = buscarContaPorNumeroAgenciaETipo(clienteDestino, numeroContaDestino, numeroAgenciaDestino, tipoContaDestino);
        if (contaDestino == null) {
            visao.exibirMensagem("Conta de destino não encontrada!");
            return;
        }
        double quantia = visao.lerDouble("Quantia a transferir: ");
        try {
            contaOrigem.transferir(contaDestino, quantia);
            visao.exibirMensagem("Transferência realizada com sucesso!");
            salvarDados(); // Salvar dados automaticamente após transferência
        } catch (SaldoInsuficienteException e) {
            visao.exibirMensagem(e.getMessage());
        } catch (LimiteDeSaquesException e) {
            visao.exibirMensagem(e.getMessage());
        }
    }

    private void pesquisarContas(Cliente cliente) {
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       PESQUISAR CONTAS");
        visao.exibirMensagem("===============================\n");
    
        if (agencias.isEmpty()) {
            visao.exibirMensagem("Nenhuma agência encontrada.");
            return;
        }
    
        for (Agencia agencia : agencias) {
            visao.exibirMensagem("Agência: " + agencia.getNumeroAgencia() + " - " + agencia.getNome());
            List<Conta> contas = agencia.getContas();
            if (contas.isEmpty()) {
                visao.exibirMensagem("  Nenhuma conta encontrada para esta agência.");
            } else {
                for (Conta conta : contas) {
                    visao.exibirMensagem("  Conta: " + conta.getNumeroConta() + ", Tipo: " + conta.getClass().getSimpleName() + ", Saldo: " + df.format(conta.getSaldo()));
                }
            }
        }
    }

    private Cliente buscarClientePorCpf(String cpf) {
        for (Cliente cliente : clientes) {
            if (cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }
        return null;
    }

    private void salvarDados() {
        try (FileWriter writer = new FileWriter(ARQUIVO_DADOS)) {
            for (Cliente cliente : clientes) {
                for (Conta conta : cliente.getContas()) {
                    writer.write(cliente.getCpf() + "," +
                                cliente.getNome() + "," +
                                conta.getAgencia().getNumeroAgencia() + "," +
                                conta.getNumeroConta() + "," +
                                conta.getClass().getSimpleName() + "," +
                                conta.getSaldo() + "\n");
                }
            }
            visao.exibirMensagem("Dados salvos com sucesso!");
        } catch (IOException e) {
            visao.exibirMensagem("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private void carregarDados() {
        try {
            GerenciadorDeArquivos.carregarDados(clientes, agencias, "dados.csv");
            visao.exibirMensagem("Dados carregados com sucesso!");
        } catch (IOException e) {
            visao.exibirMensagem("Erro ao carregar dados: " + e.getMessage());
        }
    }
    
    private void gerenciarConta() {
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       GERENCIAR CONTA");
        visao.exibirMensagem("===============================\n");
        String cpf = visao.lerString("CPF do usuário: ");
        Cliente cliente = buscarClientePorCpf(cpf);
        if (cliente == null) {
            visao.exibirMensagem("Usuário não encontrado!");
            return;
        }
        String senha = visao.lerString("Senha: ");
        if (!cliente.getSenha().equals(senha)) {
            visao.exibirMensagem("Senha incorreta!");
            return;
        }
    
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       CONTAS DO USUÁRIO");
        visao.exibirMensagem("===============================\n");
        List<Conta> contas = cliente.getContas();
        for (Conta conta : contas) {
            visao.exibirMensagem("Agência: " + conta.getAgencia().getCodigo() + ", Número da Conta: " + conta.getNumeroConta() + ", Tipo: " + conta.getClass().getSimpleName() + ", Saldo: " + df.format(conta.getSaldo()));
        }
        visao.exibirMensagem("\n===============================\n");
    
        visao.exibirMensagem("1. Excluir Conta");
        visao.exibirMensagem("2. Transferir Saldo");
        int opcao = (int) visao.lerDouble("Escolha uma opção: ");
    
        switch (opcao) {
            case 1:
                excluirConta(cliente);
                break;
            case 2:
                transferirSaldoParaOutraConta(cliente, null); // Passar null para a conta de origem, pois será solicitada no método
                break;
            default:
                visao.exibirMensagem("Opção inválida!");
        }
    }
    
    private void excluirConta(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        String tipoConta = obterTipoConta();
        if (tipoConta == null) {
            return;
        }
    
        Conta conta = buscarContaPorNumeroAgenciaETipo(cliente, numeroConta, numeroAgencia, tipoConta);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
    
        if (conta.getSaldo() > 0) {
            visao.exibirMensagem("A conta possui saldo. Escolha uma opção:");
            visao.exibirMensagem("1. Sacar saldo");
            visao.exibirMensagem("2. Transferir saldo para outra conta");
            int opcao = (int) visao.lerDouble("Escolha uma opção: ");
    
            switch (opcao) {
                case 1:
                    sacarSaldo(conta);
                    break;
                case 2:
                    transferirSaldoParaOutraConta(cliente, conta);
                    break;
                default:
                    visao.exibirMensagem("Opção inválida!");
                    return;
            }
        }
    
        Arquivador.arquivarDadosDaConta(conta); // Arquivar os dados da conta e o log de transações antes de remover a conta
        cliente.removerConta(conta);
        visao.exibirMensagem("Conta excluída com sucesso!");
        salvarDados(); // Salvar dados automaticamente após excluir conta
    }

    private Conta buscarContaPorNumeroAgenciaETipo(Cliente cliente, int numeroConta, int numeroAgencia, String tipoConta) {
        for (Conta conta : cliente.getContas()) {
            if (conta.getNumeroConta() == numeroConta &&
                conta.getAgencia().getNumeroAgencia() == numeroAgencia &&
                conta.getClass().getSimpleName().equalsIgnoreCase(tipoConta)) {
                return conta;
            }
        }
        return null;
    }
    
    private void sacarSaldo(Conta conta) {
        double saldo = conta.getSaldo();
        try {
            conta.sacar(saldo);
            visao.exibirMensagem("Saque de " + saldo + " realizado com sucesso!");
        } catch (SaldoInsuficienteException | LimiteDeSaquesException e) {
            visao.exibirMensagem("Erro ao sacar saldo: " + e.getMessage());
        }
    }
    
    private void transferirSaldoParaOutraConta(Cliente cliente, Conta contaOrigem) {
        String cpfDestino = visao.lerString("CPF do destinatário: ");
        Cliente clienteDestino = buscarClientePorCpf(cpfDestino);
        if (clienteDestino == null) {
            visao.exibirMensagem("Destinatário não encontrado!");
            return;
        }
        int numeroAgenciaDestino = (int) visao.lerDouble("Número da agência de destino: ");
        int numeroContaDestino = (int) visao.lerDouble("Número da conta de destino: ");
        String tipoContaDestino = obterTipoConta();
        if (tipoContaDestino == null) {
            return;
        }
    
        Conta contaDestino = buscarContaPorNumeroAgenciaETipo(clienteDestino, numeroContaDestino, numeroAgenciaDestino, tipoContaDestino);
        if (contaDestino == null) {
            visao.exibirMensagem("Conta de destino não encontrada!");
            return;
        }
        double saldo = contaOrigem.getSaldo();
        try {
            contaOrigem.transferir(contaDestino, saldo);
            visao.exibirMensagem("Transferência de " + saldo + " realizada com sucesso!");
        } catch (SaldoInsuficienteException | LimiteDeSaquesException e) {
            visao.exibirMensagem("Erro ao transferir saldo: " + e.getMessage());
        }
    }
}