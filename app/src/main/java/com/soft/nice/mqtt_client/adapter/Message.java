package com.soft.nice.mqtt_client.adapter;

import java.util.Date;

public class Message {
    private String topic_txt;
    private String message_txt;
    private String send_type_txt;
    private String message_date;
    public Message(String message_txt, String topic_txt, String send_type_txt, String message_date) {
        this.message_txt = message_txt;
        this.topic_txt = topic_txt;
        this.send_type_txt = send_type_txt;
        this.message_date = message_date;
    }

    public String getMessage_txt() {
        return message_txt;
    }

    public void setMessage_txt(String message_txt) {
        this.message_txt = message_txt;
    }

    public String getTopic_txt() {
        return topic_txt;
    }

    public void setTopic_txt(String topic_txt) {
        this.topic_txt = topic_txt;
    }

    public String getSend_type_txt() {
        return send_type_txt;
    }

    public void setSend_type_txt(String send_type_txt) {
        this.send_type_txt = send_type_txt;
    }

    public String getMessage_date() {
        return message_date;
    }

    public void setMessage_date(String message_date) {
        this.message_date = message_date;
    }


}
