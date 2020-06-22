package movida.marromerli;

public class BubbleSort {
    public static <T> void sort(Comparable<T>[] keys){
        int size = keys.length;
        
        for(int i = 0; i < size - 1; i++){
            boolean swapped = false;
            
            for(int j = 0; j < size - 1 - i; j++){
                if(keys[j].compareTo(keys[j+1]) > 0){
                    Comparable<T> temp = keys[j];
                    keys[j] = keys[j+1];
                    keys[j+1] = temp;

                    swapped = true;
                }
            }

            if(swapped == false) break;
        }
    }
}