package Client;
import Utils.ChatUI;
import Utils.MessageType;
import Utils.StringResources;
import InsultGenerator.RandomInsultGenerator;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ChatClient manages the client-side functionalities of a chat application.
 * It handles network communication, user interaction, and message processing.
 */
public class ChatClient {
  /** socket object*/
  public Socket socket;
  /** DataOutputStream object*/

  public DataOutputStream out;

  /** DataInputStream object*/
  public DataInputStream in;
  /** username variable*/
  public String userName;
  private static final int ONE = 1;
  private static final int TWO = 2;
  private static final int ZERO = 0;
  private static final int FIVE = 5;

  private final char SEPERATOR = ' ';
  private final String BLANK = " ";
  private final String LOGOFFCMD = "logoff";
  private final String QUERYCMD = "who";

  private final String PREFIX = "@";
  private final String SENDALLCMD = "@all";

  StringResources stringResources= new StringResources();



  /**
   * Main method for starting the chat client.
   *
   * @param args Command line arguments for IP address and port.
   * @throws IOException If an I/O error occurs.
   */
  public static void main(String[] args) throws IOException {
    if(args.length != TWO){
      System.out.println(new StringResources().ERROR_ClLIENTS_AGRS);
      return;
    }

    ChatClient client = new ChatClient();
    client.startClient(new Socket(args[ZERO], Integer.parseInt(args[ONE])));
  }

