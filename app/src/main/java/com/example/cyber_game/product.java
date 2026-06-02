package com.example.cyber_game;

public class product {
    private String name;
    private String price;
    private int imageResId; // Mã hình ảnh lưu trong thư mục drawable
    private String category; // Để phân loại: "food", "drink", "card"

    // Hàm khởi tạo (Constructor)
    public product(String name, String price, int imageResId, String category) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
    }

    // Các hàm Getter để lấy thông tin ra
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getCategory() { return category; }
}