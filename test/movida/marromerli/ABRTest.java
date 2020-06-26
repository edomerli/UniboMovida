package movida.marromerli;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ABRTest {
    @Test
    void listAddCheck(){
        Dictionary<String, ArrayList<Integer>> d = new ABR<String, ArrayList<Integer>>();
        Integer[] a = {1, 5, 3};
        ArrayList<Integer> a_list = new ArrayList<>();
        for(Integer x : a){
            a_list.add(x);
        }

        d.insert("Edo", a_list);

        d.search("Edo").add(8);
        a_list.add(8);

        assertEquals(d.search("Edo"), a_list);
    }

    @Test
    public void basicCheck() {
        SortedArrayDictionary<Integer, Integer> dictionary = new SortedArrayDictionary<>();
        assertNull(dictionary.search(31));
        dictionary.insert(35, 11);
        dictionary.insert(78, 2);
        assertEquals(dictionary.search(35), (Integer) 11);
        dictionary.insert(35, 40);
        assertEquals(dictionary.search(35), (Integer) 40);
        dictionary.remove(35);
        assertNull(dictionary.search(35));
        assertEquals(dictionary.search(78), (Integer) 2);

        dictionary.insert(8, 78);
        assertNotEquals(dictionary.search(78), (Integer) 9);
        assertNotEquals(dictionary.search(78), dictionary.search(8));

        dictionary.clear();
        assertNull(dictionary.search(78));
    }
}