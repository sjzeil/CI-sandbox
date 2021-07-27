/**
 * 
 */
package cli;

/**
 * @author zeil
 *
 */
public class RunTimeExceeded extends RuntimeException {

    /**
     * 
     */
    public RunTimeExceeded() {
        super("Simulator exceeded number of permitted transitions.");
    }

    /**
     * @param arg0
     */
    public RunTimeExceeded(String explanation) {
        super(explanation);
    }


}
