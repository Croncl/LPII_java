package model;

public class Transferencia extends Movimentacao {
    private Conta origem;
    private Conta destino;

    public Transferencia(Conta origem, Conta destino, double valor) {
        super("Transferência", valor, origem.getCliente());
        this.origem = origem;
        this.destino = destino;
    }

    public Conta getOrigem() {
        return origem;
    }

    public Conta getDestino() {
        return destino;
    }

    @Override
    public String toString() {
        return "Transferencia{" +
                "origem=" + origem.getNumeroConta() +
                ", destino=" + destino.getNumeroConta() +
                ", valor=" + getValor() +
                ", data=" + getData() +
                '}';
    }
}