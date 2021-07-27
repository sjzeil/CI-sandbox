package cli;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import grammar.Grammar;
import grammar.Production;
import grammar.UnboundGrammar;
import grammar.parse.CYKParser;

public class TestCNFConversion {
	
	public Grammar g;
	public Grammar gg;
	
	@Before
	public void setup() {
	    String[] productions = {
	            "S", "0T0",
	            "S", "1T1",
	            "T", "",
	            "T", "UTU",
	            "U", "0",
	            "U", "1"
	    };
		g = new UnboundGrammar();
		for (int i = 0; i < productions.length; i +=2) {
		    Production p = new Production(productions[i], productions[i+1]);
		    g.addProduction(p);
		}
		g.setStartVariable("S");
		
		
	    String[] productions2 = {
	            "S", "TU",
	            "T", "0T1",
	            "T", "",
	            "U", "1U",
	            "U", ""
	    };
		gg = new UnboundGrammar();
		for (int i = 0; i < productions2.length; i +=2) {
		    Production p = new Production(productions2[i], productions2[i+1]);
		    gg.addProduction(p);
		}
		gg.setStartVariable("S");

	}

	@Test
	public void testBaseGrammar() {
		CFGParse parser = new CFGParse(g);
		assertFalse(parser.parse(""));
		assertFalse (parser.parse("0"));
		assertFalse(parser.parse("1"));
		assertTrue (parser.parse("00"));
		assertFalse (parser.parse("01"));
		assertFalse (parser.parse("10"));
		assertTrue (parser.parse("11"));
		assertTrue (parser.parse("0000"));
        assertTrue (parser.parse("1001"));
        assertTrue (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));	
        assertFalse(parser.parse("000001"));  
	}
	
    @Test
    public void testLambdaRemoval() {
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.removeLambdaProductions(g);
        CFGParse parser = new CFGParse(g2);
        assertFalse(parser.parse(""));
        assertFalse (parser.parse("0"));
        assertFalse(parser.parse("1"));
        assertTrue (parser.parse("00"));
        assertFalse (parser.parse("01"));
        assertFalse (parser.parse("10"));
        assertTrue (parser.parse("11"));
        assertTrue (parser.parse("0000"));
        assertTrue (parser.parse("1001"));
        assertTrue (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));  
        assertFalse(parser.parse("000001"));  
    }
	
    @Test
    public void testUnitRemoval() {
        g.addProduction(new Production("T", "Z"));
        g.addProduction(new Production("Z", "00"));
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.removeLambdaProductions(g);
        Grammar g3 = converter.removeUnitProductions(g2);
        CFGParse parser = new CFGParse(g3);
        assertFalse(parser.parse(""));
        assertFalse (parser.parse("0"));
        assertFalse(parser.parse("1"));
        assertTrue (parser.parse("00"));
        assertFalse (parser.parse("01"));
        assertFalse (parser.parse("10"));
        assertTrue (parser.parse("11"));
        assertTrue (parser.parse("0000"));
        assertTrue (parser.parse("1001"));
        assertTrue (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));  
        assertFalse(parser.parse("000001"));  
    }

    @Test
    public void testUselessRemoval() {
        g.addProduction(new Production("T", "Z"));
        g.addProduction(new Production("Z", "XZ"));
        g.addProduction(new Production("X", "1"));
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.removeLambdaProductions(g);
        Grammar g3 = converter.removeUnitProductions(g2);
        Grammar g4 = converter.removeUselessProductions(g3);
        CFGParse parser = new CFGParse(g4);
        assertFalse(parser.parse(""));
        assertFalse (parser.parse("0"));
        assertFalse(parser.parse("1"));
        assertTrue (parser.parse("00"));
        assertFalse (parser.parse("01"));
        assertFalse (parser.parse("10"));
        assertTrue (parser.parse("11"));
        assertTrue (parser.parse("0000"));
        assertTrue (parser.parse("1001"));
        assertTrue (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));
        assertFalse(parser.parse("000001"));  
        g4.setStartVariable("X");
        CFGParse parser2 = new CFGParse(g4);
        assertFalse(parser2.parse("1"));
        
    }

    @Test
    public void testConversion() {
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.convert(g);
        CYKParser parser = new CYKParser(g2);
        assertFalse(parser.solve(""));
        assertFalse (parser.solve("0"));
        assertFalse(parser.solve("1"));
        assertTrue (parser.solve("00"));
        assertFalse (parser.solve("01"));
        assertFalse (parser.solve("10"));
        assertTrue (parser.solve("11"));
        assertTrue (parser.solve("0000"));
        assertTrue (parser.solve("1001"));
        assertTrue (parser.solve("0100"));
        assertFalse(parser.solve("1000"));
        assertFalse(parser.solve("0001"));  
        assertFalse(parser.solve("000001"));  
    }

    
	@Test
	public void testBaseGrammar2() {
		CFGParse parser = new CFGParse(gg);
        assertTrue(parser.parse(""));
        assertFalse (parser.parse("0"));
        assertTrue(parser.parse("1"));
        assertFalse (parser.parse("00"));
        assertTrue (parser.parse("01"));
        assertFalse (parser.parse("10"));
        assertTrue (parser.parse("11"));
        assertFalse (parser.parse("0000"));
        assertFalse (parser.parse("1001"));
        assertFalse (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));  
        assertTrue(parser.parse("000111111"));  
	}
	
    @Test
    public void testLambdaRemoval2() {
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.removeLambdaProductions(gg);
        CFGParse parser = new CFGParse(g2);
        assertFalse (parser.parse("0"));
        assertTrue(parser.parse("1"));
        assertFalse (parser.parse("00"));
        assertTrue (parser.parse("01"));
        assertFalse (parser.parse("10"));
        assertTrue (parser.parse("11"));
        assertFalse (parser.parse("0000"));
        assertFalse (parser.parse("1001"));
        assertFalse (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));  
        assertTrue(parser.parse("000111111"));            
    }
	
    @Test
    public void testUnitRemoval2() {
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.removeLambdaProductions(gg);
        Grammar g3 = converter.removeUnitProductions(g2);
        CFGParse parser = new CFGParse(g3);
        assertFalse (parser.parse("0"));
        assertTrue(parser.parse("1"));
        assertFalse (parser.parse("00"));
        assertTrue (parser.parse("01"));
        assertFalse (parser.parse("10"));
        assertTrue (parser.parse("11"));
        assertFalse (parser.parse("0000"));
        assertFalse (parser.parse("1001"));
        assertFalse (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));  
        assertTrue(parser.parse("000111111"));  
    }

    @Test
    public void testUselessRemoval2() {
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.removeLambdaProductions(gg);
        Grammar g3 = converter.removeUnitProductions(g2);
        Grammar g4 = converter.removeUselessProductions(g3);
        CFGParse parser = new CFGParse(g4);
        assertFalse (parser.parse("0"));
        assertTrue(parser.parse("1"));
        assertFalse (parser.parse("00"));
        assertTrue (parser.parse("01"));
        assertFalse (parser.parse("10"));
        assertTrue (parser.parse("11"));
        assertFalse (parser.parse("0000"));
        assertFalse (parser.parse("1001"));
        assertFalse (parser.parse("0100"));
        assertFalse(parser.parse("1000"));
        assertFalse(parser.parse("0001"));  
        assertTrue(parser.parse("000111111"));  
    }

    @Test
    public void testConversion2() {
        CNFConversion converter = new CNFConversion();
        Grammar g2 = converter.convert(gg);
        CYKParser parser = new CYKParser(g2);
        assertFalse (parser.solve("0"));
        assertTrue(parser.solve("1"));
        assertFalse (parser.solve("00"));
        assertTrue (parser.solve("01"));
        assertFalse (parser.solve("10"));
        assertTrue (parser.solve("11"));
        assertFalse (parser.solve("0000"));
        assertFalse (parser.solve("1001"));
        assertFalse (parser.solve("0100"));
        assertFalse(parser.solve("1000"));
        assertFalse(parser.solve("0001"));  
        assertTrue(parser.solve("000111111"));  
    }

    
    
}
