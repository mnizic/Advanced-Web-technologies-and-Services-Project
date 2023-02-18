package org.foi.nwtis.mnizic.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Autor: Mario Nižić DAO klasa dolazaka na aerodrom
 */
public class AerodromiDolasciDAO {

	/** Url. */
	private String url;

	/** Korisničko ime */
	private String korisnikDB;

	/** Lozinka */
	private String lozinkaDB;

	/** Lista aerodroma */
	List<AvionLeti> aerodromi = null; 

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
	 * Dohvaćanje upia za spremanje aerodroma.
	 *
	 * @param avion
	 * @return string upit
	 */
	private String dohvatiUpitSpremiAerodrome(AvionLeti avion) {
		return "INSERT INTO AERODROMI_DOLASCI(icao24, firstseen, estdepartureairport, lastseen,"
				+ "estarrivalairport, callsign, estdepartureairporthorizdistance, estdepartureairportvertdistance,"
				+ "estarrivalairporthorizdistance, estarrivalairportvertdistance, departureairportcandidatescount,"
				+ "arrivalairportcandidatescount, `stored`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
	}

	/**
	 * Spremanje u bazu.
	 *
	 * @param upit
	 * @param avion
	 */
	private void spremanjeUBazu(String upit, AvionLeti avion) throws SQLIntegrityConstraintViolationException {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			ps.setString(1, avion.getIcao24());
			ps.setInt(2, avion.getFirstSeen());
			ps.setString(3, avion.getEstDepartureAirport());
			ps.setInt(4, avion.getLastSeen());
			ps.setString(5, avion.getEstArrivalAirport());
			ps.setString(6, avion.getCallsign());
			ps.setInt(7, avion.getEstDepartureAirportHorizDistance());
			ps.setInt(8, avion.getEstDepartureAirportVertDistance());
			ps.setInt(9, avion.getEstArrivalAirportHorizDistance());
			ps.setInt(10, avion.getEstArrivalAirportVertDistance());
			ps.setInt(11, avion.getDepartureAirportCandidatesCount());
			ps.setInt(12, avion.getArrivalAirportCandidatesCount());

			if (avion.getEstDepartureAirport() != null) {
				boolean provjera = ps.executeUpdate() == 1 ? true : false;
				if (provjera) {
					System.out.println("Uspješno unesen aerodrom!");
				} else {
					System.out.println("Greška prilikom unosa aerodroma!");
				}
			}

		} catch (SQLException ex) {
			System.out.println("Aerodrom nije spremljen");
		}
	}

	/**
	 * Spremanje aerodroma.
	 *
	 * @param konfiguracija
	 * @param avion
	 */
	public void spremiAerodrom(PostavkeBazaPodataka konfig, AvionLeti avion) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitSpremiAerodrome(avion);

		try {
			Class.forName(konfig.getDriverDatabase(url));
			spremanjeUBazu(upit, avion);
		} catch (ClassNotFoundException | SQLIntegrityConstraintViolationException ex) {
			System.out.println("Aerodrom nije spremljen u bazu.");
		}
	}

	/**
	 * Daj dolaske aerodroma.
	 *
	 * @param konfig
	 * @param icao
	 * @param dan
	 * @return lista
	 */
	public List<AvionLeti> dajDolaskeAerodroma(PostavkeBazaPodataka konfig, String icao, String dan) {
		inicijalizacijaPodataka(konfig);
		Date datum = konverzijaStringaUDatum(dan);
		long datumMilisekunde = datum.getTime();
		long datumSekunde = datumMilisekunde / 1000;
		String upit = dohvatiUpitZaDolaskeAerodroma(icao, datumSekunde);
		try {
			Class.forName(konfig.getDriverDatabase(url));
			dohvacanjeIzBaze(upit);
		} catch (ClassNotFoundException ex) {
			System.out.println("Aerodromi nisu dohvaceni.");
		}
		return aerodromi;
	}

	/**
	 * Dohvacanje iz baze.
	 *
	 * @param upit the upit
	 */
	private void dohvacanjeIzBaze(String upit) {
		aerodromi = new ArrayList<>();
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {

			while (rs.next()) {
				dohvacanjePodataka(rs);
			}
		} catch (SQLException ex) {
			System.out.println("Ne postoji aerodrom s danim specifikacijama.");
		}

	}

	/**
	 * Dohvacanje podataka.
	 *
	 * @param resultset
	 * @throws SQLException
	 */
	private void dohvacanjePodataka(ResultSet rs) throws SQLException {
		String i = rs.getString("icao24");
		int fs = Integer.parseInt(rs.getString("firstSeen"));
		String eda = rs.getString("estDepartureAirport");
		int ls = Integer.parseInt(rs.getString("lastSeen"));
		String eaa = rs.getString("estArrivalAirport");
		String cs = rs.getString("callsign");
		int edahd = Integer.parseInt(rs.getString("estDepartureAirportHorizDistance"));
		int edavd = Integer.parseInt(rs.getString("estDepartureAirportVertDistance"));
		int eaahd = Integer.parseInt(rs.getString("estArrivalAirportHorizDistance"));
		int eaavd = Integer.parseInt(rs.getString("estArrivalAirportVertDistance"));
		int dacc = Integer.parseInt(rs.getString("departureAirportCandidatesCount"));
		int aacc = Integer.parseInt(rs.getString("arrivalAirportCandidatesCount"));
		dodajAvionLeti(i, fs, eda, ls, eaa, cs, edahd, edavd, eaahd, eaavd, dacc, aacc);
	}

	/**
	 * Dodaj avion leti.
	 */
	private void dodajAvionLeti(String i, int fs, String eda, int ls, String eaa, String cs, int edahd, int edavd,
			int eaahd, int eaavd, int dacc, int aacc) {
		AvionLeti avionLeti = new AvionLeti();
		avionLeti.setIcao24(i);
		avionLeti.setFirstSeen(fs);
		avionLeti.setEstDepartureAirport(eda);
		avionLeti.setLastSeen(ls);
		avionLeti.setEstArrivalAirport(eaa);
		avionLeti.setCallsign(cs);
		avionLeti.setEstDepartureAirportHorizDistance(edahd);
		avionLeti.setEstDepartureAirportVertDistance(edavd);
		avionLeti.setEstArrivalAirportHorizDistance(eaahd);
		avionLeti.setEstArrivalAirportVertDistance(eaavd);
		avionLeti.setDepartureAirportCandidatesCount(dacc);
		avionLeti.setArrivalAirportCandidatesCount(aacc);
		aerodromi.add(avionLeti);
	}

	/**
	 * Konverzija stringa U datum.
	 *
	 * @param stringDatum datum u stringu
	 * @return datum
	 */
	private Date konverzijaStringaUDatum(String stringDatum) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date datum = null;
		try {
			datum = dateFormat.parse(stringDatum);
		} catch (ParseException ex) {
			System.out.println("Problem s parsiranjem string u datum.");
		}
		return datum;
	}

	/**
	 * Dohvati upit za dolaske aerodroma.
	 *
	 * @param icao
	 * @param datumSekunde
	 * @return string
	 */
	private String dohvatiUpitZaDolaskeAerodroma(String icao, long datumSekunde) {
		long datumKrajDana = datumSekunde + 86400;
		return "SELECT * FROM AERODROMI_DOLASCI WHERE estArrivalAirport = '" + icao + "' AND lastSeen BETWEEN "
				+ datumSekunde + " AND " + datumKrajDana;
	}

}
