package Utils;

import Utils.MessageType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageTypeTest {

  @Test
  void testGetValue() {
    assertEquals(19, MessageType.CONNECT_MESSAGE.getValue(), "Incorrect value for CONNECT_MESSAGE");
    assertEquals(20, MessageType.CONNECT_RESPONSE.getValue(), "Incorrect value for CONNECT_RESPONSE");
  }

  @Test
  void testFromValue() {
    assertEquals(MessageType.CONNECT_MESSAGE, MessageType.fromValue(19), "Incorrect MessageType for value 19");
    assertEquals(MessageType.CONNECT_RESPONSE, MessageType.fromValue(20), "Incorrect MessageType for value 20");
  }

  @Test
  void testFromValueWithInvalidValue() {
    int invalidValue = 999;
    assertThrows(IllegalArgumentException.class, () -> MessageType.fromValue(invalidValue),
        "Should throw IllegalArgumentException for unknown value");
  }
}
