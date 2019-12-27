package org.rti.tangerine.p2p;

import org.json.JSONObject;

public class Message extends JSONObject {

    private String messageType = "";
    private String message = "";
    private JSONObject object = null;

    private String payloadData = null;
    private String destination = "";

    public Message(String messageType, String message, JSONObject object, String destination, String payloadData) {
        super();
        this.messageType = messageType;
        this.message = message;
        this.object = object;
        this.destination = destination;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(String payloadData) {
        this.payloadData = payloadData;
    }


}
