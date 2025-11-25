#!/bin/bash
# ==============================================================
# Script de Execução Completa da Aplicação
# ==============================================================
# Este script builda e executa todos os microsserviços via Docker Compose.
#
# Uso: ./run-microservices.sh

set -e

echo "🚀 Iniciando Task Manager Microservices..."
echo ""

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ==============================================================
# 1. Verificar Docker
# ==============================================================
echo -e "${BLUE}🐳 Verificando Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker não encontrado. Instale Docker primeiro.${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose não encontrado. Instale Docker Compose primeiro.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker OK${NC}"
echo ""

# ==============================================================
# 2. Parar containers antigos (se existirem)
# ==============================================================
echo -e "${YELLOW}🛑 Parando containers antigos...${NC}"
docker-compose down 2>/dev/null || true
echo ""

# ==============================================================
# 3. Build das imagens Docker
# ==============================================================
echo -e "${BLUE}🏗️  Buildando imagens Docker...${NC}"
docker-compose build --no-cache
echo -e "${GREEN}✅ Build concluído${NC}"
echo ""

# ==============================================================
# 4. Iniciar serviços
# ==============================================================
echo -e "${BLUE}🚀 Iniciando serviços...${NC}"
docker-compose up -d
echo ""

# ==============================================================
# 5. Aguardar serviços ficarem saudáveis
# ==============================================================
echo -e "${YELLOW}⏳ Aguardando serviços ficarem saudáveis...${NC}"
echo "   (Isso pode levar até 2 minutos)"
echo ""

# Função para verificar health
check_health() {
    local service=$1
    local port=$2
    local max_attempts=60
    local attempt=0

    while [ $attempt -lt $max_attempts ]; do
        if curl -s http://localhost:${port}/actuator/health | grep -q "UP"; then
            echo -e "${GREEN}✅ ${service} está UP${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        sleep 2
    done

    echo -e "${RED}❌ ${service} falhou ao iniciar${NC}"
    return 1
}

# Verificar Task Service
check_health "Task Service" 8081

# Verificar Statistics Service
check_health "Statistics Service" 8082

# Verificar API Gateway
check_health "API Gateway" 8080

echo ""

# ==============================================================
# 6. Teste básico
# ==============================================================
echo -e "${BLUE}🧪 Executando teste básico...${NC}"

# Criar uma tarefa de teste
echo "Criando tarefa de teste..."
curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tarefa de Teste - Microsserviços",
    "description": "Teste da arquitetura de microsserviços",
    "priority": "HIGH",
    "category": "WORK",
    "userId": "test-user"
  }' > /dev/null

echo -e "${GREEN}✅ Tarefa criada${NC}"

# Buscar estatísticas
echo "Buscando estatísticas..."
stats=$(curl -s http://localhost:8080/api/statistics)
echo -e "${GREEN}✅ Estatísticas obtidas${NC}"
echo ""

# ==============================================================
# 7. Resumo e informações
# ==============================================================
echo -e "${GREEN}🎉 Todos os serviços estão rodando!${NC}"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📌 ENDPOINTS DISPONÍVEIS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "🚪 API Gateway (Ponto de Entrada Principal)"
echo "   http://localhost:8080"
echo ""
echo "📦 Task Service"
echo "   API: http://localhost:8081/api/tasks"
echo "   H2 Console: http://localhost:8081/h2-console"
echo "   Health: http://localhost:8081/actuator/health"
echo ""
echo "📊 Statistics Service"
echo "   API: http://localhost:8082/api/statistics"
echo "   Health: http://localhost:8082/actuator/health"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧪 EXEMPLOS DE REQUISIÇÕES"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "# Listar tarefas"
echo "curl http://localhost:8080/api/tasks"
echo ""
echo "# Criar tarefa"
echo "curl -X POST http://localhost:8080/api/tasks \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"title\": \"Nova Tarefa\", \"priority\": \"HIGH\", \"category\": \"WORK\", \"userId\": \"user123\"}'"
echo ""
echo "# Ver estatísticas"
echo "curl http://localhost:8080/api/statistics"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🛠️  COMANDOS ÚTEIS"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "# Ver logs de todos os serviços"
echo "docker-compose logs -f"
echo ""
echo "# Ver logs de um serviço específico"
echo "docker-compose logs -f task-service"
echo ""
echo "# Status dos containers"
echo "docker-compose ps"
echo ""
echo "# Parar todos os serviços"
echo "docker-compose down"
echo ""
echo "# Restart de um serviço"
echo "docker-compose restart task-service"
echo ""
