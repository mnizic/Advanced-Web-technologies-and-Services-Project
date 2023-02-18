package org.foi.nwtis.mnizic.aplikacija_4.mvc;

import java.util.List;

import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author Mario Nižić
 * Kontroler za admina.
 */
@Controller
@Path("admin")
@RequestScoped
public class PregledAdminPoslova {
	@Inject
	private Models model;

	@Inject
	private ServletContext context;
	
	@Inject
	private Zeton zeton;
	
	@Inject
	private KorisnikBean kb;

	PostavkeBazaPodataka konfig;

	@GET
	@Path("registracijaKorisnika")
	@View("registracijaKorisnika.jsp")
	public void registracijaKorisnika() {

	}

	@POST
	@Path("registriranKorisnik")
	@View("registriranKorisnik.jsp")
	public void registriranKorisnik(@FormParam("korime") String korime, @FormParam("ime") String ime,
			@FormParam("prezime") String prezime, @FormParam("lozinka") String lozinka,
			@FormParam("email") String email) {
		ucitajKonfiguraciju();
		AdminKlijent ak = new AdminKlijent(context);
		Zeton zeton = ak.dohvatiZeton(konfig.dajPostavku("sustav.korisnik"), konfig.dajPostavku("sustav.lozinka"));
		Korisnik k = new Korisnik();
		k.setKorIme(korime);
		k.setIme(ime);
		k.setPrezime(prezime);
		k.setLozinka(lozinka);
		k.setEmail(email);
		ak.dodajKorisnika(zeton, k);
	}
	
	@GET
	@Path("prijavaKorisnika")
	@View("prijavaKorisnika.jsp")
	public void prijavaKorisnika() {
		
	}
	
	@POST
	@Path("prijavljenKorisnik")
	@View("prijavljenKorisnik.jsp")
	public void prijavljenKorisnik(@FormParam("korime") String korime, @FormParam("lozinka") String lozinka) {
		ucitajKonfiguraciju();
		AdminKlijent ak = new AdminKlijent(context);
		
		zeton.setT(ak.dohvatiZeton(korime, lozinka).getT());
		zeton.setV(ak.dohvatiZeton(korime, lozinka).getV());
		if(zeton != null) {
			kb.setKorime(korime);
			kb.setLozinka(lozinka);
			kb.setAdmin(false);
		}
	}
	
	@GET
	@Path("pregledKorisnika")
	@View("pregledKorisnika.jsp")
	public void pregledKorisnika() {
		ucitajKonfiguraciju();

		AdminKlijent ak = new AdminKlijent(context);
		List<Grupa> grupe = ak.provjeriGrupuKorisnika(kb.getKorime(), String.valueOf(zeton.getT()));
		
		for(Grupa g : grupe){
			if(g.getNaziv().equals(konfig.dajPostavku("sustav.administratori"))) {
				kb.setAdmin(true);
			}
		}
		
		List<Korisnik> k = ak.dajSveKorisnike(kb.getKorime(), String.valueOf(zeton.getT()));
		model.put("korisnici", k);
		model.put("mojeKorime", kb.getKorime());
		int a;
		if(kb.isAdmin()) {
			a = 1;
		} else {
			a = 0;
		}
		model.put("admin", a);
	}
	
	@GET
	@Path("pregledKorisnika/{korime}")
	@View("brisanjeZetona.jsp")
	public void brisanjeZetona(@PathParam("korime") String korisnik) {
		ucitajKonfiguraciju();
		AdminKlijent ak = new AdminKlijent(context);
		ak.obrisiZeton(kb.getKorime(), String.valueOf(zeton.getT()), korisnik);
	}
	
	@GET
	@Path("upravljanjePosluziteljem")
	@View("upravljanjePosluziteljem.jsp")
	public void upravljanjePosluziteljem() {
		ucitajKonfiguraciju();
		AdminKlijent ak = new AdminKlijent(context);
		String s = ak.komunikacijaPosluzitelj("STATUS");
		model.put("s", s);
	}
	
	@POST
	@Path("upravljanjePosluziteljem")
	@View("upravljanjePosluziteljem.jsp")
	public void upravljanjePosluziteljem(@FormParam("komanda") String komanda, @FormParam("loadInput") String loadInput) {
		ucitajKonfiguraciju();
		AdminKlijent ak = new AdminKlijent(context);
		
		if(!loadInput.isBlank()) {
			komanda += " ";
			komanda += loadInput;
		}
		
		String odgovor = ak.komunikacijaPosluzitelj(komanda);
		model.put("s", odgovor);
	}
	
	private void ucitajKonfiguraciju() {
		konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}

	
}
