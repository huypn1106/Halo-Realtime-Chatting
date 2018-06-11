package com.deadk.halo.dao.firebaseModel;

public class VoiceMessage {
    private String messageId;
    private String senderId;
    private String createAt;
    private String contentType;
    private int duration;

    public VoiceMessage() {
    }
    public VoiceMessage(String messageId, String senderId, String createAt, String contentType, int duration) {

        this.messageId = messageId;
        this.senderId = senderId;
        this.createAt = createAt;
        this.contentType = contentType;
        this.duration = duration;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}
