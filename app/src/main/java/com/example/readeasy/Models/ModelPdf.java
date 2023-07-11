package com.example.readeasy.Models;

public class ModelPdf {

    String uid, id, title, author, description, category, url;
    long timestamp;

    public ModelPdf() {
    }

    public ModelPdf(String uid, String id, String title, String author, String description, String category, String url, long timestamp) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.url = url;
        this.timestamp = timestamp;
    }

    //getter setter

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
