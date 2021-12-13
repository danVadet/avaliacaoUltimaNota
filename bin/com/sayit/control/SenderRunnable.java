package com.sayit.control;

import com.sayit.network.NetworkAdapter;

import java.util.LinkedList;
import java.util.Objects;

public class SenderRunnable implements Runnable {
    private RequestMediator context;
    private NetworkAdapter networkAdapter;
    private final LinkedList<RequestEvent> eventList = new LinkedList<>();


    public SenderRunnable(RequestMediator requestMediator){
        context = requestMediator;
        networkAdapter = context.getNetworkAdapter();
    }
    /**
     * Inicia a função de transmissão.
     */
    @Override
    public void run() {
        while(context.isRunning()){
            if(eventList.size() > 0){

                digestEvent(Objects.requireNonNull(eventList.pollFirst()));
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addEvent(RequestEvent event){
        eventList.add(event);

    }

    private void digestEvent(RequestEvent event) {
        switch (event.getEventType()) {
            case SEND_MESSAGE:
                digestMessageEvent(event);
                break;
        }

        networkAdapter.flushData();
    }


    private void digestMessageEvent(RequestEvent event) {
        switch (event.getMessageProtocol()) {
            case CONTACT_INFO:
            case ADD_REQUEST:
            case ADD_RESPONSE:
                sendContactInfo(event);
                break;
            case MESSAGE:
                switch (event.getMessageType()) {
                    case TEXT:
                        networkAdapter.setCurrentReceiver(event.getReceiverAddress());
                        networkAdapter.sendData(event.getMessageProtocol().getValue());
                        networkAdapter.sendData(event.getMessageType().getValue());
                        networkAdapter.sendData(event.getTextMessage());
                        break;
                }
                break;
        }
    }


    private void sendContactInfo(RequestEvent event) {
        networkAdapter.connect(event.getReceiverAddress());
        networkAdapter.setCurrentReceiver(event.getReceiverAddress());

        networkAdapter.sendData(event.getMessageProtocol().getValue());

        networkAdapter.sendData(event.getTextMessage());
        networkAdapter.sendData(event.getImageHeight());
        networkAdapter.sendData(event.getImageWidth());
        networkAdapter.sendData(event.getContentSize());
        networkAdapter.sendData(event.getByteContent());
    }
}
