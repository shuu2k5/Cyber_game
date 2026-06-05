package com.example.cyber_game;

public class ChatMessage {
    public String sender, text, time;
    public ChatMessage() {}
    public ChatMessage(String sender, String text, String time) {
        this.sender = sender;
        this.text   = text;
        this.time   = time;
    }
}