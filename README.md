# Projeto TCP Echo

Este projeto implementa uma aplicação cliente-servidor de eco (Echo) sobre o protocolo TCP, utilizando um servidor multithreaded para gerenciar conexões simultâneas e um cliente interativo para testar a comunicação, medindo também a latência das requisições.

### **Funcionalidades**

* **Servidor TCP Multithreaded:** aceita e gerencia conexões de múltiplos clientes simultaneamente, utilizando threads para processar as conexões de forma independente e eficiente. Ele usa uma fila para gerenciar as requisições de maneira organizada, de modo a atender um cliente por vez.

* **Cliente TCP:** permite que o usuário digite mensagens no console (`stdin`) e receba a resposta de eco do servidor.

* **Medição de Latência:** o cliente calcula e exibe a latência de ponta a ponta de cada requisição.

* **Gerenciamento de Argumentos:** a classe `FlagParser` organiza e valida os parâmetros passados pela linha de comando.

* **Tratamento de Erros:** O servidor implementa um sistema de `timeout` para encerrar conexões inativas.

### **Estrutura do projeto**

O projeto é composto pelas seguintes classes e interfaces principais:

* `Main.java`: lida com a lógica de inicialização do servidor ou cliente com base nos argumentos de linha de comando.

* `TcpEchoServer.java` e `TcpEchoServerImpl.java`: interface e sua implementação do servidor. Aceita conexões em uma porta específica e ecoa as mensagens recebidas.

* `TcpEchoClient.java` e `TcpEchoClientImpl.java`: interface e sua implementação do cliente. Conecta-se a um servidor, envia mensagens e mede a latência da resposta.

* `FlagParser.java`: classe utilitária responsável por analisar os argumentos de linha de comando, como `--server`, `--client`, `--ip` e `--port`.

* `ConnectionException.java`: exceção personalizada que encapsula erros de conexão, tornando o tratamento de erros mais claro.

### **Instruções de execução**

Para executar o projeto, você precisa ter o **Java Development Kit (JDK) 8 ou superior** e o **Apache Maven** instalados.

#### 1. Construir o projeto

Abra o terminal e, a partir da pasta raiz do projeto (`echo/`), execute o seguinte comando Maven para compilar o código e criar um arquivo JAR executável:

``` bash

    mvn clean package


```

Este comando irá compilar todas as classes e empacotar a aplicação em um arquivo `.jar` na pasta `target/`.

#### 2. Executar a aplicação

A execução deve ser feita a partir da pasta raiz do seu projeto (`echo/`). Utilize os seguintes comandos, dependendo do modo desejado:

**Modo servidor:**
``` bash

  mvn exec:java -Dexec.mainClass="com.br.Main" -Dexec.args="--server --port=<PORTA>"


```

**Modo cliente:**
``` bash

  mvn exec:java -Dexec.mainClass="com.br.Main" -Dexec.args="--client --ip=<IP> --port=<PORTA>"


```

> **Observação:** Substitua `<IP>` e `<PORTA>` pelos valores desejados.

### **Exemplos de uso**

#### Passo 1: Inicie o servidor

Inicie o servidor em uma porta de sua escolha (ex: porta 8080):

``` bash

  (Terminal 1)

  mvn exec:java -Dexec.mainClass="com.br.Main" -Dexec.args="--server --port=8080"   


```

Saída esperada:

``` bash

  Servidor conectado na porta: 8080


```

#### Passo 2: Inicie o cliente

Em uma nova janela do terminal, inicie o cliente. Conecte-se ao servidor na mesma porta (8080) e no IP local (`127.0.0.1 --> localhost`):

``` bash

  (Terminal 2)

  mvn exec:java -Dexec.mainClass="com.br.Main" -Dexec.args="--client --ip=localhost --port=8080"


```

Saída esperada:

``` bash

  Conectado ao host: localhost:8080
  Servidor: Sua conexão foi enfileirada. Aguarde para ser atendido.
  Servidor: O servidor está pronto para processar sua conexão.


```

#### Passo 3: Envie mensagens

Agora você pode digitar qualquer mensagem no terminal do cliente. A mensagem será enviada ao servidor, que a ecoará de volta. A latência será exibida após cada resposta.

``` bash

  Terminal 2 (cliente)

  Hello, world!

  Servidor: Hello, world!
  Latência: 0.123 ms

  Como vai?

  Servidor: Como vai?
  Latência: 0.234 ms

  quit

  Fechando conexão.


```

No terminal do servidor, você verá o registro das mensagens e das conexões:

``` bash

  Terminal 1 (servidor)

  Nova conexão aceita: /127.0.0.1:54321
  Atendendo cliente: /127.0.0.1:54321

  Mensagem recebida: Hello, world!
  Mensagem recebida: Como vai?
  Conexão finalizada pelo cliente.


```

O comando `quit` encerra a conexão do cliente de forma limpa.

### **Tecnologias utilizadas**

* **Linguagem:** Java

* **Gerenciamento de Dependências:** Apache Maven

* **Comunicação:** TCP Sockets

* **Paralelismo:** BlockingQueue e Threads