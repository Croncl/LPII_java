package controller;

import model.Cliente;
import model.Conta;
import view.Visao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Controlador: é responsável por gerenciar a lógica de negócios e interagir com as camadas Model (Cliente e Conta)
 * e View (Visao). Esta classe segue o padrão MVC, separando a lógica de controle da interface e dos dados.
 */
public class Controlador {
    // Lista para armazenar os clientes registrados no sistema.
    private List<Cliente> clientes;
    
    // Instância da classe Visao para interagir com o usuário.
    private Visao visao;
    
    // Formatação decimal para exibir saldos com duas casas decimais.
    private static final DecimalFormat df = new DecimalFormat("#.00");

    // Construtor padrão da classe Controlador.
    public Controlador() {
        this.clientes = new ArrayList<>();
        this.visao = new Visao();
    }

    /**
     * Método principal que controla o fluxo do menu do sistema, permitindo ao usuário acessar diferentes funcionalidades.
     */
    public void iniciar() {
        while (true) {
            visao.exibirMensagem("\n===============================");
            visao.exibirMensagem("       MENU PRINCIPAL");
            visao.exibirMensagem("===============================\n");
            visao.exibirMensagem("1. Cadastro de Usuário");
            visao.exibirMensagem("2. Cadastro de Conta");
            visao.exibirMensagem("3. Operações Bancárias");
            visao.exibirMensagem("4. Sair");
            visao.exibirMensagem("\n===============================\n");
            int opcao = (int) visao.lerDouble("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    cadastrarConta();
                    break;
                case 3:
                    operacoesBancarias();
                    break;
                case 4:
                    return;
                default:
                    visao.exibirMensagem("Opção inválida!");
            }
        }
    }

    /**
     * Cadastra um novo usuário no sistema, solicitando dados via a View.
     */
    private void cadastrarUsuario() {
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       CADASTRO DE USUÁRIO");
        visao.exibirMensagem("===============================\n");
        String nome = visao.lerString("Nome: ");
        String cpf = visao.lerString("CPF: ");
        String senha = visao.lerString("Senha: ");
        String email = visao.lerString("Email: ");
        String telefone = visao.lerString("Telefone: ");
        Cliente cliente = new Cliente(nome, cpf, senha, email, telefone);
        clientes.add(cliente);
        visao.exibirMensagem("Usuário cadastrado com sucesso!");
    }

    /**
     * Cadastra uma nova conta associada a um usuário existente.
     */
    private void cadastrarConta() {
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       CADASTRO DE CONTA");
        visao.exibirMensagem("===============================\n");
        String cpf = visao.lerString("CPF do usuário: ");
        Cliente cliente = buscarClientePorCpf(cpf);
        if (cliente == null) {
            visao.exibirMensagem("Usuário não encontrado!");
            return;
        }
        int agencia = (int) visao.lerDouble("Agência: ");
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        // Verificação para garantir que não exista outra conta com a mesma numeração na mesma agência
        if (existeContaNaAgencia(agencia, numeroConta)) {
            visao.exibirMensagem("Já existe uma conta com esse número na mesma agência!");
            return;
        }
        
        int tipoConta = (int) visao.lerDouble("Tipo de conta (1: poupanca, 2: corrente, 3: investimento): ");
        String tipo = "";
        switch (tipoConta) {
            case 1:
                tipo = "poupanca";
                break;
            case 2:
                tipo = "corrente";
                break;
            case 3:
                tipo = "investimento";
                break;
            default:
                visao.exibirMensagem("Tipo de conta inválido!");
                return;
        }
        double saldoInicial = visao.lerDouble("Saldo inicial: ");
        Conta conta = new Conta(cliente, agencia, numeroConta, tipo, saldoInicial);
        cliente.adicionarConta(conta);
        visao.exibirMensagem("Conta cadastrada com sucesso!");
    }
    
    private boolean existeContaNaAgencia(int agencia, int numeroConta) {
        for (Cliente cliente : clientes) {
            for (Conta conta : cliente.getContas()) {
                if (conta.getAgencia() == agencia && conta.getNumeroConta() == numeroConta) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gera o menu de operações bancárias para o cliente, permitindo consultar saldo, depositar, sacar e transferir.
     */
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
            visao.exibirMensagem("Agência: " + conta.getAgencia() + ", Número da Conta: " + conta.getNumeroConta() + ", Tipo: " + conta.getTipo() + ", Saldo: " + df.format(conta.getSaldo()));
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
            visao.exibirMensagem("6. Sair da Conta");
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
                    return;
                default:
                    visao.exibirMensagem("Opção inválida!");
            }
        }
    }

    // Abaixo estão os métodos para cada operação bancária.
    // Funções auxiliares seguem um padrão de busca por cliente ou conta, e checagem de senha quando necessário.
    // Métodos específicos como depositar() ou transferir() chamam métodos da classe Conta para realizar as operações.

