package movida.marromerli;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuickSortTest {

    private void compareSorting(Integer[] original) {
        List<Integer> quickSortedList = Arrays.asList(original.clone());
        List<Integer> standardSortedList = Arrays.asList(original.clone());
        standardSortedList.sort(Integer::compareTo);
        Sorter s = new QuickSort();
        s.sort(quickSortedList, Integer::compareTo);

        assertEquals(standardSortedList.size(), quickSortedList.size());

        for (int i = 0; i < quickSortedList.size(); i++) {
            assertEquals(standardSortedList.get(i), quickSortedList.get(i));
            System.out.println(quickSortedList.get(i));
            System.out.println(standardSortedList.get(i));
        }
    }

    @Test
    public void basicTest() {
        Integer[] array1 = new Integer[]{4, 7, 1, -4, 9, 2};
        compareSorting(array1);
        Integer[] array2 = new Integer[]{-10, 8, 8, 0, -1};
        compareSorting(array2);
        Integer[] array3 = new Integer[]{1, 2, 3, 4, 5};
        compareSorting(array3);
    }
}
