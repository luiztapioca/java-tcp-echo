package com.br;

/**
 * Exceção de tempo de execução personalizada para erros de conexão.
 * <p>
 * Esta exceção é usada para encapsular e propagar erros que ocorrem durante
 * as operações de rede, como falha ao iniciar o servidor, aceitar conexões
 * ou conectar a um host remoto.
 * </p>
 */
public class ConnectionException extends RuntimeException {
    /**
     * Construtor da exceção com uma mensagem detalhada e a causa original.
     *
     * @param msg   A mensagem detalhada.
     * @param cause A exceção original que causou o erro.
     */
    public ConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
