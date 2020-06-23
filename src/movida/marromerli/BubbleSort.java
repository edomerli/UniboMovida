package movida.marromerli;

import java.util.Comparator;
import java.util.List;

public class BubbleSort<T> implements Sorter<T> {
    @Override
    public void sort(List<T> keys, Comparator<T> comparator) {
        int size = keys.size();
        
        for(int i = 0; i < size - 1; i++){
            boolean swapped = false;
            
            for(int j = 0; j < size - 1 - i; j++){
                if(comparator.compare(keys.get(j), keys.get(j+1)) > 0){
                    T temp = keys.get(j);
                    keys.set(j, keys.get(j+1));
                    keys.set(j+1, temp);

                    swapped = true;
                }
            }

            if(swapped == false) break;
        }
    }
}