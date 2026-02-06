package model;

public class Expense {
    private int id;
    private String userId;
    private String date;
    private String category;
    private int amount;
    private String memo;

    public Expense() {}

    public Expense(String userId, String date, String category, int amount, String memo) {
        this.userId = userId;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.memo = memo;
    }

    // getter / setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
