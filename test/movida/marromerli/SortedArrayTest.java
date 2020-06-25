package movida.marromerli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class SortedArrayTest {
    @Test
    public void basicCheck() {
        SortedArrayDictionary<Integer, Integer> dictionary = new SortedArrayDictionary<>();
        assertNull(dictionary.search(82));
        dictionary.insert(50, 20);
        dictionary.insert(50, 40);
        assertEquals(dictionary.search(50), (Integer) 40);
    }
}
