# 🚪 API Gateway

## Descrição
Gateway de API que funciona como ponto de entrada único para todos os microsserviços, roteando requisições HTTP para os serviços apropriados.

## Funcionalidades
- ✅ Roteamento de requisições para microsserviços
- ✅ Ponto de entrada unificado (porta 8080)
- ✅ Configuração de CORS global
- ✅ Health checks agregados
- ✅ Timeouts e circuit breakers básicos
- ✅ Logs centralizados de requisições

## Tecnologias
- **Framework**: Spring Cloud Gateway 2023.0.0
- **Java**: 17
- **Observabilidade**: Spring Boot Actuator

## Porta
- **Gateway**: `8080` (mesma porta do monólito original)
- **Actuator**: `8080/actuator`

## Rotas Configuradas

### Tabela de Roteamento

| Requisição Externa | Roteamento Interno | Serviço |
|--------------------|-------------------|---------|
| `GET /healthcheck` | → Local (API Gateway) | Health Check Customizado |
| `GET /api/tasks/**` | → `http://task-service:8081/api/tasks/**` | Task Service |
| `GET /api/statistics/**` | → `http://statistics-service:8082/api/statistics/**` | Statistics Service |

### Health Check Customizado

O API Gateway possui um endpoint de health check customizado que retorna um JSON simples:

```bash
# Requisição
GET http://localhost:8080/healthcheck

# Resposta
HTTP/1.1 200 OK
Content-Type: application/json

{
  "status": "ok"
}
```

**Características:**
- ✅ Endpoint local (não requer comunicação com outros serviços)
- ✅ Resposta ultra-rápida (~5ms)
- ✅ Formato JSON simples
- ✅ Aceita apenas método GET
- ✅ Retorna HTTP 200 quando o gateway está operacional
- ✅ Ideal para load balancers e monitoramento externo

### Fluxo de Requisição

```
Cliente/Frontend
       ↓
   API Gateway (8080)
       ↓
    ┌──────────────┐
    │   Roteador   │
    └──────┬───────┘
           │
      ┌────┴────┐
      ↓         ↓
Task Service  Statistics Service
   (8081)        (8082)
```

## Executar Localmente

### Pré-requisitos
- JDK 17+
- Maven 3.6+
- Task Service rodando (8081)
- Statistics Service rodando (8082)

### Comandos
```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run

# Executar JAR
java -jar target/api-gateway-1.0.0.jar
```

## Executar com Docker

```bash
# Build
docker build -t api-gateway:1.0.0 .

# Executar
docker run -p 8080:8080 \
  --name api-gateway \
  --network task-manager-network \
  api-gateway:1.0.0
```

## Configuração

### Rotas (application.properties)
```properties
# Rota 1: Task Service
spring.cloud.gateway.routes[0].id=task-service
spring.cloud.gateway.routes[0].uri=http://task-service:8081
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/tasks/**

# Rota 2: Statistics Service
spring.cloud.gateway.routes[1].id=statistics-service
spring.cloud.gateway.routes[1].uri=http://statistics-service:8082
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/statistics/**
```

### CORS
```properties
# Permite todas as origens (desenvolvimento)
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origins=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,DELETE,OPTIONS
```

### Timeouts
```properties
# Timeout de conexão (30 segundos)
spring.cloud.gateway.httpclient.connect-timeout=30000
spring.cloud.gateway.httpclient.response-timeout=30s
```

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SERVER_PORT` | 8080 | Porta do gateway |
| `SPRING_CLOUD_GATEWAY_ROUTES_0_URI` | http://task-service:8081 | URI do Task Service |
| `SPRING_CLOUD_GATEWAY_ROUTES_1_URI` | http://statistics-service:8082 | URI do Statistics Service |

## Testes

### Teste do Health Check Customizado
```bash
# Health check simples
curl http://localhost:8080/healthcheck

# Resposta esperada:
# {"status":"ok"}

# Com headers detalhados
curl -i http://localhost:8080/healthcheck

# Teste de performance (10 requisições)
for i in {1..10}; do
  curl -s -o /dev/null -w "Request $i: Status %{http_code} - Time: %{time_total}s\n" \
    http://localhost:8080/healthcheck
done

# Verificar que aceita apenas GET
curl -X POST http://localhost:8080/healthcheck  # Retorna 405
curl -X PUT http://localhost:8080/healthcheck   # Retorna 405
curl -X DELETE http://localhost:8080/healthcheck # Retorna 405
curl http://localhost:8080/healthcheck           # Retorna 200
```

### Teste de Roteamento para Task Service
```bash
# Criar tarefa através do Gateway
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Teste via Gateway",
    "priority": "HIGH",
    "category": "WORK",
    "userId": "user123"
  }'

# Listar tarefas através do Gateway
curl http://localhost:8080/api/tasks
```

### Teste de Roteamento para Statistics Service
```bash
# Buscar estatísticas através do Gateway
curl http://localhost:8080/api/statistics
```

## Monitoramento

### Actuator Gateway Endpoints
```bash
# Health check do gateway
curl http://localhost:8080/actuator/health

# Rotas configuradas
curl http://localhost:8080/actuator/gateway/routes

# Refresh de configuração
curl -X POST http://localhost:8080/actuator/gateway/refresh
```

### Logs
```bash
# Ver logs de roteamento
docker logs -f api-gateway
```

## Estrutura do Projeto
```
api-gateway/
├── src/main/java/com/example/gateway/
│   └── ApiGatewayApplication.java    # Ponto de entrada
├── src/main/resources/
│   └── application.properties        # Configurações e rotas
├── Dockerfile                        # Imagem Docker
├── pom.xml                           # Dependências
└── README.md                         # Este arquivo
```

## Benefícios do API Gateway

### 1. **Ponto de Entrada Único**
- Clientes precisam conhecer apenas um endereço (localhost:8080)
- Facilita mudanças de infraestrutura sem impactar clientes

### 2. **Desacoplamento**
- Serviços podem mudar de porta/host sem afetar clientes
- Facilita versionamento de APIs

### 3. **Cross-Cutting Concerns (Futuro)**
- Autenticação/Autorização centralizadas
- Rate limiting
- Logging e monitoramento
- Cache
- Compressão
- Transformação de requisições

### 4. **Load Balancing**
- Distribuir carga entre múltiplas instâncias de um serviço
- Aumentar disponibilidade

## Próximos Passos (Melhorias Futuras)
- [ ] Implementar autenticação JWT
- [ ] Adicionar rate limiting
- [ ] Implementar circuit breaker (Resilience4j)
- [ ] Cache de respostas
- [ ] Logs estruturados (JSON)
- [ ] Integração com service discovery (Eureka/Consul)
- [ ] Métricas customizadas (Prometheus)
- [ ] Tracing distribuído (Zipkin/Jaeger)
- [ ] Transformação de requisições/respostas
- [ ] Versionamento de APIs (v1, v2)

## Troubleshooting

### Gateway não consegue rotear para serviços
1. Verificar se os serviços estão rodando:
   ```bash
   curl http://localhost:8081/actuator/health  # Task Service
   curl http://localhost:8082/actuator/health  # Statistics Service
   ```

2. Verificar configuração de rotas no log do Gateway

3. Verificar rede Docker:
   ```bash
   docker network inspect task-manager-network
   ```

### CORS Error
- Verificar configuração de CORS no application.properties
- Verificar se o frontend está usando a porta correta (8080)

### Timeout Errors
- Aumentar timeout nas configurações
- Verificar performance dos serviços downstream
- Implementar circuit breaker

## Suporte
Para dúvidas ou problemas, consulte a documentação principal do projeto.
