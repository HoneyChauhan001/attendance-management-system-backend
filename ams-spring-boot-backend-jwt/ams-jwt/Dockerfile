FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . /app
RUN ./mvnw -q -DskipTests package || (echo "mvnw not found, trying system maven" && mvn -q -DskipTests package)
EXPOSE 8080
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/data/ams;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE
VOLUME ["/data","/app/uploads"]
ENTRYPOINT ["java","-jar","target/ams-0.0.2-SNAPSHOT.jar"]
