package com.example.cyber_game;

public class product {
    private String name;
    private String price;
    private int imageResId; // Mã hình ảnh lưu trong thư mục drawable
    private String category; // Để phân loại: "food", "drink", "card"
    private int quantity = 0;

    public product() {
    }

    // Hàm khởi tạo (Constructor)
    public product(String name, String price, int imageResId, String category) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
    }

    public int getQuantity(){ return quantity;}
    public void setQuantity (int quantity) {
        this.quantity = quantity;
    }

    // Các hàm Getter để lấy thông tin ra
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getCategory() { return category; }
}