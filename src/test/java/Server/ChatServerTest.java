package Server;

import Server.ChatServer;
import Utils.StringResources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


class ChatServerTest {

    @AfterEach
    void tearDown() {
        // Clean up any resources if needed
    }

    @Test
    void main(){

    }

    private ChatServer chatServer;
    @BeforeEach
    public void setUp() throws IOException {
        chatServer = new ChatServer();


    }
    @Test
    public void testStartServer() throws IOException {
        // Set clientCount to 11 to trigger the else part in startServer
        ChatServer.clientCount = new AtomicInteger(30);

        // Use assertThrows to test the InterruptedException
        Assertions.assertThrows(InterruptedException.class, () -> {
            chatServer.startServer();
        });
    }

    @Test
    void testEquals() {
        ChatServer server1 = new ChatServer();
        ChatServer server2 = new ChatServer();

        assertEquals(server1, server2);
        assertNotEquals(null, server1);
        assertNotEquals(server1, new Object());
    }

    @Test
    void testHashCode() {
        ChatServer server1 = new ChatServer();
        ChatServer server2 = new ChatServer();
        assertEquals(server1,server1);
        assertEquals(server1.hashCode(), server2.hashCode());
    }


}
