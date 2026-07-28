FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

RUN ls -la target

EXPOSE 8081

CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]