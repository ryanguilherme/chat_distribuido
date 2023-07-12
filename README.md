# Sistema de Chat Distribuído

Este repositório contém a implementação de um sistema de chat distribuído desenvolvido como parte de um trabalho de Sistemas Distribuídos. O sistema permite que os usuários se cadastrem, enviem mensagens individuais ou em grupo, criem e entrem em grupos, bloqueiem outros usuários, excluam mensagens, pesquisem mensagens, listem usuários online e atualizem seu status.

## Equipe

- [Ítalo Araújo](https://github.com/ItaloSAraujo)
- [Saulo Bruno](https://github.com/SauloBrunoF)
- [Ryan Guilherme](https://github.com/ryanguilherme)

## Tecnologias Utilizadas

O sistema de chat distribuído foi implementado utilizando as seguintes tecnologias:

- Redis Pub/Sub: Para comunicação assíncrona entre os clientes e os servidores.
- PostgreSQL: Para o armazenamento das informações do sistema.
- Protocol Buffers: Para a representação externa de dados, proporcionando uma estrutura compacta e eficiente.
- JAVA: Linguagem de programação utilizada para implementar a lógica do sistema de chat distribuído.

## Requisitos Não Funcionais

Os seguintes requisitos não funcionais foram considerados na implementação do sistema:

- Segurança: Foram adotadas medidas de autenticação, autorização e criptografia para garantir a segurança das comunicações entre os usuários.
- Escalabilidade: A arquitetura do sistema foi projetada para suportar escalabilidade, permitindo replicação dos servidores e balanceamento de carga.

## Arquitetura do Sistema

O sistema de chat distribuído adota uma arquitetura cliente-servidor. Os principais componentes da arquitetura são:

- Servidor de Segurança: Responsável pela autenticação, autorização e criptografia das comunicações entre os usuários.
- Servidor de Lógica de Negócios: Responsável por processar as requisições dos usuários, realizar as operações do sistema de chat e interagir com o servidor de acesso a dados.
- Servidor de Acesso a Dados: Responsável por gerenciar o acesso aos bancos de dados, utilizando PostgreSQL para armazenar as informações do sistema.

A comunicação entre os clientes e os servidores é realizada por meio de canais do Redis Pub/Sub. Os clientes publicam suas requisições em um canal específico, e o servidor de segurança encaminha as requisições para o servidor de lógica de negócios. As respostas são publicadas em outro canal, permitindo que sejam recebidas por todos os clientes interessados. A comunicação individual entre os servidores é feita utilizando Sockets TCP. O Socket SSL é realizado entre o servidor de autenticação e o Redis, entre o Redis e os clientes e entre o Redis e o servidor de lógica de dados.


## Contribuições

Este repositório foi desenvolvido como parte de um trabalho acadêmico e, portanto, contribuições externas não são aceitas. No entanto, sinta-se à vontade para explorar o código e utilizar o sistema de chat distribuído como base para seus próprios projetos.

## Contato

Para mais informações sobre o projeto, entre em contato com a equipe responsável:

- Ítalo Araújo: italoaraujo1001@alu.ufc.br
- Saulo Bruno: saulobruno@alu.ufc.br
- Ryan Guilherme: ryanguilhermetbt@gmail.com

Agradecemos pelo interesse em nosso projeto!
