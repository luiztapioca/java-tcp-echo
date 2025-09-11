package com.br;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementação de um servidor TCP Echo multithreaded.
 * <p>
 * Este servidor aceita múltiplas conexões de clientes. Ele usa uma {@link java.util.concurrent.BlockingQueue}
 * para gerenciar as conexões recebidas e as processa em uma thread separada,
 * permitindo que o servidor continue aceitando novas conexões rapidamente.
 * </p>
 */
public class TcpEchoServerImpl implements TcpEchoServer {

    private final BlockingQueue<Socket> connectionQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    /**
     * Inicia o servidor em uma porta especificada.
     * O servidor aceita novas conexões em um loop e as adiciona à fila de processamento.
     * Uma thread separada é responsável por processar as conexões da fila.
     *
     * @param port A porta na qual o servidor irá escutar.
     * @throws ConnectionException se ocorrer um erro ao iniciar o servidor ou aceitar uma conexão.
     */
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

    /**
     * Envia uma mensagem inicial ao cliente informando que a conexão foi enfileirada.
     *
     * @param socket O socket do cliente para o qual a mensagem será enviada.
     * @throws ConnectionException se ocorrer um erro de I/O ao enviar a mensagem.
     */
    private void sendQueuedMessage(Socket socket) {
        try {
            var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            out.println("Sua conexão foi enfileirada. Aguarde para ser atendido.");

        } catch (IOException e) {
            throw new ConnectionException("Erro ao enviar mensagem inicial: ", e);
        }
    }

    /**
     * Método executado em uma thread separada para processar as conexões da fila.
     * Este método pega uma conexão da fila, atende o cliente e o ecoa as mensagens.
     * Além disso, trata de tempos de inatividade (com um timeout de 10 segundos),
     * encerrando a conexão se o cliente não enviar dados dentro desse intervalo.
     */
    private void processConnections() {
        while (running) {
            try (var socket = connectionQueue.take();
                 var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
                socket.setSoTimeout(10_000);
                System.out.println("\nAtendendo cliente: " + socket.getRemoteSocketAddress() + "\n");
                out.println("O servidor está pronto para processar sua conexão.");

                String line;
                while (true) {
                    try {
                        line = in.readLine();
                        if (line == null) {
                            System.out.println("\nConexão encerrada pelo cliente: " + socket.getRemoteSocketAddress());
                            break;
                        }
                        if("quit".equalsIgnoreCase(line)) {
                            System.out.println("\nConexão finalizada pelo cliente.");
                            break;
                        }
                        System.out.println("Mensagem recebida: " + line);

                        /*
                         * --> Instruções do projeto
                         * Protocolo de comunicação: cada mensagem deve ser finalizada com um '\n'
                         */
                        out.write(line + "\n");
                        out.flush();
                    } catch (SocketTimeoutException ste) {
                        out.write("Tempo limite de inatividade atingido (10s). Encerrando conexão." + "\n");
                        out.flush();
                        System.out.println("Conexão encerrada: cliente inativo -> " + socket.getRemoteSocketAddress());
                        break;
                    }
                }
            } catch (IOException e) {
                throw new ConnectionException("Erro ao aceitar nova conexão", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ConnectionException("Processamento interrompido", e);
            }
        }
    }

    /**
     * Sinaliza para o servidor que ele deve parar a execução.
     */
    public void stop() {
        running = false;
        System.out.println("Servidor será finalizado...");
    }
}
