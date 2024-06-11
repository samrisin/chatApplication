package InsultGenerator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RandomInsultGeneratorTest {

  @BeforeEach
  void setUp() {
  }

  @Test
  void testGenerateRandomInsult() {
    RandomInsultGenerator generator = new RandomInsultGenerator();
    Set<String> generatedInsults = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      String insult = generator.generateRandomInsult();
      assertNotNull(insult, "Generated insult should not be null");
      generatedInsults.add(insult);
    }

    assertTrue(generatedInsults.size() > 1, "Should generate different insults");
  }
}