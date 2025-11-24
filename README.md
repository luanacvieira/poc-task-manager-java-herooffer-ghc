# Java Task Manager Monolith

[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue?logo=github-actions)](/.github/workflows)
[![Coverage](https://img.shields.io/badge/Coverage-80%25+-success?logo=codecov)](RELATORIO-COBERTURA-TESTES.md)
[![Tests](https://img.shields.io/badge/Tests-60%20passing-success?logo=junit5)](RELATORIO-COBERTURA-TESTES.md)
[![Security](https://img.shields.io/badge/Security-Analyzed-orange?logo=security)](ANALISE-SEGURANCA.md)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=spring-boot)](https://spring.io/projects/spring-boot)

Monolito Java (Spring Boot + Vaadin + JPA/H2) que replica integralmente as funcionalidades do Task Manager original (Node/React). Projeto inicial para posterior refatoração em arquitetura modular/microservices.

> 🚀 **CI/CD Automatizado**: Build e testes automáticos em cada Pull Request e Push (cobertura mínima: 80%)

## Funcionalidades
- CRUD de tarefas
- Campos: título, descrição, prioridade (LOW/MEDIUM/HIGH/URGENT), categoria (WORK/PERSONAL/STUDY/HEALTH/OTHER), dueDate (LocalDate), tags (Set<String>), assignedTo, userId, completed, createdAt/updatedAt
- Estatísticas agregadas (total, pendentes, concluídas, urgentes)
- UI 100% Java com Vaadin (formulário + grid + edição inline)
- API REST /api/tasks espelhando versão anterior
- Persistência JPA/Hibernate (H2 memória)
- Teste de serviço básico

## Stack
| Camada | Tecnologia |
|--------|-----------|
| Web UI | Vaadin 24 |
| API REST | Spring Boot Web |
| Persistência | Spring Data JPA / Hibernate |
| Banco | H2 memória (dev) |
| Validações | Jakarta Validation |
| Build | Maven |
| Testes | Spring Boot Test / JUnit 5 / Mockito |
| Cobertura | JaCoCo 0.8.11 |
| CI/CD | GitHub Actions |

## Estrutura
```
java-task-manager-monolith/
  pom.xml
  src/main/java/com/example/taskmanager/
    TaskManagerApplication.java
    domain/ (Task, Priority, Category)
    repository/ (TaskRepository)
    service/ (TaskService)
    web/rest/ (TaskController, DTO/Mapper)
    web/ui/ (MainLayout, TaskView)
  src/main/resources/application.properties
  src/test/java/com/example/taskmanager/TaskServiceTests.java
```

## Execução
Pré-requisitos: JDK 17+
```bash
mvn spring-boot:run
```
Aplicação inicia:
- UI Vaadin: http://localhost:8080/tasks
- API REST: http://localhost:8080/api/tasks
- H2 Console: http://localhost:8080/h2-console (jdbc:h2:mem:taskdb)

## Endpoints
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | /api/tasks | Lista todas as tarefas |
| POST | /api/tasks | Cria nova tarefa |
| PUT | /api/tasks/{id} | Atualiza parcialmente |
| DELETE | /api/tasks/{id} | Remove tarefa |
| GET | /api/tasks/stats | Estatísticas agregadas |

## Exemplo cURL
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Revisar arquitetura",
    "priority": "HIGH",
    "category": "WORK",
    "dueDate": "2025-11-13",
    "tags": ["planejamento","arquitetura"],
    "userId": "user1"
  }'
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

Para mais detalhes, veja [.github/workflows/README.md](.github/workflows/README.md)

## Evolução Planejada
| Fase | Objetivo |
|------|----------|
| 1 | Monolito funcional (atual) |
| 2 | Introduzir camadas modulares (separar domínio, aplicação, infraestrutura) |
| 3 | Extrair API REST para módulo próprio |
| 4 | Migrar UI para frontend separado (React ou Vaadin separado) |
| 5 | Persistência externa (PostgreSQL/Mongo) + migração de dueDate conforme necessidade |
| 6 | Autenticação/JWT multiusuário |
| 7 | Observabilidade (logs estruturados, métricas, tracing) |

## Refatorações Futuras (Sugestões)
- Substituir `TaskService` por CQRS (Commands/Queries) moduláveis
- Introduzir DTO específico para criação/atualização (CreateTaskRequest / UpdateTaskRequest)
- Eventos de domínio (TaskCreated, TaskCompleted)
- Filtragem e paginação avançada (Specification API / QueryDSL)
- Testes de integração com Testcontainers (PostgreSQL/Mongo)

## Como Criar Novo Repositório (Git)
Dentro do diretório `java-task-manager-monolith`:
```bash
git init
git add .
git commit -m "feat: inicial monolito java task manager"
git branch -M main
git remote add origin https://github.com/<seu-user>/<novo-repo>.git
git push -u origin main
```

## Licença
ISC (ajuste conforme necessidade corporativa).

---
Documentação inicial concluída. Pronto para evoluir com desacoplamento guiado.
