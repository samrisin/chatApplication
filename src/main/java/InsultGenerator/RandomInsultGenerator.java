package InsultGenerator;

import java.util.Random;

/**
 * The RandomInsultGenerator class provides a method to generate random insults.
 * This class utilizes an array of predefined insult strings and randomly selects
 * one to return each time the generateRandomInsult method is called. It's an example
 * of using random number generation to select from a fixed set of options.
 */
public class RandomInsultGenerator {

    /**
     * Generates a random insult from a predefined list.
     * This method uses a random number generator to select an insult
     * from an array of strings.
     *
     * @return A randomly chosen insult as a String.
     */
    public String generateRandomInsult() {
        // An array holding various insult strings.
        // Each element of the array is a different insult.
        String[] insults = {
            "You're as useful as a screen door on a submarine.",
            "If you were any slower, you'd be going backward.",
            "I've seen better decisions made during a game of Rock, Paper, Scissors.",
            "You're not stupid; you just have bad luck thinking.",
            "You're the reason the gene pool needs a lifeguard."
        };

        // Create a new Random object for generating random numbers.
        Random random = new Random();
        // Generate a random index to select an insult from the array.
        int randomIndex = random.nextInt(insults.length);
        // Return the randomly selected insult.
        return insults[randomIndex];
    }
}
