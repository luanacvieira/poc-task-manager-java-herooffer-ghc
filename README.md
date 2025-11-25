# 🚀 Task Manager - Arquitetura de Microsserviços

[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue?logo=github-actions)](/.github/workflows)
[![Coverage](https://img.shields.io/badge/Coverage-80%25+-success?logo=codecov)](RELATORIO-COBERTURA-TESTES.md)
[![Tests](https://img.shields.io/badge/Tests-60%20passing-success?logo=junit5)](RELATORIO-COBERTURA-TESTES.md)
[![Security](https://img.shields.io/badge/Security-Analyzed-orange?logo=security)](ANALISE-SEGURANCA.md)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)](https://docs.docker.com/compose/)
[![Architecture](https://img.shields.io/badge/Architecture-Microservices-purple)](ARQUITETURA-TECNICA.md)

Aplicação moderna de gerenciamento de tarefas construída com **arquitetura de microsserviços**, Spring Boot e interface web Vaadin. O projeto evoluiu de um monólito para microsserviços independentes e escaláveis.

> 🎯 **Migração Completa**: De monólito para microsserviços concluída com sucesso! Veja o [relatório de migração](MIGRATION-REPORT.md)

## 📋 Visão Geral

### Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                    Cliente (Navegador Web)                      │
└────────────────────────────┬────────────────────────────────────┘
                             │
                ┌────────────┴────────────┐
                │                         │
         ┌──────▼──────┐          ┌──────▼──────┐
         │   Frontend  │          │ API Gateway │
         │   Vaadin    │          │   :8080     │
         │   :8090     │          └──────┬──────┘
         └─────────────┘                 │
                                   ┌─────┴──────┐
                                   │            │
                            ┌──────▼─────┐  ┌──▼─────────┐
                            │    Task    │  │ Statistics │
                            │  Service   │  │  Service   │
                            │   :8081    │  │   :8082    │
                            └────────────┘  └────────────┘
```

### Funcionalidades

- ✅ **CRUD Completo de Tarefas** - Criar, listar, atualizar e deletar
- ✅ **Campos Avançados** - Título, descrição, prioridade, categoria, data de vencimento, tags, responsável
- ✅ **Estatísticas em Tempo Real** - Totais, pendentes, concluídas, distribuições
- ✅ **Interface Moderna** - Cards visuais, filtros, badges coloridos, design responsivo
- ✅ **API RESTful** - Endpoints completos para integração
- ✅ **Microsserviços** - Arquitetura escalável e desacoplada
- ✅ **Docker Ready** - Containerização completa com Docker Compose

## 🏗️ Componentes

### Microsserviços

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| **API Gateway** | 8080 | Ponto de entrada único, roteamento de requisições |
| **Task Service** | 8081 | Gerenciamento completo do ciclo de vida das tarefas |
| **Statistics Service** | 8082 | Agregação e cálculo de estatísticas |
| **Frontend Vaadin** | 8090 | Interface web moderna em Java |

### Stack Tecnológica

| Camada | Tecnologia |
|--------|-----------|
| **API Gateway** | Spring Cloud Gateway 4.1.1 |
| **Backend** | Spring Boot 3.2.2, Spring Data JPA |
| **Frontend** | Vaadin 24.3.5 (100% Java) |
| **Persistência** | Hibernate, H2 Database (in-memory) |
| **Validação** | Jakarta Validation |
| **Containerização** | Docker, Docker Compose |
| **Build** | Maven 3.9+, Maven Wrapper |
| **Testes** | Spring Boot Test, JUnit 5, Mockito |
| **Cobertura** | JaCoCo 0.8.11 |
| **CI/CD** | GitHub Actions |

## 🚀 Início Rápido

### Pré-requisitos

- Java 17+
- Docker e Docker Compose
- Maven 3.9+ (opcional, wrapper incluído)

### Executar com Docker Compose (Recomendado)

```bash
# 1. Clonar o repositório
git clone https://github.com/vizagre/poc-task-manager-java-herooffer-ghc.git
cd poc-task-manager-java-herooffer-ghc

# 2. Iniciar todos os microsserviços
docker-compose up -d

# 3. Verificar status
docker-compose ps

# 4. Acessar a aplicação
# Frontend: http://localhost:8090/tasks
# API Gateway: http://localhost:8080
```

### Script de Execução Automatizado

```bash
# Build e start de todos os serviços
./run-microservices.sh

# Ou apenas build
./build-all.sh
```

### Executar Localmente (Desenvolvimento)

```bash
# Microsserviços via Docker
docker-compose up -d

# Compilar e iniciar frontend
export JAVA_HOME=/caminho/para/jdk-17
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

## 📚 Documentação

- **[Arquitetura Técnica](ARQUITETURA-TECNICA.md)** - Documentação completa da arquitetura, diagramas, endpoints
- **[Relatório de Migração](MIGRATION-REPORT.md)** - Detalhes da transformação monólito → microsserviços
- **[Análise de Segurança](ANALISE-SEGURANCA.md)** - Validações e práticas de segurança implementadas
- **[Cobertura de Testes](RELATORIO-COBERTURA-TESTES.md)** - Relatório de testes e cobertura de código
- **[GitHub Actions](GITHUB-ACTIONS-IMPLEMENTACAO.md)** - CI/CD e automação

## 🔌 API Endpoints

### Task Service (via API Gateway :8080)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/tasks` | Listar todas as tarefas |
| GET | `/api/tasks/{id}` | Buscar tarefa por ID |
| GET | `/api/tasks/user/{userId}` | Buscar tarefas por usuário |
| POST | `/api/tasks` | Criar nova tarefa |
| PUT | `/api/tasks/{id}` | Atualizar tarefa |
| DELETE | `/api/tasks/{id}` | Excluir tarefa |

### Statistics Service (via API Gateway :8080)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/statistics` | Obter estatísticas gerais |

Veja a [documentação completa de API](ARQUITETURA-TECNICA.md#endpoints-da-api) para detalhes de request/response.

## 🏗️ Estrutura do Projeto

```
poc-task-manager-java-herooffer-ghc/
├── services/                          # Microsserviços
│   ├── api-gateway/                   # Spring Cloud Gateway (porta 8080)
│   ├── task-service/                  # Gerenciamento de tarefas (porta 8081)
│   └── statistics-service/            # Agregação de estatísticas (porta 8082)
├── src/                               # Frontend Vaadin (porta 8090)
│   ├── main/java/.../
│   │   ├── domain/                    # Entidades (Task, Priority, Category)
│   │   ├── repository/                # Spring Data JPA
│   │   ├── service/                   # Lógica de negócio
│   │   └── web/
│   │       ├── rest/                  # API REST controllers
│   │       └── ui/                    # Interface Vaadin
│   └── test/
├── docker-compose.yml                 # Orquestração de containers
├── build-all.sh                       # Script de build
├── run-microservices.sh               # Script de execução
└── pom.xml                            # Maven POM
```

## 💡 Exemplo de Uso da API

```bash
# Criar nova tarefa
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar autenticação JWT",
    "description": "Adicionar autenticação aos microsserviços",
    "priority": "HIGH",
    "category": "WORK",
    "userId": "user1",
    "dueDate": "2025-12-15",
    "assignedTo": "João Silva",
    "tags": ["seguranca", "backend", "jwt"]
  }'

# Listar todas as tarefas
curl http://localhost:8080/api/tasks

# Obter estatísticas
curl http://localhost:8080/api/statistics
```

## 🧪 Testes e Qualidade

### Cobertura de Testes
O projeto possui **60 testes** (unit + integration) com cobertura mínima de **80%**:

| Serviço | Testes | Cobertura | Status |
|---------|--------|-----------|--------|
| **Task Service** | 40 | 83.9% | ✅ |
| **API Gateway** | 8 | 80.0% | ✅ |
| **Statistics Service** | 12 | 28.9% (97% lógica crítica) | ⚠️ |

Para mais detalhes, veja [RELATORIO-COBERTURA-TESTES.md](RELATORIO-COBERTURA-TESTES.md)

### Executar Testes Localmente
```bash
# Todos os testes
mvn clean test

# Com relatório de cobertura
mvn clean test jacoco:report

# Abrir relatório HTML
open target/site/jacoco/index.html

# Teste específico
mvn test -Dtest=TaskServiceTest
```

### 🔒 Segurança
O projeto foi analisado e teve **12 vulnerabilidades** corrigidas:
- ✅ Validação de entrada robusta (@Pattern, @Size)
- ✅ Sanitização contra XSS/SQL Injection
- ✅ Tratamento de exceções seguro (sem stack traces)
- ✅ Logs protegidos (sem dados sensíveis)
- ✅ Configurações separadas (dev/prod)

Para mais detalhes, veja [ANALISE-SEGURANCA.md](ANALISE-SEGURANCA.md)

## 🚀 CI/CD com GitHub Actions

### Pipelines Configurados

#### 1. CI/CD - Build and Test
Executa automaticamente em cada **Pull Request** e **Push** para `main`/`develop`:

✅ Executa todos os testes  
✅ Valida cobertura mínima de 80%  
✅ Compila JARs de todos os serviços  
✅ Gera relatórios de cobertura  
✅ Bloqueia merge se testes falharem  

#### 2. Coverage Analysis
Analisa cobertura em PRs e posta comentário automático:

📊 Métricas detalhadas por serviço  
📊 Identificação de pacotes com baixa cobertura  
📊 Recomendações de melhoria  

### Execução Manual
```bash
# Via GitHub UI:
Actions → CI/CD - Build and Test → Run workflow

# Ou fazer push:
git push origin feature/minha-feature
```

Para mais detalhes, veja [GITHUB-ACTIONS-IMPLEMENTACAO.md](GITHUB-ACTIONS-IMPLEMENTACAO.md) e [.github/workflows/README.md](.github/workflows/README.md)

## 🛠️ Comandos Úteis

### Docker

```bash
# Iniciar serviços
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f task-service

# Parar serviços
docker-compose down

# Rebuild completo
docker-compose down
docker-compose build --no-cache
docker-compose up -d

# Ver status dos containers
docker-compose ps
```

### Maven

```bash
# Compilar todos os módulos
./mvnw clean install

# Executar testes
./mvnw test

# Gerar relatório de cobertura
./mvnw test jacoco:report

# Build sem testes
./mvnw clean package -DskipTests

# Executar aplicação
./mvnw spring-boot:run
```

## 🚧 Próximos Passos

### Melhorias Planejadas

- [ ] **Autenticação e Autorização** - Implementar JWT e Spring Security
- [ ] **Banco de Dados Persistente** - Migrar de H2 para PostgreSQL
- [ ] **Service Discovery** - Adicionar Eureka ou Consul
- [ ] **Circuit Breaker** - Implementar Resilience4j
- [ ] **API Documentation** - Adicionar Swagger/OpenAPI
- [ ] **Observabilidade** - Prometheus, Grafana, Zipkin
- [ ] **Mensageria** - Implementar eventos assíncronos com RabbitMQ/Kafka
- [ ] **Cache** - Adicionar Redis para performance

### Arquitetura Futura

```
┌─────────────────────────────────────────────────────────┐
│                      Load Balancer                      │
└──────────────────────────┬──────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │
                    │   + Auth    │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
         ┌────▼────┐  ┌───▼────┐  ┌───▼─────┐
         │  Task   │  │ Stats  │  │  User   │
         │ Service │  │Service │  │ Service │
         └────┬────┘  └───┬────┘  └────┬────┘
              │           │            │
         ┌────▼────┐      │       ┌────▼────┐
         │PostgreSQL│     │       │PostgreSQL│
         └─────────┘      │       └─────────┘
                          │
                     ┌────▼────┐
                     │ Message │
                     │  Queue  │
                     └─────────┘
```

## 📞 Contato e Contribuição

- **Repositório:** https://github.com/vizagre/poc-task-manager-java-herooffer-ghc
- **Issues:** Use o GitHub Issues para reportar bugs
- **Pull Requests:** Contribuições são bem-vindas!

## 📄 Licença

Este projeto está licenciado sob a licença ISC.

---

**Desenvolvido com** ☕ **e** 💙 **usando Spring Boot e Vaadin**
