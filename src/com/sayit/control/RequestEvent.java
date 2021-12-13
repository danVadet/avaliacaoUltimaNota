package com.sayit.control;

import com.sayit.data.MessageType;
import com.sayit.network.MessageProtocol;

public class RequestEvent {

    private EventType eventType;
    private MessageProtocol messageProtocol;
    private MessageType messageType;

    private String receiverAddress;
    private String textMessage;
    private String dataName;

    private int numberInfo;
    private int imageWidth;
    private int imageHeight;

    private boolean confirmation;
    private byte[] byteContent;

    public RequestEvent(EventType eventType, MessageProtocol messageProtocol) {
        this.eventType = eventType;
        this.messageProtocol = messageProtocol;
    }

    //Send text message
    public RequestEvent(EventType eventType, MessageProtocol messageProtocol, MessageType messageType, String receiverAddress, String textMessage) {
        this.eventType = eventType;
        this.messageProtocol = messageProtocol;
        this.messageType = messageType;
        this.receiverAddress = receiverAddress;
        this.textMessage = textMessage;
    }

    //Send bynary file
    public RequestEvent(EventType eventType, MessageProtocol messageProtocol, MessageType messageType, String receiverAddress, String dataName, byte[] byteContent) {
        this.eventType = eventType;
        this.messageProtocol = messageProtocol;
        this.messageType = messageType;
        this.receiverAddress = receiverAddress;
        this.dataName = dataName;
        this.byteContent = byteContent;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }

    public void setByteContent(byte[] byteContent) {
        this.byteContent = byteContent;
    }

    public EventType getEventType() {
        return eventType;
    }

    public MessageProtocol getMessageProtocol() {
        return messageProtocol;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public String getDataName() {
        return dataName;
    }

    public int getNumberInfo() {
        return numberInfo;
    }

    public void setNumberInfo(int numberInfo) {
        this.numberInfo = numberInfo;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    public byte[] getByteContent() {
        return byteContent;
    }

    public int getContentSize() {
        return byteContent != null ? byteContent.length : -1;
    }
}
