package movida.marromerli;

import movida.commons.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe per gestire un database di film.
 */
public class MovidaCore implements IMovidaConfig, IMovidaDB, IMovidaSearch, IMovidaCollaborations {
    private SortingAlgorithm sortingAlgorithm; // Algoritmo usato
    private MapImplementation mapImplementation; // Implementazione di dizionario usata

    private List<Movie> moviesOrderedByVotes, moviesOrderedByYear;  // Liste dei film, ordinate secondo voti e anno distintamente
    private List<Person> actors, people;    // Insieme degli attori e delle persone (sia attori che direttori)

    private Sorter sorter;
    private boolean areMoviesSortedByVotes, areMoviesSortedByYear, areActorsSorted; // Flag per indicare se le liste sono ordinate


    // Dizionari distinti in base ai vari criteri di ricerca
    private Dictionary<CaseInsensitiveString, Person> personByName;

    private Dictionary<CaseInsensitiveString, Movie> moviesByTitle;
    private Dictionary<Integer, List<Movie>> moviesByYear;
    private Dictionary<CaseInsensitiveString, List<Movie>> moviesByDirector;
    private Dictionary<CaseInsensitiveString, List<Movie>> moviesByActor;

    // Grafo delle collaborazioni
    private CollaborationGraph graph;

    /**
     * Crea una nuova MovidaCore.
     */
    public MovidaCore() {
        // Scelte di default
        this.sortingAlgorithm = SortingAlgorithm.QuickSort;
        this.sorter = new QuickSort();
        this.mapImplementation = MapImplementation.ABR;

        this.moviesOrderedByVotes = new ArrayList<>();
        this.moviesOrderedByYear = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.people = new ArrayList<>();

        this.areMoviesSortedByVotes = false;
        this.areMoviesSortedByYear = false;
        this.areActorsSorted = false;
        this.personByName = new ABR<>();
        this.moviesByTitle = new ABR<>();
        this.moviesByYear = new ABR<>();
        this.moviesByDirector = new ABR<>();
        this.moviesByActor = new ABR<>();
        this.graph = new CollaborationGraph();
    }

    /**
     * Seleziona l'implementazione del dizionario
     * <p>
     * Se il dizionario scelto non è supportato dall'applicazione
     * la configurazione non cambia
     *
     * @param m l'implementazione da selezionare
     * @return <code>true</code> se la configurazione è stata modificata, <code>false</code> in caso contrario
     */
    @Override
    public boolean setMap(MapImplementation m) {
        // Se la scelta è fra le supportate ma diversa da quella attuale
        if ((m == MapImplementation.ArrayOrdinato || m == MapImplementation.ABR) && this.mapImplementation != m) {
            if (m == MapImplementation.ArrayOrdinato) {
                this.personByName = new SortedArrayDictionary<>();
                this.moviesByTitle = new SortedArrayDictionary<>();
                this.moviesByYear = new SortedArrayDictionary<>();
                this.moviesByDirector = new SortedArrayDictionary<>();
                this.moviesByActor = new SortedArrayDictionary<>();
            } else{
                this.personByName = new ABR<>();
                this.moviesByTitle = new ABR<>();
                this.moviesByYear = new ABR<>();
                this.moviesByDirector = new ABR<>();
                this.moviesByActor = new ABR<>();
            }

            // Trasferimento della knowledge-base
            for(Movie movie : moviesOrderedByVotes) {
                String title = movie.getTitle();
                CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
                Integer year = movie.getYear();
                Person director = movie.getDirector();
                String directorName = director.getName();
                CaseInsensitiveString caseInsensitiveDirectorName = new CaseInsensitiveString(directorName);
                Person[] cast = movie.getCast();

                personByName.insert(caseInsensitiveDirectorName, director);
                moviesByTitle.insert(caseInsensitiveTitle, movie);
                if (moviesByYear.search(year) == null) moviesByYear.insert(year, new ArrayList<>());
                moviesByYear.search(year).add(movie);
                if (moviesByDirector.search(caseInsensitiveDirectorName) == null)
                    moviesByDirector.insert(caseInsensitiveDirectorName, new ArrayList<>());
                moviesByDirector.search(caseInsensitiveDirectorName).add(movie);

                for (Person actor : cast) {
                    String name = actor.getName();
                    CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
                    if (moviesByActor.search(caseInsensitiveName) == null)
                        moviesByActor.insert(caseInsensitiveName, new ArrayList<>());
                    moviesByActor.search(caseInsensitiveName).add(movie);
                    if (personByName.search(caseInsensitiveName) == null)
                        personByName.insert(caseInsensitiveName, actor);
                }

            }

            this.mapImplementation = m;

            return true;
        } else {
            return false;
        }
    }

