package movida.marromerli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CaseInsensitiveStringTest {
    @Test
    public void basicTest() {
        assertEquals(new CaseInsensitiveString("Abaco"), new CaseInsensitiveString("Abaco"));
        assertEquals(new CaseInsensitiveString("ABACO"), new CaseInsensitiveString("Abaco"));
        assertEquals(new CaseInsensitiveString("abaco"), new CaseInsensitiveString("Abaco"));
        assertEquals(new CaseInsensitiveString("aBAcO"), new CaseInsensitiveString("Abaco"));
        assertEquals(new CaseInsensitiveString(""), new CaseInsensitiveString(""));
        assertNotEquals(new CaseInsensitiveString("Abac"), new CaseInsensitiveString("Abaco"));
        assertTrue(new CaseInsensitiveString("a").compareTo(new CaseInsensitiveString("B")) < 0);
        assertTrue(new CaseInsensitiveString("B").compareTo(new CaseInsensitiveString("a")) > 0);
        assertEquals(0, new CaseInsensitiveString("a").compareTo(new CaseInsensitiveString("A")));
        assertTrue(new CaseInsensitiveString("Abaco").toString().equals("Abaco"));
        assertFalse(new CaseInsensitiveString("ABACO").toString().equals("Abaco"));

        assertEquals(new CaseInsensitiveString("Abaco").hashCode(), new CaseInsensitiveString("Abaco").hashCode());
        assertEquals(new CaseInsensitiveString("ABACO").hashCode(), new CaseInsensitiveString("Abaco").hashCode());
        assertEquals(new CaseInsensitiveString("abaco").hashCode(), new CaseInsensitiveString("Abaco").hashCode());
        assertEquals(new CaseInsensitiveString("aBAcO").hashCode(), new CaseInsensitiveString("Abaco").hashCode());
        assertEquals(new CaseInsensitiveString("").hashCode(), new CaseInsensitiveString("").hashCode());

        assertNotEquals(null, new CaseInsensitiveString("Abaco"));
        CaseInsensitiveString a = new CaseInsensitiveString("Abaco");
        assertEquals(a, a);

        assertTrue(CaseInsensitiveString.contains(new CaseInsensitiveString("Abaco"), new CaseInsensitiveString("Ab")));
        assertTrue(CaseInsensitiveString.contains(new CaseInsensitiveString("abaco"), new CaseInsensitiveString("Ab")));
        assertTrue(CaseInsensitiveString.contains(new CaseInsensitiveString("Abaco"), new CaseInsensitiveString("AC")));
        assertFalse(CaseInsensitiveString.contains(new CaseInsensitiveString("Abaco"), new CaseInsensitiveString("ao")));
    }
}
