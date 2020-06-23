package movida.marromerli;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ABRTest {
    @Test
    void test1(){
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
}