  /**
   * Starts the chat client, setting up streams and initiating the main loop.
   *
   * @param socket The socket for server connection.
   */
  public void startClient(Socket socket) {
    this.socket = socket;
    setupStreams();
    listenFromServer();
    sendMessage();

  }
  /**
   * Sets up the input and output streams for the socket.
   */
  public void setupStreams()  {

      try {
        while (!socket.isClosed()){
          this.out = new DataOutputStream(socket.getOutputStream());
          this.in = new DataInputStream(socket.getInputStream());
          ChatUI ui = new ChatUI();
          this.userName = ui.getUsernameFromUser();
          sendInitialConnectionMessage(userName);
          if(receiveConnectionResponse()){
            return;
          }
        }
      } catch (IOException e) {
        System.out.println(stringResources.CONNECT_CLOSED);
      }
  }
  /**
   * Listens for messages from the server.
   */
  public void listenFromServer(){
    new Thread(() -> {
      while(!socket.isClosed()){
        try {
          int messageType = in.readInt();
          if(messageType == MessageType.CONNECT_RESPONSE.getValue() ){
            responseDisconnect();
          }

          if(messageType == MessageType.QUERY_USER_RESPONSE.getValue()){
            responseQuery();
          }

          if(messageType == MessageType.BROADCAST_MESSAGE.getValue()){
            recieveBroadcast();
          }

          if(messageType == MessageType.DIRECT_MESSAGE.getValue()){
            recieveDirectMassage();
          }

          if(messageType == MessageType.FAILED_MESSAGE.getValue()){
            recieveFailedMessage();
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }


  /**
   * Handles the disconnection response from the server.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void responseDisconnect() throws IOException {
    in.readChar();
    boolean success = in.readBoolean();
    in.readChar();
    int msgSize = in.readInt();
    in.readChar();
    byte[] messageBytes = new byte[msgSize];
    in.readFully(messageBytes);
    String message = new String(messageBytes, StandardCharsets.UTF_8);
    if(success){
      System.out.println(message);
      closeResources();
      System.exit(0);
    }
  }
  /**
   * Receives and processes broadcast messages from the server.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void recieveBroadcast() throws IOException {
    in.readChar();
    int sendUserNameSize = in.readInt();
    in.readChar();
    byte[] usernameBytes = new byte[sendUserNameSize];
    in.readFully(usernameBytes);
    String sendUserName = new String(usernameBytes, StandardCharsets.UTF_8);
    in.readChar();
    int msgSize = in.readInt();
    byte[] messageBytes = new byte[msgSize];
    in.readChar();
    in.readFully(messageBytes);
    String messages = new String(messageBytes, StandardCharsets.UTF_8);

    System.out.println(sendUserName + ": " + messages);
  }

  /**
   * Receives and displays messages indicating failed actions or errors.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void recieveFailedMessage() throws IOException {
    in.readChar();
    int msgSize = in.readInt();
    byte[] messageBytes = new byte[msgSize];
    in.readChar();
    in.readFully(messageBytes);
    String messages = new String(messageBytes, StandardCharsets.UTF_8);

    System.out.println( messages);
  }

  /**
   * Receives and handles direct messages from other users.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void recieveDirectMassage() throws IOException {

    in.readChar();
    int sendUserNameSize = in.readInt();
    System.out.println(sendUserNameSize);
    in.readChar();
    byte[] usernameBytes = new byte[sendUserNameSize];
    in.readFully(usernameBytes);
    String sendUserName = new String(usernameBytes, StandardCharsets.UTF_8);

    System.out.println(sendUserName);
    in.readChar();
    int recipientNameSize = in.readInt();
    in.readChar();
    byte[] recipientNameBytes = new byte[recipientNameSize];
    in.readFully(recipientNameBytes);
    String recipientName = new String(recipientNameBytes, StandardCharsets.UTF_8);
    System.out.println(recipientName);
    in.readChar();
    int msgSize = in.readInt();
    byte[] messageBytes = new byte[msgSize];
    in.readChar();
    in.readFully(messageBytes);
    String messages = new String(messageBytes, StandardCharsets.UTF_8);
    System.out.println(sendUserName + " to " + recipientName  + ": " +  messages);
  }

  /**
   * Handles the response to a query for connected users.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void responseQuery() throws IOException {
    in.readChar();
    int numberOfUsers = in.readInt();
    if(numberOfUsers != ZERO){
      for(int i = ZERO; i < numberOfUsers ; i ++){
        in.readChar();
        int usernameLength = in.readInt();
        in.readChar();
        byte[] messageBytes = new byte[usernameLength];
        in.readFully(messageBytes);
        String userName = new String(messageBytes, StandardCharsets.UTF_8);
        System.out.println(userName);
      }
    }else{
      System.out.println(stringResources.ERROR_QUERY_USEWR);
    }
  }

  /**
   * Sends an initial connection message to the server.
   * This method forms a message that includes the user's chosen username
   * and sends it to the server as part of the initial connection process.
   *
   * @param userName The username of the client.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendInitialConnectionMessage(String userName) throws IOException {
    MessageType messageType = MessageType.CONNECT_MESSAGE; // Define the message type as a connection message
    out.writeInt(messageType.getValue()); // Write the message type to the output stream
    out.writeChar(SEPERATOR); // Separator for readability
    out.writeInt(userName.length()); // Write the length of the username
    out.writeChar(SEPERATOR); // Separator
    out.write(userName.getBytes(StandardCharsets.UTF_8)); // Write the username in UTF-8 encoding
    out.flush(); // Flush the stream to ensure the message is sent immediately
  }

  /**
   * Receives and processes the connection response from the server.
   * This method reads the response from the server to determine whether
   * the connection was successful and prints the server's response message.
   *
   * @return true if the connection was successful, false otherwise.
   * @throws IOException If an I/O error occurs during communication.
   */
  public boolean receiveConnectionResponse() throws IOException {
    int messageType = in.readInt(); // Read the type of the message received
    in.readChar(); // Read and discard the separator character
    boolean success = in.readBoolean(); // Read the boolean indicating success or failure of the connection
    in.readChar(); // Read and discard the separator character
    int msgSize = in.readInt(); // Read the size of the following message
    in.readChar(); // Read and discard the separator character
    String message = new String(in.readNBytes(msgSize), StandardCharsets.UTF_8); // Read the message from the server
    System.out.println(message); // Print the server's response message
    return success; // Return the success status of the connection
  }



  /**
   * Handles the sending of various types of messages based on user input.
   */
  public void sendMessage() {
      ChatUI client = new ChatUI();
      client.displayMessage(stringResources.WELLCOME_MESSAGRE);
      while(!socket.isClosed()) {
        try {
          String input = client.getMessageFromUser();
          if (input.equals("?")) {
            client.displayMessage(stringResources.COMMAND_TEXt);
            client.displayMessage(stringResources.COMMAND);
          }
          if (input.equals(LOGOFFCMD)) {
            sendDisconnectMessage();
          }
          if (input.equals(QUERYCMD)) {
            sendQueryConnectedUsersMessage();
          }
          if (input.startsWith(PREFIX)) {
            if (input.startsWith(SENDALLCMD)) {
              if (input.trim().length() > FIVE) {
                String message = input.substring(FIVE).trim();
                sendBroadcastMessage(message);
              } else {
                client.displayMessage(stringResources.ERROR_BROADCAST);
                continue;
              }
            } else {
              String[] parts = input.split(BLANK, TWO);
              if (parts.length < TWO) {
                client.displayMessage(stringResources.ERROR_DIRRECTOR);
                continue;
              }
              String userName = parts[ZERO].substring(ONE);
              String message = parts[ONE];
              sendDirectMessage(userName, message);
            }
          }

          if (input.startsWith("!")) {
            sendInsultMessage(input);
          }

        }catch (IOException e){
          client.displayMessage(stringResources.CONNECT_CLOSED);
        }
      }
  }

  /**
   * Sends a message to disconnect from the server.
   *
   * @throws IOException If an I/O error occurs during communication.
   * @param message message to the recipient
   * @param recipientName recipient name
   */
  public void sendDirectMessage(String recipientName,String message) throws IOException {

    MessageType messageType = MessageType.DIRECT_MESSAGE;
    out.writeInt(messageType.getValue()); // Message type
    out.writeChar(SEPERATOR);
    out.writeInt(userName.length()); // Length of username
    out.writeChar(SEPERATOR);
    out.write(userName.getBytes(StandardCharsets.UTF_8));
    out.writeChar(SEPERATOR);
    out.writeInt(recipientName.length()); // Length of recipientName
    out.writeChar(SEPERATOR);
    out.write(recipientName.getBytes(StandardCharsets.UTF_8));
    out.writeChar(SEPERATOR);
    out.writeInt(message.length());
    out.writeChar(SEPERATOR);
    out.write(message.getBytes(StandardCharsets.UTF_8));
    out.flush();
  }

  /**
   * Sends an insult message. This should be used cautiously.
   *
   * @param input The input containing the recipient and message details.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendInsultMessage(String input) throws IOException {
    String sentence = new RandomInsultGenerator().generateRandomInsult();
    String recipientName = input.substring(ONE).trim();
    MessageType messageType = MessageType.SEND_INSULT;
    out.writeInt(messageType.getValue()); // Message type
    out.writeChar(SEPERATOR);
    out.writeInt(userName.length()); // Length of username
    out.writeChar(SEPERATOR);
    out.write(userName.getBytes(StandardCharsets.UTF_8));
    out.writeChar(SEPERATOR);
    out.writeInt(recipientName.length()); // Length of recipientName
    out.writeChar(SEPERATOR);
    out.write(recipientName.getBytes(StandardCharsets.UTF_8));
    out.writeChar(SEPERATOR);
    out.writeInt(sentence.length());
    out.writeChar(SEPERATOR);
    out.write(sentence.getBytes(StandardCharsets.UTF_8));
    out.flush();

  }
  /**
   * Sends a broadcast message to all connected users.
   *
   * @param message The message to be broadcast.
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendBroadcastMessage(String message) throws IOException {
    MessageType messageType = MessageType.BROADCAST_MESSAGE;
    out.writeInt(messageType.getValue()); // Message type
    out.writeChar(SEPERATOR);
    out.writeInt(this.userName.length()); // Length of username
    out.writeChar(SEPERATOR);
    out.write(this.userName.getBytes(StandardCharsets.UTF_8));
    out.writeChar(SEPERATOR);
    out.writeInt(message.length());
    out.writeChar(SEPERATOR);
    out.write(message.getBytes(StandardCharsets.UTF_8));
    out.flush();

  }
  /**
   * Sends a query to the server to get the list of connected users.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendQueryConnectedUsersMessage() throws IOException {
    MessageType messageType = MessageType.QUERY_CONNECTED_USERS;
    out.writeInt(messageType.getValue()); // Message type
    out.writeChar(SEPERATOR);
    out.writeInt(userName.length()); // Length of username
    out.writeChar(SEPERATOR);
    out.write(userName.getBytes(StandardCharsets.UTF_8));
    out.flush();
  }

  /**
   * Sends a message to the server to disconnect the current user.
   * This method forms a disconnect message and sends it to the server
   * to notify about the user's intention to disconnect.
   *
   * @throws IOException If an I/O error occurs during communication.
   */
  public void sendDisconnectMessage() throws IOException {
    MessageType messageType = MessageType.DISCONNECT_MESSAGE; // Define message type as disconnect
    out.writeInt(messageType.getValue()); // Write the message type to the output stream
    out.writeChar(SEPERATOR); // Separator for readability
    out.writeInt(userName.length()); // Write the length of the username
    out.writeChar(SEPERATOR); // Separator
    out.write(userName.getBytes(StandardCharsets.UTF_8)); // Write the username in UTF-8 encoding
    out.flush(); // Flush the stream to ensure the message is sent immediately
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
          out.close(); // Close the output stream
        }
        if (in != null) {
          in.close(); // Close the input stream
        }
        if (socket != null && !socket.isClosed()) {
          socket.close(); // Close the socket if it's still open
        }
      } catch (IOException e) {
        System.err.println(stringResources.ERROR_CLOSE_PREFIX + e.getMessage()); // Log any IOException during resource closure
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
    if (!(o instanceof ChatClient that)) {
      return false;
    }
    return Objects.equals(socket, that.socket) && Objects.equals(out, that.out)
        && Objects.equals(in, that.in) && Objects.equals(userName, that.userName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(socket, out, in, userName);
  }

}
