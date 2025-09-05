package com.br;

public class Main {
    public static void main(String[] args) {

        ServerConnectionImpl server = new ServerConnectionImpl();
        ClientConnectionImpl client = new ClientConnectionImpl();

        FlagParser parser = new FlagParser(args);
    }
}