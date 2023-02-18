package org.foi.nwtis.mnizic.aplikacija_3.podaci;

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
import org.foi.nwtis.rest.podaci.AvionLeti;

// TODO: Auto-generated Javadoc
/**
 * Autor: Mario Nižić DAO klasa dolazaka na aerodrom.
 */
public class AerodromiDolasciDAO {
	private String url;
	private String korisnikDB;
	private String lozinkaDB;
	List<AvionLeti> aerodromi = null;

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
	 * Daj dolaske aerodroma.
	 *
	 * @param konfig
	 * @param icao
	 * @param vrsta napisanog vremena
	 * @param vrijeme od
	 * @param vrijeme do
	 * @return lista aviona
	 */
	public List<AvionLeti> dajDolaskeAerodroma(PostavkeBazaPodataka konfig, String icao, String x, String vrijemeOd,
			String vrijemeDo) {
		inicijalizacijaPodataka(konfig);
		int vrsta = Integer.parseInt(x);

		long datumSekunde1 = 0;
		long datumSekunde2 = 0;
		if (vrsta == 0) {
			try {
				Date datum1 = konverzijaStringaUDatum(vrijemeOd);
				long datumMilisekunde1 = datum1.getTime();
				datumSekunde1 = datumMilisekunde1 / 1000;
			} catch (NullPointerException ex) {
				System.out.println("Unesen krivi format.");
			}

			try {
				Date datum2 = konverzijaStringaUDatum(vrijemeDo);
				long datumMilisekunde2 = datum2.getTime();
				datumSekunde2 = datumMilisekunde2 / 1000;
			} catch (NullPointerException ex) {
				System.out.println("Unesen krivi format.");
			}
		} else if (vrsta == 1) {
			try {
				datumSekunde1 = Long.parseLong(vrijemeOd);
				datumSekunde2 = Long.parseLong(vrijemeDo);
			} catch (NumberFormatException ex) {
				System.out.println("Unesen krivi format.");
			}
		} else {
			System.out.println("Neispravno unesena vrsta.");
			return null;
		}
		
		String upit = dohvatiUpitZaDolaskeAerodroma(icao, datumSekunde1, datumSekunde2);
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
	 * @param upit
	 */
	private void dohvacanjeIzBaze(String upit) {
		aerodromi = new ArrayList<AvionLeti>();
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
	 * @param rs
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
	 *
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
	 * @param string datum
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
	 * @param sekunde početka
	 * @param sekunde kraja
	 * @return string
	 */
	private String dohvatiUpitZaDolaskeAerodroma(String icao, long datumSekunde1, long datumSekunde2) {
		return "SELECT * FROM AERODROMI_DOLASCI WHERE "
				+ "estArrivalAirport = '" + icao + "' AND lastSeen BETWEEN "
				+ datumSekunde1 + " AND " + datumSekunde2;
	}

}