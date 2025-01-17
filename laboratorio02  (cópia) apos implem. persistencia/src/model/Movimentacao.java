package model;

import java.text.DecimalFormat;
import java.util.Date;

public class Movimentacao {
    private String tipo;
    private Date data;
    private double valor;
    private Cliente cliente;
    private static final DecimalFormat df = new DecimalFormat("#.00");

    public Movimentacao(String tipo, double valor, Cliente cliente) {
        this.tipo = tipo;
        this.data = new Date();
        this.valor = valor;
        this.cliente = cliente;
    }

    public String getTipo() {
        return tipo;
    }

    public Date getData() {
        return data;
    }

    public double getValor() {
        return valor;
    }

    public Cliente getCliente() {
        return cliente;
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