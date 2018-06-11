package com.deadk.halo.dao.model;

import android.support.annotation.NonNull;

import com.deadk.halo.common.models.IMessage;
import com.deadk.halo.common.models.MessageContentType;
import com.deadk.halo.common.models.Voice;

import java.util.Date;

public class Message implements IMessage,Comparable,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType.Voice /*and this one is for custom content type (in this case - voice message)*/ {

    private String id;
    private String text;
    private Date createdAt;
    private User user;
    private String senderId;
    private Image image;
    private Voice voice;

    public Message(){}

    public Message(String id, User user, String text) {
        this(id, user, text, new Date());
    }

    public Message(String id, User user, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Message(String id, String senderId, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    @Override
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }


    public void setImage(Image image) {
        this.image = image;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    @Override
    public Voice getVoice() {
        return voice;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Message cmpMes = (Message)o;
        return this.getId().compareTo(cmpMes.getId());
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
