#!/bin/bash
# ==============================================================
# Script de Build de Todos os Microsserviços
# ==============================================================
# Este script compila todos os microsserviços do projeto.
#
# Uso: ./build-all.sh

set -e  # Para em caso de erro

echo "🏗️  Iniciando build de todos os microsserviços..."
echo ""

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# ==============================================================
# 1. Task Service
# ==============================================================
echo -e "${BLUE}📦 Building Task Service...${NC}"
cd services/task-service
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Task Service build successful${NC}"
else
    echo -e "${RED}❌ Task Service build failed${NC}"
    exit 1
fi
cd ../..
echo ""

# ==============================================================
# 2. Statistics Service
# ==============================================================
echo -e "${BLUE}📊 Building Statistics Service...${NC}"
cd services/statistics-service
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Statistics Service build successful${NC}"
else
    echo -e "${RED}❌ Statistics Service build failed${NC}"
    exit 1
fi
cd ../..
echo ""

# ==============================================================
# 3. API Gateway
# ==============================================================
echo -e "${BLUE}🚪 Building API Gateway...${NC}"
cd services/api-gateway
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ API Gateway build successful${NC}"
else
    echo -e "${RED}❌ API Gateway build failed${NC}"
    exit 1
fi
cd ../..
echo ""

# ==============================================================
# Resumo
# ==============================================================
echo -e "${GREEN}🎉 Todos os serviços foram compilados com sucesso!${NC}"
echo ""
echo "📦 Artefatos gerados:"
echo "   - services/task-service/target/task-service-1.0.0.jar"
echo "   - services/statistics-service/target/statistics-service-1.0.0.jar"
echo "   - services/api-gateway/target/api-gateway-1.0.0.jar"
echo ""
echo "🐳 Próximos passos:"
echo "   1. Build das imagens Docker: docker-compose build"
echo "   2. Iniciar todos os serviços: docker-compose up -d"
echo "   3. Ver logs: docker-compose logs -f"
