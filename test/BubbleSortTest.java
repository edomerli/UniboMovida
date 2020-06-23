import movida.marromerli.BubbleSort;
import movida.marromerli.Sorter;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BubbleSortTest {

    @Test
    void sortingCheck(){
        Integer[] starting = {7, 1, 9, 3, 0};
        List<Integer> edo = Arrays.asList(starting);
        List<Integer> sorted = edo;
        Collections.sort(sorted);

        assertEquals(edo, Arrays.asList(starting));
        Sorter<Integer> s = new BubbleSort();

        s.sort(edo, (Integer a, Integer b) -> a - b);
        assertEquals(edo, sorted);
    }

}