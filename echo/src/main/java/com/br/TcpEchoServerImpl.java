package com.br;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

/**
 * Implementação de um servidor TCP Echo multithreaded.
 * <p>
 * Este servidor aceita múltiplas conexões de clientes e as processa em paralelo
 * usando um {@code ExecutorService} (pool de threads).
 * </p>
 */
public class TcpEchoServerImpl implements TcpEchoServer {

    private volatile boolean running = true;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private final Map<String, Socket> activeUsers = new ConcurrentHashMap<>();

    /**
     * Construtor que inicializa o pool de threads.
     * É usado um cached thread pool ({@code Executors.newCachedThreadPool()}), que cresce conforme necessário e reaproveita threads.
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
     * Execução assíncrona em uma thread do pool. O método trata a autenticação
     * do usuário (verificando se o nome de usuário está preenchido e se já
     * está em uso) e, em seguida, entra no loop de echo.
     *
     * @param socket O socket de comunicação específico para este cliente.
     */
    private void handleClient(Socket socket) {
        try (socket;
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            var out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            socket.setSoTimeout(120_000);

            String username = in.readLine();
            if(username == null || username.trim().isEmpty()){
                out.println("USERNAME_INVALID");
                return;
            }

            if(activeUsers.containsKey(username)){
                out.println("USER_ALREADY_EXISTS");
                return;
            }

            activeUsers.put(username, socket);
            System.out.println("\nAtendendo cliente: " + username + " -> " + socket.getRemoteSocketAddress() + "\n");

            out.println("Olá, " + username + "! O servidor está pronto para processar sua conexão.");

            String line;
            while ((line = readLineWithTimeout(in, out, socket)) != null) {
                if ("quit".equalsIgnoreCase(line)) {
                    System.out.println("\nConexão finalizada pelo cliente: " + username + " -> " + socket.getRemoteSocketAddress());
                    break;
                }
                System.out.println("Mensagem recebida de " + username + ": " + line);

                /*
                * --> Instruções do projeto
                * Protocolo de comunicação: cada mensagem deve ser finalizada com um '\n'
                */

                String finalLine = line;
                activeUsers.forEach((u, s) -> {
                    try {
                        if (!u.equals(username)) {
                            var tout = new PrintWriter(s.getOutputStream(), true, StandardCharsets.UTF_8);
                            tout.println(finalLine);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            activeUsers.remove(username, socket);
        } catch (IOException e) {
            System.err.println("Erro ao processar cliente " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        }
    }

    /**
     * Lê uma linha de um {@code BufferedReader}, tratando timeout do cliente.
     *
     * @param in O {@code BufferedReader} para leitura da entrada do cliente.
     * @param out O {@code PrintWriter} para envio de mensagens de erro ao cliente.
     * @param socket O {@code Socket} do cliente, usado para referenciar o endereço.
     * @return A linha lida ou {@code null} se ocorrer um {@code SocketTimeoutException}.
     * @throws IOException Se ocorrer qualquer outro erro de I/O durante a leitura.
     */
    private String readLineWithTimeout(BufferedReader in, PrintWriter out, Socket socket) throws IOException {
        try {
            return in.readLine();
        } catch (SocketTimeoutException ste) {
            out.println("Tempo limite de inatividade atingido (10s). Encerrando conexão.");
            System.out.println("\nConexão encerrada: cliente inativo -> " + socket.getRemoteSocketAddress());

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
