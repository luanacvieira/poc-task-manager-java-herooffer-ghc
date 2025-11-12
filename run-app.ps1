# Script para rodar a aplicação Spring Boot
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"

Write-Host "Iniciando aplicação Task Manager..." -ForegroundColor Green
Write-Host "Aguarde aproximadamente 30-60 segundos para inicialização completa." -ForegroundColor Yellow
Write-Host ""
Write-Host "Acesse: http://localhost:8080/tasks" -ForegroundColor Cyan
Write-Host "API REST: http://localhost:8080/api/tasks" -ForegroundColor Cyan
Write-Host "H2 Console: http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pressione Ctrl+C para parar a aplicação" -ForegroundColor Red
Write-Host ""

.\mvnw.cmd spring-boot:run
