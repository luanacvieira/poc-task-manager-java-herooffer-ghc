# 🔒 Análise de Segurança - Task Manager Microservices

**Data:** 24/11/2024  
**Projeto:** Task Manager - Arquitetura de Microserviços  
**Avaliador:** Security Review

---

## 📋 Resumo Executivo

Este documento apresenta uma análise detalhada de segurança dos três microserviços (Task Service, Statistics Service e API Gateway), identificando vulnerabilidades e propondo soluções práticas.

**Criticidade Geral:** ⚠️ **MÉDIA-ALTA**

---

## 🚨 Vulnerabilidades Identificadas

### 🔴 CRÍTICAS (Ação Imediata)

#### 1. **CORS Completamente Aberto**
**Localização:** Todos os controllers  
**Código Vulnerável:**
```java
@CrossOrigin(origins = "*") // ❌ INSEGURO
```

**Risco:** Permite que qualquer site malicioso faça requisições à API
- **OWASP:** A05:2021 – Security Misconfiguration
- **CWE:** CWE-942 - Permissive Cross-domain Policy

**Impacto:**
- ☠️ Ataques CSRF (Cross-Site Request Forgery)
- ☠️ Vazamento de dados sensíveis
- ☠️ Manipulação não autorizada de dados

**Solução:**
```java
@CrossOrigin(
    origins = {"https://app.example.com", "http://localhost:3000"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    maxAge = 3600,
    allowCredentials = "true"
)
```

---

#### 2. **Ausência de Autenticação e Autorização**
**Localização:** Todos os endpoints  
**Risco:** Qualquer pessoa pode acessar/modificar/deletar qualquer tarefa

**Problemas:**
- ❌ Sem validação de identidade do usuário
- ❌ Sem controle de acesso baseado em roles
- ❌ userId pode ser forjado no payload
- ❌ Usuário A pode manipular tarefas do usuário B

**Impacto:**
- ☠️ **IDOR** (Insecure Direct Object Reference)
- ☠️ Escalação de privilégios
- ☠️ Vazamento de dados entre usuários

**Solução:**
```java
@PreAuthorize("hasRole('USER')")
@GetMapping("/user/{userId}")
public ResponseEntity<List<Task>> getByUserId(
    @PathVariable String userId,
    @AuthenticationPrincipal UserDetails currentUser) {
    
    // Valida que o usuário só acessa suas próprias tarefas
    if (!userId.equals(currentUser.getUsername())) {
        throw new AccessDeniedException("Acesso negado");
    }
    return ResponseEntity.ok(service.findByUserId(userId));
}
```

---

#### 3. **Logs Expondo Dados Sensíveis**
**Localização:** TaskController, StatisticsService  
**Código Vulnerável:**
```java
log.info("POST /api/tasks - Criando nova tarefa: {}", task.getTitle());
log.info("GET /api/tasks/user/{} - Buscando tarefas do usuário", userId);
```

**Risco:** Logs podem conter dados sensíveis que aparecem em sistemas de monitoramento

**Problemas:**
- ❌ Logs podem ser indexados em ferramentas de busca (ELK, Splunk)
- ❌ Informações pessoais podem ser expostas
- ❌ Não conformidade com LGPD/GDPR

**Solução:**
```java
// Usar níveis adequados e sanitizar dados
log.debug("Criando tarefa para userId: {}", sanitizeUserId(task.getUserId()));
log.info("Tarefa criada - ID: {}", saved.getId()); // Sem expor título
```

---

### 🟠 ALTAS (Ação Urgente)

#### 4. **Injeção SQL via Query Manual**
**Localização:** TaskRepository  
**Código Vulnerável:**
```java
@Query("SELECT COUNT(t) FROM Task t WHERE t.priority = 'URGENT' AND t.completed = false")
long countUrgentActive();
```

**Risco:** Embora use JPQL (mais seguro), strings hardcoded podem causar problemas

**Solução:**
```java
@Query("SELECT COUNT(t) FROM Task t WHERE t.priority = :priority AND t.completed = false")
long countByPriorityAndCompleted(@Param("priority") Priority priority, boolean completed);
```

---

#### 5. **Ausência de Rate Limiting**
**Localização:** Todos os endpoints públicos

**Risco:**
- ☠️ Ataques de força bruta
- ☠️ DDoS (Denial of Service)
- ☠️ Enumeração de recursos

