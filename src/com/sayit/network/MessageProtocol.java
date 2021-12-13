package com.sayit.network;


import java.util.Map;

public enum MessageProtocol {

    MESSAGE(1), CONTACT_INFO(2), ADD_REQUEST(3), ADD_RESPONSE(4), SEND_ARCHIVE(5);

    private int value;
    private static final Map<Integer, MessageProtocol> CAST_MAP =
            Map.of(1, MESSAGE
                    , 2, CONTACT_INFO
                    , 3, ADD_REQUEST
                    , 4, ADD_RESPONSE
                    , 5, SEND_ARCHIVE);


    MessageProtocol(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

    public static MessageProtocol castFrom(int i) {
        return CAST_MAP.get(i);
    }


}
