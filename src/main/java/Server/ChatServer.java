package Server;

import Utils.StringResources;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ChatServer class that sets up a multi-client chat server.
 * It listens for connections on a specified port and handles multiple clients up to a maximum limit.
 */
public class ChatServer {
  private int PORT;
  private static final int MAX_CLIENTS = 10;
  private ServerSocket serverSocket;
  /** Concurrent Hashmap to keep records*/
  public static final   Map<String, ServerHandler> clients = new ConcurrentHashMap<>();
  /** Atomic client count variable*/
  public static  AtomicInteger clientCount = new AtomicInteger(0);
  StringResources stringResources = new StringResources();



  /**
   * Main method to start the Chat Server.
   *
   * @param args Command line arguments (not used in this implementation).
   * @throws IOException If an I/O error occurs while opening the server socket.
   */
  public static void main(String[] args) throws Exception {
    new ChatServer().startServer();
  }

  /**
   * Starts the server to listen for incoming client connections.
   * Accepts new connections if the current number of clients is below the maximum limit.
   *
   * @throws IOException If an I/O error occurs while waiting for a connection.
   */
  public void startServer() throws Exception {
    //creates a ServerSocket object and binds it to an available port on the local machine.
    ServerSocket serverSocket = new ServerSocket(0);
    PORT = serverSocket.getLocalPort();
    System.out.println(stringResources.CHAT_SERVER_PORT_MSG + PORT);

    while (true) {
      if (clientCount.get() < MAX_CLIENTS) {
        Socket socket = serverSocket.accept();
        System.out.println(stringResources.NEW_CLIENT_CONN);
        clientCount.incrementAndGet();
        new Thread(new ServerHandler(socket, this)).start();
      } else {
        throw new InterruptedException();
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
    if (!(o instanceof ChatServer that)) {
      return false;
    }
    return Objects.equals(serverSocket, that.serverSocket);
  }
  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(serverSocket);
  }
}
