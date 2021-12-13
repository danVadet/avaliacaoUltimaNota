package com.sayit.control;

import com.sayit.data.*;
import com.sayit.loader.Loader;
import com.sayit.ui.control.frame.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sayit.plataform.PlataformRun;

public class ChatApplication extends Application implements Presentable {
    //Window Constants
    public static final String HOME_TITLE = "Say It";
    public static final String ADD_TITLE = "Adicionar Contato";
    public static final String EDIT_TITLE = "Editar Perfil";
    public static final String REQUEST_TITLE = "Solicitação";
    //Layouts
    public static final String HOME_LAYOUT = "/com/sayit/resources/layout/window/layout_chat_home.fxml";
    public static final String START_LAYOUT = "/com/sayit/resources/layout/window/layout_start.fxml";
    public static final String START_FRAME = "/com/sayit/resources/layout/window/layout_frame_start.fxml";
    public static final String FIND_CONTACT_LAYOUT = "/com/sayit/resources/layout/window/layout_find_contact.fxml";
    public static final String EDIT_PROFILE_LAYOUT = "/com/sayit/resources/layout/window/layout_edit_profile.fxml";
    public static final String CONTACT_VIEW = "/com/sayit/resources/layout/view/view_contact_cell.fxml";
    public static final String MESSAGE_VIEW = "/com/sayit/resources/layout/view/view_message_cell.fxml";
    public static final String ADD_RESPONSE_LAYOUT = "/com/sayit/resources/layout/window/layout_add_response.fxml";
    //Styles
    public static final String HOME_STYLE = "/com/sayit/resources/stylesheet/style_chat_home.css";
    public static final String FIND_CONTACT_STYLE = "/com/sayit/resources/stylesheet/style_find_contact.css";
    public static final String EDIT_CONTACT_STYLE = "/com/sayit/resources/stylesheet/style_edit_profile.css";
    public static final String CONTACT_STYLE = "/com/sayit/resources/stylesheet/style_contact.css";
    public static final String MESSAGE_STYLE = "/com/sayit/resources/stylesheet/style_message.css";
    public static final String ADD_RESPONSE_STYLE = "/com/sayit/resources/stylesheet/style_add_response.css";
    public static final String START_STYLE = "/com/sayit/resources/stylesheet/style_start.css";
    //Constants
    public static final int MAX_NAME_LENGTH = 20;

    private volatile static ChatApplication instance;

    private Stage windowStage;
    private Contact currentContact;
    private ContactDao contactDao;
    private Requestable requestable;
    private ChatHomeController chatHome;
    private AddResponseController addContactController;
    private FindContactController findContactController;
    private ProfileEditController editProfileController;
    private StartFrameController startFrameController;
    private ImageProfile imageProfile;
    private PlataformRun plataformRun;
    private boolean isWaitingForContact;
    private Loader loader;
    
    private final LinkedList<Contact> requestList = new LinkedList<>();

    public ChatApplication() {
        ChatApplication.instance = this;
    }
    
    /**
     * Inicializa a aplicação e recebe uma instância da classe.
     *
     * @param args        Argumentos do sistema.
     * @param requestable Instância da classe main.
     * @param contactDao  Instância do DAO de contatos.
     * @return Uma instância da aplicação.
     */
    public synchronized static ChatApplication launchApplication(String[] args, Requestable requestable, ContactDao contactDao) {
        if(ChatApplication.instance == null) {
            new Thread(() -> Application.launch(ChatApplication.class, args)).start();
            while (ChatApplication.instance == null) Thread.onSpinWait();
            ChatApplication.instance.setContactDao(contactDao);
            ChatApplication.instance.setRequestable(requestable);
        }

        return ChatApplication.instance;
    }

   
    /**
     * Metodo chamado ao iniciar a aplicação.
     *
     * @param primaryStage Main Window
     */
    @Override
    public void start(Stage primaryStage) {
        this.windowStage = primaryStage;

        primaryStage.setTitle(HOME_TITLE);
    }

