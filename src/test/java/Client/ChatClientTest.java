package Client;

import static org.junit.jupiter.api.Assertions.*;

import Server.ChatServer;
import Server.ServerHandler;
import Utils.MessageType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
class ChatClientTest {
  private ChatClient chatClient;
  private ServerSocket serverSocket;
  private ServerHandler serverHandler;
  @BeforeEach
  public void setUp() throws IOException {
    serverSocket = new ServerSocket(0);
    int port =  serverSocket.getLocalPort();
    Thread serverThread = new Thread(() -> {
      Socket socket;
      try {
         socket =  serverSocket.accept();
        this.serverHandler =  new ServerHandler(socket, null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    serverThread.start();
    chatClient = new ChatClient();
    chatClient.socket = new Socket("localhost", port);
    chatClient.out = new DataOutputStream(chatClient.socket.getOutputStream());
    chatClient.in = new DataInputStream(chatClient.socket.getInputStream());
    chatClient.userName = "a";
  }

  @AfterEach
  public void tearDown() throws IOException {
    if (serverSocket != null && !serverSocket.isClosed()) {
      serverSocket.close();
    }
  }

  @Test
  public void testMainWithInvalidArguments() throws IOException {
    // Redirect System.out to capture the console output
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));

    // Simulate passing invalid number of arguments
    String[] args = {"127.0.0.1"}; // Only one argument instead of two
    ChatClient.main(args);

    // Check if the error message is printed to the console
    String expectedOutput = "Please provide the Ip address and port of the server you want to connect to";
    assertEquals(expectedOutput, outContent.toString().trim());

    // Reset System.out after the test
    System.setOut(System.out);
  }




  @Test
  void testRecieveDirectMassage() throws IOException {
    String sendUserName = "sender";
    String recipientName = "receiver";
    String message = "Hello";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeChar(' ');
    dos.writeInt(sendUserName.length());
    dos.writeChar(' ');
    dos.write(sendUserName.getBytes(StandardCharsets.UTF_8));
    dos.writeChar(' ');
    dos.writeInt(recipientName.length());
    dos.writeChar(' ');
    dos.write(recipientName.getBytes(StandardCharsets.UTF_8));
    dos.writeChar(' ');
    dos.writeInt(message.length());
    dos.writeChar(' ');
    dos.write(message.getBytes(StandardCharsets.UTF_8));
    byte[] data = baos.toByteArray();
    chatClient.in = new DataInputStream(new ByteArrayInputStream(data));
    chatClient.recieveDirectMassage();
  }

  @Test
  void testRecieveBroadcast() throws IOException {
    String sendUserName = "Sender";
    String message = "Hello, this is a broadcast message";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeChar(' ');  // 模拟 in.readChar();
    dos.writeInt(sendUserName.length());
    dos.writeChar(' ');
    dos.write(sendUserName.getBytes(StandardCharsets.UTF_8));
    dos.writeChar(' ');
    dos.writeInt(message.length());
    dos.writeChar(' ');
    dos.write(message.getBytes(StandardCharsets.UTF_8));

    byte[] data = baos.toByteArray();
    chatClient.in = new DataInputStream(new ByteArrayInputStream(data));

    chatClient.recieveBroadcast();
  }

  @Test
  void testRecieveFailedMessage() throws IOException {
    String failedMessage = "Error: Failed to process the request";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeChar(' ');  // 模拟 in.readChar();
    dos.writeInt(failedMessage.length());
    dos.writeChar(' ');
    dos.write(failedMessage.getBytes(StandardCharsets.UTF_8));

    byte[] data = baos.toByteArray();
    chatClient.in = new DataInputStream(new ByteArrayInputStream(data));

    chatClient.recieveFailedMessage();
  }


  @Test
  void testSendDirectMessage() {
    assertDoesNotThrow(() -> chatClient.sendDirectMessage("recipient", "message"));
  }

  @Test
  void testSendInsultMessage() {
    assertDoesNotThrow(() -> chatClient.sendInsultMessage("!recipient"));
  }

  @Test
  void testSendBroadcastMessage() {
    assertDoesNotThrow(() -> chatClient.sendBroadcastMessage("message"));
  }

  @Test
  void testSendQueryConnectedUsersMessage() {
    assertDoesNotThrow(() -> chatClient.sendQueryConnectedUsersMessage());
  }


  @Test
  void testSendDisconnectMessage() throws IOException {

    assertDoesNotThrow(() -> chatClient.sendDisconnectMessage());
  }



  @Test
  void testEqualsWithSameObject() {
    ChatClient client1 = new ChatClient();
    assertTrue(client1.equals(client1), "A client should be equal to itself");
  }



  @Test
  void testHashCodeConsistency() {
    ChatClient client = new ChatClient();
    int hashCode1 = client.hashCode();
    int hashCode2 = client.hashCode();
    assertEquals(hashCode1, hashCode2, "Hash code should be consistent");
  }


  @Test
  void testEqualsWithDifferentObject() {
    ChatClient client1 = new ChatClient();
    ChatClient client2 = new ChatClient();
    assertTrue(client1.equals(client2));
  }

  @Test
  void testEqualsWithNull() {
    ChatClient client = new ChatClient();
    assertFalse(client.equals(null), "A client should not be equal to null");
  }

  @Test
  void testEqualsWithDifferentClass() {
    ChatClient client = new ChatClient();
    Object obj = new Object();
    assertFalse(client.equals(obj), "A client should not be equal to an object of a different class");
  }

  @Test
  void testSendInitialConnectionMessage() throws IOException {
    assertDoesNotThrow(() -> chatClient.sendInitialConnectionMessage("sss"));
  }


  @Test
  void testResponseQuery() throws IOException {
    // 创建模拟的输入数据
    String[] users = {"User1", "User2", "User3"};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeChar(' ');
    dos.writeInt(users.length);
    for (String user : users) {
      dos.writeChar(' ');
      dos.writeInt(user.length());
      dos.writeChar(' ');
      dos.write(user.getBytes(StandardCharsets.UTF_8));
    }

    byte[] data = baos.toByteArray();
    chatClient.in = new DataInputStream(new ByteArrayInputStream(data));

    chatClient.responseQuery();
  }
  @Test
  void testResponseDisconnect() throws IOException {
    String disconnectMessage = "Disconnected successfully";
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeChar(' ');
    dos.writeBoolean(false);
    dos.writeChar(' ');
    dos.writeInt(disconnectMessage.length());
    dos.writeChar(' ');
    dos.write(disconnectMessage.getBytes(StandardCharsets.UTF_8));

    byte[] data = baos.toByteArray();
    chatClient.in = new DataInputStream(new ByteArrayInputStream(data));

    chatClient.responseDisconnect();


  }

  @Test
  void testReceiveConnectionResponseSuccess() throws IOException {
    String responseMessage = "Connection successful";
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(0);
    dos.writeChar(' ');
    dos.writeBoolean(true);
    dos.writeChar(' ');
    dos.writeInt(responseMessage.length());
    dos.writeChar(' ');
    dos.write(responseMessage.getBytes(StandardCharsets.UTF_8));

    byte[] data = baos.toByteArray();
    chatClient.in = new DataInputStream(new ByteArrayInputStream(data));
    boolean result = chatClient.receiveConnectionResponse();
    assertTrue(result, "The method should return true for a successful connection");
  }

  @Test
  void testCloseResources() {
    assertDoesNotThrow(() -> chatClient.closeResources());
    assertThrows(IOException.class, () -> chatClient.out.write(0), "OutputStream should be closed");
    assertThrows(IOException.class, () -> chatClient.in.read(), "InputStream should be closed");
    assertTrue(chatClient.socket.isClosed(), "Socket should be closed");
  }
}