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

}
