package com.br;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        // TODO: client server

        ConnectionImpl server = new ConnectionImpl();
        try {
            server.start(6969); 
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}