    private void pesquisarContas(Cliente cliente) {
        // Exibe o cabeçalho de pesquisa de contas
        visao.exibirMensagem("\n===============================");
        visao.exibirMensagem("       PESQUISAR CONTAS");
        visao.exibirMensagem("===============================\n");
        
        // Obtém a lista de contas do cliente
        List<Conta> contas = cliente.getContas();
        
        // Verifica se o cliente possui contas registradas
        if (contas.isEmpty()) {
            visao.exibirMensagem("Nenhuma conta encontrada para o usuário.");
        } else {
            // Exibe as contas encontradas com detalhes de agência, número, tipo e saldo
            visao.exibirMensagem("Contas encontradas:");
            for (Conta conta : contas) {
                visao.exibirMensagem("Agência: " + conta.getAgencia() + ", Número da Conta: " 
                                    + conta.getNumeroConta() + ", Tipo: " + conta.getTipo() 
                                    + ", Saldo: " + df.format(conta.getSaldo()));
            }
        }
    }
    

    private Cliente buscarClientePorCpf(String cpf) {
        // Percorre a lista de clientes e procura um cliente com o CPF especificado
        for (Cliente cliente : clientes) {
            if (cliente.getCpf().equals(cpf)) {
                return cliente; // Retorna o cliente se o CPF for encontrado
            }
        }
        return null; // Retorna null se o CPF não for encontrado
    }
    
    private void consultarSaldo(Cliente cliente) {
        // Solicita o número da conta ao usuário
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        
        // Busca a conta específica do cliente com o número fornecido
        Conta conta = buscarContaPorNumero(cliente, numeroConta);
        
        // Exibe uma mensagem se a conta não for encontrada
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        
        // Exibe o saldo da conta encontrada
        visao.exibirMensagem("Saldo: " + df.format(conta.getSaldo()));
    }
    
    private void depositar(Cliente cliente) {
        // Solicita o número da conta e a quantia para depósito
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        Conta conta = buscarContaPorNumero(cliente, numeroConta);
        
        // Verifica se a conta existe
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        
        double quantia = visao.lerDouble("Quantia a depositar: ");
        
        // Executa o depósito e confirma a operação
        conta.depositar(quantia);
        visao.exibirMensagem("Depósito realizado com sucesso!");
    }
    
    private void sacar(Cliente cliente) {
        // Solicita o número da conta e a quantia para saque
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        Conta conta = buscarContaPorNumero(cliente, numeroConta);
        
        // Verifica se a conta existe
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        
        double quantia = visao.lerDouble("Quantia a sacar: ");
        
        // Realiza o saque e exibe a mensagem com base no saldo
        if (conta.sacar(quantia)) {
            visao.exibirMensagem("Saque realizado com sucesso!");
        } else {
            visao.exibirMensagem("Saldo insuficiente!");
        }
    }
    
    private void transferir(Cliente cliente) {
        // Solicita o número da conta de origem e busca a conta
        int numeroContaOrigem = (int) visao.lerDouble("Número da conta de origem: ");
        Conta contaOrigem = buscarContaPorNumero(cliente, numeroContaOrigem);
        
        // Verifica se a conta de origem existe
        if (contaOrigem == null) {
            visao.exibirMensagem("Conta de origem não encontrada!");
            return;
        }
        
        // Solicita o CPF do destinatário e busca o cliente de destino
        String cpfDestino = visao.lerString("CPF do destinatário: ");
        Cliente clienteDestino = buscarClientePorCpf(cpfDestino);
        
        // Verifica se o cliente de destino foi encontrado
        if (clienteDestino == null) {
            visao.exibirMensagem("Destinatário não encontrado!");
            return;
        }
        
        // Solicita o número da conta de destino e busca a conta
        int numeroContaDestino = (int) visao.lerDouble("Número da conta de destino: ");
        Conta contaDestino = buscarContaPorNumero(clienteDestino, numeroContaDestino);
        
        // Verifica se a conta de destino existe
        if (contaDestino == null) {
            visao.exibirMensagem("Conta de destino não encontrada!");
            return;
        }
        
        double quantia = visao.lerDouble("Quantia a transferir: ");
        
        // Realiza a transferência e exibe a mensagem com base no saldo
        if (contaOrigem.transferir(contaDestino, quantia)) {
            visao.exibirMensagem("Transferência realizada com sucesso!");
        } else {
            visao.exibirMensagem("Saldo insuficiente!");
        }
    }
    
    private Conta buscarContaPorNumero(Cliente cliente, int numero) {
        // Percorre a lista de contas do cliente e retorna a conta com o número especificado
        for (Conta conta : cliente.getContas()) {
            if (conta.getNumeroConta() == numero) {
                return conta;
            }
        }
        return null; // Retorna null se a conta não for encontrada
    }
    
}