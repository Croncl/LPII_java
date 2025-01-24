package model;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movimentacao {
    private String tipo;
    private Date data;
    private double valor;
    private Cliente cliente;
    private static final DecimalFormat df = new DecimalFormat("#.00");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Movimentacao(String tipo, double valor, Cliente cliente) {
        this.tipo = tipo;
        this.data = new Date();
        this.valor = valor;
        this.cliente = cliente;
    }

    // Construtor para desserialização
    public Movimentacao(String tipo, String data, double valor, Cliente cliente) throws ParseException {
        this.tipo = tipo;
        this.data = sdf.parse(data);
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
        return tipo + "," + sdf.format(data) + "," + df.format(valor) + "," + cliente.getNome();
    }
}