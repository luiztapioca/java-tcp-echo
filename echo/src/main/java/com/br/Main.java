package com.br;

/**
 * Ponto de entrada da aplicação TCP Echo.
 * <p>
 * A classe {@code Main} é responsável por inicializar a aplicação,
 * determinando se ela deve ser executada como um cliente ou um servidor
 * com base nos argumentos da linha de comando.
 * </p>
 */
public class Main {

    /**
     * @param args Argumentos da linha de comando fornecidos no início da aplicação.
     */
    public static void main(String[] args) {

        var parser = new FlagParser(args);
        var server = new TcpEchoServerImpl();
        var client = new TcpEchoClientImpl();

        var runner = new FlagRunner(parser, client, server);
        runner.execute();
    }
}