package com.br;

public class FlagRunner {
    private final FlagParser parser;
    private final TcpEchoServer server;
    private final TcpEchoClient client;

    public FlagRunner(FlagParser parser, TcpEchoClient client, TcpEchoServer server) {
        this.client = client;
        this.server = server;
        this.parser = parser;
    }
    
    public void execute() {
        validateFlags();

        if(parser.has("server")) {
            int port = validatePort();
            server.start(port);
        }

        if(parser.has("client")) {
            String ip = validateIp();
            int port = validatePort();
            client.start(ip, port);
        }
    }

    private void validateFlags() {
        if(parser.has("server") && parser.has("client")) {
            throw new ConnectionException("Cliente e servidor devem ser executados separadamente: ", new RuntimeException());
        }

        if(!parser.has("server") && !parser.has("client")) {
            throw new ConnectionException("Servidor ou cliente devem ser declarados: ", new RuntimeException());
        }
    }

    private int validatePort() {
        int port = Integer.valueOf(parser.get("port"));
        if(1 > port || port > 65536) {
            throw new ConnectionException("A porta deve ser entre 1 e 65.536: ", new RuntimeException());
        }
        return port;
    }

    private String validateIp() {
        String ip = parser.get("ip");
        if(ip == null || ip.trim().isEmpty()) {
            throw new ConnectionException("O IP deve ser preenchido: ", new RuntimeException());
        }
        return ip;
    }
}
