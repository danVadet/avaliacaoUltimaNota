package com.sayit.control;

import com.sayit.data.Contact;
import com.sayit.data.Message;
import com.sayit.data.MessageHistory;

import java.util.List;

public interface Presentable {

    Contact getContactInfo(long id);

    Contact getUserProfile();

    List<Message> requestMessageList(long id);

    List<MessageHistory> getHistoryList();

    List<Contact> getContactList();

    void openAddScene();

    //fixme add support to edit contacts from contacts list
    void openEditProfileScene();

    /**
     * Request contact for add scene.
     *
     * @param name
     */
    void requestContactList(String name);


    void sendMessage(String message);

    //fixme Add method to send message by bytes
}
