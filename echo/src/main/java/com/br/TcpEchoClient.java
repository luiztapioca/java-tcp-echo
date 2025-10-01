package com.br;

/**
 * Interface para um cliente TCP Echo.
 * <p>
 * Esta interface define o contrato para um cliente que se conecta a um servidor TCP
 * e interage com ele, enviando mensagens de texto (UTF-8) e recebendo-as de volta (exatamente como enviadas) como eco.
 * </p>
 */
public interface TcpEchoClient {
    /**
     * Inicia o cliente e tenta se conectar a um servidor no endereço IP e porta especificados.
     *
     * @param ip   O endereço IP do servidor.
     * @param port A porta do servidor.
     * @param user O nome de usuário.
     */
    void start(String ip, int port, String user);
}
