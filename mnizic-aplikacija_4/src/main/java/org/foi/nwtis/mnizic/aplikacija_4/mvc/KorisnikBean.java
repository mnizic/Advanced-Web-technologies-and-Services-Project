package org.foi.nwtis.mnizic.aplikacija_4.mvc;


import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;

/**
 * @author Mario Nižić
 * Zrno korisnika koje traje koliko i sesija
 */
@SessionScoped
public class KorisnikBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6095779390320430856L;
	private String korime;
	private String lozinka;
	private boolean admin;
	
	/**
	 * Provjerava je li korisnik admin
	 *
	 * @return boolean
	 */
	public boolean isAdmin() {
		return admin;
	}
	
	/**
	 * Postavlja vrijednost admina
	 *
	 * @param admin 
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	/**
	 * Dohvaća korime.
	 *
	 * @return korime
	 */
	public String getKorime() {
		return korime;
	}
	
	/**
	 * Postavlja korime.
	 *
	 * @param korime
	 */
	public void setKorime(String korime) {
		this.korime = korime;
	}
	
	/**
	 * Dohvaća lozinka.
	 *
	 * @return lozinka
	 */
	public String getLozinka() {
		return lozinka;
	}
	
	/**
	 * Postavlja lozinka.
	 *
	 * @param lozinka the new lozinka
	 */
	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}	
}
