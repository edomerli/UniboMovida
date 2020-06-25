/* 
 * Copyright (C) 2020 - Angelo Di Iorio
 * 
 * Progetto Movida.
 * Corso di Algoritmi e Strutture Dati
 * Laurea in Informatica, UniBO, a.a. 2019/2020
 * 
*/
package movida.commons;

/**
 * Classe usata per rappresentare un film
 * nell'applicazione Movida.
 * 
 * Un film � identificato in modo univoco dal titolo 
 * case-insensitive, senza spazi iniziali e finali, senza spazi doppi. 
 * 
 * La classe pu� essere modicata o estesa ma deve implementare tutti i metodi getter
 * per recupare le informazioni caratterizzanti di un film.
 * 
 */
public class Movie {
	
	private String title;
	private Integer year;
	private Integer votes;
	private Person[] cast;
	private Person director;
	
	public Movie(String title, Integer year, Integer votes,
			Person[] cast, Person director) {
		this.title = title;
		this.year = year;
		this.votes = votes;
		this.cast = cast;
		this.director = director;
	}

	public String getTitle() {
		return this.title;
	}

	public Integer getYear() {
		return this.year;
	}

	public Integer getVotes() {
		return this.votes;
	}

	public Person[] getCast() {
		return this.cast;
	}

	public Person getDirector() {
		return this.director;
	}

	//Aggiunto
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("Title: " + this.title + "\n");
		s.append("Year: " + this.year + "\n");
		s.append("Director: " + this.director.getName() + "\n");
		s.append("Cast: ");
		for (int i = 0; i < this.cast.length; i++) {
			s.append(this.cast[i].getName());
			if (i != this.cast.length - 1) {
				s.append(", ");
			}
		}
		s.append("\n");
		s.append("Votes: " + this.votes);
		return s.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Movie)) {
			return false;
		}
		Movie other = (Movie) obj;
		return this.title.equals(other.title);
	}

	@Override
	public int hashCode() {
		return this.title.hashCode();
	}
}
