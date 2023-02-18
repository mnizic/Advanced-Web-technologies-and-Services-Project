package org.foi.nwtis.mnizic.aplikacija_2.dretva;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.mnizic.aplikacija_2.podaci.AerodromiDolasciDAO;
import org.foi.nwtis.mnizic.aplikacija_2.podaci.AerodromiPolasciDAO;
import org.foi.nwtis.mnizic.aplikacija_2.podaci.AerodromiPraceniDAO;
import org.foi.nwtis.mnizic.aplikacija_2.podaci.AerodromiProblemiDAO;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;


/**
 * Dretva koja preuzima polaske i dolaske praćenih aerodroma
 * 
 * @author Mario Nižić
 */
public class DretvaPreuzimanja extends Thread {
	private PostavkeBazaPodataka konfig;
	private OSKlijent osKlijent;
	private int ciklusVrijeme;
	private int preuzimanjePauza;
	private Date preuzimanjeOd;
	private Date preuzimanjeDo;
	private long preuzimanjeOdMilisekunde;
	private long preuzimanjeDoMilisekunde;
	private int preuzimanjeVrijeme;
	private long vrijemeObrade;
	private String openSkyKorisnik;
	private String openSkyLozinka;

	/**
	 * Konstruktor dretve
	 *
	 * @param konfiguracija
	 */
	public DretvaPreuzimanja(PostavkeBazaPodataka konfig) {
		this.konfig = konfig;
	}
	
	/**
	 * Inicijalizacija podataka
	 * 
	 */

	public void inicijalizacijaPodataka() {
		this.ciklusVrijeme = Integer.parseInt(konfig.dajPostavku("ciklus.vrijeme"));
		this.preuzimanjePauza = Integer.parseInt(konfig.dajPostavku("preuzimanje.pauza"));

		String stringPreuzimanjeOd = konfig.dajPostavku("preuzimanje.od");
		this.preuzimanjeOd = konverzijaPreuzimanjaUDatum(stringPreuzimanjeOd);

		String stringPreuzimanjeDo = konfig.dajPostavku("preuzimanje.do");
		this.preuzimanjeDo = konverzijaPreuzimanjaUDatum(stringPreuzimanjeDo);

		this.preuzimanjeVrijeme = Integer.parseInt(konfig.dajPostavku("preuzimanje.vrijeme")) * 1000 * 3600;
		
		this.openSkyKorisnik = konfig.dajPostavku("OpenSkyNetwork.korisnik");
		this.openSkyLozinka = konfig.dajPostavku("OpenSkyNetwork.lozinka");

		this.osKlijent = new OSKlijent(openSkyKorisnik, openSkyLozinka);

		preuzimanjeOdMilisekunde = preuzimanjeOd.getTime();
		preuzimanjeDoMilisekunde = preuzimanjeDo.getTime();

		preuzimanjeOd = milisekundeUDatumVrijeme(preuzimanjeOdMilisekunde);
		preuzimanjeDo = milisekundeUDatumVrijeme(preuzimanjeDoMilisekunde);

		this.vrijemeObrade = this.preuzimanjeOdMilisekunde;
	}

	@Override
	public synchronized void start() {
		inicijalizacijaPodataka();
		super.start();
	}

	/**
	 * Milisekunde u datum vrijeme.
	 *
	 * @param milisekunde
	 * @return datum
	 */
	private Date milisekundeUDatumVrijeme(long milisekunde) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Date datum = new Date(milisekunde);

