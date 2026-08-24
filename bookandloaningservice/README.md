Book and Loaning Management System
פרויקט לניהול ספרים והשאלות שנבנה כחלק מהפרקטיקום.

📋 דרישות קדם (Prerequisites)
לפני תחילת העבודה, ודאי שמותקן אצלך במחשב:

Docker Desktop (להרצת בסיס הנתונים).

Java 21/26 (או גרסת ה-JDK שאתן עובדות איתה).

IntelliJ IDEA (או כל IDE אחר ל-Java).

🚀 איך מתחילים לעבוד?
1. הפעלת בסיס הנתונים (Docker)
הפרויקט משתמש ב-PostgreSQL המוגדר דרך Docker. כדי להפעיל אותו, פתחי טרמינל בתיקייה הראשית של הפרויקט והריצי:

Bash
docker-compose up -d
הפקודה הזו תרים את בסיס הנתונים ותצור את הטבלאות הנדרשות בצורה אוטומטית.

2. הרצת הפרויקט (Spring Boot)
פתחי את תיקיית Book-and-Loaning-Management-System ב-IntelliJ.

המתיני שה-IDE יסיים להוריד את כל ה-Dependencies (הספריות).

הריצי את המחלקה הראשית BookAndLoaningManagementSystemApplication.

האפליקציה תרוץ בפורט 8080.

🛠️ הגדרות חיבור ל-DB
במידה ואת צריכה להגדיר ידנית את החיבור (בתוך ה-application.properties):

URL: jdbc:postgresql://localhost:5432/library_db

User: admin

Password: password123