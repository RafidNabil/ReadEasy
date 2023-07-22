package com.example.readeasy.Models;

public class ModelPdf {

    String uid, id, title, author, description, categoryId, url, coverUrl;
    String timestamp;

    public ModelPdf() {
    }

    public ModelPdf(String uid, String id, String title, String author, String description, String categoryId, String url, String coverUrl, String timestamp) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.coverUrl = coverUrl;
        this.timestamp = timestamp;
    }

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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
