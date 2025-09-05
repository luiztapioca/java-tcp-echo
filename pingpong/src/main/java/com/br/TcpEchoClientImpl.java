package com.br;

import java.io.BufferedReader;
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
        ) {

            System.out.println("Conectado ao host: " + ip + ":" + port);
            String msg;

            while((msg = in.readLine()) != null) {
                System.out.println(msg);
                out.println(msg);
            }
            
        } catch (Exception e) {
            throw new ConnectionException("Erro ao realizar conexão: ", e);
        }
    }

}
