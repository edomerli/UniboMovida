package movida.marromerli;

import movida.commons.Collaboration;
import movida.commons.Movie;
import movida.commons.Person;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

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
}
