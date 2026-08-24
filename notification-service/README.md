# Notification & Email Microservice

מיקרו־סרוויס עצמאי המשמש כצינור התקשורת המרכזי של מערכת **"הספרייה השיתופית"**.

השירות אחראי על ניהול, תזמון ושליחת התראות והודעות דוא"ל דינמיות לחברי הקהילה, תוך תמיכה בתבניות הודעה מותאמות אישית ובמשימות מתוזמנות.

---

## 🚀 התקנה והרצה מקומית (Prerequisites)

לפני הפעלת הפרויקט, יש לוודא כי מותקנים במחשב:

* Docker Desktop
* Java 17 ומעלה
* Maven (או שימוש ב־Maven Wrapper המצורף לפרויקט)

---

## 🐘 שלב 1: הפעלת מסד הנתונים באמצעות Docker

הפרויקט משתמש במסד נתונים PostgreSQL המורץ בתוך קונטיינר Docker, ולכן אין צורך בהתקנה מקומית של PostgreSQL.

1. ודאו כי **Docker Desktop** פועל.
2. פתחו טרמינל בתיקיית השורש של הפרויקט.
3. הריצו את הפקודה הבאה:

```bash
docker-compose up -d
```

לאחר סיום ההרצה, קונטיינר PostgreSQL יעלה ברקע ויהיה זמין עבור האפליקציה.

---
## ☕ שלב 2: הפעלת שירות Spring Boot

לאחר שמסד הנתונים עלה בהצלחה, ניתן להפעיל את השרת.

### באמצעות IntelliJ IDEA

1. פתחו את הפרויקט ב־IntelliJ IDEA.
2. נווטו לקובץ:

```text
NotificationServiceApplication.java
```

3. לחצו על כפתור ▶️ Run.

### באמצעות הטרמינל

הריצו את הפקודה הבאה מתוך תיקיית השורש של הפרויקט:

```bash
./mvnw spring-boot:run
```

---

## ✅ בדיקת תקינות

לאחר עליית האפליקציה, ניתן לוודא שהשירות פועל באמצעות גישה לכתובת:

```text
http://localhost:8080/api/notification-types/GetAllNotificationType
```

או לנתיב הבריאות (Health Check), אם מוגדר בפרויקט.

---

## 🛠️ טכנולוגיות עיקריות

* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Docker
* Maven
* Spring Scheduler
* Java Mail Sender
