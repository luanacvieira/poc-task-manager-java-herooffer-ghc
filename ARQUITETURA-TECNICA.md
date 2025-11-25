# Documentação Técnica - Task Manager

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Arquitetura de Microsserviços](#arquitetura-de-microsserviços)
3. [Componentes do Sistema](#componentes-do-sistema)
4. [Endpoints da API](#endpoints-da-api)
5. [Modelo de Dados](#modelo-de-dados)
6. [Diagramas](#diagramas)
7. [Fluxos de Comunicação](#fluxos-de-comunicação)
8. [Tecnologias Utilizadas](#tecnologias-utilizadas)
9. [Como Executar](#como-executar)

---

## 🎯 Visão Geral

O **Task Manager** é uma aplicação de gerenciamento de tarefas construída com arquitetura de microsserviços, oferecendo uma interface web moderna para criação, edição, visualização e análise de tarefas.

### Objetivo da Aplicação
Permitir que usuários gerenciem suas tarefas de forma eficiente, com recursos de:
- Criação e edição de tarefas com múltiplos atributos
- Categorização por prioridade (LOW, MEDIUM, HIGH, URGENT)
- Organização por categoria (WORK, PERSONAL, STUDY, HEALTH, OTHER)
- Sistema de tags para classificação flexível
- Atribuição de responsáveis
- Definição de datas de vencimento
- Visualização de estatísticas e métricas agregadas
- Interface visual moderna com cartões e filtros

### Evolução Arquitetural
A aplicação evoluiu de um **monólito** para uma arquitetura de **microsserviços**, mantendo compatibilidade com a API original através de um API Gateway.

---

## 🏗️ Arquitetura de Microsserviços

### Visão Arquitetural

```
┌─────────────────────────────────────────────────────────────────┐
│                          CLIENTE                                │
│                    (Navegador Web)                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ HTTP (porta 8090)
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND VAADIN                              │
│                  (Aplicação Monolítica)                         │
│                                                                 │
│  • Interface web moderna com cartões                            │
│  • Filtros e visualizações                                      │
│  • Formulários de CRUD                                          │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ HTTP/REST (porta 8080)
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY                                │
│                  (Spring Cloud Gateway)                         │
│                                                                 │
│  • Ponto de entrada único                                       │
│  • Roteamento inteligente                                       │
│  • CORS e segurança                                             │
│  • Load balancing (futuro)                                      │
└──────────────┬──────────────────────────┬───────────────────────┘
               │                          │
    /api/tasks │                          │ /api/statistics
               ▼                          ▼
┌──────────────────────────┐   ┌──────────────────────────┐
│     TASK SERVICE         │   │  STATISTICS SERVICE      │
│   (Microsserviço 1)      │   │   (Microsserviço 2)      │
│                          │   │                          │
│  • CRUD de tarefas       │◄──┤  • Agregação de dados    │
│  • Validações            │   │  • Cálculo de métricas   │
│  • Persistência          │   │  • Estatísticas          │
│  • Porta: 8081           │   │  • Porta: 8082           │
└──────────┬───────────────┘   └──────────────────────────┘
           │                              │
           │                              │ HTTP Client
           ▼                              │ (RestTemplate)
┌──────────────────────┐                  │
│   DATABASE H2        │──────────────────┘
│   (In-Memory)        │
│                      │
│  • task              │
│  • task_tags         │
└──────────────────────┘
```

### Princípios Arquiteturais

1. **Single Responsibility**: Cada microsserviço tem uma responsabilidade única
   - Task Service: Gerencia o ciclo de vida das tarefas
   - Statistics Service: Processa e agrega estatísticas

2. **API Gateway Pattern**: Ponto de entrada centralizado
   - Roteamento baseado em path
   - Abstração dos serviços internos
   - Facilita evolução futura (autenticação, rate limiting)

3. **Database per Service**: Cada serviço possui seu próprio banco (conceitual)
   - Task Service: taskdb (H2 in-memory)
   - Isolamento de dados

4. **REST API**: Comunicação via HTTP/JSON
   - Stateless
   - Padrões RESTful
   - Códigos HTTP apropriados

5. **Domain-Driven Design (DDD)**
   - Agregados bem definidos (Task)
   - Camadas claras (Domain, Service, Repository, Controller)

---

## 🧩 Componentes do Sistema

### 1. API Gateway (porta 8080)

**Responsabilidades:**
- Roteamento de requisições para microsserviços
- Ponto de entrada único para clientes externos
- Configuração de CORS
- Health checks

**Tecnologias:**
- Spring Boot 3.2.2
- Spring Cloud Gateway 4.1.1
- Java 17

**Rotas Configuradas:**
```properties
/api/tasks/**       → task-service:8081
/api/statistics/**  → statistics-service:8082
/healthcheck        → Gateway health check
```

**Arquivos Principais:**
- `HealthCheckController.java` - Health check endpoint
- `application.properties` - Configuração de rotas

---

### 2. Task Service (porta 8081)

**Responsabilidades:**
- Gerenciamento completo do ciclo de vida das tarefas
- CRUD (Create, Read, Update, Delete)
- Validações de negócio e segurança
- Persistência de dados

**Camadas:**

#### Domain Layer
- `Task.java` - Entidade principal (Aggregate Root)
- `Priority.java` - Enum de prioridades
- `Category.java` - Enum de categorias

#### Repository Layer
- `TaskRepository.java` - Interface Spring Data JPA
  - Métodos de consulta customizados
  - Queries derivadas do nome do método

#### Service Layer
- `TaskService.java` - Lógica de negócio
  - Validações
  - Transformações
  - Coordenação de transações

#### Controller Layer
- `TaskController.java` - API REST
  - Endpoints HTTP
  - Validação de entrada
  - Respostas apropriadas

**Tecnologias:**
- Spring Boot 3.2.2
- Spring Data JPA
- H2 Database (in-memory)
- Hibernate
- Jakarta Validation
- Lombok

---

### 3. Statistics Service (porta 8082)

**Responsabilidades:**
- Agregação de dados de tarefas
- Cálculo de estatísticas e métricas
- Análise de distribuições
- Identificação de tendências

**Componentes:**

#### Client Layer
- `TaskServiceClient.java` - Cliente HTTP para Task Service
  - Comunicação via RestTemplate
  - Consumo de endpoints REST
  - Tratamento de erros

#### Service Layer
- `StatisticsService.java` - Lógica de agregação
  - Coleta de dados
  - Cálculos estatísticos
  - Processamento de streams

#### Controller Layer
- `StatisticsController.java` - API REST
  - Endpoint de estatísticas
  - Health check

#### DTO Layer
- `StatisticsResponse.java` - Resposta de estatísticas
- `TaskDto.java` - DTO de tarefa

**Métricas Calculadas:**
- Total de tarefas
- Tarefas concluídas vs pendentes
- Tarefas urgentes ativas
- Taxa de conclusão (%)
- Distribuição por prioridade
- Distribuição por categoria
- Tarefas vencidas

**Tecnologias:**
- Spring Boot 3.2.2
- RestTemplate
- Java Streams API
- Lombok

---

### 4. Frontend Vaadin (porta 8090)

**Responsabilidades:**
- Interface web moderna
- Interação com usuário
- Visualização de dados
- Formulários e validações client-side

**Componentes:**

#### UI Layer
- `TaskView.java` - Página principal
  - Cards de tarefas
  - Formulário de criação/edição
  - Filtros
  - Estatísticas visuais

- `MainLayout.java` - Layout principal
  - Header
  - Menu lateral
  - Navegação

**Recursos Visuais:**
- Cards modernos para tarefas
- Badges coloridos por prioridade
- Ícones intuitivos por categoria
- Filtros dinâmicos
- Dialogs modais
- Notificações toast
- Design responsivo

**Tecnologias:**
- Vaadin 24.3.5
- Spring Boot 3.2.2
- Java 17

---

## 🔌 Endpoints da API

### Task Service (http://localhost:8081)

#### 1. Listar Todas as Tarefas
```http
GET /api/tasks
```
**Resposta:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Implementar autenticação JWT",
    "description": "Adicionar autenticação JWT aos microsserviços",
    "priority": "HIGH",
    "category": "WORK",
    "dueDate": "2025-12-15",
    "tags": ["jwt", "backend", "seguranca"],
    "assignedTo": "João Silva",
    "userId": "user1",
    "completed": false,
    "createdAt": "2025-11-25T13:56:50.155954",
    "updatedAt": "2025-11-25T13:56:50.156138"
  }
]
```

#### 2. Buscar Tarefa por ID
```http
GET /api/tasks/{id}
```
**Parâmetros:**
- `id` (path) - ID da tarefa

**Respostas:**
- `200 OK` - Tarefa encontrada
- `404 Not Found` - Tarefa não existe

#### 3. Buscar Tarefas por Usuário
```http
GET /api/tasks/user/{userId}
```
**Parâmetros:**
- `userId` (path) - ID do usuário (3-50 caracteres alfanuméricos)

**Resposta:** `200 OK` - Lista de tarefas do usuário

#### 4. Criar Nova Tarefa
```http
POST /api/tasks
Content-Type: application/json
```
**Corpo da Requisição:**
```json
{
  "title": "Estudar design patterns",
  "description": "Revisar padrões de projeto",
  "priority": "MEDIUM",
  "category": "STUDY",
  "userId": "user1",
  "dueDate": "2025-12-20",
  "assignedTo": "Maria Santos",
  "tags": ["arquitetura", "estudo"],
  "completed": false
}
```

**Validações:**
- `title`: obrigatório, 3-255 caracteres
- `priority`: obrigatório (LOW, MEDIUM, HIGH, URGENT)
- `category`: obrigatório (WORK, PERSONAL, STUDY, HEALTH, OTHER)
- `userId`: obrigatório, 3-50 caracteres
- `tags`: máximo 10, formato: [a-z0-9-]{2,20}
- `assignedTo`: opcional, máximo 50 caracteres

**Resposta:** `201 Created`
```http
Location: /api/tasks/2
```

#### 5. Atualizar Tarefa
```http
PUT /api/tasks/{id}
Content-Type: application/json
```
**Corpo da Requisição:** (campos parciais suportados)
```json
{
  "title": "Novo título",
  "completed": true
}
```

**Respostas:**
- `200 OK` - Tarefa atualizada
- `404 Not Found` - Tarefa não existe

#### 6. Excluir Tarefa
```http
DELETE /api/tasks/{id}
```
**Respostas:**
- `204 No Content` - Tarefa excluída
- `404 Not Found` - Tarefa não existe

#### 7. Health Check
```http
GET /api/tasks/health
```
**Resposta:** `200 OK`
```json
{
  "message": "Task Service is running",
  "status": "UP"
}
```

---

### Statistics Service (http://localhost:8082)

#### 1. Obter Estatísticas Gerais
```http
GET /api/statistics
```
**Resposta:** `200 OK`
```json
{
  "total": 5,
  "completed": 1,
  "pending": 4,
  "urgentActive": 1,
  "overdue": 0,
  "completionRate": 20.0,
  "byPriority": {
    "URGENT": 1,
    "HIGH": 1,
    "MEDIUM": 2,
    "LOW": 1
  },
  "byCategory": {
    "PERSONAL": 1,
    "STUDY": 1,
    "WORK": 2,
    "HEALTH": 1
  }
}
```

**Descrição dos Campos:**
- `total`: Total de tarefas no sistema
- `completed`: Tarefas concluídas
- `pending`: Tarefas pendentes
- `urgentActive`: Tarefas urgentes não concluídas
- `overdue`: Tarefas vencidas
- `completionRate`: Percentual de conclusão
- `byPriority`: Distribuição por prioridade
- `byCategory`: Distribuição por categoria

#### 2. Health Check
```http
GET /api/statistics/health
```
**Resposta:** `200 OK`
```json
{
  "service": "Statistics Service",
  "status": "UP",
  "taskServiceAvailable": true
}
```

---

### API Gateway (http://localhost:8080)

#### Health Check
```http
GET /healthcheck
```
**Resposta:** `200 OK`
```json
{
  "status": "ok"
}
```

**Proxy Routes:**
- `GET/POST/PUT/DELETE /api/tasks/**` → task-service:8081
- `GET /api/statistics/**` → statistics-service:8082

---

## 📊 Modelo de Dados

### Entidade: Task

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 255)
    private String title;
    
    @Size(max = 1000)
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private Priority priority;        // LOW, MEDIUM, HIGH, URGENT
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;        // WORK, PERSONAL, STUDY, HEALTH, OTHER
    
    private LocalDate dueDate;
    
    @ElementCollection
    private Set<String> tags;
    
    @Size(max = 50)
    private String assignedTo;
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String userId;
    
    private boolean completed;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

### Enums

#### Priority
```java
public enum Priority {
    LOW,      // Prioridade baixa
    MEDIUM,   // Prioridade média
    HIGH,     // Prioridade alta
    URGENT    // Prioridade urgente
}
```

#### Category
```java
public enum Category {
    WORK,     // Trabalho
    PERSONAL, // Pessoal
    STUDY,    // Estudos
    HEALTH,   // Saúde
    OTHER     // Outros
}
```

### Esquema do Banco de Dados

```sql
-- Tabela principal
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    priority VARCHAR(20) NOT NULL,
    category VARCHAR(20) NOT NULL,
    due_date DATE,
    assigned_to VARCHAR(50),
    user_id VARCHAR(50) NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Tabela de tags (relacionamento many-to-many)
CREATE TABLE task_tags (
    task_id BIGINT NOT NULL,
    tag VARCHAR(20),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_user_id ON tasks(user_id);
CREATE INDEX idx_priority ON tasks(priority);
CREATE INDEX idx_category ON tasks(category);
CREATE INDEX idx_completed ON tasks(completed);
CREATE INDEX idx_due_date ON tasks(due_date);
```

---

## 📐 Diagramas

### Diagrama de Classes

```
┌─────────────────────────────────────────────────────────────┐
│                         <<Entity>>                          │
│                           Task                              │
├─────────────────────────────────────────────────────────────┤
│ - id: Long                                                  │
│ - title: String                                             │
│ - description: String                                       │
│ - priority: Priority                                        │
│ - category: Category                                        │
│ - dueDate: LocalDate                                        │
│ - tags: Set<String>                                         │
│ - assignedTo: String                                        │
│ - userId: String                                            │
│ - completed: boolean                                        │
│ - createdAt: LocalDateTime                                  │
│ - updatedAt: LocalDateTime                                  │
├─────────────────────────────────────────────────────────────┤
│ + getters/setters                                           │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────┴────────┐  ┌───────┴────────┐  ┌───────┴────────┐
│  <<Enum>>      │  │  <<Enum>>      │  │ <<Repository>> │
│   Priority     │  │   Category     │  │ TaskRepository │
├────────────────┤  ├────────────────┤  ├────────────────┤
│ + LOW          │  │ + WORK         │  │ extends        │
│ + MEDIUM       │  │ + PERSONAL     │  │ JpaRepository  │
│ + HIGH         │  │ + STUDY        │  │                │
│ + URGENT       │  │ + HEALTH       │  │ + findByUserId │
└────────────────┘  │ + OTHER        │  └────────────────┘
                    └────────────────┘           │
                                                 │
                                                 ▼
┌─────────────────────────────────────────────────────────────┐
│                      <<Service>>                            │
│                      TaskService                            │
├─────────────────────────────────────────────────────────────┤
│ - repository: TaskRepository                                │
├─────────────────────────────────────────────────────────────┤
│ + create(task: Task): Task                                  │
│ + findAll(): List<Task>                                     │
│ + findById(id: Long): Optional<Task>                        │
│ + findByUserId(userId: String): List<Task>                  │
│ + update(id: Long, partial: Task): Task                     │
│ + delete(id: Long): void                                    │
│ + total(): long                                             │
│ + pending(): long                                           │
│ + completed(): long                                         │
│ + urgentActive(): long                                      │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ uses
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   <<RestController>>                        │
│                    TaskController                           │
├─────────────────────────────────────────────────────────────┤
│ - service: TaskService                                      │
├─────────────────────────────────────────────────────────────┤
│ + listAll(): ResponseEntity<List<Task>>                     │
│ + getById(id: Long): ResponseEntity<Task>                   │
│ + getByUserId(userId: String): ResponseEntity<List<Task>>   │
│ + create(task: Task): ResponseEntity<Task>                  │
│ + update(id: Long, task: Task): ResponseEntity<Task>        │
│ + delete(id: Long): ResponseEntity<Void>                    │
│ + health(): ResponseEntity<HealthResponse>                  │
└─────────────────────────────────────────────────────────────┘
```

### Diagrama de Componentes

```
┌────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                        │
│                                                                │
│  ┌──────────────────────┐       ┌──────────────────────┐      │
│  │    TaskView.java     │       │  MainLayout.java     │      │
│  │  (Vaadin UI)         │       │  (Layout)            │      │
│  └──────────────────────┘       └──────────────────────┘      │
└────────────────────┬───────────────────────────────────────────┘
                     │ HTTP REST
                     ▼
┌────────────────────────────────────────────────────────────────┐
│                      API GATEWAY LAYER                         │
│                                                                │
│  ┌──────────────────────────────────────────────────────┐     │
│  │        Spring Cloud Gateway (Port 8080)              │     │
│  │                                                      │     │
│  │  • Route: /api/tasks/** → task-service:8081         │     │
│  │  • Route: /api/statistics/** → stats-service:8082   │     │
│  └──────────────────────────────────────────────────────┘     │
└────────────────┬─────────────────────────┬─────────────────────┘
                 │                         │
    ┌────────────┘                         └────────────┐
    │                                                   │
    ▼                                                   ▼
┌─────────────────────────────┐     ┌─────────────────────────────┐
│    TASK SERVICE (8081)      │     │  STATISTICS SERVICE (8082)  │
│                             │     │                             │
│  ┌────────────────────┐     │     │  ┌────────────────────┐     │
│  │  TaskController    │     │     │  │ StatisticsController│    │
│  └──────┬─────────────┘     │     │  └──────┬─────────────┘     │
│         │                   │     │         │                   │
│  ┌──────▼─────────────┐     │     │  ┌──────▼─────────────┐     │
│  │   TaskService      │     │◄────┼──┤ StatisticsService  │     │
│  └──────┬─────────────┘     │     │  │                    │     │
│         │                   │     │  └──────┬─────────────┘     │
│  ┌──────▼─────────────┐     │     │         │                   │
│  │  TaskRepository    │     │     │  ┌──────▼─────────────┐     │
│  └──────┬─────────────┘     │     │  │ TaskServiceClient  │     │
│         │                   │     │  └────────────────────┘     │
│  ┌──────▼─────────────┐     │     │                             │
│  │   H2 Database      │     │     │  (Calls Task Service)       │
│  │   (taskdb)         │     │     │                             │
│  └────────────────────┘     │     └─────────────────────────────┘
└─────────────────────────────┘
```

---

## 🔄 Fluxos de Comunicação

### Fluxo 1: Criar Nova Tarefa

```
┌────────┐      ┌────────────┐      ┌─────────────┐      ┌──────────────┐      ┌──────────┐
│ Client │      │   Vaadin   │      │ API Gateway │      │ Task Service │      │ Database │
│(Browser)      │  Frontend  │      │   (8080)    │      │   (8081)     │      │   (H2)   │
└───┬────┘      └─────┬──────┘      └──────┬──────┘      └──────┬───────┘      └────┬─────┘
    │                 │                    │                    │                   │
    │  1. Preenche    │                    │                    │                   │
    │  formulário     │                    │                    │                   │
    │─────────────────>                    │                    │                   │
    │                 │                    │                    │                   │
    │  2. Clica       │                    │                    │                   │
    │  "Salvar"       │                    │                    │                   │
    │─────────────────>                    │                    │                   │
    │                 │                    │                    │                   │
    │                 │ 3. POST            │                    │                   │
    │                 │ /api/tasks         │                    │                   │
    │                 │ {taskData}         │                    │                   │
    │                 │───────────────────>│                    │                   │
    │                 │                    │                    │                   │
    │                 │                    │ 4. Route to        │                   │
    │                 │                    │ task-service:8081  │                   │
    │                 │                    │ POST /api/tasks    │                   │
    │                 │                    │───────────────────>│                   │
    │                 │                    │                    │                   │
    │                 │                    │                    │ 5. Validate       │
    │                 │                    │                    │    & Process      │
    │                 │                    │                    │                   │
    │                 │                    │                    │ 6. INSERT INTO    │
    │                 │                    │                    │    tasks          │
    │                 │                    │                    │──────────────────>│
    │                 │                    │                    │                   │
    │                 │                    │                    │ 7. Task created   │
    │                 │                    │                    │<──────────────────│
    │                 │                    │                    │                   │
    │                 │                    │ 8. 201 Created     │                   │
    │                 │                    │    Location: /api/ │                   │
    │                 │                    │    tasks/1         │                   │
    │                 │                    │<───────────────────│                   │
    │                 │                    │                    │                   │
    │                 │ 9. 201 Created     │                    │                   │
    │                 │    {createdTask}   │                    │                   │
    │                 │<───────────────────│                    │                   │
    │                 │                    │                    │                   │
    │ 10. Exibe       │                    │                    │                   │
    │     notificação │                    │                    │                   │
    │     de sucesso  │                    │                    │                   │
    │<─────────────────                    │                    │                   │
    │                 │                    │                    │                   │
    │                 │ 11. refreshView()  │                    │                   │
    │                 │     GET /api/tasks │                    │                   │
    │                 │───────────────────>│───────────────────>│──────────────────>│
    │                 │                    │                    │                   │
    │ 12. Atualiza    │                    │                    │                   │
    │     lista de    │<───────────────────│<───────────────────│<──────────────────│
    │     cards       │                    │                    │                   │
    │<─────────────────                    │                    │                   │
```

### Fluxo 2: Obter Estatísticas

```
┌────────┐      ┌────────────┐      ┌─────────────┐      ┌──────────────┐      ┌──────────────┐
│ Client │      │   Vaadin   │      │ API Gateway │      │ Statistics   │      │ Task Service │
│(Browser)      │  Frontend  │      │   (8080)    │      │ Service(8082)│      │   (8081)     │
└───┬────┘      └─────┬──────┘      └──────┬──────┘      └──────┬───────┘      └──────┬───────┘
    │                 │                    │                    │                      │
    │  1. Acessa      │                    │                    │                      │
    │  página         │                    │                    │                      │
    │─────────────────>                    │                    │                      │
    │                 │                    │                    │                      │
    │                 │ 2. updateStats()   │                    │                      │
    │                 │    GET /api/       │                    │                      │
    │                 │    statistics      │                    │                      │
    │                 │───────────────────>│                    │                      │
    │                 │                    │                    │                      │
    │                 │                    │ 3. Route to        │                      │
    │                 │                    │ stats-service:8082 │                      │
    │                 │                    │───────────────────>│                      │
    │                 │                    │                    │                      │
    │                 │                    │                    │ 4. GET /api/tasks    │
    │                 │                    │                    │ (consume data)       │
    │                 │                    │                    │─────────────────────>│
    │                 │                    │                    │                      │
    │                 │                    │                    │ 5. List<Task>        │
    │                 │                    │                    │<─────────────────────│
    │                 │                    │                    │                      │
    │                 │                    │                    │ 6. Calculate:        │
    │                 │                    │                    │    • total           │
    │                 │                    │                    │    • completed       │
    │                 │                    │                    │    • pending         │
    │                 │                    │                    │    • urgentActive    │
    │                 │                    │                    │    • byPriority      │
    │                 │                    │                    │    • byCategory      │
    │                 │                    │                    │    • completionRate  │
    │                 │                    │                    │                      │
    │                 │                    │ 7. 200 OK          │                      │
    │                 │                    │    {statistics}    │                      │
    │                 │                    │<───────────────────│                      │
    │                 │                    │                    │                      │
    │                 │ 8. 200 OK          │                    │                      │
    │                 │    {statistics}    │                    │                      │
    │                 │<───────────────────│                    │                      │
    │                 │                    │                    │                      │
    │ 9. Renderiza    │                    │                    │                      │
    │    cards de     │                    │                    │                      │
    │    estatísticas │                    │                    │                      │
    │<─────────────────                    │                    │                      │
```

### Fluxo 3: Atualizar Status da Tarefa

```
┌────────┐      ┌────────────┐      ┌─────────────┐      ┌──────────────┐      ┌──────────┐
│ Client │      │   Vaadin   │      │ API Gateway │      │ Task Service │      │ Database │
└───┬────┘      └─────┬──────┘      └──────┬──────┘      └──────┬───────┘      └────┬─────┘
    │                 │                    │                    │                   │
    │  1. Clica botão │                    │                    │                   │
    │  "Concluir"     │                    │                    │                   │
    │─────────────────>                    │                    │                   │
    │                 │                    │                    │                   │
    │                 │ 2. toggleTask      │                    │                   │
    │                 │    Status()        │                    │                   │
    │                 │    PUT /api/tasks/1│                    │                   │
    │                 │    {completed:true}│                    │                   │
    │                 │───────────────────>│───────────────────>│                   │
    │                 │                    │                    │                   │
    │                 │                    │                    │ 3. UPDATE tasks   │
    │                 │                    │                    │    SET completed  │
    │                 │                    │                    │    = true         │
    │                 │                    │                    │──────────────────>│
    │                 │                    │                    │                   │
    │                 │                    │ 4. 200 OK          │                   │
    │                 │                    │    {updatedTask}   │                   │
    │                 │<───────────────────│<───────────────────│                   │
    │                 │                    │                    │                   │
    │  5. Notificação │                    │                    │                   │
    │  "Status        │                    │                    │                   │
    │  atualizado!"   │                    │                    │                   │
    │<─────────────────                    │                    │                   │
    │                 │                    │                    │                   │
    │                 │ 6. refreshView()   │                    │                   │
    │                 │───────────────────>│───────────────────>│──────────────────>│
    │                 │                    │                    │                   │
    │  7. Card        │                    │                    │                   │
    │  atualizado     │<───────────────────│<───────────────────│<──────────────────│
    │  visualmente    │                    │                    │                   │
    │<─────────────────                    │                    │                   │
```

---

## 🛠️ Tecnologias Utilizadas

### Backend

| Tecnologia | Versão | Propósito |
|-----------|--------|-----------|
| Java | 17 | Linguagem de programação |
| Spring Boot | 3.2.2 | Framework de aplicação |
| Spring Cloud Gateway | 4.1.1 | API Gateway e roteamento |
| Spring Data JPA | 3.2.2 | Persistência e ORM |
| Hibernate | 6.4.1 | ORM e gerenciamento de entidades |
| H2 Database | 2.2.224 | Banco de dados in-memory |
| Jakarta Validation | 3.0.2 | Validação de beans |
| Lombok | 1.18.30 | Redução de boilerplate |
| SLF4J + Logback | 2.0.11 | Logging |
| Maven | 3.9+ | Gerenciamento de dependências |

### Frontend

| Tecnologia | Versão | Propósito |
|-----------|--------|-----------|
| Vaadin | 24.3.5 | Framework UI web |
| Vaadin Flow | 24.3.5 | Componentes UI em Java |
| Atmosphere | 3.0.4 | WebSocket e Push |

### DevOps

| Tecnologia | Versão | Propósito |
|-----------|--------|-----------|
| Docker | 24+ | Containerização |
| Docker Compose | 3.8 | Orquestração de containers |
| Maven Wrapper | 3.9.6 | Build tool portável |

### Padrões e Conceitos

- **Arquitetura de Microsserviços**
- **RESTful API**
- **API Gateway Pattern**
- **Domain-Driven Design (DDD)**
- **Repository Pattern**
- **Service Layer Pattern**
- **DTO (Data Transfer Object)**
- **CORS (Cross-Origin Resource Sharing)**
- **Bean Validation (JSR 380)**
- **Clean Architecture**

---

## 🚀 Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.9+
- Docker (opcional, para microsserviços)
- 4GB RAM disponível

### Opção 1: Executar com Docker Compose (Recomendado)

```bash
# 1. Clonar o repositório
git clone https://github.com/vizagre/poc-task-manager-java-herooffer-ghc.git
cd poc-task-manager-java-herooffer-ghc

# 2. Iniciar todos os microsserviços
docker-compose up -d

# 3. Verificar status dos containers
docker-compose ps

# 4. Acompanhar logs
docker-compose logs -f

# 5. Acessar aplicações
# API Gateway: http://localhost:8080
# Task Service: http://localhost:8081
# Statistics Service: http://localhost:8082
```

### Opção 2: Executar Localmente (com Frontend)

```bash
# 1. Configurar Java 17
export JAVA_HOME=/usr/lib/jvm/java-1.17.0-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 2. Iniciar microsserviços com Docker
docker-compose up -d

# 3. Compilar aplicação monolítica (frontend)
cd /caminho/do/projeto
./mvnw clean package -DskipTests

# 4. Iniciar frontend Vaadin
./mvnw spring-boot:run

# 5. Acessar aplicação
# Frontend: http://localhost:8090/tasks
```

### Opção 3: Executar Serviços Individuais

```bash
# Task Service
cd services/task-service
./mvnw spring-boot:run
# Acesso: http://localhost:8081

# Statistics Service
cd services/statistics-service
./mvnw spring-boot:run
# Acesso: http://localhost:8082

# API Gateway
cd services/api-gateway
./mvnw spring-boot:run
# Acesso: http://localhost:8080
```

### Verificar Saúde dos Serviços

```bash
# API Gateway
curl http://localhost:8080/healthcheck

# Task Service
curl http://localhost:8081/api/tasks/health

# Statistics Service
curl http://localhost:8082/api/statistics/health

# Frontend Vaadin
curl http://localhost:8090/tasks
```

### Criar Tarefas de Exemplo

```bash
# Criar tarefa
curl -X POST http://localhost:8081/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Exemplo de tarefa",
    "description": "Descrição da tarefa",
    "priority": "HIGH",
    "category": "WORK",
    "userId": "user1",
    "dueDate": "2025-12-31",
    "tags": ["exemplo", "teste"]
  }'

# Listar tarefas
curl http://localhost:8081/api/tasks

# Obter estatísticas
curl http://localhost:8082/api/statistics
```

### Parar Aplicação

```bash
# Parar containers Docker
docker-compose down

# Parar aplicação local (Ctrl+C no terminal)
```

---

## 📝 Notas para Novos Colaboradores

### Estrutura de Diretórios

```
poc-task-manager-java-herooffer-ghc/
├── services/
│   ├── api-gateway/          # Spring Cloud Gateway
│   │   ├── src/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   ├── task-service/         # Microsserviço de tarefas
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/.../
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── domain/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── service/
│   │   │   │   └── resources/
│   │   │   └── test/
│   │   ├── Dockerfile
│   │   └── pom.xml
│   └── statistics-service/   # Microsserviço de estatísticas
│       ├── src/
│       ├── Dockerfile
│       └── pom.xml
├── src/                      # Frontend Vaadin (monólito)
│   ├── main/
│   │   ├── java/.../
│   │   │   ├── domain/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── web/
│   │   │       ├── rest/
│   │   │       └── ui/
│   │   └── resources/
│   └── test/
├── docker-compose.yml
├── pom.xml
└── README.md
```

### Convenções de Código

1. **Nomenclatura:**
   - Classes: PascalCase (Ex: `TaskService`)
   - Métodos: camelCase (Ex: `findById`)
   - Constantes: UPPER_SNAKE_CASE (Ex: `MAX_RETRIES`)

2. **Logging:**
   - Use SLF4J com Lombok `@Slf4j`
   - Níveis: ERROR, WARN, INFO, DEBUG
   - Mensagens descritivas e contextuais

3. **Validação:**
   - Use Jakarta Validation annotations
   - Valide no Controller e no Domain
   - Mensagens de erro em português

4. **REST:**
   - Seguir padrões RESTful
   - Códigos HTTP apropriados
   - URIs em plural (Ex: `/api/tasks`)

5. **Testes:**
   - Testes unitários para service layer
   - Testes de integração para controllers
   - Coverage mínimo: 70%

### Próximos Passos Sugeridos

1. **Autenticação e Autorização:**
   - Implementar JWT
   - Spring Security
   - Endpoints protegidos

2. **Banco de Dados Persistente:**
   - Substituir H2 por PostgreSQL
   - Configurar containers Docker
   - Migrations com Flyway

3. **Observabilidade:**
   - Spring Boot Actuator
   - Prometheus + Grafana
   - Distributed Tracing (Zipkin)

4. **Resiliência:**
   - Circuit Breaker (Resilience4j)
   - Retry policies
   - Timeout configurations

5. **API Documentation:**
   - Swagger/OpenAPI
   - Documentação automática
   - API testing interface

6. **CI/CD:**
   - GitHub Actions
   - Testes automatizados
   - Deploy automatizado

---

## 📞 Contato e Suporte

Para dúvidas ou sugestões sobre a arquitetura:

- **Repositório:** https://github.com/vizagre/poc-task-manager-java-herooffer-ghc
- **Issues:** Use o GitHub Issues para reportar bugs ou solicitar features
- **Wiki:** Documentação adicional na Wiki do repositório

---

**Versão da Documentação:** 1.0
**Data:** 25 de Novembro de 2025
**Autor:** Task Manager Development Team
