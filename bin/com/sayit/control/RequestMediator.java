package com.sayit.control;

import com.sayit.data.ContactDao;
import com.sayit.data.MessageType;
import com.sayit.network.MessageProtocol;
import com.sayit.network.NetworkAdapter;

import java.util.List;

public class RequestMediator implements Requestable {

    private ChatApplication chatApplication;
    private NetworkAdapter networkAdapter;
    private volatile boolean isRunning;
    private SenderRunnable senderRunnable;
    private ReceiverRunnable receiverRunnable;

    public RequestMediator(){
        this.isRunning = true;
        this.networkAdapter = new NetworkAdapter();
        senderRunnable = new SenderRunnable(this);
        receiverRunnable = new ReceiverRunnable(this);


    }

    /**
     * Metodo principal
     *
     * @param args argumentos do sistema.
     */
    public static void main(String[] args) {
        //fixme carregar do banco de dados o contactDao
        RequestMediator mediator = new RequestMediator();
        ContactDao contactDao = new ContactDao();

        var chatApp = ChatApplication.launchApplication(args, mediator, contactDao);
        mediator.setChatApplication(chatApp);

        mediator.startServerThread();
        mediator.startMulticastServer();
        mediator.startSenderThread();
        mediator.startReceiverThread();

        chatApp.openStartScene();

    }

    /**
     * Returna se a aplicação está rodando.
     *
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Libera os recursos da aplicação e para as threads em background.
     */
    public void stopApplication() {
        //fixme salvar no banco antes de fechar.
        this.isRunning = false;
        networkAdapter.closeAdapter();
    }

    /**
     * Envia uma mensagem para o receptor atual.
     *
     * @param address     endereço do receptor.
     * @param content     conteudo da mensagem.
     * @param fileName  nome do arquivo.
     */


    /**
     * Envia uma mensagem de texto ao receptor atual.
     *
     * @param address endereço do receptor.
     * @param message mensagem.
     */
    @Override
    public void sendMessage(String address, String message) {

        RequestEvent messageEvent = new RequestEvent(EventType.SEND_MESSAGE, MessageProtocol.MESSAGE);
        messageEvent.setMessageType(MessageType.TEXT);
        messageEvent.setReceiverAddress(address);
        messageEvent.setTextMessage(message);

        senderRunnable.addEvent(messageEvent);

    }
    
    /**
    * Envia os dados do usuário para um contato específico.
    *
    * @param receiverIp ip do usuário.
    * @param name nome do usuário.
    * @param image conteúdo da imagem.
    */
    @Override
    public void sendContactResult(String receiverIp, String name, byte[] image, int width, int height) {
        RequestEvent requestEvent = new RequestEvent(EventType.SEND_MESSAGE, MessageProtocol.ADD_RESPONSE);
        requestEvent.setReceiverAddress(receiverIp);
        requestEvent.setTextMessage(name);
        requestEvent.setImageHeight(height);
        requestEvent.setImageWidth(width);
        requestEvent.setByteContent(image);

        senderRunnable.addEvent(requestEvent);
    }
    
    /**
     * Envia uma requisição de contato a todos os usuários.
     *
     * @param name nome a ser buscado.
     */
    @Override
    public void requestContact(String name) {

        networkAdapter.multicastString(name);
    }

    /**
     * Envia uma solicitação de adição para um outro contato.
     * @param ip endereço ip do novo contato.
     * @param userName nome do usuário atual.
     * @param imageBytes bytes da imagem do usuário atual.
     */
    @Override
    public void contactAdd(String ip, String userName, byte[] imageBytes, int width, int height) {
        RequestEvent requestEvent = new RequestEvent(EventType.SEND_MESSAGE, MessageProtocol.ADD_REQUEST);
        requestEvent.setReceiverAddress(ip);
        requestEvent.setTextMessage(userName);
        requestEvent.setImageHeight(height);
        requestEvent.setImageWidth(width);
        requestEvent.setByteContent(imageBytes);

        senderRunnable.addEvent(requestEvent);

    }

    /**
     * Carrega a lista de mensagens do contato específico.
     *
     * @param id identificador do usuário.
     */
    @Override
    public void loadMessageList(long id) {
    }

    /**
     * Para todos os serviços e fecha a aplicação.
     */
    @Override
    public void stopServices() {
        stopApplication();
    }

    /**
     * Carrega o perfil do usuário a partir da memória.
     *
     * @return
     */
    private String loadProfile() {
        return null;
    }

    /**
     * Carrega a lista de contatos do contato específico.
     *
     * @param id
     * @return
     */
    private List<String> loadContactList(int id) {
        return null;
    }

    /**
     * Inicia a thread receptora.
     */
    private void startReceiverThread() {
        Thread receiverThread = new Thread(receiverRunnable);
        receiverThread.start();
    }

    /**
     * Inicia a thread transmissora.
     */
    private void startSenderThread() {
        Thread senderThread = new Thread(senderRunnable);
        senderThread.start();
    }

    /**
     * Inicia a thread do servidor.
     */
    private void startServerThread() {
        Thread serverThread = new Thread(() -> {
            while(isRunning){
                networkAdapter.acceptTCPConnection();
            }
        });
        //serverThread.setDaemon(true);
        serverThread.start();
    }

    private void startMulticastServer(){
        Thread multicastServer = new Thread(() -> {
            while(isRunning){
                String name = networkAdapter.receiveMulticast();
                if(isRunning && chatApplication.checkUserRequest(name)) {

                    RequestEvent requestEvent = new RequestEvent(EventType.SEND_MESSAGE, MessageProtocol.CONTACT_INFO);
                    requestEvent.setReceiverAddress(networkAdapter.getPackageAddress());
                    requestEvent.setTextMessage(chatApplication.getUserName());
                    requestEvent.setImageHeight(chatApplication.getImageHeight());
                    requestEvent.setImageWidth(chatApplication.getImageWidth());
                    requestEvent.setByteContent(chatApplication.getUserImageBytes());

                    senderRunnable.addEvent(requestEvent);
                }

            }
        });
        //multicastServer.setDaemon(true);
        multicastServer.start();
    }

    public NetworkAdapter getNetworkAdapter(){
        return networkAdapter;
    }

    public void setChatApplication(ChatApplication chatApplication){
        this.chatApplication = chatApplication;

    }

    public ChatApplication getChatApplication(){
        return chatApplication;
    }

	@Override
	public void sendMessage(String address, byte[] content, String fileName) {
		// TODO Auto-generated method stub
		
	}


}