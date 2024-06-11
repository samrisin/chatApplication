package Utils;

/**
 * Enum representing different types of messages in the chat application.
 * Each message type is associated with a unique integer value.
 */
public enum MessageType {
  /** Message type for initiating a connection*/
  CONNECT_MESSAGE(19),
  /**Message type for initiating a connection */
  CONNECT_RESPONSE(20),
  /**Message type for disconnecting */
  DISCONNECT_MESSAGE(21),
  /**Message type for querying connected users */
  QUERY_CONNECTED_USERS(22),
  /**Message type for response to a query */
  QUERY_USER_RESPONSE(23),
  /**Message type for broadcasting a message*/
  BROADCAST_MESSAGE(24),
  /**Message type for direct messaging */
  DIRECT_MESSAGE(25),
   /** Message type for a failed operation or message */
  FAILED_MESSAGE(26),
  /** Message type for sending an insult (use with caution)*/
  SEND_INSULT(27);

  private final int value;     // Numerical value associated with the message type

  /**
   * Constructor for the MessageType enum.
   *
   * @param value The integer value associated with the message type.
   */
  MessageType(int value) {
    this.value = value;
  }

  /**
   * Retrieves the integer value of the message type.
   *
   * @return The integer value of the message type.
   */
  public int getValue() {
    return value;
  }

  /**
   * Converts an integer value to its corresponding MessageType.
   * If the value does not correspond to any MessageType, throws IllegalArgumentException.
   *
   * @param value The integer value to be converted.
   * @return The MessageType corresponding to the given value.
   * @throws IllegalArgumentException if the value does not correspond to a known MessageType.
   */
  public static MessageType fromValue(int value) {
    for (MessageType type : MessageType.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown message type value: " + value);
  }


}
