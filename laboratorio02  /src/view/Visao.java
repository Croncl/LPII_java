package view;

import java.util.Scanner;

public class Visao {
    private Scanner scanner;

    public Visao() {
        this.scanner = new Scanner(System.in);
    }

    public String lerString(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }

    public double lerDouble(String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextDouble()) {
            System.out.println("Entrada inválida. Por favor, insira um número.");
            scanner.next();
            System.out.print(mensagem);
        }
        double valor = scanner.nextDouble();
        scanner.nextLine();
        return valor;
    }

    public int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
            scanner.next();
            System.out.print(mensagem);
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public void fecharScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}