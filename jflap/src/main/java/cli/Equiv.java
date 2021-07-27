/**
 * 
 */
package cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import automata.Automaton;
import automata.AutomatonSimulator;
import automata.SimulatorFactory;
import automata.fsa.FiniteStateAutomaton;
import automata.graph.FSAEqualityChecker;
import automata.turing.TMSimulator;
import automata.turing.Tape;
import automata.turing.TuringMachine;
import file.XMLCodec;
import regular.RegularExpression;

/**
 * @author zeil
 *
 */
public class Equiv {

    /**
     * Usage:
     *   java [options] cli.Equiv jffFile1 jffFile2
     *   
     * Checks to see if two FA are equivalent.
     * 
     * @param args command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 2) {
            System.err.println(
                    "Usage:\n" +
                            "   java [options] cli.Equiv jffFile1 jffFile2");
            System.exit(-1);
        }
        new Equiv().runProgram(args);
    }

    public void runProgram(String[] args) throws IOException {
        CLI.launchedByCLI = true;
        File jffFile1 = new File(args[0]);
        File jffFile2 = new File(args[1]);

        FiniteStateAutomaton fa1 
            = (FiniteStateAutomaton)new XMLCodec().decode(jffFile1, null);
        FiniteStateAutomaton fa2 
            = (FiniteStateAutomaton)new XMLCodec().decode(jffFile2, null);

        boolean equiv = new FSAEqualityChecker().equals(fa1, fa2); 

        if (equiv)
            System.out.println("The automata are equivalent.");
        else
            System.out.println("The automata are different.");
    }   

}
