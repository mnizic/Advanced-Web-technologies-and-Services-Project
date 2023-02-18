package org.foi.nwtis.mnizic.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.ws.rs.core.Response;


/**
 * Autor: Mario Nižić
 * DAO klasa za probleme aerodroma.
 */
public class AerodromiProblemiDAO {
	
	/** Url. */
	private String url;
	
	/** Korisničko ime */
	private String korisnikDB;
	
	/** Lozinka */
	private String lozinkaDB;

	/**
	 * Inicijalizacija podataka.
	 *
	 * @param konfiguracija
	 */
	private void inicijalizacijaPodataka(PostavkeBazaPodataka konfig) {
		url = konfig.getServerDatabase() + konfig.getUserDatabase();
		korisnikDB = konfig.getUserUsername();
		lozinkaDB = konfig.getUserPassword();		
	}

	/**
	 * Dodaj novi problem.
	 *
	 * @param konfiguracija
	 * @param ident
	 * @param description
	 */
	public void dodajNoviProblem(PostavkeBazaPodataka konfig, String ident, String desc) {
		inicijalizacijaPodataka(konfig);

		String upit = dohvatiUpitSpremiProbleme();
		try {
			Class.forName(konfig.getDriverDatabase(url));
			spremanjeUBazu(upit, ident, desc);
		} catch (ClassNotFoundException ex) {
			System.out.println("Greška prilikom spremanja u bazu.");
		}
	}

	/**
	 * Dohvati upit za spremanje problema.
	 *
	 * @return upit
	 */
	private String dohvatiUpitSpremiProbleme() {
		return "INSERT INTO AERODROMI_PROBLEMI(ident, description, `stored`) VALUES (?, ?, NOW())";
	}
	
	/**
	 * Spremanje u bazu.
	 *
	 * @param upit
	 * @param ident
	 * @param desc
	 */
	private void spremanjeUBazu(String upit, String ident, String desc){
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			ps.setString(1, ident);
			ps.setString(2, desc);
			
			boolean provjera = ps.executeUpdate()==1 ? true : false;
			if(provjera) {
				System.out.println("Uspješno unesen aerodrom!");
			} else {
				System.out.println("Greška prilikom unosa aerodroma!");
			}
		} catch (SQLException ex) {
			System.out.println("Problem nije spremljen uspješno.");
		}
	}
}
