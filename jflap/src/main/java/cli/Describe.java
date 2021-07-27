/**
 * 
 */
package cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import automata.Automaton;
import automata.AutomatonSimulator;
import automata.SimulatorFactory;
import automata.State;
import automata.Transition;
import automata.fsa.FSANondeterminismDetector;
import automata.fsa.FiniteStateAutomaton;
import automata.pda.PushdownAutomaton;
import automata.turing.TMSimulator;
import automata.turing.Tape;
import automata.turing.TuringMachine;
import automata.vdg.VariableDependencyGraph;
import file.ParseException;
import file.XMLCodec;
import grammar.CNFConverter;
import grammar.Grammar;
import grammar.GrammarChecker;
import grammar.Production;
import grammar.ProductionChecker;
import grammar.UnboundGrammar;
import grammar.UnitProductionRemover;
import grammar.UnrestrictedGrammar;
import grammar.UselessProductionRemover;
import grammar.parse.CYKParser;
import regular.RegularExpression;

/**
 * @author zeil
 *
 */
public class Describe {

    /**
     * Usage:
     *   java [options] cli.RunDescribe jffFile
     *   
     * Describes the automaton contained in a jffFile.
     * First line of output indicates the type of automaton.
     * Subsequent lines list properties of the automaton.
     * 
     * First line possibilities:
     *     Finite Automaton
     *     Regular Expression
     *     Grammar
     *     Push-Down Automaton
     *     Turing Machine
     *     
     * Subsequent lines may contain:
     *     empty
     *     invalid
     *     deterministic
     *     CFG
     *     CNF
     *     
     * empty=no states, productions, or characters (regular expr)
     * invalid=regular expression/grammar cannot be parsed, automaton has no initial state
     * deterministic: (finite automata only)
     * CFL: grammar is syntactically valid CFL
     * CNF: CFL grammar is Chomsky Normal Form
     * 
     * @param args command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args.length > 2) {
            System.err.println(
                    "Usage:\n" +
                            "   java [options] cli.RunBatch jffFile < inputStrings\n" +
                            " or\n" +
                    "   java [options] cli.RunBatch jffFile inputFiles\n");
            System.exit(-1);
        }
        new Describe().runProgram(args);
    }

    public void runProgram(String[] args) throws IOException {
        CLI.launchedByCLI = true;
        File jffFile = new File(args[0]);

        Serializable ser;
        try {
            ser = new XMLCodec().decode(jffFile, null);
        } catch (ParseException ex) {
            String explain = ex.getMessage();
            if (explain.contains("Regular expression structure has no expression")) {
                System.out.println("Regular Expression\nempty");
            }
            ser = null;
        }
        if (ser != null) {
            if (ser instanceof Automaton) {
                Automaton automaton = (Automaton)ser;
                System.out.println(describe (automaton));
            } else if (ser instanceof UnboundGrammar) {
                UnboundGrammar ug = (UnboundGrammar)ser;
                System.out.println(describe (ug));
            } else {
                RegularExpression regex = (RegularExpression)ser;
                System.out.println(describe(regex));
            }
        }

    }   

    public String describe(RegularExpression regex) {
        String result = "Regular Expression";
        try {
            String regex0 = regex.asCheckedString();
            if (regex0.length() == 0) {
                result = result + "\nempty";
            } else {
                regex0 = regex0.replace('+', '|'); // Java uses| for alternation
                regex0 = regex0.replaceAll("!", "\\(.{0}\\)"); 
                Pattern.compile(regex0);
            }
        } catch (PatternSyntaxException ex) {
            result = result + "\ninvalid";
        } catch (UnsupportedOperationException ex) {
            result = result + "\ninvalid";
        }
        return result;
    }

    public String describe(UnboundGrammar ug) {
        String result = "Grammar";
        if (ug.getProductions().length == 0) {
            result = result + "\nempty";
        } else {
            GrammarChecker checker = new GrammarChecker();
            if (checker.isContextFreeGrammar(ug)) {
                result = result + "\nCFG";
                boolean isCNF = true;
                for (Production p: ug.getProductions()) {
                    String rhs = p.getRHS();
                    if (rhs.length() == 0) {
                        isCNF = false;
                        break;
                    } else if (rhs.length() == 1) {
                        // Must be a terminal
                        char symbol = rhs.charAt(0);
                        if (isAVariable(symbol)) {
                            isCNF = false;
                            break;                            
                        }
                    } else if (rhs.length() == 2) {
                        // Both nust be nonterminals
                        char symbol1 = rhs.charAt(0);
                        char symbol2 = rhs.charAt(1);
                        if (!(isAVariable(symbol1) && isAVariable(symbol2))) {
                            isCNF = false;
                            break;                            
                        }
                    } else {
                        isCNF = false;
                        break;
                    }
                }
                if (isCNF) {
                    result = result + "\nCNF";
                }
            }
        }
        return result;
    }

    private boolean isAVariable(char symbol) {
        return Character.isLetter(symbol) && Character.isUpperCase(symbol);
    }

    public String describe(Automaton automaton) {
        String result = "";
        if (automaton instanceof FiniteStateAutomaton) {
            result = "Finite Automaton";
        } else if (automaton instanceof PushdownAutomaton) {
            result = "Pushdown Automaton";
        } else if (automaton instanceof TuringMachine) {
            result = "Turing Machine";            
        }
        if (automaton.getStates().length == 0) {
            result = result + "\nempty";
        } else if (automaton.getInitialState() == null) {
            result = result + "\ninvalid";
        } else if (automaton instanceof FiniteStateAutomaton) {
            FiniteStateAutomaton fsa = (FiniteStateAutomaton)automaton;
            if (fsa.getStates().length > 0 && !isNondetermistic(fsa)) {
                result = result + "\ndeterministic";
            }            
        }
        return result;
    }

    private boolean isNondetermistic(FiniteStateAutomaton fsa) {
        FSANondeterminismDetector detector = new FSANondeterminismDetector();
        int nondetStates = detector.getNondeterministicStates(fsa).length;
        return nondetStates > 0;
    }

    private void simulate(RegularExpression regex, BufferedReader input) throws IOException {
        // JFlap doesn't apply regular expressions directly (you have to
        // convert to a FA, so we'll roll our own.
        try {
            String regex0 = regex.asCheckedString();
            regex0 = regex0.replace('+', '|'); // Java uses| for alternation
            regex0 = regex0.replaceAll("!", "\\(.{0}\\)"); // Java has no 
            // easy symbol for empty
            // string.
            String line = input.readLine();
            while (line != null) {
                boolean accepted = line.matches(regex0);
                String result = (accepted) ? "accepted" : "rejected";
                System.out.println("\"" + line + "\"\t" + result);
                line = input.readLine();
            }
        } catch (UnsupportedOperationException ex) {
            String line = input.readLine();
            String result = "invalid regular expression: " + ex.getMessage();
            System.out.println("\"" + line + "\"\t" + result);
        }

    }

    private void simulate(
            Automaton automaton, 
            BufferedReader input) throws IOException {
        AutomatonSimulator sim = SimulatorFactory.getSimulator(automaton);
        if (sim == null) throw new RuntimeException("Cannot load an automaton simulator for " + automaton.getClass());
        String line = input.readLine();
        while (line != null) {
            try {
                boolean accepted = sim.simulateInput(line);
                String result = (accepted) ? "accepted" : "rejected";
                if (accepted && (sim instanceof TMSimulator)) {
                    if (CLI.finalTapes.length > 1) {
                        System.out.println("\"" + line + "\"\t" + result);
                        for (int i = 0; i < CLI.finalTapes.length; ++i) {
                            Tape tape = CLI.finalTapes[i];
                            System.out.println("  tape " + i + ": " + tape);
                        }
                    } else {
                        Tape tape = CLI.finalTapes[0];
                        System.out.println("\"" + line + "\"\t" + result
                                + ": " + tape);
                    }
                } else {
                    System.out.println("\"" + line + "\"\t" + result);
                }
            } catch (cli.RunTimeExceeded ex) {
                System.out.println("\"" + line + "\"\tdid not halt after " + 
                        cli.CLI.TransitionLimit + " transitions");
            }
            line = input.readLine();
        }

    }



    private void simulate(
            Grammar grammar, 
            BufferedReader input) throws IOException {
        if (GrammarChecker.isContextFreeGrammar(grammar)) {
            if (grammar.getTerminals().length==0)
            {
                System.out.println ("*** This grammar does not accept any strings.***");
                return;
            }
            CNFConversion converter = new CNFConversion();
            boolean acceptsEmptyString 
            = converter.grammarAcceptsEmptyString(grammar);
            Grammar cnf = converter.convert(grammar);            

            CYKParser parser = new CYKParser(cnf);
            String line = input.readLine();
            while (line != null) {
                try {
                    boolean accepted = acceptsEmptyString;
                    if (line.length() > 0) {
                        accepted = parser.solve(line);
                    }
                    String result = (accepted) ? "accepted" : "rejected";
                    System.out.println("\"" + line + "\"\t" + result);
                } catch (cli.RunTimeExceeded ex) {
                    System.out.println("\"" + line + "\"\tdid not halt after " + 
                            cli.CLI.TransitionLimit + " transitions");
                }
                line = input.readLine();
            }

        } else {
            System.out.println("*** This grammar is not context-free. ***");
        }
    }


    // Begin code lightly modified from JFlap CYKParseAction
    /**
     * Method for getting rid of unit productions
     * @param env Our grammar environment
     * @param g Grammar in transformation
     */
    private Grammar hypothesizeUnit(Grammar g) {
        UnitProductionRemover remover = new UnitProductionRemover();
        VariableDependencyGraph vdg = new VariableDependencyGraph();
        HashSet vdgTransitions = new HashSet();
        remover.initializeDependencyGraph(vdg, g);
        // Cache the transitions we have to add.
        Production[] p = g.getProductions();
        for (int i = 0; i < p.length; i++)
            if (ProductionChecker.isUnitProduction(p[i]))
                vdgTransitions.add(remover.getTransitionForUnitProduction(
                        p[i], vdg));
        Grammar noUnits = remover.getUnitProductionlessGrammar(g, vdg);
        noUnits.setStartVariable(g.getStartVariable());
        return hypothesizeUseless(noUnits);
    }

