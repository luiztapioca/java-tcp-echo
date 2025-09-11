package com.br;

/**
 * Interface para um servidor TCP Echo.
 * <p>
 * Esta interface define o contrato para um servidor que aceita conexões TCP
 * e ecoa de volta mensagens de texto (UTF-8) recebidas de seus clientes.
 * </p>
 */
public interface TcpEchoServer {
    /**
     * Inicia o servidor e o coloca em estado de escuta para novas conexões.
     *
     * @param port A porta na qual o servidor irá escutar.
     */
    void start(int port);
}
