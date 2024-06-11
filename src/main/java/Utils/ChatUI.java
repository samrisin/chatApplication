package Utils;

import java.util.Objects;
import java.util.Scanner;

/**
 * ChatUI is a class that handles user interface operations for the chat application.
 * It manages user inputs and outputs via the console using a Scanner object.
 */
public class ChatUI {
  private Scanner scanner;

  /**
   * Constructor for ChatUI.
   * Initializes a Scanner object to read user input from the console.
   */
  public ChatUI() {
    this.scanner = new Scanner(System.in);
  }

  /**
   * Prompts the user to enter a username and reads the input.
   *
   * @return The username entered by the user.
   */
  public String getUsernameFromUser() {
    System.out.print(new StringResources().ENTER_USERNAME);
    return scanner.nextLine();
  }
  /**
   * Reads a line of text from the user input.
   *
   * @return The message entered by the user.
   */
  public String getMessageFromUser() {
    return scanner.nextLine();
  }

  /**
   * Displays a message to the user. This method is used for showing
   * any kind of information or feedback to the user in the console.
   *
   * @param message The message to be displayed.
   */
  public void displayMessage(String message) {
    System.out.println(message);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChatUI chatUI)) {
      return false;
    }
    return Objects.equals(scanner, chatUI.scanner);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(scanner);
  }
}
