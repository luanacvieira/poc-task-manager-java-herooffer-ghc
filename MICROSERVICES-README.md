# 🎯 Task Manager - Arquitetura de Microsserviços

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue)](https://docs.docker.com/compose/)
[![Architecture](https://img.shields.io/badge/Architecture-Microservices-purple)](https://microservices.io/)

## 📋 Sobre o Projeto

Este projeto demonstra a **transformação de uma aplicação monolítica** em uma **arquitetura de microsserviços** usando Spring Boot e Docker.

### Evolução da Arquitetura

```
MONÓLITO (Antes)                  MICROSSERVIÇOS (Depois)
┌─────────────────────┐          ┌──────────────────────────┐
│                     │          │     API Gateway          │
│  Task Manager       │          │       (8080)             │
│     Monolith        │   →      └───────────┬──────────────┘
│                     │                      │
│  (Porta 8080)       │              ┌───────┴────────┐
│                     │              │                │
└─────────────────────┘         ┌────▼────┐     ┌────▼────┐
                                │  Task   │     │  Stats  │
                                │ Service │     │ Service │
                                │ (8081)  │     │ (8082)  │
                                └─────────┘     └─────────┘
```

## 🏗️ Arquitetura

### Microsserviços

| Serviço | Porta | Responsabilidade | Banco de Dados |
|---------|-------|------------------|----------------|
| **API Gateway** | 8080 | Roteamento de requisições, ponto de entrada único | - |
| **Task Service** | 8081 | CRUD de tarefas, persistência | H2 (taskdb) |
| **Statistics Service** | 8082 | Agregação de estatísticas, métricas | - |

### Comunicação

```
Cliente/Frontend
      ↓
  API Gateway (8080)
      ↓
  ┌───┴────┐
  ↓        ↓
Task     Statistics ← (HTTP) ← Task Service
Service   Service
```

- **Síncrona**: REST/HTTP entre serviços
- **Gateway Pattern**: API Gateway como ponto de entrada único
- **Database per Service**: Cada serviço tem seu próprio banco isolado

## 🚀 Início Rápido

### Pré-requisitos

- **Docker** 20.10+
- **Docker Compose** 2.0+
- (Opcional) **JDK 17+** e **Maven 3.6+** para desenvolvimento local

### Executar com Docker Compose

```bash
# 1. Clone o repositório
git clone <repo-url>
cd poc-task-manager-java-herooffer-ghc

# 2. Build e execução automatizada
./run-microservices.sh

# Ou manualmente:
docker-compose build
docker-compose up -d

# 3. Ver logs
docker-compose logs -f

# 4. Parar serviços
docker-compose down
```

### Verificar Saúde dos Serviços

```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Task Service
curl http://localhost:8081/actuator/health

# Statistics Service
curl http://localhost:8082/actuator/health
```

## 📡 Endpoints da API

### Via API Gateway (Recomendado)

#### Tarefas
```bash
# Listar todas as tarefas
curl http://localhost:8080/api/tasks

# Criar tarefa
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar microsserviços",
    "description": "Migração completa do monólito",
    "priority": "HIGH",
    "category": "WORK",
    "userId": "dev123"
  }'

# Buscar tarefa por ID
curl http://localhost:8080/api/tasks/1

# Atualizar tarefa
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"completed": true}'

# Excluir tarefa
curl -X DELETE http://localhost:8080/api/tasks/1
```

#### Estatísticas
```bash
# Obter estatísticas agregadas
curl http://localhost:8080/api/statistics

# Exemplo de resposta:
{
  "total": 10,
  "completed": 5,
  "pending": 5,
  "urgentActive": 2,
  "overdue": 1,
  "completionRate": 50.0,
  "byPriority": {
    "HIGH": 3,
    "MEDIUM": 4,
    "LOW": 2,
    "URGENT": 1
  },
  "byCategory": {
    "WORK": 6,
    "PERSONAL": 3,
    "STUDY": 1
  }
}
```

## 📂 Estrutura do Projeto

```
poc-task-manager-java-herooffer-ghc/
├── services/                          # Microsserviços
│   ├── task-service/                  # Serviço de tarefas
│   │   ├── src/
│   │   │   └── main/java/com/example/taskservice/
│   │   │       ├── TaskServiceApplication.java
│   │   │       ├── domain/            # Entidades (Task, Priority, Category)
│   │   │       ├── repository/        # Repositories JPA
│   │   │       ├── service/           # Lógica de negócio
│   │   │       └── controller/        # REST Controllers
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── README.md
│   │
│   ├── statistics-service/            # Serviço de estatísticas
│   │   ├── src/
│   │   │   └── main/java/com/example/statisticsservice/
│   │   │       ├── StatisticsServiceApplication.java
│   │   │       ├── client/            # Cliente HTTP (TaskServiceClient)
│   │   │       ├── dto/               # Data Transfer Objects
│   │   │       ├── service/           # Agregação e cálculos
│   │   │       └── controller/        # REST Controllers
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── README.md
│   │
│   └── api-gateway/                   # Gateway de API
│       ├── src/
│       │   └── main/java/com/example/gateway/
│       │       └── ApiGatewayApplication.java
│       ├── Dockerfile
│       ├── pom.xml
│       └── README.md
│
├── docker-compose.yml                 # Orquestração de containers
├── build-all.sh                       # Script de build
├── run-microservices.sh               # Script de execução
└── README.md                          # Este arquivo
```

## 🔧 Desenvolvimento Local

### Compilar Todos os Serviços

```bash
./build-all.sh

# Ou manualmente para cada serviço:
cd services/task-service
mvn clean install
cd ../statistics-service
mvn clean install
cd ../api-gateway
mvn clean install
```

### Executar Serviço Individual (sem Docker)

```bash
# Task Service
cd services/task-service
mvn spring-boot:run

# Statistics Service
cd services/statistics-service
mvn spring-boot:run

# API Gateway
cd services/api-gateway
mvn spring-boot:run
```

### Acessar Console H2 (Task Service)

```
URL: http://localhost:8081/h2-console
JDBC URL: jdbc:h2:mem:taskdb
Username: sa
Password: (vazio)
```

## 🐳 Docker

### Comandos Úteis

```bash
# Build de todos os serviços
docker-compose build

# Iniciar em background
docker-compose up -d

# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f task-service

# Status dos containers
docker-compose ps

# Restart de um serviço
docker-compose restart statistics-service

# Parar todos os serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v

# Rebuild sem cache
docker-compose build --no-cache
```

### Escalar Serviços

```bash
# Criar 3 instâncias do Task Service
docker-compose up -d --scale task-service=3
```

## 📊 Monitoramento

### Health Checks

Todos os serviços expõem endpoints de health via Spring Boot Actuator:

```bash
# Health checks individuais
curl http://localhost:8080/actuator/health  # Gateway
curl http://localhost:8081/actuator/health  # Task Service
curl http://localhost:8082/actuator/health  # Statistics Service
```

### Métricas

```bash
# Métricas detalhadas
curl http://localhost:8081/actuator/metrics

# Métrica específica
curl http://localhost:8081/actuator/metrics/jvm.memory.used
```

### Rotas do Gateway

```bash
# Ver todas as rotas configuradas
curl http://localhost:8080/actuator/gateway/routes
```

## 🧪 Testes

### Teste de Fluxo Completo

```bash
# 1. Criar tarefa via Gateway
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Teste de Integração",
    "priority": "HIGH",
    "category": "WORK",
    "userId": "test-user"
  }'

# 2. Listar tarefas
curl http://localhost:8080/api/tasks

# 3. Ver estatísticas (deve incluir a nova tarefa)
curl http://localhost:8080/api/statistics
```

## 🎯 Padrões e Práticas Implementados

### Arquitetura
- ✅ **API Gateway Pattern** - Ponto de entrada único
- ✅ **Database per Service** - Bancos isolados por serviço
- ✅ **Service Discovery** - Comunicação via DNS do Docker
- ✅ **Health Check Pattern** - Monitoramento de saúde

### Código
- ✅ **Clean Architecture** - Separação clara de camadas
- ✅ **Repository Pattern** - Abstração de persistência
- ✅ **DTO Pattern** - Transferência de dados entre serviços
- ✅ **Dependency Injection** - Injeção de dependências via Spring
- ✅ **Logging** - Logs estruturados com Slf4j
- ✅ **Validation** - Bean Validation (Jakarta)

### DevOps
- ✅ **Containerização** - Docker para todos os serviços
- ✅ **Orquestração** - Docker Compose
- ✅ **Multi-stage Build** - Otimização de imagens Docker
- ✅ **Health Checks** - Verificação automática de saúde
- ✅ **Scripts de Automação** - Build e deploy automatizados

## 📈 Próximos Passos (Roadmap)

### Curto Prazo
- [ ] Implementar testes unitários e de integração
- [ ] Adicionar cache (Redis) no Statistics Service
- [ ] Implementar Circuit Breaker (Resilience4j)
- [ ] Adicionar tratamento de erros padronizado

### Médio Prazo
- [ ] Implementar autenticação JWT
- [ ] Service Discovery (Eureka/Consul)
- [ ] Configuração centralizada (Spring Cloud Config)
- [ ] Mensageria assíncrona (RabbitMQ/Kafka)
- [ ] Migrar para PostgreSQL em produção

### Longo Prazo
- [ ] Tracing distribuído (Zipkin/Jaeger)
- [ ] Métricas com Prometheus + Grafana
- [ ] CI/CD completo (GitHub Actions)
- [ ] Deploy em Kubernetes
- [ ] API Gateway com Autenticação e Rate Limiting
- [ ] Event Sourcing e CQRS

## 🔍 Troubleshooting

### Porta já em uso
```bash
# Ver processos usando portas
lsof -i :8080
lsof -i :8081
lsof -i :8082

# Parar containers Docker
docker-compose down
```

### Serviço não inicia
```bash
# Ver logs do serviço
docker-compose logs task-service

# Verificar health check
docker inspect task-service

# Restart do serviço
docker-compose restart task-service
```

### Comunicação entre serviços falha
```bash
# Verificar rede Docker
docker network inspect task-manager-network

# Testar conectividade
docker exec -it statistics-service wget http://task-service:8081/actuator/health
```

## 📚 Documentação Adicional

- [Task Service README](services/task-service/README.md)
- [Statistics Service README](services/statistics-service/README.md)
- [API Gateway README](services/api-gateway/README.md)
- [Docker Compose File](docker-compose.yml)

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

## 📝 Licença

Este projeto é um exemplo educacional de migração de monólito para microsserviços.

## 👥 Autores

**Task Manager Team**

---

## 📞 Suporte

Para dúvidas, problemas ou sugestões:
- Abra uma [Issue](../../issues)
- Consulte a [Documentação](docs/)
- Entre em contato com a equipe

---

**Feito com ❤️ e ☕ usando Spring Boot e Docker**
