# Sistema de Chat Distribuído

Este repositório contém a implementação de um sistema de chat distribuído desenvolvido como parte de um trabalho de Sistemas Distribuídos. O sistema permite que os usuários se cadastrem e façam login a partir do nome de usuário e senha(com nome de usuário único), entrem e saiam de grupos, acessem grupos(para começar a receber mensagens desse grupo, acessando apenas um grupo por vez), listem usuários online do grupo acessado, enviem mensagens individuais ou em grupo(para o grupo acessado), atualizem seu status e verifiquem o status de outros usuários.

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
- Protocolo Requisição/Resposta: Protocolo criado para padronizar as mensagens trocadas entre clientes e servidores, facilitando a comunicação.
- Sockets TCP: Para comunicação entre os servidores.
 
## Requisitos Não Funcionais

Os seguintes requisitos não funcionais foram considerados na implementação do sistema:

- Segurança - As seguintes medidas foram adotadas: criptografia usando SSL para comunicação com o servidor Redis(tanto pela parte dos clientes quanto dos servidores), autenticação e autorização realizados pelo servidor de autenticação e autorização.
- Escalabilidade - A arquitetura do sistema foi projetada para suportar escalabilidade, com a utilização do Redis Pub Sub e a separação de responsabilidades entre os servidores.

## Arquitetura do Sistema

O sistema de chat distribuído adota uma arquitetura cliente-servidor. Os principais componentes da arquitetura são:

- Servidor de Autenticação e Autorização: Responsável pela autenticação e autorização das requisições enviadas pelos clientes. Ele recebe as requisições dos clientes por meio de um canal do Redis Pub Sub e encaminha as requisições autorizadas para o servidor de lógica de negócios. Para cadastro de usuários e login, ele se comunica com o servidor de acesso a dados e envia a resposta aos clientes por meio de outro canal do Redis Pub Sub, no qual os clientes estão inscritos.
- Servidor de Lógica de Negócios: Responsável por processar as requisições dos clientes, realizar as operações do sistema de chat e interagir com o servidor de acesso a dados. Após processar as requisições dos clientes com a ajuda do servidor de acesso a dados, ele envia as respostas no canal em que os clientes estão inscritos.
- Servidor de Acesso a Dados: Responsável por gerenciar o acesso aos bancos de dados, utilizando PostgreSQL para armazenar as informações do sistema. Ele se comunica com os outros dois servidores quando eles precisam utilizar os bancos de dados do sistema.

A comunicação entre os clientes e os servidores é realizada por meio de dois canais do Redis Pub/Sub. Os clientes publicam suas requisições em um canal específico, no qual o servidor de autenticação e autorização está inscrito. As respostas são publicadas(tanto pelo servidor de autenticação - para as operações de cadastro e login - quanto pelo servidor de lógica de negócios) em outro canal no qual os clientes estão inscritos, permitindo que sejam recebidas por todos os clientes interessados. A comunicação individual entre os servidores é feita utilizando Sockets TCP. Criptografia usando SSL é realizada na comunicação entre o servidor de autenticação e o Redis, entre o Redis e os clientes e entre o Redis e o servidor de lógica de dados.

## Contribuições

Este repositório foi desenvolvido como parte de um trabalho acadêmico e, portanto, contribuições externas não são aceitas. No entanto, sinta-se à vontade para explorar o código e utilizar o sistema de chat distribuído como base para seus próprios projetos.

## Contato

Para mais informações sobre o projeto, entre em contato com a equipe responsável:

- Ítalo Araújo: italoaraujo1001@alu.ufc.br
- Saulo Bruno: saulobruno@alu.ufc.br
- Ryan Guilherme: ryanguilhermetbt@gmail.com

Agradecemos pelo interesse em nosso projeto!
