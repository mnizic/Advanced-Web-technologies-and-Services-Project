package org.foi.nwtis.mnizic.aplikacija_4.mvc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


/**
 * @author Mario Nižić
 * Klasa admin klijent
 */
public class AdminKlijent {
	ServletContext context;
	PostavkeBazaPodataka konfig;

	/**
	 * Konstruktor
	 *
	 * @param context
	 */
	public AdminKlijent(ServletContext context) {
		this.context = context;
		this.konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}

	/**
	 * Dohvati zeton.
	 *
	 * @param korisnik
	 * @param lozinka
	 * @return zeton
	 */
	public Zeton dohvatiZeton(String korisnik, String lozinka) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3") + "/provjere");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).get();
		Zeton zeton = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			zeton = new Zeton();
			zeton = gson.fromJson(odgovor, Zeton.class);
		}
		return zeton;
	}

	/**
	 * Dodaj korisnika.
	 *
	 * @param zeton
	 * @param korisnik
	 */
	public void dodajKorisnika(Zeton z, Korisnik korisnik) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3") + "/korisnici");
		Response restOdgovor = webResource.request().header("korisnik", konfig.dajPostavku("sustav.korisnik"))
				.header("zeton", z.getT()).post(Entity.entity(korisnik, MediaType.APPLICATION_JSON), Response.class);
		if (restOdgovor.getStatus() == 200) {
			System.out.println("Uspjesno unesen korisnik.");
		}
	}

	/**
	 * Provjeri grupu korisnika.
	 *
	 * @param korisnicko ime
	 * @param zeton
	 * @return lista grupa
	 */
	public List<Grupa> provjeriGrupuKorisnika(String korime, String zeton) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3") + "/korisnici").path(korime)
				.path("grupe");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korime)
				.header("zeton", zeton).get();

		List<Grupa> grupe = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			grupe = new ArrayList<>();
			grupe.addAll(Arrays.asList(gson.fromJson(odgovor, Grupa[].class)));
		}
		return grupe;
	}

	/**
	 * Daj sve korisnike.
	 *
	 * @param korisnicko ime
	 * @param zeton
	 * @return lista korisnika
	 */
	public List<Korisnik> dajSveKorisnike(String korime, String zeton) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3") + "/korisnici");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korime)
				.header("zeton", zeton).get();

		List<Korisnik> korisnici = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			korisnici = new ArrayList<>();
			korisnici.addAll(Arrays.asList(gson.fromJson(odgovor, Korisnik[].class)));
		}
		return korisnici;
	}

	/**
	 * Obrisi zeton.
	 *
	 * @param korisnicno ime
	 * @param zeton
	 * @param korisnik
	 */
	public void obrisiZeton(String korime, String zeton, String korisnik) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3") + "/provjere").path("korisnik")
				.path(korisnik);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korime)
				.header("zeton", zeton).delete();
		if (restOdgovor.getStatus() == 200) {
			System.out.println("Uspješno obrisan žeton.");
		}
	}
	
	/**
	 * Komunikacija posluzitelj.
	 *
	 * @param komanda
	 * @return odgovor
	 */
	public String komunikacijaPosluzitelj(String komanda) {
		String adresa = konfig.dajPostavku("a");
		int port = Integer.parseInt(konfig.dajPostavku("p"));
		return posaljiKomandu(komanda, adresa, port);
	}

	/**
	 * Posalji komandu.
	 *
	 * @param komanda
	 * @param adresa
	 * @param port
	 * @return odgovor
	 */
	private String posaljiKomandu(String komanda, String a, int p) {
		try (Socket vezaServerGlavni = new Socket(a, p);
				InputStreamReader isr = new InputStreamReader(vezaServerGlavni.getInputStream(),
						Charset.forName("UTF-8"));
				OutputStreamWriter osw = new OutputStreamWriter(vezaServerGlavni.getOutputStream(),
						Charset.forName("UTF-8"));) {
			osw.write(komanda);
			osw.flush();

			vezaServerGlavni.shutdownOutput();
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			vezaServerGlavni.shutdownInput();
			vezaServerGlavni.close();
			return tekst.toString();
		} catch (SocketException e) {
			return "ERROR 14 - Server glavni nije pokrenut!";
		} catch (IOException ex) {
			return "ERROR 14 - " + ex.getMessage();
		}
	}
}
