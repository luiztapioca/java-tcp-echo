package com.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            String msg;

            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println("Servidor: " + response);
                    }
                } catch (Exception e) {
                    System.out.println("Fechando conexão.");
                }
            }).start();

            while ((msg = stdin.readLine()) != null) {
                out.println(msg);

                if(msg.equalsIgnoreCase("quit")) {
                    System.out.println("Fechando conexão.");
                    break;
                }
            }
            
        } catch (Exception e) {
            throw new ConnectionException("Erro ao realizar conexão: ", e);
        }
    }
}
