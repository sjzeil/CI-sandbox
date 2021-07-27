package cli;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestRunBatch {

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
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("\"\"\taccepted"));
        assertThat (results, containsString("\"0\"\taccepted"));
        assertThat (results, containsString("\"1\"\taccepted"));
        assertThat (results, containsString("\"00\"\taccepted"));
        assertThat (results, containsString("\"01\"\taccepted"));
        assertThat (results, containsString("\"10\"\trejected"));
        assertThat (results, containsString("\"11\"\taccepted"));
    }

    @Test(timeout=5000)
    public void testRunFSAEmpty() throws IOException {
        String[] params = {"src/test/data/fsaEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("empty"));
    }

    @Test(timeout=5000)
    public void testRunNFA() throws IOException {
        String[] params = {"src/test/data/nfaNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("\"\"\taccepted"));
        assertThat (results, containsString("\"0\"\taccepted"));
        assertThat (results, containsString("\"1\"\taccepted"));
        assertThat (results, containsString("\"00\"\taccepted"));
        assertThat (results, containsString("\"01\"\taccepted"));
        assertThat (results, containsString("\"10\"\trejected"));
        assertThat (results, containsString("\"11\"\taccepted"));
    }

    @Test(timeout=5000)
    public void testRunRE() throws IOException {
        String[] params = {"src/test/data/regexpNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("\"\"\taccepted"));
        assertThat (results, containsString("\"0\"\taccepted"));
        assertThat (results, containsString("\"1\"\taccepted"));
        assertThat (results, containsString("\"00\"\taccepted"));
        assertThat (results, containsString("\"01\"\taccepted"));
        assertThat (results, containsString("\"10\"\trejected"));
        assertThat (results, containsString("\"11\"\taccepted"));
    }

    @Test(timeout=5000)
    public void testInvalidRE() throws IOException {
        String[] params = {"src/test/data/regexpInvalid.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("invalid"));
    }

    @Test(timeout=5000)
    public void testRunREEmpty() throws IOException {
        String[] params = {"src/test/data/regexpEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("empty"));
    }

    @Test //(timeout=5000)
    public void testRunPDA2() throws IOException {
        String[] params = {"src/test/data/pda0n1n.jff",
        "src/test/data/stringsOver01toLength6.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("\"\"\taccepted"));
        assertThat (results, containsString("\"0\"\trejected"));
        assertThat (results, containsString("\"1\"\trejected"));
        assertThat (results, containsString("\"00\"\trejected"));
        assertThat (results, containsString("\"01\"\taccepted"));
        assertThat (results, containsString("\"10\"\trejected"));
        assertThat (results, containsString("\"11\"\trejected"));
        assertThat (results, containsString("\"001\"\trejected"));
        assertThat (results, containsString("\"011\"\trejected"));
        assertThat (results, containsString("\"0011\"\taccepted"));
        assertThat (results, containsString("\"000111\"\taccepted"));
    }



    @Test(timeout=5000)
    public void testRunCFG() throws IOException {
        String[] params = {"src/test/data/CFG-both-ends-1.jff",
        "src/test/data/stringsOver01toLength6.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("\"\"\trejected"));
        assertThat (results, containsString("\"0\"\trejected"));
        assertThat (results, containsString("\"1\"\trejected"));
        assertThat (results, containsString("\"11\"\taccepted"));
        assertThat (results, containsString("\"101\"\taccepted"));
        assertThat (results, containsString("\"100\"\trejected"));
        assertThat (results, containsString("\"111\"\taccepted"));
    }

    @Test //(timeout=5000)
    public void testRunCFG_no_s() throws IOException {
        String[] params = {"src/test/data/cfg-no-S.jff",
        "src/test/data/stringsOver01toLength6.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("\"\"\trejected"));
        assertThat (results, containsString("\"0\"\trejected"));
        assertThat (results, containsString("\"1\"\taccepted"));
        assertThat (results, containsString("\"11\"\trejected"));
        assertThat (results, containsString("\"010\"\taccepted"));
        assertThat (results, containsString("\"000\"\trejected"));
        assertThat (results, containsString("\"111\"\taccepted"));
        assertThat (results, containsString("\"101\"\trejected"));
        assertThat (results, containsString("\"110\"\taccepted"));
        assertThat (results, containsString("\"100\"\trejected"));
    }

    @Test(timeout=5000)
    public void testRunCFGEmpty() throws IOException {
        String[] params = {"src/test/data/cfgEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("empty"));
    }

    @Test// (timeout=5000)
    public void testRunCFGInvalid() throws IOException {
        String[] params = {"src/test/data/cfgInvalid.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("Not a valid CFG"));
        assertThat (results, not(containsString("rejected")));
        assertThat (results, not(containsString("accepted")));
    }

    @Test(timeout=5000)
    public void testRunPDA() throws IOException {
        String[] params = {"src/test/data/pdaNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("\"\"\taccepted"));
        assertThat (results, containsString("\"0\"\taccepted"));
        assertThat (results, containsString("\"1\"\taccepted"));
        assertThat (results, containsString("\"00\"\taccepted"));
        assertThat (results, containsString("\"01\"\taccepted"));
        assertThat (results, containsString("\"10\"\trejected"));
        assertThat (results, containsString("\"11\"\taccepted"));
    }

    @Test(timeout=5000)
    public void testRunPDAEmpty() throws IOException {
        String[] params = {"src/test/data/pdaEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("empty"));
    }

    @Test(timeout=5000)
    public void testRunTM() throws IOException {
        String[] params = {"src/test/data/tmNo10.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        String[] answers = results.split("\n"); 

        boolean[] expectedResult = {
                true, true, true, 
                true, true, false, true  
        };
        String[] expectedTapes = {
                "[□□]", "[a□]", "[b□]",
                "[aa□]", "[ab□]", "NA", "[bb□]"
        };

        int offset = 2;
        assertThat (answers[offset], containsString("\"\"\taccepted: [□□]"));
        assertThat (answers.length, is(expectedResult.length + offset));
        for (int i = 0; i < expectedResult.length; ++i) {
            if (expectedResult[i]) {
                assertThat (answers[i+offset], 
                        containsString("accepted: " + expectedTapes[i]));
            } else {
                assertThat (answers[i+offset], 
                        containsString("rejected"));
            }
        }
    }

    @Test(timeout=5000)
    public void testRunTMEmpty() throws IOException {
        String[] params = {"src/test/data/tmEmpty.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("empty"));
    }

    @Test(timeout=5000)
    public void testRunNonHaltingTM() throws IOException {
        String[] params = {"src/test/data/tmNotHaltingOn1.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        String[] answers = results.split("\n"); 

        String[] expectedResults = {
                "accepted", "accepted", "did not halt after", 
                "accepted", "did not halt after", 
                "did not halt after", "did not halt after"  
        };
        String[] expectedTapes = {
                "[□□]", "[a□]", "[b□]",
                "[aa□]", "[ab□]", "NA", "[bb□]"
        };

        int offset = 2;
        assertThat (answers[offset], containsString("\"\"\taccepted: [□□]"));
        assertThat (answers.length, is(expectedResults.length + offset));
        for (int i = 0; i < expectedResults.length; ++i) {
            assertThat (expectedResults[i], 
                    containsString(expectedResults[i]));
            if (expectedResults[i].equals("accepted")) {
                assertThat (answers[i+offset], 
                        containsString("accepted: " + expectedTapes[i]));
            }
        }

    }

    
    @Test //(timeout=5000)
    public void testRunTMmulti() throws IOException {
        String[] params = {"src/test/data/tmMulti.jff",
        "src/test/data/stringsOver01toLength2.txt"};
        new RunBatch().runProgram(params);
        String results = getOutput();
        String[] resultLines = results.split("\n");
                
        String[] expectedResult = {
                "rejected", "accepted", "accepted", 
                "accepted", "rejected", "accepted", "accepted"  
        };

        StringBuffer pattern = new StringBuffer();
        pattern.append(".*");
        int matching = 0;
        for (String line: resultLines) {
            if (line.contains("accepted") || line.contains("rejected")) {
                assertThat ("result #" + matching, 
                        line, containsString(expectedResult[matching]));
                ++matching;
            }
        }	
        assertThat (matching, equalTo(expectedResult.length));
    }

    
    
}
