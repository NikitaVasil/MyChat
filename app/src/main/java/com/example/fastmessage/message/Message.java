package com.example.fastmessage.message;

public class Message {
    private String mesId, ownerId, text, date;

    public Message(String mesId, String ownerId, String text, String date) {
        this.mesId = mesId;
        this.ownerId = ownerId;
        this.text = text;
        this.date = date;
    }

    public String getMesId() {
        return mesId;
    }

    public void setMesId(String mesId) {
        this.mesId = mesId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
