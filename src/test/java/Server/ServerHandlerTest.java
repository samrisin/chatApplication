package Server;

import static Server.ChatServer.clients;
import static org.junit.jupiter.api.Assertions.*;


import InsultGenerator.RandomInsultGenerator;
import Server.ChatServer;
import Server.ServerHandler;
import Utils.MessageType;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.chrono.IsoChronology;

class ServerHandlerTest {
    private ChatServer chatServer;
    private ServerHandler serverHandler;
    private DataOutputStream clientOut;
    private DataInputStream clientIn;
    private Thread serverThread;
    public Socket socket;
    private final char SEPERATOR = ' ';

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        chatServer = new ChatServer();
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        serverThread = new Thread(() -> {
            try {
                serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        Socket s =  new Socket("localhost", port);
        serverHandler = new ServerHandler(s, chatServer);

        // 连接客户端
        clientOut = serverHandler.out;
        clientIn = serverHandler.in;
        serverHandler.userName = "hao";
    }

//    @AfterEach
//    void tearDown() {
////        serverHandler.closeResources();
//    }




    @Test
    void testEquals() {
        ServerHandler serverHandler = new ServerHandler(new Socket(),new ChatServer());
        ServerHandler serverHandler1 = new ServerHandler(new Socket(),new ChatServer());
        Assertions.assertNotEquals(serverHandler1,serverHandler);
        Assertions.assertNotEquals(serverHandler1,null);
        Assertions.assertEquals(serverHandler,serverHandler);
        Assertions.assertNotEquals(serverHandler1,new Object());
    }

    @Test
    void testHashCode() {
        ServerHandler serverHandler = new ServerHandler(new Socket(),new ChatServer());
        Assertions.assertEquals(serverHandler.hashCode(),serverHandler.hashCode());

    }
    @Test
    void sendConnectedResponse(){
        assertDoesNotThrow(()->serverHandler.sendConnectResponse(true,"Connected"));
    }


    @Test
    void sendFailedMessage() {
        assertDoesNotThrow(() -> serverHandler.sendFailedMessage("You are as useless as corners in a circle"));
    }
    @Test
    void sendQueryConnectedUsers() {

        assertDoesNotThrow(() -> serverHandler.sendQueryConnectedUsers());
    }
    @Test
    void sendDirectMessage() {

        clients.put("a",serverHandler);
        clients.put("b",serverHandler);
        assertDoesNotThrow(() -> serverHandler.sendDirectMessage("a","a","sada"));
    }

    @Test
    void testSendBroadcastMsg()  {
        Assertions.assertDoesNotThrow(()->serverHandler.sendBroadcastMsg("sdsds"));

    }

    @Test
    void testSendInsulMsg() throws IOException {
        Assertions.assertDoesNotThrow(()->serverHandler.sendInsult("You moron!"));

    }

    @Test
    void testsendQueryConnectedUsers() throws IOException {
        Assertions.assertDoesNotThrow(()->serverHandler.sendQueryConnectedUsers());
    }

    @Test
    void testsendConnectResponse() throws IOException {
        Assertions.assertDoesNotThrow(()->serverHandler.sendConnectResponse(true,"sdsd"));
    }

    @Test
    void testprocessOtherRequest27() throws IOException {
        String input = "@pan";
        String sentence = "aaaa";
        String recipientName = input.substring(1).trim();
        MessageType messageType = MessageType.SEND_INSULT;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt( "hao".length()); // Length of username
        dos.writeChar(' ');
        dos.write( "hao".getBytes(StandardCharsets.UTF_8));
        dos.writeChar(' ');
        dos.writeInt(recipientName.length()); // Length of recipientName
        dos.writeChar(' ');
        dos.write(recipientName.getBytes(StandardCharsets.UTF_8));
        dos.writeChar(' ');
        dos.writeInt(sentence.length());
        dos.writeChar(' ');
        dos.write(sentence.getBytes(StandardCharsets.UTF_8));
        byte[] data = baos.toByteArray();
        serverHandler.in = new DataInputStream(new ByteArrayInputStream(data));
        Assertions.assertDoesNotThrow(()->serverHandler.processOtherRequest(27));





//        Assertions.assertDoesNotThrow(()->serverHandler.processOtherRequest(24));
//
//
//
//        Assertions.assertDoesNotThrow(()->serverHandler.processOtherRequest(21));


    }

    @Test
    void testprocessOtherRequest25() throws IOException {

        String input = "@pan";
        String sentence = "aaaa";
        String recipientName = input.substring(1).trim();
        MessageType messageType = MessageType.SEND_INSULT;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt( "hao".length()); // Length of username
        dos.writeChar(' ');
        dos.write( "hao".getBytes(StandardCharsets.UTF_8));
        dos.writeChar(' ');
        dos.writeInt(recipientName.length()); // Length of recipientName
        dos.writeChar(' ');
        dos.write(recipientName.getBytes(StandardCharsets.UTF_8));
        dos.writeChar(' ');
        dos.writeInt(sentence.length());
        dos.writeChar(' ');
        dos.write(sentence.getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();
        serverHandler.in = new DataInputStream(new ByteArrayInputStream(data));

        Assertions.assertDoesNotThrow(()->serverHandler.processOtherRequest(25));

    }



    @Test
    void testprocessOtherRequest22() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt( "hao".length()); // Length of username
        dos.writeChar(' ');
        dos.write( "hao".getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();
        serverHandler.in = new DataInputStream(new ByteArrayInputStream(data));

        Assertions.assertDoesNotThrow(()->serverHandler.processOtherRequest(22));

    }

    @Test
    void testprocessOtherRequest24() throws IOException {

        String input = "@pan";
        String sentence = "aaaa";
        String recipientName = input.substring(1).trim();
        MessageType messageType = MessageType.SEND_INSULT;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt( "hao".length()); // Length of username
        dos.writeChar(' ');
        dos.write( "hao".getBytes(StandardCharsets.UTF_8));
        dos.writeChar(' ');
        dos.writeInt(sentence.length());
        dos.write(sentence.getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();
        serverHandler.in = new DataInputStream(new ByteArrayInputStream(data));

        Assertions.assertDoesNotThrow(()->serverHandler.processOtherRequest(24));

    }

    @Test
    void testprocessOtherRequest21() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt( "hao".length()); // Length of username
        dos.writeChar(' ');
        dos.write( "hao".getBytes(StandardCharsets.UTF_8));

        byte[] data = baos.toByteArray();
        serverHandler.in = new DataInputStream(new ByteArrayInputStream(data));
        Assertions.assertDoesNotThrow(()->serverHandler.processOtherRequest(21));
    }




}

