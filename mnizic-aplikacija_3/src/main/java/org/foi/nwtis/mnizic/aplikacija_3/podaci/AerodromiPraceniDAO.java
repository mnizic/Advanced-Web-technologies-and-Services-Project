package org.foi.nwtis.mnizic.aplikacija_3.podaci;

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
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;

import jakarta.ws.rs.core.Response;

/**
 * Autor: Mario Nižić
 * DAO klasa za praćene aerodrome
 */
public class AerodromiPraceniDAO {
	
	/** Url. */
	private String url;
	
	/** Korisničko ime */
	private String korisnikDB;
	
	/** Lozinka */
	private String lozinkaDB;
	
	/** Lista aerodroma */;
	List<Aerodrom> aerodromi = null;
	
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
	 * Dohvaćanje upita za sve aerodrome.
	 *
	 * @param avion
	 * @return upit u stringu
	 */
	private String dohvatiUpitZaSveAerodrome() {
		return "SELECT a.ident, type, name, elevation_ft, continent, iso_country, iso_region, municipality, "
				+ "gps_code, iata_code, local_code, coordinates FROM airports a JOIN AERODROMI_PRACENI ap "
				+ "ON a.ident = ap.ident";
	}
	
	/**
	 * Dohvacanje iz baze.
	 *
	 * @param upit the upit
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
			System.out.println("Aerodrom nije dohvacen.");
		}
	}
	
	/**
	 * Kreiranje i dodavanje aerodrom.
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
	 * @param koordinate
	 * @return lokacija
	 */
	private Lokacija dohvatiLokaciju(String coordinates) {
		return new Lokacija(coordinates.split(",")[1], coordinates.split(",")[0]);
	}
	
	/**
	 * Dohvati iz baze aerodrom praceni.
	 *
	 * @param konfiguracija
	 * @return lista
	 */
	public List<Aerodrom> dohvatiIzBazeAerodromPraceni(PostavkeBazaPodataka konfig){
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaSveAerodrome();

		try {
			Class.forName(konfig.getDriverDatabase(url));
			dohvacanjeIzBaze(upit);
		} catch (ClassNotFoundException ex) {
			System.out.println("Problem prilikom dohvacanja aerodroma.");
		}
		System.out.println("Aerodromi praceni size: " + aerodromi.size());
		return aerodromi;
	}
	
	/**
	 * Dodavanje aerodroma.
	 *
	 * @param konfiguracija
	 * @param aerodrom
	 * @return odgovor
	 */
	public Response dodavanjeAerodroma(PostavkeBazaPodataka konfig, Aerodrom aerodrom) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaDodavanjeAerodroma();
		try {
			Class.forName(konfig.getDriverDatabase(url));
			return unosUBazu(upit, aerodrom);
		} catch (ClassNotFoundException ex) {
			System.out.println("Problem pri unosu.");
		}
		return Response.status(Response.Status.BAD_REQUEST).entity("Nije unesen ispravan aerodrom.").build();
	}

	/**
	 * Unos u bazu.
	 *
	 * @param upit
	 * @param aerodrom
	 * @return odgovor
	 */
	private Response unosUBazu(String upit, Aerodrom aerodrom) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			ps.setString(1, aerodrom.getIcao());
			
			boolean provjera = ps.executeUpdate()==1 ? true : false;
			if(provjera) {
				return Response
	                    .status(Response.Status.OK)
	                    .entity("Aerodrom unesen u bazu.")
	                    .build();
			}
		} catch (SQLException ex) {
			System.out.println("Aerodrom nije unesen.");
		}
		return Response
                .status(Response.Status.NOT_FOUND)
                .entity("Greska prilikom unosa.")
                .build();
		
	}

	/**
	 * Dohvati upit za dodavanje aerodroma.
	 *
	 * @return upit
	 */
	private String dohvatiUpitZaDodavanjeAerodroma() {
		return "INSERT INTO AERODROMI_PRACENI(ident, `stored`) VALUES (?, NOW())";
	}
}
