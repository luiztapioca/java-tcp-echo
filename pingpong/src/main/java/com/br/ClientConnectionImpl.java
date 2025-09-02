package com.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnectionImpl implements ClientConnection {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
        } catch (IOException e) {
            throw new ConnectionException("Erro na conexão: ", e);
        }
    }

    @Override
    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new ConnectionException("Erro ao fechar conexão: ", e);
        }
    }

    @Override
    public String sendMessage(String msg) {
        try {
            out.println(msg);
            var resp = in.readLine();
            return resp;
        } catch (Exception e) {
            throw new ConnectionException("Erro ao enviar mensagem: ", e);
        }
    }

}
