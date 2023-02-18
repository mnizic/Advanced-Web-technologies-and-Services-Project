package org.foi.nwtis.mnizic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

/**
 * DAO klasa korisnika
 */
public class KorisnikDAO {
	private String url;
	private String korisnikDB;
	private String lozinkaDB;
	private List<Korisnik> korisnici = null;
	private List<Grupa> grupe = null;

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
	 * Dohvati sve korisnike.
	 *
	 * @param konfig
	 * @return lista korisnika
	 */
	public List<Korisnik> dohvatiSveKorisnike(PostavkeBazaPodataka konfig) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaSveKorisnike();
		try {
			Class.forName(konfig.getDriverDatabase(url));
			dohvacanjeSvihKorisnika(upit);
		} catch (ClassNotFoundException ex) {
			System.out.println("Nisu dohvaćeni svi korisnici.");
		}
		return korisnici;
	}

	/**
	 * Dohvacanje svih korisnika.
	 *
	 * @param upit
	 */
	private void dohvacanjeSvihKorisnika(String upit) {
		korisnici = new ArrayList<Korisnik>();
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {

			while (rs.next()) {
				String korime = rs.getString("korisnik");
				String ime = rs.getString("ime");
				String prezime = rs.getString("prezime");
				String lozinka = rs.getString("lozinka");
				String email = rs.getString("email");

				kreirajIDodajKorisnika(korime, ime, prezime, lozinka, email);
			}
		} catch (SQLException ex) {
			System.out.println("Greška prilikom dohvaćanja korisnika.");
		}
	}

	/**
	 * Kreiraj I dodaj korisnika.
	 *
	 * @param korime
	 * @param ime
	 * @param prezime
	 * @param lozinka
	 * @param email
	 */
	private void kreirajIDodajKorisnika(String korime, String ime, String prezime, String lozinka, String email) {
		Korisnik k = new Korisnik();
		k.setKorIme(korime);
		k.setIme(ime);
		k.setPrezime(prezime);
		k.setLozinka(lozinka);
		k.setEmail(email);
		korisnici.add(k);
	}

	/**
	 * Dohvati upit za sve korisnike.
	 *
	 * @return upit
	 */
	private String dohvatiUpitZaSveKorisnike() {
		return "SELECT * FROM KORISNICI";
	}

	/**
	 * Dodaj novog korisnika.
	 *
	 * @param konfig
	 * @param noviKorisnik
	 * @return boolean
	 */
	public boolean dodajNovogKorisnika(PostavkeBazaPodataka konfig, Korisnik noviKorisnik) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaDodavanjeKorisnika();	
		try {
			Class.forName(konfig.getDriverDatabase(url));
			boolean provjeraUnosa = unosKorisnika(upit, noviKorisnik);
			if(provjeraUnosa) {
				upit = dohvatiUpitZaDodavanjeUloge();
				return unosUloge(upit, noviKorisnik.getKorIme());
			}
		} catch (ClassNotFoundException ex) {
			System.out.println("Problem pri unosu korisnika u bazu.");
		}
		
		return false;
	}

	/**
	 * Unos uloge.
	 *
	 * @param upit
	 * @param korisnik
	 * @return boolean
	 */
	private boolean unosUloge(String upit, String korisnik) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			ps.setString(1, korisnik);
			
			boolean provjera = ps.executeUpdate()==1 ? true : false;
			if(provjera) return true;
		} catch (SQLException ex) {
			System.out.println("Uloga nije unesena.");
		}
		return false;
	}

	/**
	 * Dohvati upit za dodavanje uloge.
	 *
	 * @return upit
	 */
	private String dohvatiUpitZaDodavanjeUloge() {
		return "INSERT INTO ULOGE (korisnik, grupa) VALUES (?, 'nwtis')";
	}

	/**
	 * Unos korisnika.
	 *
	 * @param upit
	 * @param noviKorisnik
	 * @return boolean
	 */
	private boolean unosKorisnika(String upit, Korisnik noviKorisnik) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			ps.setString(1, noviKorisnik.getKorIme());
			ps.setString(2, noviKorisnik.getIme());
			ps.setString(3, noviKorisnik.getPrezime());
			ps.setString(4, noviKorisnik.getLozinka());
			ps.setString(5, noviKorisnik.getEmail());
			
			boolean provjera = ps.executeUpdate()==1 ? true : false;
			if(provjera) return true;
		} catch (SQLException ex) {
			System.out.println("Korisnik nije unesen.");
		}
		return false;
	}

	/**
	 * Dohvati upit za dodavanje korisnika.
	 *
	 * @return upit
	 */
	private String dohvatiUpitZaDodavanjeKorisnika() {
		return "INSERT INTO KORISNICI (korisnik, ime, prezime, lozinka, email) VALUES (?, ?, ?, ?, ?)";
	}

	/**
	 * Dohvati trazenog korisnika.
	 *
	 * @param konfig
	 * @param trazeniKorisnik
	 * @return korisnik
	 */
	public Korisnik dohvatiTrazenogKorisnika(PostavkeBazaPodataka konfig, String trazeniKorisnik) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaTrazenogKorisnika(trazeniKorisnik);
		try {
			Class.forName(konfig.getDriverDatabase(url));
			return dohvacanjeTrazenogKorisnika(upit);
		} catch (ClassNotFoundException ex) {
			System.out.println("Traženi korisnik nije pronađen.");
		}
		return null;
	}

	/**
	 * Dohvacanje trazenog korisnika.
	 *
	 * @param upit
	 * @return korisnik
	 */
	private Korisnik dohvacanjeTrazenogKorisnika(String upit) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {

			if (rs.next()) {
				String korime = rs.getString("korisnik");
				String ime = rs.getString("ime");
				String prezime = rs.getString("prezime");
				String lozinka = rs.getString("lozinka");
				String email = rs.getString("email");

				return kreirajIVratiKorisnika(korime, ime, prezime, lozinka, email);
			}
		} catch (SQLException ex) {
			System.out.println(ex);
			System.out.println("Greška prilikom dohvaćanja korisnika.");
		}
		return null;
	}

	/**
	 * Kreiraj I vrati korisnika.
	 *
	 * @param korime
	 * @param ime
	 * @param prezime
	 * @param lozinka
	 * @param email
	 * @return korisnik
	 */
	private Korisnik kreirajIVratiKorisnika(String korime, String ime, String prezime, String lozinka, String email) {
		Korisnik k = new Korisnik();
		k.setKorIme(korime);
		k.setIme(ime);
		k.setPrezime(prezime);
		k.setLozinka(lozinka);
		k.setEmail(email);
		return k;
	}

	/**
	 * Dohvati upit za trazenog korisnika.
	 *
	 * @param trazeniKorisnik
	 * @return upit
	 */
	private String dohvatiUpitZaTrazenogKorisnika(String trazeniKorisnik) {
		return "SELECT * FROM KORISNICI WHERE korisnik = '" + trazeniKorisnik + "'";
	}

	
	/**
	 * Dohvati grupe korisnika.
	 *
	 * @param konfig
	 * @param korisnik
	 * @return lista grupa
	 */
	public List<Grupa> dohvatiGrupeKorisnika(PostavkeBazaPodataka konfig, String korisnik) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaGrupeKorisnika(korisnik);
		try {
			Class.forName(konfig.getDriverDatabase(url));
			dohvacanjeGrupaIzBaze(upit);
		} catch (ClassNotFoundException ex) {
			System.out.println("Traženi korisnik nema grupa ili ne postoji.");
		}
		return grupe;
	}

	/**
	 * Dohvacanje grupa iz baze.
	 *
	 * @param upit
	 */
	private void dohvacanjeGrupaIzBaze(String upit) {
		grupe = new ArrayList<Grupa>();
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {

			while (rs.next()) {
				String grupa = rs.getString("grupa");
				String naziv = rs.getString("naziv");

				kreirajIDodajGrupu(grupa, naziv);
			}
		} catch (SQLException ex) {
			System.out.println("Greška prilikom dohvaćanja korisnika.");
		}
	}

	/**
	 * Kreiraj I dodaj grupu.
	 *
	 * @param grupa
	 * @param naziv
	 */
	private void kreirajIDodajGrupu(String grupa, String naziv) {
		Grupa g = new Grupa();
		g.setGrupa(grupa);
		g.setNaziv(naziv);
		grupe.add(g);
	}

	/**
	 * Dohvati upit za grupe korisnika.
	 *
	 * @param korisnik
	 * @return upit
	 */
	private String dohvatiUpitZaGrupeKorisnika(String korisnik) {
		return "SELECT g.grupa, g.naziv FROM GRUPE g INNER JOIN "
				+ "ULOGE u ON u.grupa = g.grupa WHERE "
				+ "u.korisnik = '" + korisnik + "'";
	}

	
}
