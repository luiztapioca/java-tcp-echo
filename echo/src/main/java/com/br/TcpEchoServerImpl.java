package com.br;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpEchoServerImpl implements TcpEchoServer {

    private final BlockingQueue<Socket> connectionQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    @Override
    public void start(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("\nServidor conectado na porta: " + port);

            new Thread(this::processConnections, "ConnectionProcessor").start();

            while (running) {
                var socket = serverSocket.accept();
                connectionQueue.put(socket);

                System.out.println("\nNova conexão aceita: " + socket.getRemoteSocketAddress());

                sendQueuedMessage(socket);
            }
        } catch (IOException e) {
            throw new ConnectionException("Erro ao aceitar nova conexão", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Servidor interrompido.");
        }
    }

    private void sendQueuedMessage(Socket socket) {
        try {
            var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            out.println("Sua conexão foi enfileirada. Aguarde para ser atendido.");

        } catch (IOException e) {
            throw new ConnectionException("Erro ao enviar mensagem inicial: ", e);
        }
    }

    private void processConnections() {
        while (running) {
            try (var socket = connectionQueue.take();
                var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

                System.out.println("\nAtendendo cliente: " + socket.getRemoteSocketAddress() + "\n");
                out.println("O servidor está pronto para processar sua conexão.");

                String line;
                while ((line = in.readLine()) != null) {
                    if("quit".equalsIgnoreCase(line)) {
                        System.out.println("\nConexão finalizada pelo cliente.");
                        break;
                    }
                    System.out.println("Mensagem recebida: " + line);

                    /*
                    --> Instruções do projeto
                    - Protocolo de comunicação: cada mensagem deve ser finalizada com um '\n'
                    */
                    out.write(line + "\n");
                    out.flush();
                }
                System.out.printf("Conexão encerrada: " + socket.getRemoteSocketAddress());

            } catch (IOException e) {
                throw new ConnectionException("Erro ao aceitar nova conexão", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ConnectionException("Processamento interrompido", e);
            }
        }
    }

    public void stop() {
        running = false;
        System.out.println("Servidor será finalizado...");
    }
}
