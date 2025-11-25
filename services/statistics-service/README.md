# 📊 Statistics Service

## Descrição
Microsserviço responsável por agregar e fornecer estatísticas sobre tarefas, consumindo dados do Task Service.

## Funcionalidades
- ✅ Calcular total de tarefas
- ✅ Calcular tarefas concluídas e pendentes
- ✅ Identificar tarefas urgentes ativas
- ✅ Calcular tarefas vencidas
- ✅ Taxa de conclusão percentual
- ✅ Distribuição por prioridade
- ✅ Distribuição por categoria
- ✅ Comunicação via REST com Task Service

## Tecnologias
- **Framework**: Spring Boot 3.2.4
- **Java**: 17
- **Cliente HTTP**: WebClient (Spring WebFlux)
- **Observabilidade**: Spring Boot Actuator

## Porta
- **Aplicação**: `8082`
- **Actuator**: `8082/actuator`

## Endpoints

### Estatísticas
```
GET /api/statistics        # Retorna estatísticas agregadas
GET /api/statistics/health # Health check do serviço
```

### Exemplo de Resposta
```json
{
  "total": 50,
  "completed": 30,
  "pending": 20,
  "urgentActive": 5,
  "overdue": 3,
  "completionRate": 60.0,
  "byPriority": {
    "LOW": 10,
    "MEDIUM": 20,
    "HIGH": 15,
    "URGENT": 5
  },
  "byCategory": {
    "WORK": 25,
    "PERSONAL": 15,
    "STUDY": 10
  }
}
```

## Integração com Task Service

O Statistics Service consome dados do Task Service via HTTP:

```
Statistics Service (8082)  
         |
         | HTTP GET /api/tasks
         v
   Task Service (8081)
```

### Configuração
```properties
# URL do Task Service
task.service.url=http://task-service:8081
```

## Executar Localmente

### Pré-requisitos
- JDK 17+
- Maven 3.6+
- Task Service rodando na porta 8081

### Comandos
```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run

# Executar JAR
java -jar target/statistics-service-1.0.0.jar
```

## Executar com Docker

```bash
# Build
docker build -t statistics-service:1.0.0 .

# Executar (dependente do Task Service)
docker run -p 8082:8082 \
  -e TASK_SERVICE_URL=http://task-service:8081 \
  --name statistics-service \
  statistics-service:1.0.0
```

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | 8082 | Porta do servidor |
| `TASK_SERVICE_URL` | http://task-service:8081 | URL do Task Service |
| `LOGGING_LEVEL_COM_EXAMPLE_STATISTICSSERVICE` | INFO | Nível de log |

## Estrutura do Projeto
```
statistics-service/
├── src/main/java/com/example/statisticsservice/
│   ├── StatisticsServiceApplication.java  # Ponto de entrada
│   ├── client/                            # Clientes HTTP
│   │   └── TaskServiceClient.java         # Cliente do Task Service
│   ├── dto/                               # Objetos de transferência
│   │   ├── TaskDto.java
│   │   └── StatisticsResponse.java
│   ├── service/                           # Lógica de negócio
│   │   └── StatisticsService.java
│   └── controller/                        # Controllers REST
│       └── StatisticsController.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── pom.xml
└── README.md
```

## Resiliência

### Tratamento de Falhas
- Retorna estatísticas vazias se Task Service estiver indisponível
- Logs de erro para monitoramento
- Health check verifica disponibilidade do Task Service

### Exemplo de Health Check
```bash
curl http://localhost:8082/api/statistics/health

# Resposta
{
  "service": "Statistics Service",
  "status": "UP",
  "taskServiceAvailable": true
}
```

## Próximos Passos (Melhorias Futuras)
- [ ] Implementar cache de estatísticas (Redis)
- [ ] Adicionar Circuit Breaker (Resilience4j)
- [ ] Implementar retry automático
- [ ] Adicionar timeout configurável
- [ ] Estatísticas por período (dia, semana, mês)
- [ ] Gráficos e visualizações
- [ ] Notificações de anomalias
- [ ] Machine Learning para previsões
- [ ] Exportação de relatórios (PDF, Excel)
- [ ] Testes unitários e de integração

## Monitoramento

```bash
# Verificar estatísticas
curl http://localhost:8082/api/statistics

# Health check
curl http://localhost:8082/actuator/health

# Métricas
curl http://localhost:8082/actuator/metrics
```

## Troubleshooting

### Statistics Service não consegue conectar ao Task Service
1. Verificar se o Task Service está rodando:
   ```bash
   curl http://localhost:8081/api/tasks/health
   ```
2. Verificar variável de ambiente `TASK_SERVICE_URL`
3. Verificar rede Docker (se usando containers)

### Estatísticas vazias mesmo com tarefas
1. Verificar logs do Statistics Service
2. Verificar comunicação entre serviços
3. Verificar configuração de CORS

## Suporte
Para dúvidas ou problemas, consulte a documentação principal do projeto.
