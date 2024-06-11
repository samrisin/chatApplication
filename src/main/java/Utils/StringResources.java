package Utils;

/**
 * StringResources holds a collection of constant string values used throughout the chat application.
 * These constants primarily include messages displayed to the user and error messages.
 */
public class StringResources {

/** Prompt for entering a username */
  public  final String ENTER_USERNAME = "Enter your username:  ";
  /**Error message prefix used when there's an error in closing resources */
  public  final String ERROR_CLOSE_PREFIX = "Error while closing resources: ";
  /**Error message displayed when client arguments are incorrect or missing */
  public  final String ERROR_ClLIENTS_AGRS = "Please provide the Ip address and port of the server you want to connect to ";
  /** Message indicating the connection has been closed */
  public  final String CONNECT_CLOSED = "Connection is closed!";
  /**Error message when no users are found in the chat */
  public  final String ERROR_QUERY_USEWR = "No user is in chat!";
  /**Welcome message for users when they join the chat application */
  public  final String WELLCOME_MESSAGRE = "Welcome to the Chat Application!,Type '?' to see available commands.";
  /** Instructions for various commands available in the chat application */

  public  final String COMMAND_TEXt = """
    • logoff: sends a DISCONNECT_MESSAGE to the server
    • who: sends a QUERY_CONNECTED_USERS to the server
    • @user: sends a DIRECT_MESSAGE to the specified user to the server
    • @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected
    • !user: sends a SEND_INSULT message to the server, to be sent to the specified user
    """;
  /**Error message when no message is provided for broadcast */
  public  final String ERROR_BROADCAST = "No message provided for broadcast.";
  /**Error message for incorrect direct message format */
  public  final String ERROR_DIRRECTOR = "Invalid direct message format. Usage: @username message";
  /** Error message when the connection response indicates the username already exists*/

  public  final String Error_CONNECT_RESPONSE = "This user already exists";
  /**Error message when the specified user does not exist */

  public  final String REEOR_NONEUER_MESSAGE = "When the user does not exist, no other instructions are processed";
  /** Confirmation message when a user is successfully disconnected */
  public  final String TRUE_DISCONNCETED_MESSAGE = "You are no longer connected.";
  /**Error message when the username does not match */

  public  final String ERROR_NAME_NOTMACH = "UserName doesn't match!";
  /**Error message when the sender's username does not match */
  public  final String ERROR_SENDUSERNAME_NOTMACH = "SendUserName doesn't match!";
  /** Error message when the recipient's username does not match*/
  public  final String ERROR_RECIPIENTUSERNAME_NOTMACH = "Recipient UserName doesn't match!";
  /** Server port broadcast Message  */
  public final String CHAT_SERVER_PORT_MSG = "Chat Server is listening on port ";
  /**New Client Connected Message */
  public final String NEW_CLIENT_CONN = "New Client Connected ";
  /**Server Interrupted Error */
  public final String SERVER_INTRUPT = "Server interrupted: ";
  /**Prompt to enter a correct command */
  public  final String COMMAND = "Please enter the correct command and ensure that it is in the above command!";
}
