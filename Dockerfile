# Base image com JDK 17
FROM openjdk:17-jdk-slim

# Instala Maven
RUN apt-get update && apt-get install -y maven git && rm -rf /var/lib/apt/lists/*

# Define diretório do app
WORKDIR /app

# Copia todos os arquivos do projeto
COPY . .

# Build do projeto
RUN mvn clean package -DskipTests

# Porta que o Spring Boot vai usar
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "target/loja_brinquedos-0.0.1-SNAPSHOT.jar"]
