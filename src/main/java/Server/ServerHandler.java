package Server;

import static Server.ChatServer.clientCount;
import static Server.ChatServer.clients;

import Protocol.Protocol;
import Utils.MessageType;
import Utils.StringResources;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ServerHandler is responsible for managing client connections and processing client messages.
 * It implements Runnable for concurrent handling of multiple clients and Protocol for
 * specific communication protocols.
 */
public class ServerHandler implements Runnable , Protocol {
  /** Socket instance for managing network connections. */
  public  Socket socket;

  /** Reference to the ChatServer for handling server-side operations. */
  public  ChatServer server;

  /** DataOutputStream for sending data to the server. */
  public DataOutputStream out;

  /** DataInputStream for receiving data from the server. */
  public DataInputStream in;

  /** Stores the user name for the current client session. */
  public String userName;

  /** Constant integer value ONE, set to 1. */
  public final int ONE = 1;

  /** Constant String BLANK, representing a single space. */
  public final String BLANK =" ";

  /** Character constant for a space separator used in data formatting. */
  public final char SEPERATOR = ' ';

  /** Instance of StringResources for managing string resources. */
  public StringResources stringResources = new StringResources();


  /**
   * Constructor for ServerHandler.
   * Initializes IO streams and sets the socket and server references.
   *
   * @param socket The client socket.
   * @param server The instance of the ChatServer.
   */
  public ServerHandler(Socket socket, ChatServer server)  {
    this.socket = socket;
    this.server = server;
    try {
      this. out = new DataOutputStream(socket.getOutputStream());
      this. in = new DataInputStream(socket.getInputStream());
    } catch (IOException e) {
      closeResources();
      System.err.println(new StringResources().ERROR_CLOSE_PREFIX + e.getMessage());
    }

  }

  /**
   * The main run method for the thread. It continuously listens for messages from the client
   * and processes them according to the server protocol.
   */
  @Override
  public void run() {
    while(socket.isConnected()){
      try {
        ServerProtocol();
      } catch (IOException e) {
       if(clients.containsKey(userName)){
         clients.remove(userName);
         clientCount.decrementAndGet();
       }
        closeResources();
       break;
      }
    }

  }

