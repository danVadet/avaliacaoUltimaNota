package com.sayit.data;

import javafx.scene.image.Image;

import java.util.*;

public class ContactDao {

    private Contact userProfile;
    private List<Contact> contactList;
    private Map<Long, MessageHistory> messageMap;
    private List<Message> messageList;
    private static List <String []> endereco;
    
    public ContactDao() {
        contactList = new ArrayList<>();
        messageMap = new LinkedHashMap<>();
    }

    public static long parseAddress(String address) {
    	 String end = "";
    	
    	endereco.add(address.split("\\."));
    	      
       
        for(int i = 0; i < endereco.size(); i++){
            end += endereco.get(i);
        }
        long ip = Long.parseLong(end);

        return ip;
    }


    /**
     * Adciona o contato na lista de contatos.
     *
     * @param contact
     */
    public void addContact(Contact contact) {

        contactList.add(contact);
        messageMap.put(contact.getId(), new MessageHistory(contact)); 
    }

    /**
     * Adiciona a mensagem no hostórico de mensagens.
     *
     * @param id Identificador do contato
     * @param message Mensagen
     */
    public void addMessage(long id, Message message) {

        if(!messageMap.containsKey(id)) {
            messageMap.put(id, new MessageHistory(getContact(id)));
        }

        messageMap.get(id).addMessage(message);
    }

    /**
     * Busca um contato pelo ID.
     *
     * @param id Identificador do contato
     * @return
     */
    public Contact getContact(long id) {
        for (int i = 0; i < contactList.size(); i++) {
            if(id == contactList.get(i).getId()) {
                return contactList.get(i);
            }

        }

        return null;
    }

    /**
     * Retorna a lista de mensagens do contato.
     *
     * @param id Id do contato.
     * @return
     */
    public List<Message> getMessageList(long id) {
        var messageList = messageMap.get(id).getMessageList();
        messageList.sort(Comparator.comparing(Message::getMessageDate));
        return messageList;
    }

    /**
     * Retorna a lista de históricos de mensagem.
     *
     * @return
     */
    public List<MessageHistory> getHistoryList() {

        Collection<MessageHistory> list = messageMap.values();
        var historyList = new ArrayList<>(list);
        historyList.sort(Comparator.comparing(MessageHistory::getLastDateTime).reversed());
        return historyList;
    }

    /**
     * Edita o nome de um determinado contato.
     *
     * @param id Identificador do contato
     * @param newName Novo nome
     */
    public void editContact(int id, String newName) {

        for (int i = 0; i < contactList.size(); i++) {
            if (id == contactList.get(i).getId());
            {
                contactList.get(i).setName(newName);
            }
        }
    }

    /**
     * Edita o perfil do usuário.
     *
     * @param name Novo nome
     * @param image Nova imagem
     */
    public void editProfile(String name, Image image){

        userProfile.setName(name);
        userProfile.setPhoto(image);
    }

    public Contact getUserProfile() {
        return userProfile;
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setUserProfile(Contact userProfile) {
        this.userProfile = userProfile;
    }


    public List<String> getMessagesData(int id) {
        return null;
    }

    public List<String> getContactsData() {
        return null;
    }

    public List<String> getHistoryData() {
        return null;
    }

}
