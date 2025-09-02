package com.br;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        ServerConnectionImpl server = new ServerConnectionImpl();
        ClientConnectionImpl client = new ClientConnectionImpl();

        client.startConnection("127.0.0.1", 6969);
        var response = client.sendMessage("Ping");
    }
}