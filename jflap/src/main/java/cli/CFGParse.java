/**
 * 
 */
package cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import grammar.Grammar;
import grammar.Production;

/**
 * Replacement for Brute-force parse that can be used in a context
 * where it needs to halt eventually.
 * 
 * Currently used in testing CNF conversion, because CYK parser cannot
 * be applied to grammars that have not yet been fully converted to
 * CNF.
 * 
 * @author zeil
 *
 */
public class CFGParse {

	public static final int MAXPARSESTEPS = 128 * 1024;
	
	private class Derivation implements Comparable<Derivation>{
		public Derivation(String derivation, String input) {
			this.derived = derivation;
			target = input;
		}
		
		public String derived;
		public String target;
		
		public String toString() {
			return derived + " => " + target;
		}
		
		public int hashCode() {
			return toString().hashCode();
		}
		
		public boolean equals (Object obj) {
			if (obj instanceof Derivation) {
				Derivation d = (Derivation)obj;
				return derived.equals(d.derived) && target.equals(d.target);
			} else {
				return false;
			}
		}

		@Override
		public int compareTo(Derivation d) {
			if (target.length() < d.target.length() ) {
				return -1;
			} else if (target.length() > d.target.length() ) {
					return 1;
			} else if (derived.length() < d.derived.length() ) {
					return -1;
			} else if (derived.length() > d.derived.length() ) {
				return 1;
			} else {
				return toString().compareTo(d.toString());
			}
		}
	}
	
	private Grammar g; 
	
	
	/**
	 * Create a parser. 
	 * @param grammar    Grammar for the new parser
	 */
	public CFGParse (Grammar grammar) {
		g = grammar;
		productions = new HashMap<>();
		for (Production p: g.getProductions()) {
			if (productions.containsKey(p.getLHS())) {
				productions.get(p.getLHS()).add(p);
			} else {
				List<Production> list = new ArrayList<Production>();
				list.add(p);
				productions.put(p.getLHS(), list);
			}
		}
		usefulVariables = getUsefulVariables();
		alreadySeen = new HashSet<>();
	}
	
	
	private Map<String,List<Production>> productions;
	private Set<String> usefulVariables;
	private Set<Derivation> alreadySeen;
	
	/**
	 * Attempt to parse a string
	 * 
	 * @param input  input string
	 * @return true iff the grammar accepts the input string within
	 *     MAXPARSESTEPS steps
	 */
	public boolean parse (String input) {
		HashSet<String> terminals = new HashSet<>();
		for (String term: g.getTerminals()) {
			terminals.add(term);
		}
		for (int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				// Input string contains non-terminals. Cannot be parsed.
				return false;
			} else if (!terminals.contains(""+c)) {
				// Input string contains a terminal not mentioned in the grammar
				return false;
			}
		}
		
		if (!usefulVariables.contains(g.getStartVariable())) {
			return false;
		}
		