  /**
   * Implements the server-side protocol for processing different types of client messages.
   * This method handles various message types like CONNECT_MESSAGE, DISCONNECT_MESSAGE, etc.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  @Override
  public void ServerProtocol() throws IOException {
    int messageType = in.readInt();
    in.readChar();
    if (messageType == MessageType.CONNECT_MESSAGE.getValue() && userName == null) {
      int usernameLength = in.readInt();
      in.readChar();
      byte[] messageBytes = new byte[usernameLength];
      in.readFully(messageBytes);
      this.userName = new String(messageBytes, StandardCharsets.UTF_8);
      if (clients.containsKey(userName)) {
        this.userName = null;
        sendConnectResponse(false , stringResources.Error_CONNECT_RESPONSE);
      }else{
        sendConnectResponse(true,"There are " + clients.size() +  " other connected clients!");
        clients.put(userName,this);
      }
    } else if(userName != null && messageType != MessageType.CONNECT_MESSAGE.getValue() ){
      processOtherRequest(messageType);
    }else{
      sendFailedMessage(stringResources.REEOR_NONEUER_MESSAGE);
    }
  }

  /**
   * Processes other requests based on the message type received from the client.
   *
   * @param messageType The type of message received.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void processOtherRequest(int messageType) throws IOException {
    if(messageType == MessageType.DISCONNECT_MESSAGE.getValue()){
      int  msgsize = in.readInt();
      in.readChar();
      String userName = new String(in.readNBytes(msgsize), StandardCharsets.UTF_8);
      sendConnectResponse(true, stringResources.TRUE_DISCONNCETED_MESSAGE);
      clients.remove(userName);
      clientCount.decrementAndGet();
      closeResources();
    }

    if(messageType == MessageType.BROADCAST_MESSAGE.getValue()){
      int msgsize = in.readInt();
      in.readChar();
      String userName = new String(in.readNBytes(msgsize), StandardCharsets.UTF_8);
      in.readChar();
      int brocastSize = in.readInt();
      in.readChar();
      String brocastMsg = new String(in.readNBytes(brocastSize), StandardCharsets.UTF_8);
      System.out.println(userName + BLANK + this.userName);
      if(userName.equals(this.userName)){
        sendBroadcastMsg(brocastMsg);
      }else{
        sendFailedMessage(stringResources.ERROR_NAME_NOTMACH);
      }
    }
    if(messageType == MessageType.DIRECT_MESSAGE.getValue()){
      int sendSize = in.readInt();
      in.readChar();
      String sendUserName = new String(in.readNBytes(sendSize), StandardCharsets.UTF_8);
      in.readChar();
      int recipientSize = in.readInt();
      in.readChar();
      String recipientUserName = new String(in.readNBytes(recipientSize), StandardCharsets.UTF_8);
      in.readChar();
      int msgsize = in.readInt();
      in.readChar();
      String message = new String(in.readNBytes(msgsize), StandardCharsets.UTF_8);

      if(!this.userName.equals(sendUserName)){
        sendFailedMessage(stringResources.ERROR_SENDUSERNAME_NOTMACH);
      }else if(!clients.containsKey(recipientUserName)){
        sendFailedMessage(stringResources.ERROR_RECIPIENTUSERNAME_NOTMACH);
      }else{
        sendDirectMessage(sendUserName ,recipientUserName , message );
      }
    }

    if(messageType == MessageType.SEND_INSULT.getValue()){
      int sendSize = in.readInt();
      in.readChar();
      String sendUserName = new String(in.readNBytes(sendSize), StandardCharsets.UTF_8);
      in.readChar();
      int recipientSize = in.readInt();
      in.readChar();
      String recipientUserName = new String(in.readNBytes(recipientSize), StandardCharsets.UTF_8);
      in.readChar();
      int msgsize = in.readInt();
      in.readChar();
      String message = new String(in.readNBytes(msgsize), StandardCharsets.UTF_8);
      if(!this.userName.equals(sendUserName) ){
        sendFailedMessage(stringResources.ERROR_SENDUSERNAME_NOTMACH);
      }else if(!clients.containsKey(recipientUserName)){
        sendFailedMessage(stringResources.ERROR_RECIPIENTUSERNAME_NOTMACH);
      }else{
        message =  "->" + recipientUserName + ":" +  message;
        sendInsult(  message );
      }
    }

    if(messageType == MessageType.QUERY_CONNECTED_USERS.getValue()){
      int  msgsize = in.readInt();
      in.readChar();
      String userName = new String(in.readNBytes(msgsize), StandardCharsets.UTF_8);
      if(!userName.equals( this.userName)){
        sendFailedMessage(stringResources.ERROR_NAME_NOTMACH);
      }else{
        sendQueryConnectedUsers();
      }
    }
  }

  /**
   * Sends the list of currently connected users to the client.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendQueryConnectedUsers() throws IOException {
    out.writeInt(MessageType.QUERY_USER_RESPONSE.getValue());
    out.writeChar(SEPERATOR);
    out.writeInt(clients.size() - ONE);
    for(String name :  clients.keySet()){
      if(name != this.userName){
        out.writeChar(SEPERATOR);
        out.writeInt(name.length());
        out.writeChar(SEPERATOR);
        out.write(name.getBytes(StandardCharsets.UTF_8));
      }
    }
  }

  /**
   * Sends an insult message to all clients.
   *
   * @param message The insult message to be sent.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendInsult( String message ) throws IOException {
    sendBroadcastMsg(message);
  }

  /**
   * Sends a direct message from one user to another.
   *
   * @param sendUserName      The username of the sender.
   * @param recipientUserName The username of the recipient.
   * @param message           The message to be sent.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendDirectMessage(String sendUserName ,String recipientUserName , String message  )
      throws IOException {
    DataOutputStream out = clients.get(recipientUserName).out;
    out.writeInt(MessageType.DIRECT_MESSAGE.getValue());
    out.writeChar(SEPERATOR);
    out.writeInt(sendUserName.length());
    out.writeChar(SEPERATOR);
    out.write(sendUserName.getBytes(StandardCharsets.UTF_8));
    out.writeChar(SEPERATOR);
    out.writeInt(recipientUserName.length());
    out.writeChar(SEPERATOR);
    out.write(recipientUserName.getBytes(StandardCharsets.UTF_8));
    out.writeChar(SEPERATOR);
    out.writeInt(message.length());
    out.writeChar(SEPERATOR);
    out.write(message.getBytes(StandardCharsets.UTF_8));
    out.flush();
  }
  /**
   * Broadcasts a message to all connected clients.
   *
   * @param message The message to be broadcast.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendBroadcastMsg(String message) throws IOException {
    for(ServerHandler handler : clients.values()){
      handler.out.writeInt(MessageType.BROADCAST_MESSAGE.getValue());
      handler.out.writeChar(SEPERATOR);
      handler.out.writeInt(userName.length());
      handler.out.writeChar(SEPERATOR);
      handler.out.write(userName.getBytes(StandardCharsets.UTF_8));
      handler.out.writeChar(SEPERATOR);
      handler.out.writeInt(message.length());
      handler.out.writeChar(SEPERATOR);
      handler.out.write(message.getBytes(StandardCharsets.UTF_8));
      handler.out.flush();
    }
  }

  /**
   * Sends a failure message to the client.
   *
   * @param message The failure message to be sent.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void  sendFailedMessage(String message) throws IOException {
    MessageType messageType = MessageType.FAILED_MESSAGE;
    out.writeInt(messageType.getValue());
    out.writeChar(SEPERATOR);
    out.writeInt(message.length());
    out.writeChar(SEPERATOR);
    out.write(message.getBytes(StandardCharsets.UTF_8));
    out.flush();
  }

  /**
   * Sends a connection response to the client.
   *
   * @param success Whether the connection attempt was successful.
   * @param message The message to be sent as part of the connection response.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void  sendConnectResponse(boolean success , String message) throws IOException {
    MessageType messageType = MessageType.CONNECT_RESPONSE;
    out.writeInt(messageType.getValue());
    out.writeChar(SEPERATOR);
    out.writeBoolean(success);
    out.writeChar(SEPERATOR);
    out.writeInt(message.length());
    out.writeChar(SEPERATOR);
    out.write(message.getBytes(StandardCharsets.UTF_8));
    out.flush();
  }


  /**
   * Closes all the resources including socket, input and output streams.
   * This method is typically called when the client is shutting down
   * to ensure all resources are released properly.
   */
  public void closeResources() {
    if(this.socket.isConnected()){
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
        if (socket != null && !socket.isClosed()) {
          socket.close();
        }
      } catch (IOException e) {
        System.err.println(stringResources.ERROR_CLOSE_PREFIX + e.getMessage());
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ServerHandler that)) {
      return false;
    }
    return Objects.equals(socket, that.socket) && Objects.equals(server,
        that.server) && Objects.equals(out, that.out) && Objects.equals(in,
        that.in) && Objects.equals(userName, that.userName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(socket, server, out, in, userName);
  }
}
