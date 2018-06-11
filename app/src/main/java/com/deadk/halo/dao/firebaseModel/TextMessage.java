package com.deadk.halo.dao.firebaseModel;

public class TextMessage {
    private String messageId;
    private String senderId;
    private String createAt;
    private String contentType;
    private String content;
    public TextMessage(){}

    public TextMessage(String messageId, String senderId, String createAt, String contentType, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.createAt = createAt;
        this.contentType = contentType;
        this.content = content;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
