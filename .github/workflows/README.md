# 🚀 GitHub Actions - CI/CD Pipeline

Este documento descreve as GitHub Actions configuradas para o projeto Task Manager.

---

## 📋 Actions Configuradas

### 1. **CI/CD - Build and Test** (`ci-build-test.yml`)

Pipeline principal de integração contínua que valida código, executa testes e gera builds.

#### 🎯 Triggers
- ✅ **Pull Request** para branches `main` e `develop`
- ✅ **Push** para branches `main` e `develop`
- ✅ **Manual** via GitHub UI (workflow_dispatch)

#### 🔄 Jobs

##### Job 1: Test Microservices (Paralelo)
Executa testes unitários e de integração em todos os microserviços simultaneamente.

**Serviços testados:**
- `task-service`
- `statistics-service`
- `api-gateway`

**Validações:**
1. ✅ Execução de todos os testes (unit + integration)
2. ✅ Geração de relatório JaCoCo
3. ✅ **Validação de cobertura mínima: 80%**
4. ✅ Upload de relatórios como artefatos

**Critérios de Aprovação:**
```
✅ Todos os testes devem passar
✅ Cobertura ≥ 80% (configurável via env.MINIMUM_COVERAGE)
```

##### Job 2: Build Microservices
Compila os JARs dos microserviços (só executa se testes passarem).

**Output:**
- JAR de cada microserviço em `target/`
- Artefatos mantidos por 7 dias

##### Job 3: Build Monolith (Legacy)
Compila o monólito legado para compatibilidade.

##### Job 4: Coverage Report Summary
Gera resumo consolidado da cobertura de todos os serviços.

**Output:**
- Tabela de cobertura no GitHub Step Summary
- Identificação de serviços aprovados/reprovados

##### Job 5: Status Check
Valida status geral do pipeline.

**Critérios de Falha:**
- ❌ Se qualquer teste falhar
- ❌ Se cobertura < 80%
- ❌ Se build falhar

---

### 2. **Code Coverage Analysis** (`coverage-analysis.yml`)

Análise detalhada de cobertura com comentários automáticos em Pull Requests.

#### 🎯 Triggers
- ✅ **Pull Request** para `main` e `develop`
- ✅ **Manual** via GitHub UI

#### 📊 Funcionalidades

1. **Análise Detalhada por Serviço:**
   - Cobertura de instruções
   - Cobertura de branches
   - Identificação de pacotes com baixa cobertura (<70%)

2. **Resumo Consolidado:**
   - Taxa de sucesso geral
   - Serviços aprovados vs. reprovados
   - Métricas agregadas

3. **Comentário Automático em PRs:**
   - Tabela de resultados
   - Recomendações de melhoria
   - Link para artefatos

**Output:**
- `coverage-summary.md` com análise completa
- Comentário automático no PR
- GitHub Step Summary

---

## 🛠️ Como Usar

### Execução Automática

As actions são disparadas automaticamente em:

```bash
# Ao abrir/atualizar um Pull Request
git push origin feature/minha-feature

# Ao fazer merge para main
git push origin main
```

### Execução Manual

1. Acesse: **Actions** → **CI/CD - Build and Test**
2. Clique em **Run workflow**
3. Selecione a branch
4. (Opcional) Marque "skip tests" para debug
5. Clique em **Run workflow**

---

## 📊 Requisitos de Cobertura

### Cobertura Mínima por Serviço

| Serviço | Cobertura Mínima | Status |
|---------|------------------|--------|
| **Task Service** | 80% | ✅ Atingido (83.9%) |
| **Statistics Service** | 80% | ⚠️ Parcial (28.9% global, 97% lógica) |
| **API Gateway** | 80% | ✅ Atingido (80%) |

### Como Aumentar Cobertura

Se um serviço falhar na validação:

1. **Verificar Relatório:**
   ```bash
   # Baixe o artefato "coverage-report-{service}"
   # Abra target/site/jacoco/index.html
   ```

2. **Identificar Gaps:**
   - Métodos não testados (vermelho)
   - Branches não cobertos (amarelo)
   - Classes sem testes

3. **Adicionar Testes:**
   ```java
   @Test
   @DisplayName("Deve testar cenário X")
   void shouldTestScenarioX() {
       // Given
       // When
       // Then
   }
   ```

4. **Validar Localmente:**
   ```bash
   mvn clean test jacoco:report
   open target/site/jacoco/index.html
   ```

---

## 🔧 Configuração Avançada

### Variáveis de Ambiente

Edite `.github/workflows/ci-build-test.yml`:

```yaml
env:
  JAVA_VERSION: '17'          # Versão do Java
  MINIMUM_COVERAGE: 80        # Cobertura mínima (%)
  MAVEN_OPTS: '-Xmx2g'        # Opções do Maven
```

