package view;

import java.util.Scanner;

public class Visao {
    // Scanner para entrada de dados do usuário via console
    private Scanner scanner;

    // Construtor da classe Visao que inicializa o objeto Scanner
    public Visao() {
        this.scanner = new Scanner(System.in);
    }


    public String lerString(String mensagem) {
        System.out.print(mensagem); // Exibe a mensagem solicitando a entrada
        return scanner.nextLine(); // Lê e retorna a linha inserida pelo usuário
    }

    public double lerDouble(String mensagem) {
        System.out.print(mensagem); // Exibe a mensagem solicitando a entrada
        while (!scanner.hasNextDouble()) { // Verifica se a entrada é um double válido
            System.out.println("Entrada inválida. Por favor, insira um número."); // Exibe erro
            scanner.next(); // Descarta a entrada inválida
            System.out.print(mensagem); // Solicita a entrada novamente
        }
        double valor = scanner.nextDouble(); // Lê o valor double válido
        scanner.nextLine(); // Consome a nova linha após o valor
        return valor; // Retorna o valor double inserido pelo usuário
    }


    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem); // Exibe a mensagem no console
    }
}
