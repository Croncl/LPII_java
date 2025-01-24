package model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Arquivador {

    public static void arquivarDadosDaConta(Conta conta) {
        String tipoConta = conta.getClass().getSimpleName();
        String nomeArquivoLog = "log_" + tipoConta + "_" + conta.getAgencia().getNumeroAgencia() + "_" + conta.getNumeroConta() + ".txt";
        String nomeArquivoDados = "dados_" + tipoConta + "_" + conta.getAgencia().getNumeroAgencia() + "_" + conta.getNumeroConta() + ".txt";
        Path origemLog = Paths.get(nomeArquivoLog);
        Path destinoLog = Paths.get("arquivados/" + nomeArquivoLog);
        Path destinoDados = Paths.get("arquivados/" + nomeArquivoDados);

        try {
            // Criar diretório de destino se não existir
            Files.createDirectories(destinoLog.getParent());

            // Mover o arquivo de log para o diretório de arquivamento
            if (Files.exists(origemLog)) {
                Files.move(origemLog, destinoLog, StandardCopyOption.REPLACE_EXISTING);
            }

            // Arquivar os dados da conta
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinoDados.toFile()))) {
                writer.write("Cliente: " + conta.getCliente().getNome() + "\n");
                writer.write("CPF: " + conta.getCliente().getCpf() + "\n");
                writer.write("Agência: " + conta.getAgencia().getNumeroAgencia() + "\n");
                writer.write("Número da Conta: " + conta.getNumeroConta() + "\n");
                writer.write("Tipo de Conta: " + tipoConta + "\n");
                writer.write("Saldo: " + conta.getSaldo() + "\n");
            }

            System.out.println("Dados da conta e log de transações arquivados com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao arquivar dados da conta e log de transações: " + e.getMessage());
        }
    }
}