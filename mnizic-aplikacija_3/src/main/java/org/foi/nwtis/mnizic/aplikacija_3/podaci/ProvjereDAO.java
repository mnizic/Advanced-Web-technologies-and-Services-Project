package org.foi.nwtis.mnizic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * DAO klasa provjera.
 */
public class ProvjereDAO {
	private String url;
	private String korisnikDB;
	private String lozinkaDB;
	private int rokTrajanja;
	private String adminGrupa;

	/**
	 * Inicijalizacija podataka.
	 *
	 * @param konfig
	 */
	private void inicijalizacijaPodataka(PostavkeBazaPodataka konfig) {
		url = konfig.getServerDatabase() + konfig.getUserDatabase();
		korisnikDB = konfig.getUserUsername();
		lozinkaDB = konfig.getUserPassword();
		rokTrajanja = Integer.parseInt(konfig.dajPostavku("zeton.trajanje"));
		adminGrupa = konfig.dajPostavku("sustav.administratori");
	}

	/**
	 * Daj provjeru korisnika.
	 *
	 * @param konfig
	 * @param korisnik
	 * @param lozinka
	 * @return zeton
	 */
	public Zeton dajProvjeruKorisnika(PostavkeBazaPodataka konfig, String korisnik, String lozinka) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaProvjeruKorPodataka(korisnik, lozinka);
		boolean provjera;
		try {
			Class.forName(konfig.getDriverDatabase(url));
			provjera = provjeraKorisnikaUBazi(upit);

			if (provjera) {
				upit = dohvatiUpitZaProvjeruRokaTrajanja(korisnik);
				int rokTrajanjaSekunde = vratiRokTrajanjaPosljednjegZetona(upit);
				int trenutnoVrijeme = (int) (System.currentTimeMillis() / 1000);
				
				if(rokTrajanjaSekunde > trenutnoVrijeme) {
					upit = dohvatiUpitZaPosljednjiZeton(korisnik);
					return vratiPosljednjiZeton(upit);
				}

				upit = dohvatiUpitZaKreiranjeZetona();
				if (unosNovogZetona(upit, korisnik)) {
					upit = dohvatiUpitZaPosljednjiZeton(korisnik);
					return vratiPosljednjiZeton(upit);
				}
			}
		} catch (ClassNotFoundException ex) {
			System.out.println("Navedeni korisnički podaci nisu ispravni.");
		}