    /**
     * Method for getting rid of useless productions
     * @param env Our grammar environment
     * @param g Grammar in transformation
     */
    protected Grammar hypothesizeUseless(Grammar g) {
        UselessProductionRemover remover = new UselessProductionRemover();

        Grammar g2 = UselessProductionRemover
                .getUselessProductionlessGrammar(g);
        g2.setStartVariable(g.getStartVariable());
        if (g2.getTerminals().length==0)
        {
            System.out.println("*** This grammar does not accept any strings.***");
            return null;
        }
        return hypothesizeChomsky(g2);
    }

    /**
     * Method for finalizing Chomsky form
     * @param env Our grammar environment
     * @param g Grammar in transformation
     */
    protected Grammar hypothesizeChomsky(Grammar g) {
        //System.out.println("Chomsky TIME");

        CNFConverter converter = null;
        try {
            converter = new CNFConverter(g);
        } catch (IllegalArgumentException e) {
            System.out.println("*** Illegal Grammar ***");
            return null;
        }
        Production[] p = g.getProductions();
        boolean chomsky = true;
        for (int i = 0; i < p.length; i++)
            chomsky &= converter.isChomsky(p[i]);

        if (!chomsky) {
            ArrayList <Production> resultList=new ArrayList <Production>();
            for (int i=0; i<p.length; i++)
            {
                ArrayList <Production> myTempCNF=new ArrayList <Production>();
                converter = new CNFConverter(g);
                convertToCNF(converter, p[i], myTempCNF);
                resultList.addAll(myTempCNF);
            }
            Production[] pp=new Production[resultList.size()];
            for (int i=0; i<pp.length; i++)
            {
                pp[i]=resultList.get(i);
            }
            pp=converter.convert(pp);
            String var=g.getStartVariable();
            g=new UnrestrictedGrammar();
            g.addProductions(pp);
            g.setStartVariable(var);

        }
        return g;
    }

    private void convertToCNF(CNFConverter converter, Production p, 
            List<Production> converted)
    {
        if (!converter.isChomsky(p))
        {
            Production temp[]=converter.replacements(p);
            for (int j=0; j<temp.length; j++)
            {
                p=temp[j];
                convertToCNF(converter, p, converted);
            }
        }   
        else
            converted.add(p);
    }
}
