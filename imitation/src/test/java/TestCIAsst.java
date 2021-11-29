import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TestCIAsst {
    @Test
    void thisOnePasses() {
        CIAsst cia = new CIAsst();
        assertEquals (42, cia.add1(41));
    }

    @Test
    void thisOneFails() {
        CIAsst cia = new CIAsst();
        assertEquals (1, cia.add1(1));
    }

}
