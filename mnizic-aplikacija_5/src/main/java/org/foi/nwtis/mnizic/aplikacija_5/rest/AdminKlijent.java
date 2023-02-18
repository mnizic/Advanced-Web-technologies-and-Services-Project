package org.foi.nwtis.mnizic.aplikacija_5.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

import com.google.gson.Gson;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class AdminKlijent {
	PostavkeBazaPodataka konfig;

	public AdminKlijent(PostavkeBazaPodataka konfig) {
		this.konfig = konfig;
	}

	public Aerodrom dajTrazeniAerodrom(String korisnik, String zeton, String icao) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3")).path("aerodromi").path(icao);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
		Aerodrom aerodrom = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodrom = new Aerodrom();
			aerodrom = gson.fromJson(odgovor, Aerodrom.class);
		}
		return aerodrom;
	}

	public boolean dodajAerodrom(String korisnik, String zeton, Aerodrom aerodrom) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3")).path("aerodromi");
		Response restOdgovor = webResource.request().header("korisnik", korisnik).header("zeton", zeton)
				.post(Entity.entity(aerodrom, MediaType.APPLICATION_JSON), Response.class);
		if (restOdgovor.getStatus() == 200) {
			System.out.println("Uspjesno unesen aerodrom.");
			return true;
		}
		return false;
	}

	public List<AvionLeti> dajPolaskeDan(String korisnik, String zeton, String icao, String danOd, String danDo) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3")).path("aerodromi").path(icao)
				.path("polasci").queryParam("vrsta", 0).queryParam("od", danOd).queryParam("do", danDo);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
//		System.out.println("Korisnik: " + korisnik);
//		System.out.println("Zeton: " + zeton);
//		System.out.println("Icao: " + icao);
//		System.out.println("danOd: " + danOd);
//		System.out.println("danDo " + danDo);
		List<AvionLeti> aerodromi = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodromi = new ArrayList<>();
			aerodromi.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLeti[].class)));
		}
		System.out.println(aerodromi.size());
		return aerodromi;
	}

	public List<AvionLeti> dajPolaskeVrijeme(String korisnik, String zeton, String icao, String vrijemeOd,
			String vrijemeDo) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3")).path("aerodromi").path(icao)
				.path("polasci").queryParam("vrsta", 1).queryParam("od", vrijemeOd).queryParam("do", vrijemeDo);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
		List<AvionLeti> aerodromi = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodromi = new ArrayList<>();
			aerodromi.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLeti[].class)));
		}
		return aerodromi;
	}

	public Zeton dohvatiZeton(String korisnik, String lozinka) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3")).path("provjere");
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

	public List<Aerodrom> dajPraceneAerodrome(String korisnik, String zeton){
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(konfig.dajPostavku("putanja.app3")).path("aerodromi").queryParam("preuzimanje");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
		List<Aerodrom> aerodromi = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodromi = new ArrayList<>();
			aerodromi.addAll(Arrays.asList(gson.fromJson(odgovor, Aerodrom[].class)));
		}
		return aerodromi;
	}
}
