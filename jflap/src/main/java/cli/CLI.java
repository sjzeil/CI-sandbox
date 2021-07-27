/**
 * 
 */
package cli;

import automata.turing.Tape;

/**
 * Provides a boolean flag used to suppress pop-up dialogs
 * when being run as a non-interactiove command-line tool.
 *  
 * @author zeil
 *
 */
public class CLI {
    /**
     * Will be set to true by CLI main() drivers only. 
     */
    public static boolean launchedByCLI = false;
    
    /**
     * After a successful TM simulation, the final tapes are stored here.
     */
    public static Tape[] finalTapes;
    
    /**
     * Limit on number of Turing machine transition that will be
     * simulated before giving up.
     */
    public static final int TransitionLimit = 5000;


}