    /**
     * Adiciona uma mensagem de texto ao contato especificado.
     *
     * @param sid      o identificador do contato.
     * @param message a mensagem a ser adicionada
     */
    public void addMessage(String sid, String message) {
    	
        long id = ContactDao.parseAddress(sid);
        Contact contact = contactDao.getContact(id);
        Message newMessage = new Message(contact, false, message, MessageType.TEXT);
        newMessage.setMessageDate(new Date());
        contactDao.addMessage(id, newMessage);
        
        plataformRun.runMessageContact(id, currentContact, contactDao, chatHome);
        
    }

    /**
     * Adiciona uma mensagem ao contato específico.
     *
     * @param sid          identificador do contato.
     * @param content     conteúdo da mensagem.
     * @param fileName tipo da mensagem.
     */
    public void addMessage(String sid, byte[] content, String fileName) {

        //fixme archives will be implemented next
        long id = ContactDao.parseAddress(sid);
        Contact contact = contactDao.getContact(id);
        //fixme get file type from name
        //contactDao.addMessage(id, new Message(contact, false, content, messageType));
    }


    /**
     * Adiciona um contato na lista de requisições de contato.
     * @param name
     * @param address
     * @param image
     * @param width
     * @param height
     */
    public void addContact(String name, String address, byte[] image, int width, int height) {
    	
        requestList.add(new Contact(name, imageProfile.writeImageBytes(image, width, height), address));

        plataformRun.runProcessingRequests(requestList);
    }

    /**
     * Adiciona um contato na lista de contatos.
     *
     * @param name
     * @param address
     * @param image
     * @param width
     * @param heigh
     */
    public void addContactResult(String name, String address, byte[] image, int width, int height) {
        
    	contactDao.addContact(new Contact(name, imageProfile.writeImageBytes(image, width, height), address));
    	
    }

    /**
     * Abre a tela de confirmação de contato.
     *
     * @param contact contato a ser confirmado.
     */
    public void openContactRequest(Contact contact) {
    	
    	loader.loaderAddContact(ADD_RESPONSE_LAYOUT, ADD_RESPONSE_STYLE, addContactController);
    	
    	 var requestWindow = loader.createModalParent(ADD_RESPONSE_LAYOUT, 400, 300, REQUEST_TITLE);
    	 
    	 addContactController.setConfirmCallback(contact1 -> {
            requestable.sendContactResult(contact.getIpAddress(), getUserName(), getUserImageBytes(), getImageWidth(), getImageHeight());
            contactDao.addContact(contact1);
            requestWindow.close();
        });
    	addContactController.setCancelCallback(e -> requestWindow.close());

        requestWindow.showAndWait();
    }

    /**
     * Abre a tela inicial da aplicação, contendo a tela de edição de perfil.
     */
    public void openStartScene() {
    	
    	loader.loaderStart(START_FRAME, START_STYLE, startFrameController);
    	
        startFrameController.setConcludeCallback(contact -> {
            contactDao.setUserProfile(contact);
            openHomeScene();
        });

        plataformRun.runScene(windowStage, loader.getParent(START_FRAME));
    }

    /**
     * Abre a tela de chat.
     */
    public void openHomeScene() {
    	
    	loader.loaderHome(HOME_LAYOUT, HOME_STYLE, chatHome, windowStage);
        
        chatHome.setPresentable(this);

        chatHome.setUserProfile(contactDao.getUserProfile());
        chatHome.setHistoryList(contactDao.getHistoryList());

        plataformRun.runScene(windowStage, loader.getParent(HOME_LAYOUT));

    }

    /**
     * Adiciona uma requisição de contato para a lista.
     *
     * @param name Contact name
     * @param image Contact Icon
     */
    public void addContactRequest(String name, String address, byte[] image, int width, int height) {
        if(isWaitingForContact) {
        	
            findContactController.addContact(new Contact(name, imageProfile.writeImageBytes(image, width, height), address));
        }
    }

    /**
     * Verifica se o usuário atual foi requisitado.
     *
     * @param name Nome requisitado.
     * @return verdadeiro caso seja requisitado.
     */
    public boolean checkUserRequest(String name) {
        return contactDao.getUserProfile().getName().toUpperCase().contains(name.toUpperCase());
    }

    private void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    private void setRequestable(Requestable requestable) {
        this.requestable = requestable;
    }

    /**
     * Retorna o nome do usuário atual.
     *
     * @return Retorna o nome do usuário atual.
     */
    public String getUserName() {
        return contactDao.getUserProfile().getName();
    }

