package com.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.nio.charset.StandardCharsets;

public class TcpEchoClientImpl implements TcpEchoClient {

    @Override
    public void start(String ip, int port) {
        try (
                var clientSocket = new Socket(ip, port);
                var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                var out = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8);
                var stdin = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
        ) {

            System.out.println("\nConectado ao host: " + ip + ":" + port);

            System.out.println("Servidor: " + in.readLine());
            System.out.println("Servidor: " + in.readLine());

            String line;

            while ((line = stdin.readLine()) != null) {
                var ini = System.nanoTime();

                /*
                --> Instruções do projeto
                - Protocolo de comunicação: cada mensagem deve ser finalizada com um '\n'
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
