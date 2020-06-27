package movida.marromerli;

import movida.commons.Collaboration;
import movida.commons.Movie;
import movida.commons.Person;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CollaborationGraphTest {
    @Test
    public void basicTest() {
        CollaborationGraph graph = new CollaborationGraph();
        assertEquals(graph.getDirectCollaboratorsOf(null).length, 0);
        Person person1 = new Person("Geronimo Stilton");
        Person person2 = new Person("Hugh Jackman");
        Person person3 = new Person("Mickey Mouse");
        Person director = new Person("Quentin Tarantino");
        Person[] cast = new Person[]{person1, person2, person3};

        Collaboration collaboration1 = new Collaboration(person1, person2);
        assertTrue(graph.addCollaboration(collaboration1));
        assertFalse(graph.addCollaboration(collaboration1));

        graph.clear();

        Movie movie = new Movie("Via col Vento", 2020, 1000, cast, director);
        graph.addMovie(movie);

        List<Person> person1Collaborators = Arrays.asList(graph.getDirectCollaboratorsOf(person1));

        assertFalse(person1Collaborators.contains(person1));
        assertTrue(person1Collaborators.contains(person2));
        assertTrue(person1Collaborators.contains(person3));

        graph.removeMovie(movie);
        assertEquals(graph.getDirectCollaboratorsOf(person1).length, 0);

        Person[] cast2 = new Person[]{person1, director};
        Movie movie2 = new Movie("Up", 2020, 1000, cast2, director);
        graph.addMovie(movie);
        graph.addMovie(movie2);
        graph.removeMovie(movie);

        person1Collaborators = Arrays.asList(graph.getDirectCollaboratorsOf(person1));
        assertTrue(person1Collaborators.contains(director));
        assertFalse(person1Collaborators.contains(person1));
        assertFalse(person1Collaborators.contains(person2));
        assertFalse(person1Collaborators.contains(person3));

        assertEquals(graph.getDirectCollaboratorsOf(director).length, 1);
        assertEquals(graph.getDirectCollaboratorsOf(person1).length, 1);
        assertEquals(graph.getDirectCollaboratorsOf(person2).length, 0);
        assertEquals(graph.getDirectCollaboratorsOf(person3).length, 0);

    }

    @Test
    public void BFSTest() {
        CollaborationGraph graph = new CollaborationGraph();
        Person hero1 = new Person("IronMan");
        Person hero2 = new Person("Hulk");
        Person hero3 = new Person("Capitan America");
        Person director1 = new Person("StanLee");
        Person[] cast1 = new Person[]{hero1, hero2, hero3};
        Movie movie1 = new Movie("Avengers Endgame", 2020, 99999, cast1, director1);
        graph.addMovie(movie1);

        Person person1 = new Person("Geronimo Stilton");
        Person person2 = new Person("Hugh Jackman");
        Person person3 = new Person("Mickey Mouse");
        Person director2 = new Person("Quentin Tarantino");
        Person[] cast2 = new Person[]{person1, person2, person3};
        Movie movie2 = new Movie("Via col Vento", 2020, 1000, cast2, director2);
        graph.addMovie(movie2);

        Person[] expectedTeam = new Person[]{hero1, hero3};
        Arrays.sort(expectedTeam);
        Person[] actualTeam = graph.getTeamOf(hero2);
        Arrays.sort(actualTeam);
        assertArrayEquals(actualTeam, expectedTeam);

        Person[] expectedTeam2 = new Person[]{person2, person3};
        Arrays.sort(expectedTeam2);
        Person[] actualTeam2 = graph.getTeamOf(person1);
        Arrays.sort(actualTeam2);
        assertArrayEquals(actualTeam2, expectedTeam2);

        Person[] cast3 = new Person[]{hero1, person1};
        Person director3 = new Person("Edo & Sam");
        Movie spinoff = new Movie("Via con Thanos", 2021, 8004, cast3, director3);
        graph.addMovie(spinoff);

        Person[] expectedTeam3 = new Person[]{hero1, hero2, person1, person2, person3};
        Arrays.sort(expectedTeam3);
        Person[] actualTeam3 = graph.getTeamOf(hero3);
        Arrays.sort(actualTeam3);
        assertArrayEquals(actualTeam3, expectedTeam3);

        graph.removeMovie(spinoff);
        assertArrayEquals(actualTeam, expectedTeam);

    }

    private double collaborationSum(Collaboration[] collaborations) {
        double total = 0;
        for (Collaboration c : collaborations) {
            total += c.getScore();
        }

        return total;
    }

    @Test
    public void MSTTest() {
        CollaborationGraph graph = new CollaborationGraph();
        Person person1 = new Person("A");
        Person person2 = new Person("B");
        Person person3 = new Person("C");
        Person person4 = new Person("D");
        Person director = new Person("Spielberg");
        Person[] cast1 = new Person[]{person2, person3, person4};
        Movie movie1 = new Movie("Jurassic Park", 2020, 10, cast1, director);

        Person[] cast2 = new Person[]{person1, person2};
        Movie movie2 = new Movie("E.T.", 2020, 50, cast2, director);

        graph.addMovie(movie1);
        graph.addMovie(movie2);

        /*Collaboration collab1 = new Collaboration(person1, person2);
        Collaboration collab2 = new Collaboration(person2, person3);
        Collaboration collab3 = new Collaboration(person2, person4);
        Collaboration collab4 = new Collaboration(person3, person4);

        collab1.getMovies().add(movie2);
        assertEquals(50.0, collab1.getScore());

        collab2.getMovies().add(movie1);
        collab3.getMovies().add(movie1);
        collab4.getMovies().add(movie1);*/

        Collaboration[] actualMST = graph.maximiseCollaborations(person1);
        assertEquals(70.0, collaborationSum(actualMST));

    }

    @Test
    public void MSTTest2() {
        CollaborationGraph graph = new CollaborationGraph();

        Person a = new Person("a");
        Person b = new Person("b");
        Person c = new Person("c");
        Person d = new Person("d");
        Person e = new Person("e");
        Person f = new Person("f");
        Person g = new Person("g");
        Person h = new Person("h");
        Person i = new Person("i");
        Person j = new Person("j");
        Person k = new Person("k");
        Person director = new Person("Steven Spielberg");

        Person[] cast1 = new Person[]{a, b};
        Movie movie1 = new Movie("Movie1", 0, 20, cast1, director);
        Person[] cast2 = new Person[]{a, c};
        Movie movie2 = new Movie("Movie2", 0, 5, cast2, director);
        Person[] cast3 = new Person[]{b, d};
        Movie movie3 = new Movie("Movie3", 0, 3, cast3, director);
        Person[] cast4 = new Person[]{c, d};
        Movie movie4 = new Movie("Movie4", 0, 8, cast4, director);
        Person[] cast5 = new Person[]{a, e};
        Movie movie5 = new Movie("Movie5", 0, 10, cast5, director);
        Person[] cast6 = new Person[]{b, e};
        Movie movie6 = new Movie("Movie6", 0, 15, cast6, director);

        Person[] cast7 = new Person[]{g, h};
        Movie movie7 = new Movie("Movie7", 0, 20, cast7, director);
        Person[] cast8 = new Person[]{f, h};
        Movie movie8 = new Movie("Movie8", 0, 40, cast8, director);
        Person[] cast9 = new Person[]{f, g};
        Movie movie9 = new Movie("Movie9", 0, 10, cast9, director);

        Person[] cast10 = new Person[]{i, j};
        Movie movie10 = new Movie("Movie10", 0, 10, cast10, director);
        Person[] cast11 = new Person[]{j, k};
        Movie movie11 = new Movie("Movie11", 0, 30, cast11, director);

        graph.addMovie(movie1);
        graph.addMovie(movie2);
        graph.addMovie(movie3);
        graph.addMovie(movie4);
        graph.addMovie(movie5);
        graph.addMovie(movie6);
        graph.addMovie(movie7);
        graph.addMovie(movie8);
        graph.addMovie(movie9);
        graph.addMovie(movie10);
        graph.addMovie(movie11);

        assertEquals(48, collaborationSum(graph.maximiseCollaborations(a)));
        assertEquals(48, collaborationSum(graph.maximiseCollaborations(b)));
        assertEquals(48, collaborationSum(graph.maximiseCollaborations(c)));
        assertEquals(48, collaborationSum(graph.maximiseCollaborations(d)));
        assertEquals(48, collaborationSum(graph.maximiseCollaborations(e)));
        assertEquals(60, collaborationSum(graph.maximiseCollaborations(f)));
        assertEquals(60, collaborationSum(graph.maximiseCollaborations(g)));
        assertEquals(60, collaborationSum(graph.maximiseCollaborations(h)));
        assertEquals(40, collaborationSum(graph.maximiseCollaborations(i)));
        assertEquals(40, collaborationSum(graph.maximiseCollaborations(j)));
        assertEquals(40, collaborationSum(graph.maximiseCollaborations(k)));
    }
}
