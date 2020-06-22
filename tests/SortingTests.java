import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import movida.marromerli.*;

package tests;

public class SortingTests {

    @Test
    void FirstTest() {
        Integer[] edo = {6, 1, 4, 2, 9, 0};
        Sorter s = new BubbleSort();
        s.sort(edo, (Integer a, Integer b) -> a - b));
        assertEquals(edo, {0, 1, 2, 4, 6, 9});
    }

}