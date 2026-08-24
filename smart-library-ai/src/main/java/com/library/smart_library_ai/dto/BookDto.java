package com.library.smart_library_ai.dto;

public class BookDto {

        private int bookId;       // מזהה הספר
        private String title;     // שם הספר
        private String category;  // קטגוריית הספר
        private String city;  // מיקום הספר (העיר/הסניף)

        // 1. קונסטרקטור ריק - חובה עבור Spring Boot (לצורך הפיכה ל-JSON וממנו)
        public BookDto() {}

        // 2. קונסטרקטור מלא - בדיוק כמו שצריך בשביל ה-availableBooks.add שלכן
        public BookDto(int bookId, String title, String category, String city) {
            this.bookId = bookId;
            this.title = title;
            this.category = category;
            this.city = city;
        }

        // 3. Getters & Setters - כדי שפונקציה 3 תוכל למשוך את הנתונים (get)
        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

