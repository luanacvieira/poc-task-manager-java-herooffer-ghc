# Como Resolver o Problema de Compilação

## Problema Identificado

O projeto requer **JDK 17**, mas você tem o **JDK 25** instalado, que é muito novo e incompatível com as ferramentas usadas (Spring Boot 3.2.4, Maven Compiler Plugin, Lombok).

## Solução

### Opção 1: Instalar JDK 17 (Recomendado)

1. **Baixar JDK 17:**
   - Acesse: https://adoptium.net/temurin/releases/?version=17
   - Baixe a versão **Windows x64** (arquivo `.msi`)
   - Execute o instalador e siga as instruções

2. **Configurar o ambiente:**
   ```powershell
   .\setup-jdk17.ps1
   ```

3. **Iniciar a aplicação:**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

### Opção 2: Usar JDK 17 sem desinstalar JDK 25

Se você já tiver o JDK 17 instalado ou quiser manter o JDK 25 para outros projetos:

```powershell
# Configurar JAVA_HOME temporariamente para esta sessão
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verificar versão
java -version

# Iniciar aplicação
.\mvnw.cmd spring-boot:run
```

## Após Resolver

A aplicação estará disponível em:

- **Frontend (Vaadin UI):** http://localhost:8080/tasks
- **API REST:** http://localhost:8080/api/tasks
- **H2 Console (BD):** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:taskdb`
  - Username: `sa`
  - Password: (deixe vazio)

## Observações

- Esta aplicação **NÃO usa MongoDB** - usa H2 (banco em memória)
- O MongoDB Compass que você tem instalado não é necessário
- Após instalar o JDK 17, o Maven Wrapper (`mvnw.cmd`) gerenciará todas as dependências automaticamente
