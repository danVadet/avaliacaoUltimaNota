package com.sayit.network;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NetworkAdapter {

    private ServerSocket serverSocket;
    private MulticastSocket multicastSocket;
    private final Map<String, Connection> connectionMap = new LinkedHashMap<>();
    ;
    private final List<Connection> connectionList = new ArrayList<>();
    private Connection currentTransmitter;
    private Connection currentReceiver;
    private InetAddress multicastGrup;
    private DatagramPacket currentPackage;
    private int currentTransmitterControl;

    private final String MCAST_ADDR = "239.239.239.239";
    private final int MCAST_DEST_PORT = 7777;
    private final int SERVER_SOCKET_DEST_PORT = 6000;
    private final int BUFFER_SIZE = 1024;

    /**
     *
     * Instancia a interface Map com um LinkedHashMap.
     * Instancia a interface List com um LinkedList.
     * Cria um sevidor que escutará a porta 5000.
     *
     **/
    public NetworkAdapter() {

        //fixme get ip adresses for package check
        currentTransmitterControl = 0;

        try {

            serverSocket = new ServerSocket(SERVER_SOCKET_DEST_PORT);

            multicastSocket = new MulticastSocket(MCAST_DEST_PORT);

            multicastGrup = InetAddress.getByName(MCAST_ADDR);

            multicastSocket.joinGroup(multicastGrup);

        } catch (IOException ea) {
            ea.printStackTrace();

        }

    }


    /**
     * Retorna a representação string do IP do Transmissor atual.
     * @return IP do transmissor.
     */
    public String getStringAddress() {

        return currentTransmitter.getpIPAddress();

    }

    /**
     * Busca um receptor no mapa de conexões e o coloca como receptor atual.
     *
     * @param address Endereço da conexão.
     */
    public boolean setCurrentReceiver(String address) {
        if(connectionMap.containsKey(address)) {
            Connection c = connectionMap.get(address);
            if(c.isOnline()) {
                currentReceiver = c;
                return true;
            }

        }
        return false;

    }

    /**
     *
     * Busca o próximo transmissor na lista e o define como trasmissor atual.
     * A função só retorna verdadeiro caso o transmissor esteja online e possua mensagens
     * na stream.
     * Caso o transmissor esteja desconectado, fecha a conexão e retorna
     * false.
     * Fechar e remover todas as conexão offline
     *
     * @return true se a conexão está ativa ou
     * false se a conexão está inativa ou offline.
     *
     */
    public boolean nextTransmitter() {

        if(connectionList.size() > 0) {
            if(currentTransmitterControl >= connectionList.size()) currentTransmitterControl = 0;

            Connection c = connectionList.get(currentTransmitterControl);

            if(c.isOnline()) {
                if(c.haveMessage()) {
                    currentTransmitter = c;
                    return true;
                }
            } else {
                closeConnection(c.getpIPAddress());
                currentTransmitterControl--;
            }

            currentTransmitterControl++;

        }

        return false;
    }

    /**
     * Envia o texto para todos no grupo de multicasting.
     *
     * @param text Nome a ser buscado na rede.
     */
    public void multicastString(String text) {


        byte[] multicastMessage = text.getBytes();


        try {

            DatagramPacket packet = new DatagramPacket(multicastMessage, multicastMessage.length, multicastGrup, MCAST_DEST_PORT);

            multicastSocket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     * Recebe uma string do grupo multicast.
     *
     * @return string do grupo multicast. null caso nenhuma.
     *
     */
    public String receiveMulticast() {
        byte[] buffer = new byte[BUFFER_SIZE];

        DatagramPacket datagramReceivePacket = new DatagramPacket(buffer, BUFFER_SIZE);


        try {

            //aparentemente os acentos estão sendo enviados.
            multicastSocket.receive(datagramReceivePacket);
            currentPackage = datagramReceivePacket;
            int i = 0;
            while (buffer[i] != 0) i++;
            byte[] textContent = new byte[i];
            System.arraycopy(buffer, 0, textContent, 0, i);
            return new String(textContent);

        } catch (IOException e) {
            System.out.println("Multicast fechado.");
        }
        return null;
    }

    /**
     * Retorna o endereço ip de quem enviou o datagram packet atual.
     *
     * @return string contendo endereço ip do pacote.
     */
    public String getPackageAddress() {
        return currentPackage.getAddress().getHostAddress();
    }


    /**
     *
     * Inicia uma nova conexão TCP através do IP especificado
     * e a adciona na lista de conecções.
     *
     * @param ipAddress Endereço ip do socket.
     *
     */
    public void connect(String ipAddress) {

        try {

            Socket client = new Socket(InetAddress.getByName(ipAddress),SERVER_SOCKET_DEST_PORT);
            Connection connection = new Connection(client);
            connectionList.add(connection);
            connectionMap.put(connection.getpIPAddress(),connection);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Busca uma conexão na lista e fecha a conexão. Usado também para
     * fechar as conexões inativas.
     *
     * @param address Endereço da conexão.
     */
    public void closeConnection(String address) {

        Connection c = connectionMap.get(address);
        c.closeConnection();
        connectionMap.remove(c);
        connectionList.remove(c);


    }

    /**
     * Aceita uma nova conecção à partir do server socket.
     * Encapsula o socket em uma classe connection e adiciona nas listas.
     */
    public void acceptTCPConnection() {

        try {
            Connection connection = new Connection(serverSocket.accept());
            connectionList.add(connection);
            connectionMap.put(connection.getpIPAddress(),connection);

        } catch (IOException e) {
            System.out.println("Servidor fechado.");
        }

    }

    public void flushData(){
        try {
            currentReceiver.getDataOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Envia informação para o receptor atual.
     *
     * @param data envia um inteiro.
     *
     */
    public void sendData(int data) {

        try {
            currentReceiver.getDataOutputStream().writeInt(data);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    /**
     * Envia informação para o receptor atual.
     *
     * @param data envia uma String.
     */
    public void sendData(String data) {
        try {
            currentReceiver.getDataOutputStream().writeUTF(data);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Envia informação para o receptor atual.
     *
     * @param data envia um array de bytes.
     */
    public void sendData(byte[] data) {
        try {
            currentReceiver.getDataOutputStream().write(data);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Envia informação para o receptor atual.
     *
     * @param data envia um valor booleano.
     */
    public void sendData(boolean data) {

        try {
            currentReceiver.getDataOutputStream().writeBoolean(data);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Recebe um valor inteiro do transmisso r atual.
     *
     * @return um valor inteiro.
     */
    public int receiveInt() {
        try {
            return currentTransmitter.getDataInputStream().readInt();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Recebe um texto do transmissor atual.
     *
     * @return uma string.
     */
    public String receiveString() {

        try {
            return currentTransmitter.getDataInputStream().readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * Recebe um array de bytes de tamanho específico do trasmissor atual.
     * O parametro expecifíca a quantidade de bytes a ser lido do stream.
     *
     * @return array de bytes.
     * @param size quantidade de informação a ser recebida.
     */
    public byte[] receiveBytes(int size) {
        byte[] buffer = new byte[size];
        DataInputStream stream = currentTransmitter.getDataInputStream();

        try {

            int readBytes;
            int offset = 0;
            int curLen = size;

            while ((readBytes = stream.read(buffer, offset, curLen)) > -1 && offset < size) {
                offset += readBytes;
                curLen -= readBytes;
            }

            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Recebe um valor boleano do transmissor atual.
     *
     * @return um valor boleano.
     */
    public boolean receiveBoolean() {

        try {
            return currentTransmitter.getDataInputStream().readBoolean();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * Encerra o adaptador liberando todos os recursos
     * e fechando todas as conexões.
     *
     */
    public void closeAdapter() {
        //fixme resolve so timeout
        try {
            serverSocket.close();
            multicastSocket.close();

            if(currentReceiver != null)
                currentReceiver.closeConnection();
            if(currentTransmitter != null)
                currentTransmitter.closeConnection();

        } catch (IOException e) {
            e.printStackTrace();

        }


    }


}
