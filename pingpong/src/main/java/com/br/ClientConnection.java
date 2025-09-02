package com.br;

public interface ClientConnection {
    void startConnection(String ip, int port);
    void stopConnection();
    String sendMessage(String msg);
}
