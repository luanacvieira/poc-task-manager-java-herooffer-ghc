# 🚀 Como Rodar a Aplicação Task Manager

## ⚠️ IMPORTANTE: A aplicação precisa ficar rodando em um terminal

A aplicação Spring Boot é um **servidor web** que precisa estar **sempre rodando** para funcionar.

## 📋 Passo a Passo

### Opção 1: Via Terminal VS Code (Recomendado)

1. **Abra um novo terminal** no VS Code (Terminal > Novo Terminal)
2. Execute o comando:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
   .\mvnw.cmd spring-boot:run
   ```
3. **AGUARDE** aproximadamente 30-60 segundos
4. Procure pela mensagem: `Started TaskManagerApplication in X seconds`
5. **DEIXE O TERMINAL ABERTO** - não feche nem pressione Ctrl+C
6. Acesse no navegador: http://localhost:8080/tasks

### Opção 2: Via Script PowerShell

1. Execute o script criado:
   ```powershell
   .\run-app.ps1
   ```
2. **AGUARDE** a inicialização
3. **DEIXE O TERMINAL ABERTO**
4. Acesse: http://localhost:8080/tasks

### Opção 3: Via PowerShell Externo

1. Abra o **PowerShell** como aplicativo separado (não no VS Code)
2. Navegue até a pasta do projeto:
   ```powershell
   cd "C:\Projetos\Patria\java-task-manager-monolith"
   ```
3. Execute:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
   .\mvnw.cmd spring-boot:run
   ```
4. **DEIXE A JANELA ABERTA**
5. Acesse: http://localhost:8080/tasks

## 🌐 URLs Disponíveis

Quando a aplicação estiver rodando:

- **Interface Vaadin (UI)**: http://localhost:8080/tasks
- **API REST**: http://localhost:8080/api/tasks
- **H2 Console (Banco de Dados)**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:taskdb`
  - Username: `sa`
  - Password: _(deixe em branco)_

## ✅ Como Saber se Está Rodando

No terminal, você verá estas mensagens:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.4)

2025-XX-XX INFO ... : Started TaskManagerApplication in XX.XXX seconds
2025-XX-XX INFO ... : Tomcat started on port 8080 (http)
```

## 🛑 Como Parar a Aplicação

Quando quiser parar:
- Pressione **Ctrl+C** no terminal onde a aplicação está rodando
- Ou feche a janela do terminal

## ❌ Erros Comuns

### "localhost refused to connect" ou "can't reach this page"
**Causa**: A aplicação não está rodando
**Solução**: Execute novamente o comando acima e **aguarde** a inicialização completa

### "Port 8080 already in use"
**Causa**: Já existe uma instância rodando
**Solução**: 
```powershell
# Encontre o processo usando a porta 8080
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object OwningProcess
# Mate o processo (substitua XXXX pelo número do processo)
Stop-Process -Id XXXX -Force
```

## 💡 Dica

Durante o desenvolvimento, mantenha **dois terminais abertos**:
1. **Terminal 1**: Aplicação rodando (`mvnw.cmd spring-boot:run`)
2. **Terminal 2**: Para executar outros comandos (git, testes, etc)
