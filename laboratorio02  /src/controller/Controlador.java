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
import java.io.File;

import exceptions.LimiteDeSaquesException;
import exceptions.SaldoInsuficienteException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controlador {
    private List<Cliente> clientes;
    private List<Agencia> agencias;
    private Visao visao;
    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static final String ARQUIVO_DADOS = "dados.csv";
    private ScheduledExecutorService scheduler;

//Construtor da classe controlador
    public Controlador() {
        this.clientes = new ArrayList<>();
        this.agencias = new ArrayList<>();
        this.visao = new Visao();
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists()) {
            GerenciadorDeArquivos.carregarDados(clientes, agencias); // Carregar dados se o arquivo existir
        }
        iniciarAgendamentoDeTaxas();// Inicia o agendamento das taxas
    }

    public void iniciar() {
        try {
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
                        return; // Sair do loop e encerrar o programa
                    default:
                        visao.exibirMensagem("Opção inválida!");
                }
            }
        } finally {
            encerrar();
        }
    }

    private void encerrar() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        GerenciadorDeArquivos.salvarDados(clientes); // Salvar dados automaticamente ao sair do programa
        System.out.println("Dados salvos com sucesso!");
    }

    // Cria uma conta do tipo especificado
    private Conta criarContaPorTipo(String tipoConta, Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial) {
        switch (tipoConta.toLowerCase()) {
            case "poupanca":
                return new ContaPoupanca(cliente, agencia, numeroConta, saldoInicial, 0.05);
            case "corrente":
                return new ContaCorrente(cliente, agencia, numeroConta, saldoInicial, 1000.0, 10.0); // Incluindo limiteChequeEspecial e taxaManutencao
            case "salario":
                return new ContaSalario(cliente, agencia, numeroConta, saldoInicial, 3);
            default:
                throw new IllegalArgumentException("Tipo de conta desconhecido: " + tipoConta);
        }
    }

    private int gerarNumeroConta(Agencia agencia) {
        int maiorNumeroConta = 0;
        for (Cliente cliente : clientes) {
            for (Conta conta : cliente.getContas()) {
                if (conta.getAgencia().equals(agencia) && conta.getNumeroConta() > maiorNumeroConta) {
                    maiorNumeroConta = conta.getNumeroConta();
                }
            }
        }
        return maiorNumeroConta + 1;
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
    // Abre uma nova conta para um cliente
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
            visao.exibirMensagem("Usuário encontrado. Por favor, insira sua senha para abrir uma nova conta.");
            String senha = visao.lerString("Senha: ");
            if (!cliente.getSenha().equals(senha)) {
                visao.exibirMensagem("Senha incorreta! Operação cancelada.");
                return;
            }
            visao.exibirMensagem("Senha correta. Deseja abrir uma nova conta?");
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
        String tipoConta = obterTipoConta();
        if (tipoConta == null) {
            return;
        }
        int numeroConta = gerarNumeroConta(agencia);
        double saldoInicial = visao.lerDouble("Saldo inicial: ");
        Conta conta = criarContaPorTipo(tipoConta, cliente, agencia, numeroConta, saldoInicial);
        cliente.adicionarConta(conta);
        conta.registrarTransacao("Conta criada com saldo inicial de " + saldoInicial);
        GerenciadorDeArquivos.salvarDados(clientes); // Salvar dados automaticamente após cadastrar conta
        visao.exibirMensagem("Conta cadastrada com sucesso!");
        exibirDadosConta(conta); // Exibir os dados da conta criada
    }

    private void exibirDadosConta(Conta conta) {
        visao.exibirMensagem("Agência: " + conta.getAgencia().getCodigo() + 
                             ", Número da Conta: " + conta.getNumeroConta() + 
                             ", Tipo: " + conta.getClass().getSimpleName() + 
                             ", Saldo: " + df.format(conta.getSaldo()));
    }

    private Agencia buscarAgenciaPorNumero(int numero) {
        for (Agencia agencia : agencias) {
            if (agencia.getNumeroAgencia() == numero) {
                return agencia;
            }
        }
        return null;
    }
    // Gerencia as operações bancárias
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
            exibirDadosConta(conta);
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
            visao.exibirMensagem("7. Voltar");
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

    private void consultarSaldo(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        Conta conta = buscarContaPorNumeroAgencia(cliente, numeroConta, numeroAgencia);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        visao.exibirMensagem("Saldo: " + df.format(conta.getSaldo()));
    }

    private void exibirExtrato(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        Conta conta = buscarContaPorNumeroAgencia(cliente, numeroConta, numeroAgencia);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        conta.exibirExtrato();
    }
    
    private void depositar(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        Conta conta = buscarContaPorNumeroAgencia(cliente, numeroConta, numeroAgencia);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        double quantia = visao.lerDouble("Quantia a depositar: ");
        conta.depositar(quantia);
        visao.exibirMensagem("Depósito realizado com sucesso!");
        GerenciadorDeArquivos.salvarDados(clientes); // Salvar dados automaticamente após depósito
    }
    
    private void sacar(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        Conta conta = buscarContaPorNumeroAgencia(cliente, numeroConta, numeroAgencia);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        double quantia = visao.lerDouble("Quantia a sacar: ");
        try {
            conta.sacar(quantia);
            visao.exibirMensagem("Saque realizado com sucesso!");
            GerenciadorDeArquivos.salvarDados(clientes); // Salvar dados automaticamente após saque
        } catch (SaldoInsuficienteException e) {
            visao.exibirMensagem(e.getMessage());
        } catch (LimiteDeSaquesException e) {
            visao.exibirMensagem(e.getMessage());
        }
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

    private void transferir(Cliente cliente) {
        int numeroAgenciaOrigem = (int) visao.lerDouble("Número da agência de origem: ");
        int numeroContaOrigem = (int) visao.lerDouble("Número da conta de origem: ");
        
        Conta contaOrigem = buscarContaPorNumeroAgencia(cliente, numeroContaOrigem, numeroAgenciaOrigem);
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
    
        Conta contaDestino = buscarContaPorNumeroAgencia(clienteDestino, numeroContaDestino, numeroAgenciaDestino);
        if (contaDestino == null) {
            visao.exibirMensagem("Conta de destino não encontrada!");
            return;
        }
        double quantia = visao.lerDouble("Quantia a transferir: ");
        try {
            contaOrigem.transferir(contaDestino, quantia);
            visao.exibirMensagem("Transferência realizada com sucesso!");
            GerenciadorDeArquivos.salvarDados(clientes); // Salvar dados automaticamente após transferência
        } catch (SaldoInsuficienteException e) {
            visao.exibirMensagem(e.getMessage());
        } catch (LimiteDeSaquesException e) {
            visao.exibirMensagem(e.getMessage());
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
    
        Conta contaDestino = buscarContaPorNumeroAgencia(clienteDestino, numeroContaDestino, numeroAgenciaDestino);
        if (contaDestino == null) {
            visao.exibirMensagem("Conta de destino não encontrada!");
            return;
        }
        double quantia = contaOrigem.getSaldo();
        try {
            contaOrigem.transferir(contaDestino, quantia);
            visao.exibirMensagem("Transferência de " + quantia + " realizada com sucesso!");
            GerenciadorDeArquivos.salvarDados(clientes); // Salvar dados automaticamente após transferência
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
    
        List<Conta> contas = cliente.getContas();
        if (contas.isEmpty()) {
            visao.exibirMensagem("Nenhuma conta encontrada para este titular.");
            return;
        }
    
        for (Conta conta : contas) {
            visao.exibirMensagem("Conta: " + conta.getNumeroConta() + ", Agência: " + conta.getAgencia().getNumeroAgencia() + ", Tipo: " + conta.getClass().getSimpleName() + ", Saldo: " + df.format(conta.getSaldo()));
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
            exibirDadosConta(conta);
        }
        visao.exibirMensagem("\n===============================\n");
    
        visao.exibirMensagem("1. Excluir Conta");
        visao.exibirMensagem("2. Voltar");
        int opcao = (int) visao.lerDouble("Escolha uma opção: ");
    
        switch (opcao) {
            case 1:
                excluirConta(cliente);
                break;
            case 2:
                return;
            default:
                visao.exibirMensagem("Opção inválida!");
        }
    }

    private void excluirConta(Cliente cliente) {
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        Conta conta = buscarContaPorNumeroAgencia(cliente, numeroConta, numeroAgencia);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
    
        while (conta.getSaldo() > 0) {
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
    
        conta.registrarTransacao("Conta excluída");
        Arquivador.arquivarDadosDaConta(conta); // Arquivar os dados da conta e o log de transações antes de remover a conta
        cliente.removerConta(conta);
        visao.exibirMensagem("Conta excluída com sucesso!");
        GerenciadorDeArquivos.salvarDados(clientes); // Salvar dados automaticamente após excluir conta
    }

    private Conta buscarContaPorNumeroAgencia(Cliente cliente, int numeroConta, int numeroAgencia) {
        for (Conta conta : cliente.getContas()) {
            if (conta.getNumeroConta() == numeroConta &&
                conta.getAgencia().getNumeroAgencia() == numeroAgencia) {
                return conta;
            }
        }
        return null;
    }
    
    private void iniciarAgendamentoDeTaxas() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::aplicarTaxas, 0, 30, TimeUnit.DAYS); // Aplica as taxas a cada 30 dias
    }
    
    // Aplica as taxas de manutenção e rendimentos
    private void aplicarTaxas() {
        for (Cliente cliente : clientes) {
            for (Conta conta : cliente.getContas()) {
                if (conta instanceof ContaCorrente) {
                    ContaCorrente contaCorrente = (ContaCorrente) conta;
                    try {
                        contaCorrente.aplicarTaxaManutencao();
                    } catch (SaldoInsuficienteException e) {
                        visao.exibirMensagem("Saldo insuficiente para aplicar taxa de manutenção na conta " + conta.getNumeroConta());
                    }
                } else if (conta instanceof ContaPoupanca) {
                    ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
                    contaPoupanca.aplicarRendimento();
                }
            }
        }
    }
}