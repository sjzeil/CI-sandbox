
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


/**
 * @author zeil
 *
 */
public class Vocabulary {
    
    /**
     * Create a new vocabulary object with empty lists of
     * starting words and no word pairs.
     */
    public Vocabulary ()
    {
        startingWords = new ArrayList<String>();
        followers = new HashMap<String, ArrayList<String>>();
        nPairs = 0;
    }

    /**
     * Add a starting word occurrence. Duplicate words are permitted and are
     * retained, not discarded.
     * 
     * @param word a word that can begin a new sentence
     */
    public void addStartingWord(String word) {
        startingWords.add(word);
    }

    /**
     * Add a word pair from a document. Duplicate pairs are permitted
     * and are retained, not discarded.
     * 
     * @param lastWord a word that can precede another
     * @param word a word that can follow lastWord
     */
    public void addWordPair(String lastWord, String word) {
        ArrayList<String> follow = followers.get(lastWord);
        if (follow == null) {
            follow = new ArrayList<String>();
            followers.put (lastWord, follow);
        }
        follow.add(word);
        ++nPairs;
    }

    /**
     * Number of word occurrences that can start a  sentence.
     * @return number of sentence-starting words
     */
    public int getNumStartingWords() {
        return startingWords.size();
    }

    /**
     * Number of word pairs (preceding-word, following-word)
     * that have been entered.
     * @return number of work pairs
     */
    public int getNumWordPairs() {
        return nPairs;
    }



    /**
     * Get a list of all known starting words, in the order added.
     * If no starting words have been added, returns a list of length 0.
     * 
     * @return list of starting words
     **/
    public List<String> getStartingWords() {
        return startingWords;
    }

    /**
     * Returns a list of all words that are known to have followed lastWord,
     * in the order added. If no such words exist, returns a list of length 0.
     **/
    public List<String> getWordsThatCanFollow(String lastWord) {
        ArrayList<String> follow = followers.get(lastWord);
        if (follow == null) {
            follow = new ArrayList<String>();
        }
        return follow;
    }
    
    private ArrayList<String> startingWords;
    private HashMap<String, ArrayList<String>> followers;
    private int nPairs;
    

}
