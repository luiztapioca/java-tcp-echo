# java-tcp-echo
Projeto de Echo server usando TCP em Java

TODO:
- [ ] Colocar UTF-8

`mvn compile`
`mvn exec:java -Dexec.mainClass="com.br.Main" -Dexec.args="--server --port=8080"`
`mvn exec:java -Dexec.mainClass="com.br.Main" -Dexec.args="--client --ip=localhost --port=8080"`