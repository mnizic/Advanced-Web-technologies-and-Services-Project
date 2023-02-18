package org.foi.nwtis.mnizic.aplikacija_3.rest;

import java.util.List;

import org.foi.nwtis.mnizic.aplikacija_3.podaci.Grupa;
import org.foi.nwtis.mnizic.aplikacija_3.podaci.KorisnikDAO;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/*
 * Rest servis korisnici
 */
@Path("korisnici")
public class RestKorisnici {

	@Inject
	ServletContext context;

	Response odgovor;
	PostavkeBazaPodataka konfig;

	/**
	 * Ucitaj konfiguraciju.
	 */
	private void ucitajKonfiguraciju() {
		konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}

	/**
	 * Daj korisnike.
	 *
	 * @param korisnik
	 * @param zeton
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajKorisnike(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton) {
		ucitajKonfiguraciju();
		KorisnikDAO kd = new KorisnikDAO();
		List<Korisnik> k = kd.dohvatiSveKorisnike(konfig);
		return Response.status(Response.Status.OK).entity(k).build();
	}

	/**
	 * Dodaj korisnika.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param novi korisnik
	 * @return odgovor
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response dodajKorisnika(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton,
			Korisnik noviKorisnik) {
		ucitajKonfiguraciju();
		KorisnikDAO kd = new KorisnikDAO();
		boolean provjera = kd.dodajNovogKorisnika(konfig, noviKorisnik);
		if (provjera)
			return Response.status(Response.Status.OK).entity("Korisnik uspješno unesen").build();
		return Response.status(Response.Status.NOT_FOUND).entity("Korisnik nije unesen.").build();
	}

	/**
	 * Daj trazenog korisnika.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param trazeniKorisnik
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}")
	public Response dajTrazenogKorisnika(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton,
			@PathParam("korisnik") String trazeniKorisnik) {
		ucitajKonfiguraciju();
		KorisnikDAO kd = new KorisnikDAO();
		Korisnik k = kd.dohvatiTrazenogKorisnika(konfig, trazeniKorisnik);
		if (k != null)
			return Response.status(Response.Status.OK).entity(k).build();
		return Response.status(Response.Status.NOT_FOUND).entity("Trazeni korisnik nije pronađen").build();
	}

	/**
	 * Daj grupe trazenog korisnika.
	 *
	 * @param korisnik
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}/grupe")
	public Response dajGrupeTrazenogKorisnika(@PathParam("korisnik") String korisnik) {
		ucitajKonfiguraciju();
		KorisnikDAO kd = new KorisnikDAO();
		List<Grupa> grupe = kd.dohvatiGrupeKorisnika(konfig, korisnik);
		if (grupe != null)
			return Response.status(Response.Status.OK).entity(grupe).build();
		return Response.status(Response.Status.NOT_FOUND).entity("Trazeni korisnik nema grupe ili ne postoji.").build();
	}
}
