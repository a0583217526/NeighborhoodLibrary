package com.library.smart_library_ai.dto;

import java.util.List;

public class UserDto {
    private int userId;
    private String city;
    private List<LoanHistoryDto> loanHistoryDto;


    public UserDto() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public List<LoanHistoryDto> getLoanHistoryDto() {
        return loanHistoryDto;
    }

    public void setLoanHistoryDto(List<LoanHistoryDto> loanHistoryDto) {
        this.loanHistoryDto = loanHistoryDto;
    }
}