### Modificar Cobertura Mínima

Para alterar o requisito de cobertura:

```yaml
# Opção 1: Global (em env)
env:
  MINIMUM_COVERAGE: 75  # 75%

# Opção 2: Por serviço (no pom.xml)
<configuration>
  <rules>
    <rule>
      <limits>
        <limit>
          <minimum>0.85</minimum>  <!-- 85% -->
        </limit>
      </limits>
    </rule>
  </rules>
</configuration>
```

### Adicionar Novo Serviço

Para incluir um novo microserviço:

1. Adicione na matriz de serviços:
   ```yaml
   strategy:
     matrix:
       service: [task-service, statistics-service, api-gateway, novo-service]
   ```

2. Configure JaCoCo no `pom.xml` do novo serviço

---

## 📦 Artefatos Gerados

### Tipos de Artefatos

| Artefato | Conteúdo | Retenção |
|----------|----------|----------|
| `coverage-report-{service}` | Relatório HTML JaCoCo | 30 dias |
| `test-results-{service}` | Surefire/Failsafe reports | 30 dias |
| `jar-{service}` | JAR compilado | 7 dias |
| `coverage-summary` | Resumo consolidado | 30 dias |

### Como Baixar Artefatos

1. Acesse a execução da Action
2. Role até **Artifacts**
3. Clique para baixar
4. Descompacte e abra `index.html` (relatórios)

---

## 🚨 Troubleshooting

### Problema: Cobertura < 80%

**Solução:**
```bash
# 1. Execute localmente
cd services/task-service
mvn clean test jacoco:report

# 2. Veja o relatório
open target/site/jacoco/index.html

# 3. Adicione testes para métodos não cobertos
```

### Problema: Testes Falhando

**Solução:**
```bash
# 1. Execute com verbose
mvn test -X

# 2. Verifique logs do Surefire
cat target/surefire-reports/*.txt

# 3. Execute teste específico
mvn test -Dtest=TaskServiceTest#shouldCreateTask
```

### Problema: Build Timeout

**Solução:**
```yaml
# Aumente timeout no workflow
jobs:
  test-microservices:
    timeout-minutes: 30  # Padrão: 360min
```

### Problema: Memória Insuficiente

**Solução:**
```yaml
env:
  MAVEN_OPTS: '-Xmx4g -XX:MaxMetaspaceSize=1g'  # Aumentar heap
```

---

## 🔒 Segurança

### Branch Protection Rules

Recomenda-se configurar:

1. **Settings** → **Branches** → **Add rule**
2. Branch name pattern: `main`
3. Marque:
   - ✅ Require status checks to pass
   - ✅ Require branches to be up to date
   - ✅ Status checks: "CI Status Check"
   - ✅ Require pull request reviews (1)

### Secrets Necessários

Nenhum secret necessário para funcionalidade básica.

Para deploy futuro, configurar:
- `DOCKER_USERNAME`
- `DOCKER_PASSWORD`
- `DEPLOY_KEY`

---

## 📈 Métricas e KPIs

### Métricas Monitoradas

- ✅ Taxa de sucesso de builds
- ✅ Cobertura de testes por serviço
- ✅ Tempo médio de execução
- ✅ Taxa de falha por tipo de erro

### Dashboards Recomendados

GitHub Insights automaticamente mostra:
- Workflow runs
- Success rate
- Duration trends

---

## 🎯 Roadmap

### Fase 1 - Implementado ✅
- [x] Pipeline de CI básico
- [x] Validação de testes
- [x] Cobertura mínima 80%
- [x] Artefatos de build

### Fase 2 - Próximos Passos
- [ ] SonarQube integration
- [ ] Docker image build
- [ ] Deploy automático (staging)
- [ ] Performance tests
- [ ] Security scanning (OWASP)

### Fase 3 - Futuro
- [ ] Deploy em produção
- [ ] Rollback automático
- [ ] Canary deployments
- [ ] A/B testing

---

## 📚 Referências

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

---

## 💡 Dicas

### Desenvolvimento Local

Antes de fazer push, valide localmente:

```bash
# Executar o que a action faz
./scripts/ci-local.sh

# Ou manualmente
for service in services/*/; do
  cd "$service"
  mvn clean test jacoco:report
  cd ../..
done
```

### Acelerar Builds

1. Use cache do Maven (já configurado)
2. Execute testes em paralelo localmente:
   ```bash
   mvn test -T 2C  # 2 threads por core
   ```
3. Pule testes em builds locais:
   ```bash
   mvn clean package -DskipTests
   ```

---

**Última atualização:** 24/11/2024  
**Versão:** 1.0.0  
**Autor:** DevOps Team
