package cli;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestRunEquiv {

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
	public void testEqual() throws IOException {
		String[] params = {"src/test/data/fsaNo10.jff",
						   "src/test/data/nfaNo10.jff"};
		new Equiv().runProgram(params);
		String results = getOutput();
		assertThat (results, containsString("equivalent"));
	}

    @Test(timeout=5000)
    public void testDifferent1() throws IOException {
        String[] params = {"src/test/data/fsaNo10.jff",
                           "src/test/data/fsaNo1.jff"};
        new Equiv().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("different"));
    }

    @Test(timeout=5000)
    public void testDifferent2() throws IOException {
        String[] params = {"src/test/data/nfaNo10.jff",
                           "src/test/data/fsaNo1.jff"};
        new Equiv().runProgram(params);
        String results = getOutput();
        assertThat (results, containsString("different"));
    }
}
