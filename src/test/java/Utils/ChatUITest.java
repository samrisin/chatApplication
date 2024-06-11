package Utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatUITest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  public void setUp() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  public void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  public void testDisplayMessage() {
    ChatUI ui = new ChatUI();
    String testMessage = "Test Message";
    ui.displayMessage(testMessage);
    assertEquals(testMessage + System.lineSeparator(), outContent.toString());
  }

  @Test
  public void testEqualsAndHashCode() {
    ChatUI ui1 = new ChatUI();
    ChatUI ui2 = new ChatUI();

    assertFalse(ui1.equals(ui2));
    assertNotEquals(ui1.hashCode(), ui2.hashCode());
  }
}