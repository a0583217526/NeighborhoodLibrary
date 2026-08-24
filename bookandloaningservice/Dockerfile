# שלב 1: בנייה עם Java 21 (או 17, בהתאם למה שמוגדר ב-pom.xml שלך)
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# העתקת קבצי הפרויקט ישירות מהשורש החדש שסידרת
COPY pom.xml .
COPY src ./src

# קימפול ובנייה - ללא שום פקודת cd!
RUN mvn clean package -DskipTests

# שלב 2: הרצה
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# העתקת ה-JAR שנוצר מתוך תיקיית target
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]