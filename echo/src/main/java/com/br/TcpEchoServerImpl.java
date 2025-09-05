package com.br;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpEchoServerImpl implements TcpEchoServer {

    @Override
    public void start(int port) {
        try {
            var serverSocket = new ServerSocket(port);
            System.out.println("Servidor conectado na porta: " + port);

            while(true) {
                try (
                    Socket socket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
                    System.out.println("Conexão aceita");
                    String line;

                    while((line = in.readLine()) != null) {
                        System.out.println("Mensagem recebida: " + line);
                        out.println(line);
                        out.flush();

                        if("quit".equalsIgnoreCase(line)) {
                            System.out.println("Fechando conexão.");
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ConnectionException("Erro no echo server:", e);
        }

    }

}
