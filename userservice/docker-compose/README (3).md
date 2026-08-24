# 📚 Neighborhood Library - User & Auth Microservice

פרויקט זה מהווה חלק מארכיטקטורת מיקרו-סרוויסים (**Microservices**) עבור מערכת ניהול ספרייה שכונתית.
שירות זה הוא **User & Auth Microservice** האחראי על ניהול המשתמשים, השכונות, אבטחת השרת באמצעות JWT, ואינטגרציה מלאה מול Google OAuth2.

---

## 🏗️ ארכיטקטורת המערכת (System Architecture)

המערכת בנויה משלושה רכיבים מרכזיים:

* **User Service (Backend)**
  שרת עצמאי המפותח ב־Spring Boot (Java 17) ומאזין על פורט **9000**

* **Library UI (Frontend)**
  ממשק משתמש מודרני ב־React המאזין על פורט **3000**

* **Database (PostgreSQL)**
  מסד נתונים ייעודי לשירות המשתמשים, רץ בתוך Docker Container

---

## 🛠️ דרישות קדם (Prerequisites)

לפני הרצת המערכת, ודאו שמותקנים:

* **Java SDK 17** (או גרסה מתקדמת יותר)
* **Node.js 18+**
* **Docker Desktop**

---

## 🚀 הוראות הרצה (Step-by-Step Run Guide)

### 1️⃣ שכפול הפרויקט (Clone)

```bash
git clone https://rachelhroniyan-admin@bitbucket.org/chanaazar_worspce/library-system.git
cd library-project
```

---

### 2️⃣ הרצת מסד הנתונים (PostgreSQL עם Docker)

```bash
docker-compose up -d
```

📌 פעולה זו:

* מפעילה PostgreSQL על פורט **5432**
* יוצרת בסיס נתונים בשם `library_users_db`

---

### 3️⃣ הרצת השרת (Spring Boot - User Service)

1. פתחו את תיקיית `userservice` ב־IDE (למשל IntelliJ)
2. עדכנו את קובץ ההגדרות במידת הצורך:

```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

3. הריצו את המחלקה:

```
UserServiceApplication
```

🌐 השרת יפעל בכתובת:
http://localhost:9000

---

### 4️⃣ הרצת צד הלקוח (React UI)

```bash
cd library-ui
npm install
npm start
```

🌐 האפליקציה תיפתח בכתובת:
http://localhost:3000

---

## 🔒 אבטחה וזרימת מידע (Security & Data Flows)

### 🔑 1. אימות משתמשים עם JWT

#### צד שרת

* שימוש ב־`JwtAuthenticationFilter`
* שימוש ב־`JwtTokenProvider`
* כל בקשה לנתיב מוגן מחייבת טוקן תקף

#### צד לקוח

Axios Interceptor מוסיף טוקן אוטומטית:

```javascript
config.headers.Authorization = `Bearer ${token}`;
```

---

### 🔐 2. התחברות עם Google OAuth2

תהליך ההתחברות:

* המשתמש מופנה ל־Google OAuth
* לאחר אימות — חזרה לאפליקציה

#### משתמש קיים

* כניסה מיידית
* שמירת הנתונים:

  * Token
  * Email
  * Full Name
  * ID

#### משתמש חדש (Onboarding)

* הפניה לדף `CompleteGooglePage`
* השלמת:

  * מספר טלפון
  * בחירת שכונה (נטען מה־API)

---

### 🔄 3. סנכרון מצב התחברות

* שימוש ב־`useLocation` מתוך `react-router-dom`
* עדכון Navbar בזמן אמת
* ללא צורך ברענון דף

---

## 📌 הערות נוספות

* המערכת בנויה בארכיטקטורת Microservices וניתנת להרחבה
* כל שירות פועל באופן עצמאי
* ניתן להוסיף שירותים נוספים (כגון Book Service, Loan Service וכו')

---

## 👨‍💻 טכנולוגיות בשימוש

* **Backend:** Spring Boot, Spring Security, JWT
* **Frontend:** React, Axios
* **Database:** PostgreSQL
* **DevOps:** Docker, Docker Compose
* **Authentication:** Google OAuth2

---

## 📬 סיכום

ה־User & Auth Microservice מספק פתרון מלא לניהול משתמשים ואבטחה:

* הרשמה והתחברות
* JWT Authentication
* Google OAuth2
* ניהול שכונות
* סנכרון Frontend-Backend

מערכת זו מהווה בסיס יציב להמשך פיתוח מערכת ספרייה מודרנית ומבוזרת.