		return null;
	}

	/**
	 * Vrati rok trajanja posljednjeg zetona.
	 *
	 * @param upit
	 * @return vrijeme u sekundama
	 */
	private int vratiRokTrajanjaPosljednjegZetona(String upit) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {
			if (rs.next()) {
				int rokTrajanja = rs.getInt("roktrajanja");
				return rokTrajanja;
			}
		} catch (SQLException ex) {
			System.out.println("Greska prilikom dohvaćanja žetona.");
		}
		return 0;
	}

	/**
	 * Dohvati upit za provjeru roka trajanja.
	 *
	 * @param korisnik
	 * @return upit
	 */
	private String dohvatiUpitZaProvjeruRokaTrajanja(String korisnik) {
		return "SELECT roktrajanja FROM ZETONI WHERE korisnik = '" + korisnik + "' ORDER BY id DESC LIMIT 1";
	}

	/**
	 * Dohvati upit za posljednji zeton.
	 *
	 * @param korisnik
	 * @return upit
	 */
	private String dohvatiUpitZaPosljednjiZeton(String korisnik) {
		return "SELECT id, roktrajanja FROM ZETONI WHERE korisnik = '" + korisnik + "' ORDER BY id DESC LIMIT 1";
	}

	/**
	 * Vrati posljednji zeton.
	 *
	 * @param upit
	 * @return zeton
	 */
	private Zeton vratiPosljednjiZeton(String upit) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {
			if (rs.next()) {
				int id = rs.getInt("id");
				int rokTrajanja = rs.getInt("roktrajanja");
				Zeton z = new Zeton();
				z.setT(id);
				z.setV(rokTrajanja);
				return z;
			}
		} catch (SQLException ex) {
			System.out.println("Greska prilikom dohvaćanja žetona.");
		}
		return null;
	}

	/**
	 * Unos novog zetona.
	 *
	 * @param upit
	 * @param korisnik
	 * @return boolean
	 */
	private boolean unosNovogZetona(String upit, String korisnik) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			int trenutnoVrijemeSekunde = (int) (System.currentTimeMillis() / 1000);

			rokTrajanja += trenutnoVrijemeSekunde;
			ps.setInt(1, rokTrajanja);
			ps.setString(2, korisnik);

			boolean provjera = ps.executeUpdate() == 1 ? true : false;
			if (provjera) {
				System.out.println("Žeton kreiran.");
				return true;
			} else {
				System.out.println("Žeton nije kreiran.");
			}
		} catch (SQLException ex) {
			System.out.println("Žeton nije kreiran.");
		}
		return false;
	}

	/**
	 * Dohvati upit za kreiranje zetona.
	 *
	 * @return upit
	 */
	private String dohvatiUpitZaKreiranjeZetona() {
		return "INSERT INTO ZETONI (roktrajanja, korisnik, status) VALUES (?, ?, 1)";
	}

	/**
	 * Provjera korisnika U bazi.
	 *
	 * @param upit
	 * @return boolean
	 */
	private boolean provjeraKorisnikaUBazi(String upit) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {
			if (rs.next()) {
				return true;
			}
		} catch (SQLException ex) {
			System.out.println("Korisnik ne postoji.");
		}
		return false;
	}

	/**
	 * Dohvati upit za provjeru korisničkih podataka.
	 *
	 * @param korisnik
	 * @param lozinka
	 * @return upit
	 */
	private String dohvatiUpitZaProvjeruKorPodataka(String korisnik, String lozinka) {
		return "SELECT * FROM KORISNICI WHERE korisnik = '" + korisnik + "' AND lozinka = '" + lozinka + "'";
	}

	/**
	 * Provjeri zeton.
	 *
	 * @param konfig
	 * @param korisnik
	 * @param lozinka
	 * @param zeton 
	 * @return odgovor
	 */
	public String provjeriZeton(PostavkeBazaPodataka konfig, String korisnik, String lozinka, String zeton) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaDohvacanjeZetona(zeton);
		return dohvatiIzBazeZetonIProvjeri(upit, korisnik);
	}

	/**
	 * Dohvati iz baze zeton I provjeri.
	 *
	 * @param upit
	 * @param korisnik1
	 * @return odgovor
	 */
	private String dohvatiIzBazeZetonIProvjeri(String upit, String korisnik1) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {
			if (rs.next()) {
				int id = rs.getInt("id");
				int rokTrajanja = rs.getInt("roktrajanja");
				String korisnik2 = rs.getString("korisnik");
				int status = rs.getInt("status");

				int trenutnoVrijeme = (int) (System.currentTimeMillis() / 1000);

				if (!korisnik1.equals(korisnik2)) {
					return "401";
				} else if (rokTrajanja < trenutnoVrijeme) {
					upit = dohvatiUpitZaDeaktiviranjeZetona();
					deaktivirajZeton(upit, id);
					return "408";
				}

				if (status == 1) {
					return "200";
				} else {
					return "403";
				}
			}
		} catch (SQLException ex) {
			System.out.println("Greska prilikom dohvaćanja žetona.");
		}
		return "404";
	}

	/**
	 * Dohvati upit za dohvacanje zetona.
	 *
	 * @param zeton
	 * @return upit
	 */
	private String dohvatiUpitZaDohvacanjeZetona(String zeton) {
		return "SELECT * FROM ZETONI WHERE id = '" + zeton + "'";
	}

	/**
	 * Deaktiviraj zeton.
	 *
	 * @param konfig
	 * @param korisnik
	 * @param lozinka
	 * @param zeton
	 * @return odgovor
	 */
	public String deaktivirajZeton(PostavkeBazaPodataka konfig, String korisnik, String lozinka, String zeton) {
		inicijalizacijaPodataka(konfig);
		String upit = dohvatiUpitZaDohvacanjeZetona(zeton);
		String provjera = dohvatiIzBazeZetonIProvjeri(upit, korisnik);

		if(provjera.equals("200")) {
			upit = dohvatiUpitZaDeaktiviranjeZetona();
			deaktivirajZeton(upit, Integer.parseInt(zeton));
		} 
		return provjera;		
	}

	/**
	 * Deaktiviraj zeton.
	 *
	 * @param upit 
	 * @param zeton
	 */
	private void deaktivirajZeton(String upit, int zeton) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			ps.setInt(1, zeton);

			boolean provjera = ps.executeUpdate() == 1 ? true : false;
			if (provjera) {
				System.out.println("Žeton deaktiviran.");
			} else {
				System.out.println("Žeton nije deaktiviran.");
			}
		} catch (SQLException ex) {
			System.out.println("Žeton nije deaktiviran.");
		}	
	}

	/**
	 * Dohvati upit za deaktiviranje zetona.
	 *
	 * @return upit
	 */
	private String dohvatiUpitZaDeaktiviranjeZetona() {
		return "UPDATE ZETONI SET status = 0 WHERE id = ?";
	}

	/**
	 * Provjeri aktivnost zetona korisnika.
	 *
	 * @param konfig
	 * @param korisnik
	 * @param lozinka
	 * @param korisnik koji se provjerava
	 * @return odgovor
	 */
	public String provjeriAktivnostZetonaKorisnika(PostavkeBazaPodataka konfig, String korisnik, String lozinka,
			String k) {
		inicijalizacijaPodataka(konfig);
		String upit;
		upit = dohvatiUpitProvjeriAktivnostZetonaKorisnika(k);
		boolean provjera = dohvatiIzBazeAktivneZetoneKorisnika(upit);
		if(provjera) {
			if(korisnik.equals(k)) {
				upit = dohvatiUpitDeaktivirajSveZetoneKorisnika();
				return deaktivirajZetoneKorisnika(upit, k);
			}
			
			upit = dohvatiUpitZaDohvacanjeGrupeKorisnika(korisnik);
			
			if(dohvatiGrupuKorisnika(upit)) {
				upit = dohvatiUpitDeaktivirajSveZetoneKorisnika();
				return deaktivirajZetoneKorisnika(upit, k);
			}
		}
		return "401";
	}

	/**
	 * Dohvati grupu korisnika.
	 *
	 * @param upit
	 * @return boolean
	 */
	private boolean dohvatiGrupuKorisnika(String upit) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {
			if (rs.next()) {
				return true;
			}
		} catch (SQLException ex) {
			System.out.println("Greska prilikom dohvaćanja naziva grupe.");
		}
		return false;
	}

	/**
	 * Dohvati iz baze aktivne zetone korisnika.
	 *
	 * @param upit
	 * @return boolean
	 */
	private boolean dohvatiIzBazeAktivneZetoneKorisnika(String upit) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery(upit)) {
			if (rs.next()) {
				return true;
			}
		} catch (SQLException ex) {
			System.out.println("Korisnik nema aktivni žeton.");
		}
		return false;
	}

	/**
	 * Dohvati upit provjeri aktivnost zetona korisnika.
	 *
	 * @param korisnik
	 * @return upit
	 */
	private String dohvatiUpitProvjeriAktivnostZetonaKorisnika(String k) {
		return "SELECT status FROM ZETONI WHERE korisnik = '" + k + "'";
	}

	/**
	 * Deaktiviraj zetone korisnika.
	 *
	 * @param upit
	 * @param korisnik
	 * @return odgovor
	 */
	private String deaktivirajZetoneKorisnika(String upit, String k) {
		try (Connection con = DriverManager.getConnection(url, korisnikDB, lozinkaDB);
				PreparedStatement ps = con.prepareStatement(upit)) {
			ps.setString(1, k);
			ps.executeUpdate();
			return "200";
		} catch (SQLException ex) {
			System.out.println("Žetoni navedenog korisnika nisu deaktivirani.");
		}	
		return "404";
	}

	/**
	 * Dohvati upit deaktiviraj sve zetone korisnika.
	 *
	 * @return upit
	 */
	private String dohvatiUpitDeaktivirajSveZetoneKorisnika() {
		return "UPDATE ZETONI SET status = 0 WHERE korisnik = ?";
	}

	/**
	 * Dohvati upit za dohvacanje grupe korisnika.
	 *
	 * @param korisnik
	 * @return upit
	 */
	private String dohvatiUpitZaDohvacanjeGrupeKorisnika(String korisnik) {
		return "SELECT naziv FROM GRUPE WHERE grupa = "
				+ "(SELECT g.grupa FROM GRUPE g INNER JOIN "
				+ "ULOGE u ON u.grupa = g.grupa WHERE "
				+ "u.korisnik = '" + korisnik + "' AND g.naziv = '"
				+ adminGrupa + "')";
	}

}
