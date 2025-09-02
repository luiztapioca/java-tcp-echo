package com.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionImpl implements ServerConnection {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            var msg = in.readLine();
            var ping = "Ping";

            if(ping.equals(msg)){
                out.println("Pong!");
            }
        } catch (IOException e) {
            throw new ConnectionException("Erro na conexão: ", e);
        }
    }

    @Override
    public void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch(IOException e) {
            throw new ConnectionException("Erro ao parar conexão: ", e);
        }
    }

}
