package org.foi.nwtis.mnizic.aplikacija_2.podaci;

/**
 * Autor:Mario Nižić
 * Klasa objekta problema.
 */
public class Problem {
	
	/** Id. */
	protected int id;
    
    /** Icao */
    protected String ident;
    
    /** Opis */
    protected String description;
    
    /** Vrijeme zapisa */
    protected String timestamp;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public String getIdent() {
		return ident;
	}
	

	public void setIdent(String ident) {
		this.ident = ident;
	}
	

	public String getDescription() {
		return description;
	}
	

	public void setDescription(String description) {
		this.description = description;
	}
	

	public String getTimestamp() {
		return timestamp;
	}
	

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	

	public Problem(int id, String ident, String description, String timestamp) {
		super();
		this.id = id;
		this.ident = ident;
		this.description = description;
		this.timestamp = timestamp;
	}

    
}
