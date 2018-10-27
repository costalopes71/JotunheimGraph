# JotunheimGraph
Gerador de imagens de grafos que representam as ligações entre dCoois pontos (pontos determinados a partir de qualquer idéia, no projeto em questão foi utilizado como estudo uma rede elétrica de todo um estado do Brasil.

# Tipo de projeto
Projeto console.

# Necessidades e preocupações
* Muitas pesquisas pesadas em centenas de bancos de dados SQLite em disco.
* Performance
* Uso dos recursos de hardware

# Tecnologias utilizadas
* JAVA 8
* SQL
* JDBC
* SQLite
* Log4J
* BAT

# Tópicos interessantes do projeto
* Concorrência : utilizando a interface Runnable, variás threads fazendo processamentos em paralelo, utilização de classes thread-safe como por exemplo a ConcorrentHashMap<> (implementação thread-safe da interface Map)
* Conexão em diversos bancos de dados SQLite diferentes simultaneamente
* Execucao de arquivo BAT diretamente pela aplicação usando apenas JAVA
* Geração das imagens de grafos utilizando a biblioteca GraphViz.
* Divisão em camadas
* Criacao de arquivo de log da aplicação utilizando Log4J
