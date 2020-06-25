package movida.commons;

import java.util.ArrayList;

public class Collaboration {

	Person actorA;
	Person actorB;
	ArrayList<Movie> movies;
	Integer count;

	public Collaboration(Person actorA, Person actorB) {
		this.actorA = actorA;
		this.actorB = actorB;
		this.movies = new ArrayList<Movie>();
		this.count = 1;
	}

	public Person getActorA() {
		return actorA;
	}

	public Person getActorB() {
		return actorB;
	}

	public Double getScore() {

		Double score = 0.0;

		for (Movie m : movies)
			score += m.getVotes();

		return score / movies.size();
	}

	public ArrayList<Movie> getMovies() {
		return movies;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Collaboration)) {
			return false;
		}
		Collaboration other = (Collaboration) obj;
		boolean sameEquals = this.actorA.equals(other.actorA) && this.actorB.equals(other.actorB);
		boolean symmetricEquals = this.actorA.equals(other.actorB) && this.actorB.equals(other.actorA);

		return sameEquals || symmetricEquals;
	}
}
