# 📦 Task Service

## Descrição
Microsserviço responsável pelo gerenciamento completo do ciclo de vida das tarefas (CRUD).

## Funcionalidades
- ✅ Criar tarefas
- ✅ Listar tarefas (todas ou por usuário)
- ✅ Buscar tarefa por ID
- ✅ Atualizar tarefas (atualização parcial)
- ✅ Excluir tarefas
- ✅ Calcular estatísticas básicas (totais, concluídas, pendentes, urgentes)

## Tecnologias
- **Framework**: Spring Boot 3.2.4
- **Java**: 17
- **Banco de Dados**: H2 (in-memory)
- **Persistência**: Spring Data JPA / Hibernate
- **Validação**: Jakarta Bean Validation
- **Observabilidade**: Spring Boot Actuator

## Porta
- **Aplicação**: `8081`
- **Actuator**: `8081/actuator`
- **H2 Console**: `8081/h2-console`

## Endpoints

### Tarefas
```
GET    /api/tasks              # Lista todas as tarefas
GET    /api/tasks/{id}         # Busca tarefa por ID
GET    /api/tasks/user/{userId}  # Tarefas de um usuário
POST   /api/tasks              # Cria nova tarefa
PUT    /api/tasks/{id}         # Atualiza tarefa
DELETE /api/tasks/{id}         # Exclui tarefa
GET    /api/tasks/health       # Health check
```

### Exemplo de Requisição (Criar Tarefa)
```bash
curl -X POST http://localhost:8081/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar microsserviços",
    "description": "Migrar monólito para arquitetura de microsserviços",
    "priority": "HIGH",
    "category": "WORK",
    "userId": "user123",
    "completed": false
  }'
```

## Banco de Dados

### Esquema
- **Tabela principal**: `tasks`
- **Tabela auxiliar**: `task_tags` (ElementCollection)

### Acesso H2 Console
```
URL: http://localhost:8081/h2-console
JDBC URL: jdbc:h2:mem:taskdb
Username: sa
Password: (vazio)
```

## Executar Localmente

### Pré-requisitos
- JDK 17+
- Maven 3.6+

### Comandos
```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run

# Executar JAR
java -jar target/task-service-1.0.0.jar

# Executar com profile específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Executar com Docker

```bash
# Build da imagem
docker build -t task-service:1.0.0 .

# Executar container
docker run -p 8081:8081 --name task-service task-service:1.0.0

# Logs
docker logs -f task-service
```

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | 8081 | Porta do servidor |
| `SPRING_DATASOURCE_URL` | jdbc:h2:mem:taskdb | URL do banco H2 |
| `SPRING_JPA_SHOW_SQL` | true | Mostrar SQL no log |
| `LOGGING_LEVEL_COM_EXAMPLE_TASKSERVICE` | INFO | Nível de log |

## Monitoramento

### Actuator Endpoints
```bash
# Health check
curl http://localhost:8081/actuator/health

# Métricas
curl http://localhost:8081/actuator/metrics

# Info
curl http://localhost:8081/actuator/info
```

## Estrutura do Projeto
```
task-service/
├── src/main/java/com/example/taskservice/
│   ├── TaskServiceApplication.java    # Ponto de entrada
│   ├── domain/                        # Entidades de domínio
│   │   ├── Task.java
│   │   ├── Priority.java
│   │   └── Category.java
│   ├── repository/                    # Repositórios JPA
│   │   └── TaskRepository.java
│   ├── service/                       # Lógica de negócio
│   │   └── TaskService.java
│   └── controller/                    # Controllers REST
│       └── TaskController.java
├── src/main/resources/
│   └── application.properties         # Configurações
├── Dockerfile                         # Imagem Docker
├── pom.xml                            # Dependências Maven
└── README.md                          # Este arquivo
```

## Próximos Passos (Melhorias Futuras)
- [ ] Adicionar cache (Redis) para queries frequentes
- [ ] Implementar paginação e ordenação
- [ ] Adicionar filtros avançados de busca
- [ ] Implementar auditoria de mudanças
- [ ] Adicionar testes unitários e de integração
- [ ] Migrar para banco PostgreSQL em produção
- [ ] Implementar autenticação JWT
- [ ] Adicionar rate limiting
- [ ] Implementar circuit breaker (Resilience4j)

## Suporte
Para dúvidas ou problemas, consulte a documentação principal do projeto.
