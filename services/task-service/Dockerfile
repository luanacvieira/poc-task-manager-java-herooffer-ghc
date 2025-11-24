# ==============================================================
# Dockerfile para Task Service
# ==============================================================
# Usa build multi-stage para otimizar tamanho da imagem final

# Stage 1: Build
# Usa imagem Maven com JDK 17 para compilar a aplicação
FROM maven:3.9-eclipse-temurin-17 AS build

# Define diretório de trabalho
WORKDIR /app

# Copia arquivos de configuração do Maven primeiro (melhor cache)
COPY pom.xml .

# Baixa dependências (cached se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copia código fonte
COPY src ./src

# Compila a aplicação (skip tests para build mais rápido)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
# Usa imagem JRE menor para executar a aplicação
FROM eclipse-temurin:17-jre-alpine

# Informações da imagem
LABEL maintainer="Task Manager Team"
LABEL service="task-service"
LABEL version="1.0.0"

# Cria usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Define diretório de trabalho
WORKDIR /app

# Copia JAR compilado do stage de build
COPY --from=build /app/target/*.jar app.jar

# Expõe porta do serviço
EXPOSE 8081

# Configurações JVM otimizadas para container
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1
