---
marp: true
theme: default
class:
    - invert
paginate: true
---

# Projeto Echo TCP
## Implementação de Cliente e Servidor TCP em Java

---

## O que é TCP?

**TCP (Transmission Control Protocol)** é um protocolo de comunicação:

- **Orientado à conexão** - Estabelece uma conexão antes da transmissão
- **Confiável** - Garante que os dados chegem ao destino
- **Ordenado** - Mantém a ordem dos dados enviados
- **Controle de erro** - Detecta e corrige erros de transmissão
- **Camada de Transporte** - Opera na camada 4 do modelo OSI

---

## Como funciona o TCP?

---

### 1. Estabelecimento da Conexão (Three-Way Handshake)
```
Cliente ──SYN──> Servidor
Cliente <──SYN-ACK── Servidor  
Cliente ──ACK──> Servidor
```

---

### 2. Transmissão de Dados
- Dados são divididos em **segmentos**
- Cada segmento possui um **número de sequência**
- Receptor envia **ACK** para confirmar recebimento

---

### 3. Encerramento da Conexão
```
Cliente ──FIN──> Servidor
Cliente <──ACK── Servidor
```

---

## Arquitetura do Projeto

```
📁 src/main/java/com/br/
├──  Main.java               # Ponto de entrada
├──  FlagParser.java         # Parser de argumentos
├──  TcpEchoServer.java      # Interface do servidor
├──  TcpEchoServerImpl.java  # Implementação do servidor
├──  TcpEchoClient.java      # Interface do cliente
├──  TcpEchoClientImpl.java  # Implementação do cliente
└──  ConnectionException.java # Exceções customizadas
```

### Padrão Utilizado: **Interface + Implementação**

---

## Classe Main - Ponto de Entrada

```java
public class Main {
    public static void main(String[] args) {
        var parser = new FlagParser(args);
        var server = new TcpEchoServerImpl();
        var client = new TcpEchoClientImpl();

        if(parser.has("server")) {
            server.start(Integer.valueOf(parser.get("port")));
        }

        if(parser.has("client")) {
            client.start(parser.get("ip"), Integer.valueOf(parser.get("port")));
        }
    }
}
```

---

**Uso:**
- `--server --port=6969` → Inicia servidor
- `--client --ip=localhost --port=6969` → Inicia cliente

---

## Implementação do Servidor TCP

### Características Principais:

-  **ServerSocket** para aceitar conexões
-  **Queue de conexões** com processamento assíncrono
-  **Timeout de 10 segundos** para inatividade
-  **Multi-threading** para múltiplos clientes
-  **Protocolo simples** - cada mensagem termina com `\n`

---

## Servidor - Código Principal

```java
@Override
public void start(int port) {
    try (var serverSocket = new ServerSocket(port)) {
        System.out.println("Servidor conectado na porta: " + port);
        
        // Thread para processar conexões
        new Thread(this::processConnections, "ConnectionProcessor").start();
        
        while (running) {
            var socket = serverSocket.accept();
            connectionQueue.put(socket);  // Enfileira conexão
            sendQueuedMessage(socket);    // Notifica cliente
        }
    }
}
```

---

## Servidor - Processamento de Mensagens

```java
private void processConnections() {
    while (running) {
        try (var socket = connectionQueue.take();
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String line;
            while ((line = in.readLine()) != null) {
                if("quit".equalsIgnoreCase(line)) break;
                
                // ECHO: retorna a mesma mensagem
                out.write(line + "\n");
                out.flush();
            }
        }
    }
}
```

---

## Implementação do Cliente TCP

- **Socket** para conectar ao servidor
- **Input do usuário** via `System.in`
- **Medição de latência** para cada mensagem
- **Comando "quit"** para encerrar conexão
- **Protocolo simples** - cada mensagem termina com `\n`

---

## Cliente - Código Principal

```java
@Override
public void start(String ip, int port) {
    try (var clientSocket = new Socket(ip, port);
         var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         var out = new PrintWriter(clientSocket.getOutputStream(), true);
         var stdin = new BufferedReader(new InputStreamReader(System.in))) {
        
        String line;
        while ((line = stdin.readLine()) != null) {
            var ini = System.nanoTime();
            
            out.write(line + "\n");  // Envia mensagem
            out.flush();
            
            var resp = in.readLine(); // Recebe echo
            var fim = System.nanoTime();
            
            // Calcula e exibe latência
            double latenciaMs = (double) (fim - ini) / 1_000_000.0;
            System.out.printf("Latência: %.3f ms\n", latenciaMs);
        }
    }
}
```

---

## Funcionalidades Avançadas

### FlagParser - Processamento de Argumentos
```java
// Suporta: --flag=value ou --flag
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
```

---

### Tratamento de Exceções
- `ConnectionException` personalizada
- Timeouts automáticos
- Cleanup de recursos com try-with-resources

---

## Demonstração Prática

### 1. Compilar o projeto:
```bash
mvn compile
```

---

### 2. Executar o servidor:
```bash
java -cp target/classes com.br.Main --server --port=6969
```

---

### 3. Executar o cliente:
```bash
java -cp target/classes com.br.Main --client --ip=localhost --port=6969
```

---

### 4. Testar comunicação:
- Digite mensagens no cliente
- Veja o echo retornado pelo servidor
- Digite "quit" para encerrar

---

## Conceitos TCP Aplicados

| Conceito TCP | Implementação no Projeto |
|--------------|--------------------------|
| **Conexão Confiável** | `ServerSocket.accept()` e `Socket` |
| **Stream de Bytes** | `BufferedReader` e `PrintWriter` |
| **Controle de Fluxo** | `flush()` e bloqueio de I/O |
| **Timeout** | `socket.setSoTimeout(10_000)` |
| **Encerramento Graceful** | Comando "quit" e `close()` |
| **Multiplexação** | Queue de conexões + Threading |

---

## Possíveis Melhorias

**Pool de Threads** - Para melhor performance
**SSL/TLS** - Para comunicação segura
**Métricas** - Throughput, conexões ativas
**Testes unitários** - Cobertura de código

---

## Conclusão

O projeto demonstra uma implementação **sólida e prática** do protocolo TCP.

**Repositório:** java-tcp-echo
**Tecnologias:** Java 11+, Maven, TCP Sockets