package model;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Classe que representa uma movimentação financeira realizada em uma conta bancária.
 * Cada movimentação possui um tipo (como depósito ou saque), data, valor e um cliente associado.
 */
public class Movimentacao {
    // Atributos que definem o tipo de movimentação, data, valor e o cliente envolvido
    private String tipo;
    private Date data;
    private double valor;
    private Cliente cliente;
    private static final DecimalFormat df = new DecimalFormat("#.00"); // Formatação para exibir valor com duas casas decimais

    public Movimentacao(String tipo, double valor, Cliente cliente) {
        this.tipo = tipo;
        this.data = new Date(); // Registra a data atual como data da movimentação
        this.valor = valor;
        this.cliente = cliente;
    }

    // Métodos Getters e Setters para acessar e modificar os atributos da movimentação

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


    @Override
    public String toString() {
        return "Movimentacao{" +
                "tipo='" + tipo + '\'' +
                ", data=" + data +
                ", valor=" + df.format(valor) +
                ", cliente=" + cliente.getNome() +
                '}';
    }
}
