package com.example.dailyexpense;

public class User {
    private int id;
    private String expenseType;
    private int expenseAmount;
    private String expenseDate;
    private String expenseTime;
    private String expenseImage;

    public User(int id, String expenseType, int expenseAmount, String expenseDate, String expenseTime,String expenseImage) {
        this.id = id;
        this.expenseType = expenseType;
        this.expenseAmount = expenseAmount;
        this.expenseDate = expenseDate;
        this.expenseTime = expenseTime;
        this.expenseImage = expenseImage;
    }


    public int getId() {
        return id;
    }

    public String getExpenseImage() {
        return expenseImage;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public int getExpenseAmount() {
        return expenseAmount;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public String getExpenseTime() {
        return expenseTime;
    }
}
