🔨 Sistema de Leilão em Tempo Real - Backend
Este é um projeto de sistema de leilão em tempo real robusto e escalável, desenvolvido com o ecossistema Spring Boot. O principal objetivo deste sistema é gerenciar lances simultâneos com baixíssima latência e garantir a consistência absoluta dos dados de forma segura.

🚀 Desafios Arquiteturais Solucionados
Comunicação Bidirecional: Uso de WebSockets para atualizar as telas dos clientes instantaneamente a cada novo lance, sem necessidade de polling (requisições HTTP repetitivas).

Concorrência Crítica: Mecanismo híbrido de persistência usando Redis para validação rápida de lances em memória e Locks (Bloqueios) no banco relacional para evitar Race Conditions (dois usuários arrematarem o mesmo item no mesmo milissegundo).

Segurança de Transações: Proteção de endpoints e canais WebSocket através do Spring Security com autenticação via tokens JWT (Stateless).

🛠️ Tecnologias e Ferramentas Utilizadas
Linguagem: Java 17 ou 21 (LTS)

Framework Principal: Spring Boot 3.x

Spring Web (API REST para cadastros e consultas)

Spring WebSocket + STOMP (Transmissão de lances em tempo real)

Spring Data JPA (Abstração da camada de persistência)

Spring Security (Autenticação e controle de acesso)

Spring Boot Validation (Garantia de integridade dos dados de entrada)

Bancos de Dados:

MySQL 8.0: Armazenamento relacional persistente (Usuários, Itens, Histórico de Lances).

Redis: Banco chave-valor em memória para gerenciar o estado dos leilões ativos e controle de concorrência veloz.

Produtividade: Project Lombok

Infraestrutura: Docker e Docker Compose

📐 Modelo de Dados (Entidades Principais)
Usuario: Representa os compradores e vendedores (vínculo por e-mail e senha criptografada).

Item: O produto que está sendo anunciado para leilão, contendo uma descrição e um preço base inicial.

Leilao: A entidade central do domínio. Controla datas de início/fim, o preço atualizado do maior lance e o status (AGENDADO, ATIVO, FINALIZADO).

Lance: Registro histórico imutável contendo o valor ofertado, o usuário remetente e o registro de data/hora oficial do servidor.

🛠️ Como Executar o Ambiente de Desenvolvimento
Pré-requisitos
Docker e Docker Compose instalados na máquina.

JDK 17 ou superior.

Sua IDE de preferência (IntelliJ IDEA, VS Code, etc).

1. Configurando as Variáveis de Ambiente
Na raiz do projeto, crie um arquivo chamado .env com base no exemplo abaixo:

Properties
# CONFIGURAÇÕES DO BANCO DE DADOS (MYSQL)
DB_NAME=leilao_db
DB_PASSWORD=sua_senha_segura
DB_PORT=3306

# CONFIGURAÇÕES DO CACHE (REDIS)
REDIS_PORT=6379
2. Subindo a Infraestrutura (Containers)
Abra o terminal na raiz do projeto e execute o comando:

Bash
docker compose up -d
Este comando irá baixar e iniciar as imagens do MySQL 8.0 e do Redis em segundo plano, expondo as portas configuradas no arquivo .env.

3. Executando a Aplicação Spring Boot
Importe o projeto na sua IDE e execute a classe principal LeilaoApplication.java, ou utilize o terminal via Maven:

Bash
./mvnw spring-boot:run
📝 Próximos Passos do Desenvolvimento
[ ] Configuração do arquivo application.properties no Spring.

[ ] Criação do pacote de Modelos (@Entity) e mapeamento do banco de dados.

[ ] Implementação dos Repositories e DTOs de dados.

[ ] Configuração do Broker de Mensagens WebSocket (STOMP).

[ ] Implementação das regras de negócio concorrentes com Redis e Spring Services.

[ ] Acoplamento do Spring Security + JWT.
