package movida.marromerli;

import movida.commons.Movie;
import movida.commons.Person;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class BubbleSortTest {

    @Test
    void integerCheck(){
        Integer[] starting = {7, 1, 9, 3, 0};
        List<Integer> edo = Arrays.asList(starting);
        List<Integer> sorted = edo;
        Collections.sort(sorted);

        assertEquals(edo, Arrays.asList(starting));
        Sorter s = new BubbleSort();

        s.sort(edo, (Integer a, Integer b) -> a - b);
        assertEquals(edo, sorted);
    }

    @Test
    void emptyCheck() {
        Sorter s = new BubbleSort();
        List<Movie> empty = new ArrayList<>();
        s.sort(empty, (Movie a, Movie b) -> a.getYear() - b.getYear());
        assertEquals(0, empty.size());
    }

    @Test
    void votesOrdered() {
        Person person1 = new Person("A");
        Person[] cast = new Person[]{person1};
        List<Movie> l = new ArrayList<>();

        Random rand = new Random();
        int n = 10000;
        for(int i=0; i<n; ++i){
            Movie m = new Movie("A", 2020, rand.nextInt(6793), cast, person1);
            l.add(m);
        }

        boolean sorted = true;
        for(int i=0; i<n-1; ++i){
            if(l.get(i).getVotes() > l.get(i+1).getVotes()) sorted = false;
        }
        assertEquals(false, sorted);

        Sorter s = new BubbleSort();
        s.sort(l, (Movie a, Movie b) -> a.getVotes() - b.getVotes());
        assertEquals(n, l.size());

        sorted = true;
        for(int i=0; i<n-1; ++i){
            if(l.get(i).getVotes() > l.get(i+1).getVotes()) sorted = false;
        }
        assertEquals(true, sorted);
    }

    @Test
    void yearOrdered() {
        Person person1 = new Person("A");
        Person[] cast = new Person[]{person1};
        List<Movie> l = new ArrayList<>();

        Random rand = new Random();
        int n = 10000;
        for(int i=0; i<n; ++i){
            Movie m = new Movie("A", rand.nextInt(2020), rand.nextInt(6793), cast, person1);
            l.add(m);
        }

        boolean sorted = true;
        for(int i=0; i<n-1; ++i){
            if(l.get(i).getYear() > l.get(i+1).getYear()) sorted = false;
        }
        assertEquals(false, sorted);

        Sorter s = new BubbleSort();
        s.sort(l, (Movie a, Movie b) -> a.getYear() - b.getYear());
        assertEquals(n, l.size());

        sorted = true;
        for(int i=0; i<n-1; ++i){
            if(l.get(i).getYear() > l.get(i+1).getYear()) sorted = false;
        }
        assertEquals(true, sorted);
    }

    @Test
    void activityOrdered() {
        int people_count = 8;
        Person person1 = new Person("A");
        Person person2 = new Person("B");
        Person person3 = new Person("C");
        Person person4 = new Person("D");
        Person person5 = new Person("E");
        Person person6 = new Person("F");
        Person person7 = new Person("G");
        Person person8 = new Person("H");
        Person[] people = new Person[]{person1, person2, person3, person4, person5, person6, person7, person8};
        List<Person> l = Arrays.asList(people);

        Dictionary<String, List<Movie>> moviesByActor = new ABR<String, List<Movie>>();
        Random rand = new Random();
        int n = 10000;
        for(int i=0; i<n; ++i){
            List<Person> castList = new ArrayList<>();
            boolean[] chosen = new boolean[people_count];
            int cast_size = rand.nextInt(people_count + 1);
            for(int j=0; j<cast_size; ++j){
                int person_index;
                do{
                    person_index = rand.nextInt(people_count);
                }
                while(chosen[person_index]);
                chosen[person_index] = true;
                castList.add(l.get(person_index));
            }
            Movie m = new Movie("A", rand.nextInt(2020), rand.nextInt(6793), castList.toArray(new Person[0]), person1);
            for(Person p : castList){
                String name = p.getName();
                if(moviesByActor.search(name) == null) moviesByActor.insert(name, new ArrayList<Movie>());
                moviesByActor.search(name).add(m);
            }
        }

        boolean sorted = true;
        for(int i=0; i<l.size() - 1; ++i){
            if(moviesByActor.search(l.get(i).getName()).size() > moviesByActor.search(l.get(i+1).getName()).size()){
                sorted = false;
            }
        }
        assertEquals(sorted, false);

        Sorter s = new BubbleSort();
        s.sort(l, Comparator.comparingInt((Person a) -> moviesByActor.search(a.getName()).size()));

        sorted = true;
        for(int i=0; i<l.size() - 1; ++i){
            if(moviesByActor.search(l.get(i).getName()).size() > moviesByActor.search(l.get(i+1).getName()).size()){
                sorted = false;
            }
        }
        assertEquals(sorted, true);
    }
}