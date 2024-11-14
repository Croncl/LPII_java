package model;

/**
 * Classe que representa uma transferência bancária.
 * A transferência é uma extensão da classe Movimentacao, que armazena informações
 * sobre a movimentação de fundos entre contas de origem e destino.
 */
public class Transferencia extends Movimentacao {
    // Atributos para armazenar as contas de origem e destino da transferência
    private Conta origem;
    private Conta destino;

    public Transferencia(Conta origem, Conta destino, double valor) {
        super("Transferencia", valor, origem.getCliente()); // Define o tipo como "Transferencia" e associa o cliente da conta de origem
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
