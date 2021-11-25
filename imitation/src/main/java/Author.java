
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * @author zeil
 *
 */
public class Author {

    private Vocabulary vocabulary;
    private Random rand;
    private String generatedText;

    /**
     * Construct an author object to simulate the vocabulary usage
     * indicated by a sample text file.
     *  
     * @param inputFile
     * @throws FileNotFoundException 
     */
    public Author(File inputFile) throws FileNotFoundException {
        vocabulary = new Vocabulary();
        rand = new Random();
        generatedText = "";
        Scanner input = new Scanner (new BufferedReader(new FileReader(inputFile)));
        String lastWord = "";
        while (input.hasNext()) {
            String word = input.next();
            if (lastWord.equals("")) {
                vocabulary.addStartingWord (word);
            } else {
                vocabulary.addWordPair (lastWord, word);
            }
            char finalCharacter = word.charAt(word.length()-1);
            if (finalCharacter == '.' || finalCharacter == ';' || finalCharacter == '?' || finalCharacter == '!') {
                lastWord = "";
            } else {
                lastWord = word;
            }
        }
    }

    /**
     * Main program takes two command-line parameters.
     * The first is the name of an input file.
     * The second is the number of words of output to generate.
     * 
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 2) {
            System.err.println ("Usage: java imitation.Author inputFileName numberOfWordsDesired");
            System.exit(-1);
        }
        File inputFile = new File(args[0]);
        int numWords = Integer.parseInt(args[1]);
        Author au = new Author(inputFile);
        au.generateText (numWords);
        System.out.print (au.formatText());
    }

    private void generateText(int numWords) {
        String lastWord = "";
        StringBuffer results = new StringBuffer();
        for (int i = 0; i < numWords; ++i) {
            List<String> choices = null;
            if (lastWord.equals("")) {
                choices = vocabulary.getStartingWords();
            } else {
                choices = vocabulary.getWordsThatCanFollow(lastWord);
                if (choices.size() == 0) {
                    choices = vocabulary.getStartingWords();
                }
            }
            int k = rand.nextInt(choices.size());
            String word = choices.get(k);
            if (results.length() > 0) {
                results.append(' ');                
            }
            results.append(word);
            char finalCharacter = word.charAt(word.length()-1);
            if (finalCharacter == '.' || finalCharacter == ';' || finalCharacter == '?' || finalCharacter == '!') {
                lastWord = "";
            } else {
                lastWord = word;
            }
        }
        generatedText = results.toString();
    }

    private final int LineLimit = 60;

    private String formatText() {
        StringBuffer results = new StringBuffer();
        int lineLen = 0;
        Scanner text = new Scanner(generatedText);
        while (text.hasNext()) {
            String word = text.next();
            if (lineLen > 0 && word.length() + lineLen + 1 > LineLimit) {
                results.append("\n");
                lineLen = 0;
            }
            char finalCharacter = word.charAt(word.length()-1);
            char firstCharacter = word.charAt(0);
            if (finalCharacter == ':' && Character.isUpperCase(firstCharacter)) {
                results.append("\n");
                lineLen = 0;                
            }
            if (lineLen > 0) {
                results.append(' ');
                ++lineLen;
            }
            results.append (word);
            lineLen += word.length();
            if (finalCharacter == '.' || finalCharacter == ';' || finalCharacter == '?' || finalCharacter == '!') {
                results.append("\n");
                lineLen = 0;                      
            }
        }
        if (lineLen > 0) {
            results.append("\n");
        }
        text.close();
        return results.toString();
    }

}
