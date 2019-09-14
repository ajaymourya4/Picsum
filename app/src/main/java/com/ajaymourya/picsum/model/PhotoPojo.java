package com.ajaymourya.picsum.model;

import com.google.gson.annotations.SerializedName;

public class PhotoPojo {

    @SerializedName("id")
    private Integer id;
    @SerializedName("filename")
    private String filename;
    @SerializedName("author")
    private String author;
    @SerializedName("author_url")
    private String authorUrl;
    @SerializedName("post_url")
    private String postUrl;

    public PhotoPojo(Integer id, String filename, String author, String authorUrl, String postUrl) {
        this.id = id;
        this.filename = filename;
        this.author = author;
        this.authorUrl = authorUrl;
        this.postUrl = postUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }
}
