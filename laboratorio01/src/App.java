import controller.Controlador;

/**
 * Classe principal do aplicativo bancário.
 */
public class App {
    public static void main(String[] args) {
        // Cria uma instância do controlador responsável por gerenciar o sistema bancário
        Controlador controlador = new Controlador();

        // Inicia o controlador, iniciando a execução do sistema bancário
        controlador.iniciar();
    }
}
