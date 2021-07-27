/**
 * 
 */
package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import grammar.Grammar;
import grammar.LambdaProductionRemover;
import grammar.Production;
import grammar.UnboundGrammar;
import grammar.UnrestrictedGrammar;
import grammar.UselessProductionRemover;

/**
 * Converts a grammar to Chomsky Normal Form
 * @author zeil
 *
 */
public class CNFConversion {
    
    
    public CNFConversion () {
    }
    
    public boolean grammarAcceptsEmptyString(Grammar grammar) {
        LambdaProductionRemover remover = new LambdaProductionRemover();
        Set lambdaDerivers = remover.getCompleteLambdaSet(grammar);
        return lambdaDerivers.contains(grammar.getStartVariable());
    }

    public Grammar removeLambdaProductions(Grammar grammar) {
        LambdaProductionRemover remover = new LambdaProductionRemover();
        Set lambdaDerivers = remover.getCompleteLambdaSet(grammar);
        boolean acceptsEmptyString 
            = lambdaDerivers.contains(grammar.getStartVariable());
        Grammar noLambdas = remover.getLambdaProductionlessGrammar(grammar, 
                lambdaDerivers);
        noLambdas.setStartVariable(grammar.getStartVariable());
                

        return noLambdas;
    }
    
    
    public Grammar removeUnitProductions(Grammar grammar) {
    	Map<String, Set<String>> productions = new TreeMap<>();
    	for (Production p: grammar.getProductions()) {
    		String lhs = p.getLHS();
    		String rhs = p.getRHS();
    		if (!productions.containsKey(lhs)) {
    			productions.put(lhs,  new TreeSet<String>());
    		} 
    		productions.get(lhs).add(rhs);
    	}
    	
    	boolean changed = true;
    	while (changed) {
    		changed = false;
    		for (String var: grammar.getVariables()) {
    			Set<String> vprod = productions.get(var);
    			if (vprod == null)
    			    continue;
    			String[] vproda = vprod.toArray(new String[0]);
    			for (String rhs: vproda) {
    				if (isAVariable(rhs)) {
    					// Found a unit production.  Remove it and
    					// generate replacements.
    					changed = true;
    					vprod.remove(rhs);
    					Set<String> rprod = productions.get(rhs);
    					if (rprod == null) {
    					    continue;
    					}
    					for (String newRHS: rprod) {
    						if (!var.equals(newRHS)) {
    							vprod.add(newRHS);
    						}
    					}
    				}
    			}
    		}
    	}
    	Grammar newG = new UnboundGrammar();
    	for (String lhs: grammar.getVariables()) {
    	    Set<String> prod = productions.get(lhs);
    	    if (prod == null)
    	        continue;
    		for (String rhs: prod) {
    			newG.addProduction(new Production(lhs, rhs));
    		}
    	}
    	newG.setStartVariable(grammar.getStartVariable());
    	return newG;
    	
    }
    
    private boolean isAVariable(String rhs) {
		if (rhs.length() != 1)
			return false;
		char c = rhs.charAt(0);
		return (c >= 'A' && c <= 'Z');
	}

	public Grammar removeUselessProductions(Grammar grammar) {
        UselessProductionRemover remover = new UselessProductionRemover();
        
        Grammar g2 = UselessProductionRemover
                .getUselessProductionlessGrammar(grammar);
        g2.setStartVariable(grammar.getStartVariable());

        return g2;
    }
    
    
    public Grammar convert(Grammar g) {
        Grammar g2 = removeLambdaProductions(g);
        g2 = removeUnitProductions(g2);
        g2 = removeUselessProductions(g2);
        g2 = rewriteToChomsky(g2);
        return g2;
    }

        
    public Grammar rewriteToChomsky(Grammar g) {        
        grammar.CNFConverter converter = null;
        try {
            converter = new grammar.CNFConverter(g);
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
                //converter = new grammar.CNFConverter(g);
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
    

    private void convertToCNF(grammar.CNFConverter converter, Production p, 
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
