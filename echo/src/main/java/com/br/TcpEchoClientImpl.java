package com.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;

public class TcpEchoClientImpl implements TcpEchoClient {

    @Override
    public void start(String ip, int port) {
        try (
            var clientSocket = new Socket(ip, port);
            var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var out = new PrintWriter(clientSocket.getOutputStream(), true);
            var stdin = new BufferedReader(new InputStreamReader(System.in))
        ) {

            System.out.println("Conectado ao host: " + ip + ":" + port);
            String line;

            while ((line = stdin.readLine()) != null) {
                System.out.println(line);
                var ini = System.nanoTime();

                out.println(line);
                out.flush();

                var resp = in.readLine();

                if(resp == null) {
                    System.out.println("Servidor encerrou conexão.");
                    break;
                }

                var fim = System.nanoTime(); 

                if("quit".equalsIgnoreCase(line)) {
                    System.out.println("Fechando conexão.");
                    break;
                }
                System.out.println("Servidor: " + resp);
                System.out.println("Latência: " + Duration.ofMillis(fim - ini).toMillis() + "ms");
            }
            
        } catch (Exception e) {
            throw new ConnectionException("Erro ao realizar conexão: ", e);
        }
    }
}
