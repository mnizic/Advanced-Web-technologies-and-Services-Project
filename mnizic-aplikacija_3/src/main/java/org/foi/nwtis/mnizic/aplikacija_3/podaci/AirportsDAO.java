package org.foi.nwtis.mnizic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;

import jakarta.ws.rs.core.Response;

/**
 * Autor:Mario Nižić
 * DAO klasa aerodroma.
 */
public class AirportsDAO {
	private String url;
	private String korisnikDB;
	private String lozinkaDB;
	List<Aerodrom> aerodromi = null;
	
	/**
	 * Inicijalizacija podataka.
	 *
	 * @param konfig
	 */
	private void inicijalizacijaPodataka(PostavkeBazaPodataka konfig) {
		url = konfig.getServerDatabase() + konfig.getUserDatabase();
		korisnikDB = konfig.getUserUsername();
		lozinkaDB = konfig.getUserPassword();
	}
	
	/**
	 * Dohvati upit dohvati sve aerodrome.
	 *
	 * @return upit
	 */
	private String dohvatiUpitDohvatiSveAerodrome() {
		return "SELECT ident, type, name, elevation_ft, continent, iso_country, iso_region, municipality, "
				+ "gps_code, iata_code, local_code, coordinates FROM airports";
	}
	
	/**
	 * Dohvacanje iz baze.
	 *
	 * @param upit
	 */
	private void dohvacanjeIzBaze(String upit){
		aerodromi = new ArrayList<>();
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {

			while (rs.next()) {
				String ident = rs.getString("ident");
				String name = rs.getString("name");
				String iso_country = rs.getString("iso_country");
				String coordinates = rs.getString("coordinates");

				Lokacija location = dohvatiLokaciju(coordinates);
				kreirajIDodajAerodrom(ident, name, iso_country, location);
			}
		} catch (SQLException ex) {
			System.out.println("Greška prilikom dohvaćanja aerodroma.");
		}
	}
	
	/**
	 * Kreiraj I dodaj aerodrom.
	 *
	 * @param ident
	 * @param name
	 * @param iso_country
	 * @param location
	 */
	private void kreirajIDodajAerodrom(String ident, String name, String iso_country, Lokacija location) {
		Aerodrom a = new Aerodrom(ident, name, iso_country, location);
		aerodromi.add(a);
	}

	/**
	 * Dohvati lokaciju.
	 *
	 * @param coordinates
	 * @return lokacija
	 */
	private Lokacija dohvatiLokaciju(String coordinates) {
		return new Lokacija(coordinates.split(",")[1], coordinates.split(",")[0]);
	}
	
	/**
	 * Dohvati sve aerodrome.
	 *
	 * @param konfig
	 * @return lista aerodroma
	 */
	public List<Aerodrom> dohvatiSveAerodrome(PostavkeBazaPodataka konfig) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitDohvatiSveAerodrome();

		try {
			Class.forName(konfig.getDriverDatabase(url));
			dohvacanjeIzBaze(upit);
		} catch (ClassNotFoundException ex) {
			System.out.println("Nisu dohvaćeni svi aerodromi.");
		}

		return aerodromi;
	}

	/**
	 * Dohvati aerodrom.
	 *
	 * @param konfig
	 * @param icao
	 * @return aerodrom
	 */
	public Aerodrom dohvatiAerodrom(PostavkeBazaPodataka konfig, String icao){
		inicijalizacijaPodataka(konfig);
		String upit = "SELECT a.ident, type, name, elevation_ft, continent, iso_country, iso_region, municipality, "
				+ "gps_code, iata_code, local_code, coordinates FROM airports AS a WHERE a.ident LIKE UPPER('" + icao + "')";
		try {
			Class.forName(konfig.getDriverDatabase(url));
			dohvacanjeIzBaze(upit);
		} catch (ClassNotFoundException ex) {
			System.out.println("Traženi aerodrom nije dohvaćen.");
		}
		if(aerodromi.size() > 0) {
			return aerodromi.get(0);
		}
		return null;
	}
}