		String noviDatumString = dateFormat.format(datum);
		Date noviDatum = null;
		try {
			noviDatum = dateFormat.parse(noviDatumString);
		} catch (ParseException ex) {
			System.out.println("Datum nije parsiran.");
		}
		return noviDatum;
	}

	/**
	 * Konverzija preuzimanja u datum.
	 *
	 * @param stringDatum
	 * @return datum
	 */
	private Date konverzijaPreuzimanjaUDatum(String stringDatum) {
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date datum = null;
		try {
			datum = dateFormat.parse(stringDatum);
		} catch (ParseException ex) {
			System.out.println("Datum nije parsiran.");
		}
		return datum;
	}

	@Override
	public synchronized void run() {
		List<Aerodrom> aerodromi = new ArrayList<>();
		AerodromiPraceniDAO ap = new AerodromiPraceniDAO();
		aerodromi = ap.dohvatiIzBazeAerodromPraceni(konfig);
		while (vrijemeObrade < preuzimanjeDoMilisekunde) {
			iteracijaKrozAerodrome(aerodromi);
			spavanjeDretve();
		}
	}

	/**
	 * Spavanje dretve.
	 */
	private void spavanjeDretve() {
		try {
			sleep(ciklusVrijeme*1000);
		} catch (InterruptedException e) {
			System.out.println("Dretva je prekinuta.");
		}
	}

	/**
	 * Iteracija kroz aerodrome.
	 *
	 * @param aerodromi
	 */
	private void iteracijaKrozAerodrome(List<Aerodrom> aerodromi) {
		for (Aerodrom a : aerodromi) {
			System.out.println("Polasci s aerodroma: " + a.getIcao());
			polasciSAerodroma(a);
			System.out.println("Dolasci na aerodrom: " + a.getIcao());
			dolasciNaAerodrom(a);
			pauzaZaPreuzimanje();
		}
		this.vrijemeObrade += preuzimanjeVrijeme;
	}

	/**
	 * Pauza za preuzimanje.
	 */
	private void pauzaZaPreuzimanje() {
		try {
			sleep(preuzimanjePauza);
		} catch (InterruptedException e) {
			System.out.println("Dretva je prekinuta.");
		}
	}

	/**
	 * Polasci s aerodroma.
	 *
	 * @param aerodrom
	 */
	private void polasciSAerodroma(Aerodrom a) {
		List<AvionLeti> avioniPolasci;
		try {
			avioniPolasci = osKlijent.getDepartures(a.getIcao(), this.vrijemeObrade / 1000,
					(vrijemeObrade + preuzimanjeVrijeme) / 1000);
			if (avioniPolasci != null) {
				System.out.println("Broj letova: " + avioniPolasci.size());
				for (AvionLeti avion : avioniPolasci) {
					AerodromiPolasciDAO apol = new AerodromiPolasciDAO();
					apol.spremiAerodrom(konfig, avion);
					System.out.println("Avion: " + avion.getIcao24() + " Odredište: " + avion.getEstArrivalAirport());
				}
			}
		} catch (NwtisRestIznimka e) {
			AerodromiProblemiDAO problem = new AerodromiProblemiDAO();
			problem.dodajNoviProblem(konfig, a.getIcao(), e.toString());
		}
	}

	/**
	 * Dolasci na aerodrom.
	 *
	 * @param aerodrom
	 */
	private void dolasciNaAerodrom(Aerodrom a) {
		List<AvionLeti> avioniDolasci;
		try {
			avioniDolasci = osKlijent.getArrivals(a.getIcao(), this.vrijemeObrade / 1000,
					(vrijemeObrade + preuzimanjeVrijeme) / 1000);
			if (avioniDolasci != null) {
				System.out.println("Broj letova: " + avioniDolasci.size());
				for (AvionLeti avion : avioniDolasci) {
					AerodromiDolasciDAO ad = new AerodromiDolasciDAO();
					ad.spremiAerodrom(konfig, avion);
					System.out.println("Avion: " + avion.getIcao24() + " Odredište: " + avion.getEstDepartureAirport());
				}
			}
		} catch (NwtisRestIznimka e) {
			AerodromiProblemiDAO problem = new AerodromiProblemiDAO();
			problem.dodajNoviProblem(konfig, a.getIcao(), e.toString());
		}

	}

	@Override
	public void interrupt() {
		super.interrupt();
	}

}
