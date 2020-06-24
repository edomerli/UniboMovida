package movida.marromerli;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


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
