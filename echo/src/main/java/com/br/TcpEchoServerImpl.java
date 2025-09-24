package com.br;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementação de um servidor TCP Echo multithreaded.
 * <p>
 * Este servidor aceita múltiplas conexões de clientes e as processa em paralelo
 * usando um pool de threads.
 * </p>
 */
public class TcpEchoServerImpl implements TcpEchoServer {

    private volatile boolean running = true;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;

    /**
     * Construtor que inicializa o pool de threads.
     * É usado um cached thread pool, que cresce conforme necessário e reaproveita threads.
     */
    public TcpEchoServerImpl() {
        this.threadPool = Executors.newCachedThreadPool();
    }

    /**
     * Inicia o servidor em uma porta especificada.
     * 
     * @param port A porta na qual o servidor irá escutar.
     * @throws ConnectionException se ocorrer um erro ao iniciar o servidor.
     */
    @Override
    public void start(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            this.serverSocket = ss;
            System.out.println("\nServidor conectado na porta: " + port);

            while (running) {
                var socket = serverSocket.accept();

                System.out.println("\nNova conexão aceita: " + socket.getRemoteSocketAddress());

                threadPool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            if(running){
                throw new ConnectionException("Erro ao aceitar nova conexão", e);
            }
        }
    }

    /**
     * Lógica de atendimento de cada cliente.
     * Execução assíncrona em uma thread do pool.
     */
    private void handleClient(Socket socket) {
        try (socket;
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            socket.setSoTimeout(10_000);
            System.out.println("Atendendo cliente: " + socket.getRemoteSocketAddress());

            out.println("O servidor está pronto para processar sua conexão.");

            String line;
            while ((line = readLineWithTimeout(in, out, socket)) != null) {
                if ("quit".equalsIgnoreCase(line)) {
                    System.out.println("Conexão finalizada pelo cliente: " + socket.getRemoteSocketAddress());
                    break;
                }
                System.out.println("Mensagem recebida de " + socket.getRemoteSocketAddress() + ": " + line);

                /*
                * --> Instruções do projeto
                * Protocolo de comunicação: cada mensagem deve ser finalizada com um '\n'
                */
                out.write(line + "\n");
                out.flush();
            }

        } catch (IOException e) {
            System.err.println("Erro ao processar cliente " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        }
    }

    /**
     * Lê uma linha tratando timeout do cliente.
     */
    private String readLineWithTimeout(BufferedReader in, PrintWriter out, Socket socket) throws IOException {
        try {
            return in.readLine();
        } catch (SocketTimeoutException ste) {
            out.println("Tempo limite de inatividade atingido (10s). Encerrando conexão.");
            System.out.println("Conexão encerrada: cliente inativo -> " + socket.getRemoteSocketAddress());

            return null;
        }
    }

    /**
     * Sinaliza para o servidor que ele deve parar a execução e encerra o pool de threads.
     */
    public void stop() {
        running = false;
        threadPool.shutdownNow();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao fechar ServerSocket: " + e.getMessage());
        }
        System.out.println("Servidor será finalizado...");
    }
}
