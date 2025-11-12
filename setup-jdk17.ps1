# Script para configurar JDK 17 para o projeto
# Este script verifica se há JDK 17 instalado e configura as variáveis de ambiente

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "Setup JDK 17 para Java Task Manager" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se existe JDK 17 instalado
$jdk17Paths = @(
    "C:\Program Files\Eclipse Adoptium\jdk-17*",
    "C:\Program Files\Java\jdk-17*",
    "C:\Program Files\AdoptOpenJDK\jdk-17*"
)

$foundJdk = $null
foreach ($path in $jdk17Paths) {
    $jdks = Get-Item $path -ErrorAction SilentlyContinue
    if ($jdks) {
        $foundJdk = $jdks[0].FullName
        break
    }
}

if ($foundJdk) {
    Write-Host "✓ JDK 17 encontrado em: $foundJdk" -ForegroundColor Green
    Write-Host ""
    Write-Host "Configurando variáveis de ambiente para esta sessão..." -ForegroundColor Yellow
    $env:JAVA_HOME = $foundJdk
    $env:PATH = "$foundJdk\bin;$env:PATH"
    
    Write-Host "✓ JAVA_HOME = $env:JAVA_HOME" -ForegroundColor Green
    Write-Host ""
    Write-Host "Testando Java..." -ForegroundColor Yellow
    java -version
    Write-Host ""
    Write-Host "✓ Ambiente configurado com sucesso!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Agora você pode executar:" -ForegroundColor Cyan
    Write-Host "  .\mvnw.cmd spring-boot:run" -ForegroundColor White
} else {
    Write-Host "✗ JDK 17 não encontrado!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor, baixe e instale o JDK 17 em:" -ForegroundColor Yellow
    Write-Host "  https://adoptium.net/temurin/releases/?version=17" -ForegroundColor White
    Write-Host ""
    Write-Host "Após instalar, execute este script novamente." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "NOTA: O JDK 25 que você tem instalado é muito novo e incompatível" -ForegroundColor Yellow
    Write-Host "      com as ferramentas usadas neste projeto (Spring Boot 3.2.4)." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "====================================" -ForegroundColor Cyan
