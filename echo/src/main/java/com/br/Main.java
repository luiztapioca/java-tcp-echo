package com.br;

public class Main {
    public static void main(String[] args) {

        var parser = new FlagParser(args);
        var server = new TcpEchoServerImpl();
        var client = new TcpEchoClientImpl();

        if(parser.has("server")) {
            server.start(Integer.valueOf(parser.get("port")));
        }

        if(parser.has("client")) {
            client.start(parser.get("ip"), Integer.valueOf(parser.get("port")));
        }
    }
}