package cli;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import grammar.Grammar;
import grammar.Production;
import grammar.UnboundGrammar;

public class TestCFGParse {
	
	public Grammar g;
	
	@Before
	public void setup() {
		g = new UnboundGrammar();
		g.addProduction(new Production("S", "TU"));
		g.addProduction(new Production("T", "0T"));
		g.addProduction(new Production("T", "0"));
		g.addProduction(new Production("U", "U1"));
		g.addProduction(new Production("U", ""));
		g.setStartVariable("S");
	}

	@Test
	public void testParse() {
		CFGParse parser = new CFGParse(g);
		assertFalse(parser.parse(""));
		assertTrue (parser.parse("0"));
		assertFalse(parser.parse("1"));
		assertTrue (parser.parse("00"));
		assertTrue (parser.parse("01"));
		assertFalse (parser.parse("10"));
		assertFalse (parser.parse("11"));
		assertTrue (parser.parse("0000111"));
		assertFalse (parser.parse("00001110"));
		assertFalse (parser.parse("a"));
		assertFalse (parser.parse("S"));
		assertFalse (parser.parse("T"));
		assertFalse (parser.parse("TU"));
	}

	
	@Test
	public void testUseless() {
		g.addProduction(new Production("S", "Z"));
		g.addProduction(new Production("Z", "SZ"));
		CFGParse parser = new CFGParse(g);
		assertFalse(parser.parse(""));
		assertTrue (parser.parse("0"));
		assertFalse(parser.parse("1"));
		assertTrue (parser.parse("00"));
		assertTrue (parser.parse("01"));
		assertFalse (parser.parse("10"));
		assertFalse (parser.parse("11"));
		assertTrue (parser.parse("0000111"));
		assertFalse (parser.parse("00001110"));
		assertFalse (parser.parse("a"));
		assertFalse (parser.parse("S"));
		assertFalse (parser.parse("T"));
		assertFalse (parser.parse("TU"));
	}


	@Test
	public void testEmptyLang() {
		g = new UnboundGrammar();
		g.addProduction(new Production("S", "SZ"));
		g.addProduction(new Production("Z", "SZ"));
		g.addProduction(new Production("Z", "0"));
		CFGParse parser = new CFGParse(g);
		assertFalse(parser.parse(""));
		assertFalse (parser.parse("0"));
		assertFalse(parser.parse("1"));
		assertFalse (parser.parse("00"));
		assertFalse (parser.parse("01"));
		assertFalse (parser.parse("10"));
		assertFalse (parser.parse("11"));
		assertFalse (parser.parse("00000000"));
	}

	
	
}
