# שלב 1: בנייה עם Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# העתקת ה-pom והקוד משורש התיקייה
COPY pom.xml .
COPY src ./src

# קימפול
RUN mvn clean package -DskipTests

# שלב 2: הרצה עם Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]