**Solução:**
```java
// Adicionar ao pom.xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>

// Implementar filtro
@Component
public class RateLimitFilter implements Filter {
    private final Bucket bucket = Bucket.builder()
        .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1)))
        .build();
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res);
        } else {
            ((HttpServletResponse) res).setStatus(429);
        }
    }
}
```

---

#### 6. **Validação Insuficiente de Entrada**
**Localização:** Task entity, Controllers

**Problemas Identificados:**
```java
@Column(length = 255)
private String title; // ❌ Sem validação de tamanho

@Column(length = 1000)
private String description; // ❌ Pode conter XSS

private Set<String> tags = new HashSet<>(); // ❌ Sem limite de quantidade

@PathVariable String userId // ❌ Sem validação de formato
```

**Solução:**
```java
@NotBlank(message = "Título obrigatório")
@Size(min = 3, max = 255, message = "Título deve ter entre 3 e 255 caracteres")
@Pattern(regexp = "^[\\p{L}\\p{N}\\s.,!?-]+$", message = "Título contém caracteres inválidos")
private String title;

@Size(max = 1000, message = "Descrição muito longa")
private String description;

@Size(max = 10, message = "Máximo de 10 tags por tarefa")
private Set<@Pattern(regexp = "^[a-z0-9-]{2,20}$") String> tags;

// No controller
@PathVariable @Pattern(regexp = "^[a-zA-Z0-9]{3,50}$") String userId
```

---

#### 7. **Tratamento de Erros Expondo Stack Traces**
**Localização:** Ausência de @ControllerAdvice

**Código Atual:**
```java
catch (IllegalArgumentException e) {
    log.warn("Erro ao atualizar tarefa {}: {}", id, e.getMessage());
    return ResponseEntity.notFound().build();
}
```

**Problema:** Em produção, exceções não tratadas podem vazar informações do sistema

**Solução:** Implementar Global Exception Handler

---

### 🟡 MÉDIAS (Atenção Necessária)

#### 8. **Configuração de Banco H2 Exposta**
**Localização:** application.properties  
**Código:**
```properties
spring.h2.console.enabled=true  # ❌ Expõe console web
spring.datasource.password=     # ❌ Senha vazia
spring.jpa.show-sql=true        # ❌ SQL nos logs
```

**Solução:**
```properties
# Usar perfis
spring.profiles.active=${PROFILE:dev}

# application-prod.properties
spring.h2.console.enabled=false
spring.jpa.show-sql=false
spring.datasource.password=${DB_PASSWORD}
```

---

#### 9. **Falta de HTTPS/TLS**
**Localização:** Configuração de servidor

**Risco:** Dados trafegam em texto plano na rede

**Solução:**
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

---

#### 10. **Falta de Sanitização de Saída**
**Localização:** Retorno de entidades completas

**Problema:**
```java
return ResponseEntity.ok(task); // Expõe TUDO, incluindo timestamps internos
```

**Solução:** Usar DTOs
```java
public record TaskResponse(
    Long id,
    String title,
    String description,
    Priority priority,
    Category category,
    LocalDate dueDate,
    boolean completed
    // Não expor: userId, createdAt, updatedAt
) {}
```

---

### 🔵 BAIXAS (Melhorias Recomendadas)

#### 11. **Ausência de Auditoria**
**Problema:** Não há registro de quem fez o quê e quando

**Solução:**
```java
@EntityListeners(AuditingEntityListener.class)
public class Task {
    @CreatedBy
    private String createdBy;
    
    @LastModifiedBy
    private String lastModifiedBy;
}
```

---

#### 12. **Falta de Validação de Business Logic**
**Exemplo:**
```java
// Permitir dueDate no passado? ❌
task.setDueDate(LocalDate.of(1900, 1, 1));

// Permitir título só com espaços? ❌
task.setTitle("    ");
```

**Solução:**
```java
@AssertTrue(message = "Data de vencimento não pode estar no passado")
private boolean isDueDateValid() {
    return dueDate == null || !dueDate.isBefore(LocalDate.now());
}
```

---

## 🛠️ Implementações Prioritárias

### 1. Global Exception Handler (CRÍTICO)

```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", "Dados inválidos", null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse("ACCESS_DENIED", "Acesso negado", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        // NÃO expor detalhes em produção
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "Erro interno do servidor", null));
    }

    record ErrorResponse(String code, String message, Map<String, String> details) {}
}
```

### 2. Input Sanitizer (ALTO)