		alreadySeen.clear();
		List<Derivation> deq = new LinkedList<>();
		Derivation starter = new Derivation(g.getStartVariable(), input); 
		deq.add(starter);
		alreadySeen.add(starter);
		int stepCount = 0;
		// Attempt parallel leftmost and rightmost derivations (so
		// timing is OK for grammars featuring both left-recursive and 
		// right-recursive constructions.
		while (stepCount <= MAXPARSESTEPS && !deq.isEmpty()) {
			Derivation current = deq.get(0);
			boolean done = attemptLeftDerivationStep(current, deq);
			if (done)
				return true;
			if (current.derived.length() > 1) {
				done = attemptRightDerivationStep(current, deq);
				if (done)
					return true;
			}
			deq.remove(0);
			++stepCount;
		}
		return false;
	}


	private boolean attemptLeftDerivationStep(Derivation current, 
			List<Derivation> deq) {
		String var = current.derived.substring(0, 1);
		String remainder = current.derived.substring(1);
		for (Production p: productions.get(var)) {
			// Apply derivation to leftmost variable
			String newDerivation0 = p.getRHS() + remainder;
			
			Derivation d = reduceDerivation(new Derivation(newDerivation0, 
			        current.target));
			
			if (d == null)
			    continue;
			
			if (d.derived.isEmpty() && d.target.isEmpty())
			    return true;
			
			if (!alreadySeen.contains(d)) {
				deq.add(d);
				alreadySeen.add(d);
			}
		}
		return false;
	}

	/**
	 * Strip terminals from each end of a derived string and its target as long
	 * as they match
	 * @param current  derivation to reduce
	 * @return reduced derivation with empty derived string or with nonterminals
	 *      at each end, or null if terminals at either end of the derived string
	 *      do not match the target string.
	 */
    private Derivation reduceDerivation (Derivation current) {

        String derivedString = current.derived;
        // Now, discard any matching terminals on the left end
            int firstNTPos = 0;
            boolean OK = true;
            while (OK && firstNTPos < derivedString.length()
                    && isaTerminal(derivedString.charAt(firstNTPos))) {
                if (firstNTPos >= current.target.length() ||
                        derivedString.charAt(firstNTPos) 
                        != current.target.charAt(firstNTPos)) {
                    OK = false;
                }
                ++firstNTPos;
            }
            // and discard any matching terminals on the right end.
            int lastNTPos = derivedString.length() - 1;
            int lastMatched = current.target.length() - 1;
            while (OK && (lastNTPos > firstNTPos) 
                    && isaTerminal(derivedString.charAt(lastNTPos))) {
                if (lastMatched < firstNTPos
                        || derivedString.charAt(lastNTPos) 
                          != current.target.charAt(lastMatched)) {
                    OK = false;
                }
                --lastNTPos;
                --lastMatched;
            }

            if (!OK) {
                // We had non-matching terminals in the derived string.
                // Try a different production.
                return null;
            }
            
            // Trimmed derivation string: will either be empty or will
            // have a non-terminal variable at each end.
            String trimmedDerivedString 
            = derivedString.substring(firstNTPos, lastNTPos+1);
            String newTarget = current.target.substring(firstNTPos, lastMatched+1);
            // If both the derived string and the target are empty, we have
            // a successful parse.
            if (trimmedDerivedString.isEmpty() && newTarget.isEmpty()) {
                return new Derivation("", "");
            }
            // If the derivation is empty, but we have unmatched terminals
            // in the target string, this production will not derive
            // the target.
            if (trimmedDerivedString.isEmpty()) {
                return null;
            }

            // Scan to be sure we are not introducing any useless variables
            int newTerminalCount = 0;
            for (int i = 0; OK && i < trimmedDerivedString.length(); ++i) {
                String symbol = trimmedDerivedString.substring(i, i+1);
                if (isaTerminal(symbol.charAt(0))) {
                    ++newTerminalCount;
                } else if (!usefulVariables.contains(symbol)) {
                    OK = false;
                }
            }
            if (!OK) {
                return null;
            }
            // If the derived string has more terminals than the target,
            // this production is useless.
            if (newTerminalCount > newTarget.length()) {
                return null;
            }
            Derivation d = new Derivation(trimmedDerivedString, newTarget);
            return d;
    }

    
    private boolean isaTerminal(char sym) {
		for (String t : g.getTerminals()) {
			if (t.charAt(0) == sym) {
				return true;
			}
		}
		return false;
	}

	private boolean attemptRightDerivationStep(Derivation current, 
			List<Derivation> deq) {
        String var = current.derived.substring(current.derived.length()-1, 
                current.derived.length());
        String remainder = current.derived.substring(0, current.derived.length()-1);
        for (Production p: productions.get(var)) {
            // Apply derivation to leftmost variable
            String newDerivation0 = remainder + p.getRHS();
            
            Derivation d = reduceDerivation(new Derivation(newDerivation0, 
                    current.target));
            
            if (d == null)
                continue;
            
            if (d.derived.isEmpty() && d.target.isEmpty())
                return true;
            
            if (!alreadySeen.contains(d)) {
                deq.add(d);
                alreadySeen.add(d);
            }
        }
        return false;
	}

	private Set<String> getUsefulVariables() {
		HashSet<String> result = new HashSet<>();
		boolean done = false;
		while (!done) {
			done = true;
			for (Production p: g.getProductions()) {
				String var = p.getLHS();
				if (!result.contains(var)) {
					boolean allUseful = true;
					for (int i = 0; allUseful && i < p.getRHS().length(); ++i) {
						String symbol = p.getRHS().substring(i, i+1);
						char c = symbol.charAt(0);
						if (c >= 'A' && c <= 'Z'  
								&& !result.contains(symbol)) {
							allUseful = false;
						}
					}
					if (allUseful) {
						done = false;
						result.add(var);
					}
				}
			}
		}
		return result;
	}
}
