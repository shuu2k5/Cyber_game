package com.example.cyber_game;

public class Order {
    private String orderId;
    private long totalPrice;
    private String status;

    // Hàm rỗng bắt buộc cho Firebase
    public Order() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}