    /**
     * Seleziona l'algoritmo di ordinamento.
     * Se l'algortimo scelto non è supportato dall'applicazione
     * la configurazione non cambia
     *
     * @param a l'algoritmo da selezionare
     * @return <code>true</code> se la configurazione è stata modificata, <code>false</code> in caso contrario
     */
    @Override
    public boolean setSort(SortingAlgorithm a) {
        // Se la scelta è fra le supportate ma diversa da quella attuale
        if ((a == SortingAlgorithm.BubbleSort || a == SortingAlgorithm.QuickSort) && this.sortingAlgorithm != a) {

            if (a == SortingAlgorithm.BubbleSort) {
                sorter = new BubbleSort();
            } else sorter = new QuickSort();

            this.sortingAlgorithm = a;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Carica i dati da un file, organizzato secondo il formato MOVIDA (vedi esempio-formato-dati.txt)
     * <p>
     * Un film è identificato in modo univoco dal titolo (case-insensitive), una persona dal nome (case-insensitive).
     * Semplificazione: non sono gestiti omonimi e film con lo stesso titolo.
     * <p>
     * I nuovi dati sono aggiunti a quelli già caricati.
     * <p>
     * Se esiste un film con lo stesso titolo il record viene sovrascritto.
     * Se esiste una persona con lo stesso nome non ne viene creata un'altra.
     * <p>
     * Se il file non rispetta il formato, i dati non sono caricati e
     * viene sollevata un'eccezione.
     *
     * @param f il file da cui caricare i dati
     * @throws MovidaFileException in caso di errore di caricamento
     */
    @Override
    public void loadFromFile(File f) {
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String[] movieData = new String[4];
                for (int i = 0; i < 4; i++) {
                    movieData[i] = s.nextLine().split(":")[1].trim();
                }

                String title = movieData[0];
                int year = Integer.parseInt(movieData[1]);
                String directorName = movieData[2];
                String[] castNames;

                if (movieData[3].equals("")) {
                    castNames = new String[0];
                } else {
                    castNames = movieData[3].split(", ");
                }

                // Cast vuoto
                if (castNames.length == 1 && castNames[0].equals("")) {
                    castNames = new String[0];
                }

                int votes = Integer.parseInt(s.nextLine().split(":")[1].trim());

                Person director = new Person(directorName);
                Person[] cast = new Person[castNames.length];
                for (int i = 0; i < castNames.length; i++) {
                    cast[i] = new Person(castNames[i]);
                }

                Movie movie = new Movie(title, year, votes, cast, director);
                if (s.hasNextLine()) {
                    s.nextLine();
                }

                importMovie(movie);
            }
            s.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new MovidaFileException();
        }
    }

