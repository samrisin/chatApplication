package Protocol;

import java.io.IOException;

/**
 * Defines the protocol interface for server communication.
 * This interface declares methods necessary for implementing
 * the communication protocol between a client and server.
 */
public interface Protocol {

    /**
     * Defines the server protocol to be implemented.
     * This method should contain the logic for handling
     * the specific protocol used in server communication.
     *
     * @throws IOException If an I/O error occurs during communication.
     */
    void ServerProtocol() throws IOException;
}
