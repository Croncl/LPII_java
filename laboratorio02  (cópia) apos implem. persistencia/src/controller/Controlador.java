package controller;

import model.Agencia;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.ContaSalario;
import utils.GerenciadorDeArquivos;
import view.Visao;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import exceptions.LimiteDeSaquesException;
import exceptions.SaldoInsuficienteException;

public class Controlador {
    private List<Cliente> clientes;
    private List<Agencia> agencias;
    private Visao visao;
    private static final DecimalFormat df = new DecimalFormat("#.00");

    public Controlador() {
        this.clientes = new ArrayList<>();
        this.agencias = new ArrayList<>();
        this.visao = new Visao();
        inicializarAgencias();
        carregarDados(); // Carregar dados automaticamente ao iniciar o programa
    }

    private void inicializarAgencias() {
        adicionarAgenciaSeNaoExistir(new Agencia(1, "Agencia Alto"));
        adicionarAgenciaSeNaoExistir(new Agencia(2, "Agencia Baixo"));
        adicionarAgenciaSeNaoExistir(new Agencia(3, "Agencia Leste"));
        adicionarAgenciaSeNaoExistir(new Agencia(4, "Agencia Oeste"));
        adicionarAgenciaSeNaoExistir(new Agencia(5, "Agencia Centro"));
    }

    private void adicionarAgenciaSeNaoExistir(Agencia novaAgencia) {
        for (Agencia agencia : agencias) {
            if (agencia.getNumeroAgencia() == novaAgencia.getNumeroAgencia()) {
                return; // Agência já existe, não adiciona novamente
            }
        }
        agencias.add(novaAgencia); // Agência não existe, adiciona à lista
    }

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
                    salvarDados(); // Salvar dados automaticamente ao sair do programa
                    return;
                default:
                    visao.exibirMensagem("Opção inválida!");
            }
        }
    }

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
        salvarDados(); // Salvar dados automaticamente após cadastrar usuário
    }

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
        visao.exibirMensagem("Agências disponíveis:");
        for (Agencia agencia : agencias) {
            visao.exibirMensagem(agencia.getNumeroAgencia() + " - " + agencia.getNome());
        }
        int numeroAgencia = (int) visao.lerDouble("Número da agência: ");
        Agencia agencia = buscarAgenciaPorNumero(numeroAgencia);
        if (agencia == null) {
            visao.exibirMensagem("Agência não encontrada!");
            return;
        }
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        if (existeContaNaAgencia(agencia, numeroConta)) {
            visao.exibirMensagem("Já existe uma conta com esse número na mesma agência!");
            return;
        }
        int tipoConta = (int) visao.lerDouble("Tipo de conta (1: poupanca, 2: corrente, 3: salario): ");
        if (tipoConta < 1 || tipoConta > 3) {
            visao.exibirMensagem("Tipo de conta inválido!");
            return;
        }
        double saldoInicial = visao.lerDouble("Saldo inicial: ");
        Conta conta = criarContaPorTipo(tipoConta, cliente, agencia, numeroConta, saldoInicial);
        cliente.adicionarConta(conta);
        agencia.adicionarConta(conta);
        visao.exibirMensagem("Conta cadastrada com sucesso!");
        salvarDados(); // Salvar dados automaticamente após cadastrar conta
    }

    private boolean existeContaNaAgencia(Agencia agencia, int numeroConta) {
        for (Cliente cliente : clientes) {
            for (Conta conta : cliente.getContas()) {
                if (conta.getAgencia().equals(agencia) && conta.getNumeroConta() == numeroConta) {
                    return true;
                }
            }
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

    private Conta criarContaPorTipo(int tipoConta, Cliente cliente, Agencia agencia, int numeroConta, double saldoInicial) {
        switch (tipoConta) {
            case 1:
                return new ContaPoupanca(cliente, agencia, numeroConta, saldoInicial, 0.05);
            case 2:
                return new ContaCorrente(cliente, agencia, numeroConta, saldoInicial, 10.0);
            case 3:
                return new ContaSalario(cliente, agencia, numeroConta, saldoInicial, 3);
            default:
                throw new IllegalArgumentException("Tipo de conta desconhecido: " + tipoConta);
        }
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

    private void consultarSaldo(Cliente cliente) {
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        Conta conta = buscarContaPorNumero(cliente, numeroConta);
        if (conta == null) {
            visao.exibirMensagem("Conta não encontrada!");
            return;
        }
        visao.exibirMensagem("Saldo: " + df.format(conta.getSaldo()));
    }

    private void depositar(Cliente cliente) {
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        Conta conta = buscarContaPorNumero(cliente, numeroConta);
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
        int numeroConta = (int) visao.lerDouble("Número da conta: ");
        Conta conta = buscarContaPorNumero(cliente, numeroConta);
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
        int numeroContaOrigem = (int) visao.lerDouble("Número da conta de origem: ");
        Conta contaOrigem = buscarContaPorNumero(cliente, numeroContaOrigem);
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
        Agencia agenciaDestino = buscarAgenciaPorNumero(numeroAgenciaDestino);
        if (agenciaDestino == null) {
            visao.exibirMensagem("Agência de destino não encontrada!");
            return;
        }
        int numeroContaDestino = (int) visao.lerDouble("Número da conta de destino: ");
        Conta contaDestino = buscarContaPorNumeroEAgencia(clienteDestino, numeroContaDestino, agenciaDestino);
        if (contaDestino == null) {
            visao.exibirMensagem("Conta de destino não encontrada na agência especificada!");
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

    private Conta buscarContaPorNumero(Cliente cliente, int numero) {
        for (Conta conta : cliente.getContas()) {
            if (conta.getNumeroConta() == numero) {
                return conta;
            }
        }
        return null;
    }

    private Conta buscarContaPorNumeroEAgencia(Cliente cliente, int numeroConta, Agencia agencia) {
        for (Conta conta : cliente.getContas()) {
            if (conta.getNumeroConta() == numeroConta && conta.getAgencia().equals(agencia)) {
                return conta;
            }
        }
        return null;
    }

    private void salvarDados() {
        try {
            GerenciadorDeArquivos.salvarDados(clientes, agencias, "dados.csv");
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
}