    /**
     * Salva tutti i dati su un file.
     * <p>
     * Il file è sovrascritto.
     * Se non è possibile salvare, ad esempio per un problema di permessi o percorsi,
     * viene sollevata un'eccezione.
     *
     * @param f il file su cui salvare i dati
     * @throws MovidaFileException in caso di errore di salvataggio
     */
    @Override
    public void saveToFile(File f) {
        try {
            FileWriter writer = new FileWriter(f, false);

            for (Movie movie : this.moviesOrderedByYear) {
                writer.append(movie.toString());
                writer.append("\n\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new MovidaFileException();
        }
    }

    /**
     * Cancella tutti i dati.
     * <p>
     * Sarà quindi necessario caricarne altri per proseguire.
     */
    @Override
    public void clear() {
        moviesOrderedByVotes.clear();
        moviesOrderedByYear.clear();
        actors.clear();
        people.clear();


        personByName.clear();
        moviesByTitle.clear();
        moviesByActor.clear();
        moviesByYear.clear();
        moviesByDirector.clear();

        graph.clear();
    }

    /**
     * Restituisce il numero di film
     *
     * @return numero di film totali
     */
    @Override
    public int countMovies() {
        return moviesOrderedByYear.size();
    }

    /**
     * Restituisce il numero di persone
     *
     * @return numero di persone totali
     */
    @Override
    public int countPeople() {
        return people.size();
    }

    /**
     * Cancella il film con un dato titolo, se esiste.
     *
     * @param title titolo del film
     * @return <code>true</code> se il film è stato trovato e cancellato,
     * <code>false</code> in caso contrario
     */
    @Override
    public boolean deleteMovieByTitle(String title) {
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        Movie toBeDeleted = moviesByTitle.search(caseInsensitiveTitle);
        if (toBeDeleted == null) return false;

        moviesOrderedByYear.remove(toBeDeleted);
        moviesOrderedByVotes.remove(toBeDeleted);

        moviesByTitle.remove(caseInsensitiveTitle);

        String directorName = toBeDeleted.getDirector().getName();
        CaseInsensitiveString caseInsensitiveDirectorName = new CaseInsensitiveString(directorName);
        moviesByDirector.search(caseInsensitiveDirectorName).remove(toBeDeleted);
        if (moviesByDirector.search(caseInsensitiveDirectorName).size() == 0) {
            moviesByDirector.remove(caseInsensitiveDirectorName);

            // Se non e' anche un attore, rimuovilo dalle persone
            if (moviesByActor.search(caseInsensitiveDirectorName) == null) {
                people.remove(toBeDeleted.getDirector());
                personByName.remove(caseInsensitiveDirectorName);
            }
        }

        Integer year = toBeDeleted.getYear();
        moviesByYear.search(year).remove(toBeDeleted);
        if(moviesByYear.search(year).size() == 0){
            moviesByYear.remove(year);
        }

        for(Person actor : toBeDeleted.getCast()) {
            String name = actor.getName();
            CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
            moviesByActor.search(caseInsensitiveName).remove(toBeDeleted);
            if (moviesByActor.search(caseInsensitiveName).size() == 0) {
                moviesByActor.remove(caseInsensitiveName);
                actors.remove(actor);

                // Se non e' anche un direttore, rimuovilo dalle persone
                if (moviesByDirector.search(caseInsensitiveName) == null) {
                    people.remove(actor);
                    personByName.remove(caseInsensitiveName);
                }
            }
        }

        this.graph.removeMovie(toBeDeleted);
        return true;
    }

    /**
     * Restituisce il record associato ad un film
     *
     * @param title il titolo del film
     * @return record associato ad un film
     */
    @Override
    public Movie getMovieByTitle(String title) {
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        return moviesByTitle.search(caseInsensitiveTitle);
    }

    /**
     * Restituisce il record associato ad una persona, attore o regista
     *
     * @param name il nome della persona
     * @return record associato ad una persona
     */
    @Override
    public Person getPersonByName(String name) {
        CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
        return personByName.search(caseInsensitiveName);
    }

    /**
     * Restituisce il vettore di tutti i film
     *
     * @return array di film
     */
    @Override
    public Movie[] getAllMovies() {
        return moviesOrderedByYear.toArray(new Movie[0]);
    }

    /**
     * Restituisce il vettore di tutte le persone
     *
     * @return array di persone
     */
    @Override
    public Person[] getAllPeople() {
        return people.toArray(new Person[0]);
    }

    /**
     * Ricerca film per titolo.
     * <p>
     * Restituisce i film il cui titolo contiene la stringa
     * <code>title</code> passata come parametro.
     * <p>
     * Per il match esatto usare il metodo <code>getMovieByTitle(String s)</code>
     * <p>
     * Restituisce un vettore vuoto se nessun film rispetta il criterio di ricerca.
     *
     * @param title titolo del film da cercare
     * @return array di film
     */
    @Override
    public Movie[] searchMoviesByTitle(String title) {
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        return moviesByTitle.searchAll(caseInsensitiveTitle, CaseInsensitiveString::contains).toArray(new Movie[0]);
    }

    /**
     * Ricerca film per anno.
     * <p>
     * Restituisce i film usciti in sala nell'anno
     * <code>anno</code> passato come parametro.
     * <p>
     * Restituisce un vettore vuoto se nessun film rispetta il criterio di ricerca.
     *
     * @param year anno del film da cercare
     * @return array di film
     */
    @Override
    public Movie[] searchMoviesInYear(Integer year) {
        List<Movie> results = moviesByYear.search(year);
        if (results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    /**
     * Ricerca film per regista.
     * <p>
     * Restituisce i film diretti dal regista il cui nome è passato come parametro.
     * <p>
     * Restituisce un vettore vuoto se nessun film rispetta il criterio di ricerca.
     *
     * @param name regista del film da cercare
     * @return array di film
     */
    @Override
    public Movie[] searchMoviesDirectedBy(String name) {
        CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
        List<Movie> results = moviesByDirector.search(caseInsensitiveName);
        if (results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    /**
     * Ricerca film per attore.
     * <p>
     * Restituisce i film a cui ha partecipato come attore
     * la persona il cui nome è passato come parametro.
     * <p>
     * Restituisce un vettore vuoto se nessun film rispetta il criterio di ricerca.
     *
     * @param name attore coinvolto nel film da cercare
     * @return array di film
     */
    @Override
    public Movie[] searchMoviesStarredBy(String name) {
        CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
        List<Movie> results = moviesByActor.search(caseInsensitiveName);
        if (results != null) return results.toArray(new Movie[0]);
        return new Movie[0];
    }

    /**
     * Ricerca film più votati.
     * <p>
     * Restituisce gli <code>N</code> film che hanno
     * ricevuto più voti, in ordine decrescente di voti.
     * <p>
     * Se il numero di film totali è minore di N restituisce tutti i film,
     * comunque in ordine.
     *
     * @param N numero di film che la ricerca deve resistuire
     * @return array di film
     */
    @Override
    public Movie[] searchMostVotedMovies(Integer N) {
        if (!areMoviesSortedByVotes) {
            sorter.sort(moviesOrderedByVotes, (Movie a, Movie b) -> b.getVotes() - a.getVotes());
            this.areMoviesSortedByVotes = true;
        }

        int size = moviesOrderedByVotes.size();
        if (N >= size) return moviesOrderedByVotes.toArray(new Movie[0]);
        return moviesOrderedByVotes.subList(0, N).toArray(new Movie[0]);
    }

    /**
     * Ricerca film più recenti.
     * <p>
     * Restituisce gli <code>N</code> film più recenti,
     * in base all'anno di uscita in sala confrontato con l'anno corrente.
     * <p>
     * Se il numero di film totali è minore di N restituisce tutti i film,
     * comunque in ordine.
     *
     * @param N numero di film che la ricerca deve resistuire
     * @return array di film
     */
    @Override
    public Movie[] searchMostRecentMovies(Integer N) {
        if (!areMoviesSortedByYear) {
            sorter.sort(moviesOrderedByYear, (Movie a, Movie b) -> b.getYear() - a.getYear());
            this.areMoviesSortedByYear = true;
        }

        int size = moviesOrderedByYear.size();
        if (N >= size) return moviesOrderedByYear.toArray(new Movie[0]);
        return moviesOrderedByYear.subList(0, N).toArray(new Movie[0]);
    }

    /**
     * Ricerca gli attori più attivi.
     * <p>
     * Restituisce gli <code>N</code> attori che hanno partecipato al numero
     * più alto di film
     * <p>
     * Se il numero di attori è minore di N restituisce tutti gli attori,
     * comunque in ordine.
     *
     * @param N numero di attori che la ricerca deve resistuire
     * @return array di attori
     */
    @Override
    public Person[] searchMostActiveActors(Integer N) {
        if (!areActorsSorted) {
            sorter.sort(actors, (Person a, Person b) -> moviesByActor.search(new CaseInsensitiveString(b.getName())).size() - moviesByActor.search(new CaseInsensitiveString(a.getName())).size());
            this.areActorsSorted = true;
        }

        int size = actors.size();
        if (N >= size) return actors.toArray(new Person[0]);
        return actors.subList(0, N).toArray(new Person[0]);
    }

    /**
     * Identificazione delle collaborazioni
     * dirette di un attore
     * <p>
     * Restituisce gli attori che hanno partecipato
     * ad almeno un film con l'attore
     * <code>actor</code> passato come parametro.
     *
     * @param actor attore di cui cercare i collaboratori diretti
     * @return array di persone
     */
    @Override
    public Person[] getDirectCollaboratorsOf(Person actor) {
        return graph.getDirectCollaboratorsOf(actor);
    }

    /**
     * Identificazione del team di un attore
     * <p>
     * Restituisce gli attori che hanno
     * collaborazioni dirette o indirette
     * con l'attore <code>actor</code> passato come parametro.
     * <p>
     * Vedi slide per maggiori informazioni su collaborazioni e team.
     *
     * @param actor attore di cui individuare il team
     * @return array di persone
     */
    @Override
    public Person[] getTeamOf(Person actor) {
        return graph.getTeamOf(actor);
    }

    /**
     * Identificazione dell'insieme di collaborazioni
     * caratteristiche (ICC) del team di cui un attore fa parte
     * e che ha lo score complessivo più alto
     * <p>
     * Vedi slide per maggiori informazioni su score e ICC.
     * <p>
     * Si noti che questo metodo richiede l'invocazione
     * del metodo precedente <code>getTeamOf(Person actor)</code>
     *
     * @param actor attore di cui individuare il team
     * @return array di collaborazioni
     */
    @Override
    public Collaboration[] maximizeCollaborationsInTheTeamOf(Person actor) {
        return graph.maximiseCollaborations(actor);
    }

    /**
     * Inserisce un film nelle strutture dati di MovidaCore.
     *
     * @param movie Il film da inserire
     */
    private void importMovie(Movie movie) {
        String title = movie.getTitle();
        CaseInsensitiveString caseInsensitiveTitle = new CaseInsensitiveString(title);
        Integer year = movie.getYear();
        Person director = movie.getDirector();
        String directorName = director.getName();
        CaseInsensitiveString caseInsensitiveDirectorName = new CaseInsensitiveString(directorName);
        Person[] cast = movie.getCast();

        if (moviesByTitle.search(caseInsensitiveTitle) == null) {
            moviesOrderedByVotes.add(movie);
            moviesOrderedByYear.add(movie);
            this.areMoviesSortedByVotes = this.areMoviesSortedByYear = false;

            moviesByTitle.insert(caseInsensitiveTitle, movie);

            // Se non c'era nessun film con quest'anno registrato
            if (moviesByYear.search(year) == null) {
                moviesByYear.insert(year, new ArrayList<>());
            }
            moviesByYear.search(year).add(movie);

            // Se non era fra i direttori finora, aggiungilo
            if (personByName.search(caseInsensitiveDirectorName) == null) {
                personByName.insert(caseInsensitiveDirectorName, director);
                people.add(director);
            }
            if(moviesByDirector.search(caseInsensitiveDirectorName) == null) {
                moviesByDirector.insert(caseInsensitiveDirectorName, new ArrayList<>());
            }
            moviesByDirector.search(caseInsensitiveDirectorName).add(movie);

            for (Person actor : cast) {
                String name = actor.getName();
                CaseInsensitiveString caseInsensitiveName = new CaseInsensitiveString(name);
                // Se non era fra gli attori finora, aggiungilo
                if (moviesByActor.search(caseInsensitiveName) == null) {
                    moviesByActor.insert(caseInsensitiveName, new ArrayList<>());
                    actors.add(actor);
                    this.areActorsSorted = false;
                }
                moviesByActor.search(caseInsensitiveName).add(movie);

                if (personByName.search(caseInsensitiveName) == null) {
                    personByName.insert(caseInsensitiveName, actor);
                    people.add(actor);
                }
            }

            this.graph.addMovie(movie);
        } else {
            deleteMovieByTitle(title);
            importMovie(movie);
        }
    }
}
