package com.br;

import java.util.HashMap;
import java.util.Map;

/**
 * Analisa argumentos da linha de comando formatados como flags.
 * <p>
 * Esta classe processa um array de strings, identificando argumentos
 * que começam com "--" e os armazena em um mapa. As flags podem ser
 * booleanas (ex: {@code --help}) ou ter um valor (ex: {@code --port=8080}).
 * </p>
 * <b>Formato Esperado:</b>
 * <ul>
 * <li>{@code --flag} (valor padrão "true")</li>
 * <li>{@code --flag=valor}</li>
 * </ul>
 */
public class FlagParser {
    private final Map<String, String> flags = new HashMap<>();

    /**
     * Construtor que processa os argumentos da linha de comando.
     *
     * @param args O array de strings de argumentos da linha de comando.
     */
    public FlagParser(String[] args) {
        for(String arg: args) {
            if(arg.startsWith("--")) {
                String[] part = arg.substring(2).split("=",2);

                if(part.length == 2) {
                    flags.put(part[0], part[1]);
                } else {
                    flags.put(part[0], "true");
                }
            }
        }
    }

    /**
     * Obtém o valor de uma flag específica.
     *
     * @param key A chave (nome) da flag.
     * @return O valor da flag como uma string, ou {@code null} se a flag não existir.
     */
    public String get(String key) {
        return flags.get(key);
    }

    /**
     * Verifica se uma flag específica está presente nos argumentos.
     *
     * @param key A chave (nome) da flag a ser verificada.
     * @return {@code true} se a flag estiver presente, caso contrário {@code false}.
     */
    public boolean has(String key) {
        return flags.containsKey(key);
    }

}
