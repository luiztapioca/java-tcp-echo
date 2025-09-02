package com.br;

import java.io.IOException;

public interface Connection {
    public void start(int port) throws IOException;
    public void stop() throws IOException;
}
