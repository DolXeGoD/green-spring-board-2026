package com.green.spring_board;

public class BoardCreateRequest {
    private String title;
    private String content;

    public BoardCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public BoardCreateRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
