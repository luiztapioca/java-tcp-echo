package com.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.nio.charset.StandardCharsets;

/**
 * Implementação de um cliente TCP Echo.
 * <p>
 * Esta classe permite que o usuário se conecte a um servidor, envie mensagens
 * do console ({@code stdin}) e exiba as mensagens de eco recebidas do servidor.
 * Ela também mede e exibe a latência de cada requisição.
 * </p>
 */
public class TcpEchoClientImpl implements TcpEchoClient {

    /**
     * Inicia o cliente, conecta-o ao servidor e gerencia a comunicação de eco.
     *
     * @param ip   O endereço IP do servidor para se conectar.
     * @param port A porta do servidor.
     * @param user O nome de usuário a ser enviado ao servidor para identificação.
     * @throws ConnectionException se ocorrer um erro durante a conexão ou comunicação.
     */
    @Override
    public void start(String ip, int port, String user) {
        try (
            var clientSocket = new Socket(ip, port);
            var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            var out = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8);
            var stdin = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
        ) {


            out.println(user);

            String serverResp = in.readLine();
            if("USER_ALREADY_EXISTS".equals(serverResp)){
                System.out.println("Servidor recusou conexão: nome de usuário já está em uso.");
                return;
            }
            if("USERNAME_INVALID".equals(serverResp)){
                System.out.println("Servidor recusou conexão: nome de usuário inválido.");
                return;
            }
            
            System.out.println("\nConectado ao host: " + ip + ":" + port);

            System.out.println("Servidor: " + serverResp);

            String line;

            while ((line = stdin.readLine()) != null) {
                var ini = System.nanoTime();

                /*
                 * --> Instruções do projeto
                 * Protocolo de comunicação: cada mensagem deve ser finalizada com um '\n'
                 */
                out.write(line + "\n");
                out.flush();

                var resp = in.readLine();
                if(resp == null || resp.contains("Tempo limite de inatividade atingido")) {
                    System.out.println("\nServidor: " + resp);
                    System.out.println("Servidor encerrou conexão.");
                    break;
                }

                var fim = System.nanoTime();

                if("quit".equalsIgnoreCase(line)) {
                    System.out.println("Fechando conexão.");
                    break;
                }
                System.out.println("\nServidor: " + resp);

                double latenciaMs = (double) (fim - ini) / 1_000_000.0;
                System.out.printf("Latência: %.3f ms\n\n", latenciaMs);
            }
        } catch (IOException e) {
            throw new ConnectionException("Erro ao realizar conexão", e);
        } catch (Exception e) {
            throw new ConnectionException("Erro inesperado ao realizar conexão", e);
        }
    }
}
