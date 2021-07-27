package cli;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestDescribe {

    ByteArrayOutputStream out;
    PrintStream systemOut;

    @Before
    public void setup() {
        out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        systemOut = System.out;
        System.setOut(ps);
    }

    public String getOutput() {
        try {
            out.flush();
        } catch (IOException e) {
            return "XXXXX";
        }
        return out.toString();
    }

    @After
    public void tearDown() {
        try {
            out.close();
        } catch (IOException e) {
            // ignore
        }
        System.setOut(systemOut);
    }

    @Test(timeout=5000)
    public void testRunFSA() throws IOException {
        String[] params = {"src/test/data/fsaNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Finite Automaton"));
        assertThat (results, containsString("deterministic"));
        assertThat (results, not(containsString("empty")));
        assertThat (results, not(containsString("invalid")));
    }

    @Test(timeout=5000)
    public void testRunFSAEmpty() throws IOException {
        String[] params = {"src/test/data/fsaEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Finite Automaton"));
        assertThat (results, not(containsString("deterministic")));
        assertThat (results, containsString("empty"));
        assertThat (results, not(containsString("invalid")));
    }

    @Test(timeout=5000)
    public void testRunFSANoInitial() throws IOException {
        String[] params = {"src/test/data/fsaNoInitial.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Finite Automaton"));
        assertThat (results, not(containsString("empty")));
        assertThat (results, containsString("invalid"));
    }

    @Test(timeout=5000)
    public void testRunNFA() throws IOException {
        String[] params = {"src/test/data/nfaNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Finite Automaton"));
        assertThat (results, not(containsString("deterministic")));
        assertThat (results, not(containsString("empty")));
        assertThat (results, not(containsString("invalid")));
    }

    @Test(timeout=5000)
    public void testRunRE() throws IOException {
        String[] params = {"src/test/data/regexpNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Regular Expression"));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, not(containsString("empty")));
    }

    @Test //(timeout=5000)
    public void testInvalidRE() throws IOException {
        String[] params = {"src/test/data/regexpInvalid.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Regular Expression"));
        assertThat (results, containsString("invalid"));
        assertThat (results, not(containsString("empty")));
    }

    @Test(timeout=5000)
    public void testRunREEmpty() throws IOException {
        String[] params = {"src/test/data/regexpEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Regular Expression"));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, containsString("empty"));
    }


    @Test(timeout=5000)
    public void testRunCFG() throws IOException {
        String[] params = {"src/test/data/CFG-both-ends-1.jff",
        "src/test/data/stringsOver01toLength6.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Grammar"));
        assertThat (results, containsString("CFG"));
        assertThat (results, not(containsString("CNF")));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, not(containsString("empty")));
    }

    @Test(timeout=5000)
    public void testRunCFGEmpty() throws IOException {
        String[] params = {"src/test/data/cfgEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Grammar"));
        assertThat (results, not(containsString("CFG")));
        assertThat (results, not(containsString("CNF")));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, containsString("empty"));
    }

    @Test(timeout=5000)
    public void testRunCFGnotCFL() throws IOException {
        String[] params = {"src/test/data/cfgInvalid.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Grammar"));
        assertThat (results, not(containsString("CFG")));
        assertThat (results, not(containsString("CNF")));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, not(containsString("empty")));
    }


    @Test //(timeout=5000)
    public void testRunPDA() throws IOException {
        String[] params = {"src/test/data/pdaNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Pushdown Automaton"));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, not(containsString("empty")));
    }

    @Test(timeout=5000)
    public void testRunPDAEmpty() throws IOException {
        String[] params = {"src/test/data/pdaEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Pushdown Automaton"));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, containsString("empty"));
    }
    
    @Test(timeout=5000)
    public void testRunPDAInvalid() throws IOException {
        String[] params = {"src/test/data/pdaNoInitial.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Pushdown Automaton"));
        assertThat (results, not(containsString("empty")));
        assertThat (results, containsString("invalid"));
    }
    
    @Test(timeout=5000)
    public void testRunTM() throws IOException {
        String[] params = {"src/test/data/tmNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        
        assertThat (results, containsString("Turing Machine"));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, not(containsString("empty")));
    }

    @Test(timeout=5000)
    public void testRunTMEmpty() throws IOException {
        String[] params = {"src/test/data/tmEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Turing Machine"));
        assertThat (results, not(containsString("invalid")));
        assertThat (results, containsString("empty"));
    }

    @Test(timeout=5000)
    public void testRunTMInvalid() throws IOException {
        String[] params = {"src/test/data/tmNoInitial.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new Describe().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Turing Machine"));
        assertThat (results, containsString("invalid"));
        assertThat (results, not(containsString("empty")));
    }

}