    /**
     * Retorna a imagem do usuário em bytes.
     *
     * @return byte[]
     */
    public byte[] getUserImageBytes() {
        return imageProfile.readImageBytes(contactDao.getUserProfile().getPhoto());
    }

    /**
     * Retorna largura da foto do usuário.
     *
     * @return largura da image
     */
    public int getImageWidth() {
        return (int) contactDao.getUserProfile().getPhoto().getWidth();
    }

    /**
     * Retorna a altura da imagem.
     *
     * @return altura da imagem.
     */
    public int getImageHeight() {
        return (int) contactDao.getUserProfile().getPhoto().getHeight();
    }


    /**
     * Retorna um contato específico.
     *
     * @param id identificador do contato.
     * @return A contact object
     */
    @Override
    public Contact getContactInfo(long id) {
        currentContact = contactDao.getContact(id);
        return currentContact;
    }

    /**
     * Retorna o perfil do usuário atual.
     *
     * @return the current user contact
     */
    @Override
    public Contact getUserProfile() {
        return contactDao.getUserProfile();
    }

    /**
     * Retorna a lista de mensagens de um contato específico. E configura o contato
     * como receptor atual.
     *
     * @param id User id for search
     * @return list of messages from contact
     */
    @Override
    public List<Message> requestMessageList(long id) {
        //fixme addload from database function
        var messageList = contactDao.getMessageList(id);
        if(messageList == null) requestable.loadMessageList(id);
        return messageList;
    }

    /**
     * Retorna a lista de históricos.
     *
     * @return history list.
     */
    @Override
    public List<MessageHistory> getHistoryList() {
        return contactDao.getHistoryList();
    }

    /**
     * Retorna a lista de contatos;
     *
     * @return contact list.
     */
    @Override
    public List<Contact> getContactList() {
        return contactDao.getContactList();
    }


    /**
     * Abre a tela de adição de contatos.
     */
    @Override
    public void openAddScene() {

    	loader.loaderFindContact(FIND_CONTACT_LAYOUT, FIND_CONTACT_STYLE, findContactController);

        var windowFind = loader.createModalParent(FIND_CONTACT_LAYOUT, 400, 300, ADD_TITLE);

        findContactController.setCloseCallback(() -> {
            isWaitingForContact = false;
            windowFind.close();
        });

        findContactController.setSearchCallback(searchInput -> {

            requestable.requestContact(searchInput);
        });

        findContactController.setContactResult(contact -> {
            requestable.contactAdd(contact.getIpAddress(), getUserName(), getUserImageBytes(), getImageWidth(), getImageHeight());
            windowFind.close();
            isWaitingForContact = false;
        });

        findContactController.requestSearchFocus();

        isWaitingForContact = true;
        windowFind.showAndWait();
    }

    /**
     * Abre a tela de edição de perfil.
     */
    @Override
    public void openEditProfileScene() {
    	
    	loader.editProfile(EDIT_PROFILE_LAYOUT, EDIT_CONTACT_STYLE, editProfileController);

        var windowEdit  = loader.createModalParent(EDIT_PROFILE_LAYOUT, 400, 300, EDIT_TITLE);

        editProfileController.setOwnerWindow(windowEdit);
        if(getUserProfile() != null) {
        	editProfileController.setContact(getUserProfile());
        }
        editProfileController.setConcludeCallback(contact -> {
            contactDao.setUserProfile(contact);
            chatHome.setUserProfile(contact);
            windowEdit.close();
        });
        editProfileController.setBackCallback(windowEdit::close);

        windowEdit.showAndWait();
    }


    /**
     * Requisita a lista de contatos.
     *
     * @param name contact name for add.
     */
    @Override
    public void requestContactList(String name) {
        requestable.requestContact(name);
    }

    /**
     * Envia uma mensagem para o contato atual.
     *
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        Message sendMessage = new Message(currentContact, true, message, MessageType.TEXT);
        sendMessage.setMessageDate(new Date());
        requestable.sendMessage(currentContact.getIpAddress(), message);
        contactDao.addMessage(currentContact.getId(), sendMessage);
        chatHome.setMessageList(contactDao.getMessageList(currentContact.getId()));
        chatHome.setHistoryList(contactDao.getHistoryList());
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestable.stopServices();
    }
}
