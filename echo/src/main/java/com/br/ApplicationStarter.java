package com.br;

/**
 * Orquestra o início da aplicação, validando os argumentos
 * da linha de comando e iniciando o modo de execução correto (cliente ou servidor).
 * <p>
 * Esta classe centraliza a lógica de validação de flags, IP, porta e nome de usuário,
 * garantindo que a aplicação seja executada de forma segura e com os
 * parâmetros esperados.
 * </p>
 */
public class ApplicationStarter {
    private final FlagParser parser;
    private final TcpEchoServer server;
    private final TcpEchoClient client;

    /**
     * Constrói uma nova instância do {@code ApplicationStarter}.
     *
     * @param parser O analisador de flags para processar os argumentos da linha de comando.
     * @param client O cliente TCP a ser iniciado, se o modo cliente for selecionado.
     * @param server O servidor TCP a ser iniciado, se o modo servidor for selecionado.
     */
    public ApplicationStarter(FlagParser parser, TcpEchoClient client, TcpEchoServer server) {
        this.client = client;
        this.server = server;
        this.parser = parser;
    }
    
    /**
     * Executa a lógica de inicialização da aplicação.
     * <p>
     * Este método valida os argumentos fornecidos e, em seguida, inicia
     * o servidor ou o cliente com base nas flags `--server` ou `--client`.
     * </p>
     *
     * @throws ConnectionException se as flags de modo estiverem ausentes ou duplicadas,
     * ou se os argumentos de IP ou porta forem inválidos.
     */
    public void execute() {
        validateFlags();

        if(parser.has("server")) {
            int port = validatePort();
            server.start(port);
        }

        if(parser.has("client")) {
            String ip = validateIp();
            int port = validatePort();
            String user = validateUser();
            client.start(ip, port, user);
        }
    }

    /**
     * Valida as flags de modo de execução (`--server` e `--client`).
     * <p>
     * Garante que exatamente uma dessas flags esteja presente nos argumentos.
     * </p>
     *
     * @throws ConnectionException se ambas as flags estiverem presentes, ou se nenhuma
     * delas for encontrada.
     */
    private void validateFlags() {
        if(parser.has("server") && parser.has("client")) {
            throw new ConnectionException("Cliente e servidor devem ser executados separadamente: ", new RuntimeException());
        }

        if(!parser.has("server") && !parser.has("client")) {
            throw new ConnectionException("Servidor ou cliente devem ser declarados: ", new RuntimeException());
        }
    }

    /**
     * Valida a porta fornecida nos argumentos.
     *
     * @return O número da porta validado.
     * @throws ConnectionException se o valor da porta não for um número inteiro válido
     * ou estiver fora do intervalo de 1 a 65536.
     */
    private int validatePort() {
        int port = Integer.valueOf(parser.get("port"));
        if(1 > port || port > 65536) {
            throw new ConnectionException("A porta deve ser entre 1 e 65.536: ", new RuntimeException());
        }
        return port;
    }

    /**
     * Valida o endereço IP fornecido nos argumentos.
     *
     * @return O endereço IP validado.
     * @throws ConnectionException se o IP for nulo ou estiver vazio.
     */
    private String validateIp() {
        String ip = parser.get("ip");
        if(ip == null || ip.trim().isEmpty()) {
            throw new ConnectionException("O IP deve ser preenchido: ", new RuntimeException());
        }
        return ip;
    }

    /**
     * Valida o nome de usuário fornecido.
     *
     * @return O nome de usuário validado.
     * @throws ConnectionException se o nome de usuário for nulo ou estiver vazio.
     */
    private String validateUser() {
        String user = parser.get("user");
        if(user == null || user.trim().isEmpty()){
            throw new ConnectionException("O nome de usuário deve ser preenchido.", new RuntimeException());
        }
        return user.trim();
    }
}
