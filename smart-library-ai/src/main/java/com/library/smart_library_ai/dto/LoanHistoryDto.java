package com.library.smart_library_ai.dto;

public class LoanHistoryDto {
    private int bookId;
    private String bookCategory; //  (בשביל לבדוק מה הוא אוהב)
    private String branchCity;   // מאיזה סניף הוא לקח (בשביל לבדוק אם זה מהעיר שלו)

    public LoanHistoryDto() {}

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getBookCategory() { return bookCategory; }
    public void setBookCategory(String bookCategory) { this.bookCategory = bookCategory; }
    public String getBranchCity() { return branchCity; }
    public void setBranchCity(String branchCity) { this.branchCity = branchCity; }
}