```java
@Component
public class InputSanitizer {
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "<script|javascript:|onerror=|onload=",
        Pattern.CASE_INSENSITIVE
    );
    
    public String sanitize(String input) {
        if (input == null) return null;
        
        // Remove scripts
        String clean = input.replaceAll(XSS_PATTERN.pattern(), "");
        
        // Trim e normaliza espaços
        clean = clean.trim().replaceAll("\\s+", " ");
        
        // Limita tamanho
        if (clean.length() > 1000) {
            clean = clean.substring(0, 1000);
        }
        
        return clean;
    }
}
```

### 3. Security Config (CRÍTICO)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .cors(cors -> cors.configurationSource(corsConfig()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/*/health", "/actuator/health").permitAll()
                .requestMatchers("/h2-console/**").denyAll() // Bloquear em prod
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
            "https://app.example.com",
            "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
```

---

## 📊 Matriz de Priorização

| Vulnerabilidade | Criticidade | Esforço | Prioridade |
|-----------------|-------------|---------|------------|
| CORS Aberto | 🔴 Crítica | Baixo | **P0** |
| Sem Autenticação | 🔴 Crítica | Alto | **P0** |
| Logs Sensíveis | 🔴 Crítica | Baixo | **P0** |
| Exception Handler | 🟠 Alta | Médio | **P1** |
| Validação Entrada | 🟠 Alta | Médio | **P1** |
| Rate Limiting | 🟠 Alta | Médio | **P1** |
| H2 Console Exposto | 🟡 Média | Baixo | **P2** |
| Sem HTTPS | 🟡 Média | Médio | **P2** |
| DTOs vs Entities | 🟡 Média | Alto | **P3** |
| Auditoria | 🔵 Baixa | Alto | **P4** |

---

## ✅ Checklist de Segurança

### Fase 1 - Quick Wins (1-2 dias)
- [ ] Remover `@CrossOrigin(origins = "*")`
- [ ] Adicionar `@ControllerAdvice` global
- [ ] Desabilitar H2 console em produção
- [ ] Adicionar validações `@Size`, `@Pattern`
- [ ] Sanitizar logs (remover dados sensíveis)
- [ ] Configurar `spring.profiles` (dev/prod)

### Fase 2 - Hardening (1 semana)
- [ ] Implementar Spring Security + OAuth2/JWT
- [ ] Adicionar Rate Limiting
- [ ] Criar DTOs de resposta
- [ ] Implementar Input Sanitizer
- [ ] Configurar HTTPS/TLS
- [ ] Adicionar validações de business logic

### Fase 3 - Avançado (2-3 semanas)
- [ ] Implementar auditoria completa
- [ ] Adicionar testes de segurança
- [ ] Configurar SAST/DAST (SonarQube, OWASP ZAP)
- [ ] Implementar circuit breaker
- [ ] Adicionar observabilidade (métricas de segurança)
- [ ] Documentação de segurança

---

## 🔍 Ferramentas Recomendadas

### Análise Estática (SAST)
- **SonarQube** - Análise de código
- **SpotBugs** - Detecção de bugs
- **OWASP Dependency-Check** - Vulnerabilidades em libs

### Análise Dinâmica (DAST)
- **OWASP ZAP** - Pen testing automatizado
- **Burp Suite** - Proxy de interceptação
- **Postman/Newman** - Testes de API

### Monitoramento
- **ELK Stack** - Análise de logs
- **Prometheus + Grafana** - Métricas
- **Jaeger** - Distributed tracing

---

## 📚 Referências

- **OWASP Top 10 2021:** https://owasp.org/Top10/
- **Spring Security Docs:** https://spring.io/projects/spring-security
- **LGPD/GDPR Compliance:** Guia de adequação
- **CWE Top 25:** https://cwe.mitre.org/top25/

---

## 🎯 Conclusão

A aplicação possui **vulnerabilidades críticas** que devem ser corrigidas antes de qualquer deploy em produção:

🚨 **Risco Atual:** ALTO  
✅ **Risco Após Implementação:** BAIXO  

**Tempo Estimado para Hardening Completo:** 3-4 semanas  
**Custo vs Benefício:** ⭐⭐⭐⭐⭐ Muito Alto

---

**Próximos Passos Imediatos:**
1. Implementar Global Exception Handler
2. Configurar CORS restritivo
3. Adicionar Spring Security básico
4. Sanitizar logs

---

**Documento gerado em:** 24/11/2024  
**Próxima revisão:** Após implementação das correções P0/P1
