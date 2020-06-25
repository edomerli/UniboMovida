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
        dictionary.insert(60, 30);
        assertEquals(dictionary.search(50), (Integer) 20);
        dictionary.insert(50, 40);
        assertEquals(dictionary.search(50), (Integer) 40);
        dictionary.remove(50);
        assertNull(dictionary.search(50));
        assertEquals(dictionary.search(60), (Integer) 30);
        dictionary.clear();
        assertNull(dictionary.search(60));
    }
}
