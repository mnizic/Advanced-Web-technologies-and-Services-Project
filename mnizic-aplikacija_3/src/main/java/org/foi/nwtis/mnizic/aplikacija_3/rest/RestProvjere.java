package org.foi.nwtis.mnizic.aplikacija_3.rest;

import org.foi.nwtis.mnizic.aplikacija_3.podaci.ProvjereDAO;
import org.foi.nwtis.mnizic.aplikacija_3.podaci.Zeton;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Rest servis provjere
 */
@Path("provjere")
public class RestProvjere {

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
	 * Daj provjeru korisnika.
	 *
	 * @param korisnik
	 * @param lozinka
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajProvjeruKorisnika(@HeaderParam("korisnik") String korisnik,
			@HeaderParam("lozinka") String lozinka) {
		ucitajKonfiguraciju();
		ProvjereDAO pd = new ProvjereDAO();
		Zeton z = pd.dajProvjeruKorisnika(konfig, korisnik, lozinka);
		if (z != null) {
			return Response.status(Response.Status.OK).entity(z).build();
		}
		return Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik nije autenticiran.").build();
	}

	/**
	 * Provjeri zeton.
	 *
	 * @param korisnik
	 * @param lozinka
	 * @param zeton
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response provjeriZeton(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka,
			@PathParam("token") String zeton) {
		ucitajKonfiguraciju();
		ProvjereDAO pd = new ProvjereDAO();
		String provjera = pd.provjeriZeton(konfig, korisnik, lozinka, zeton);
		if (provjera.equals("200")) {
			return Response.status(Response.Status.OK).entity("Žeton je aktivan.").build();
		} else if (provjera.equals("401")) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("Korisnik se ne poklapa s korisnikom čiji je žeton.").build();
		} else if (provjera.equals("403")) {
			return Response.status(Response.Status.FORBIDDEN).entity("Žeton nije važeći.").build();
		} else if (provjera.equals("408")) {
			return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Rok trajanja žetona je istekao.").build();
		}
		return Response.status(Response.Status.NOT_FOUND).entity("Žeton nije pronađen.").build();
	}

	/**
	 * Deaktiviraj zeton.
	 *
	 * @param korisnik
	 * @param lozinka
	 * @param zeton
	 * @return odgovor
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response deaktivirajZeton(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka,
			@PathParam("token") String zeton) {
		ucitajKonfiguraciju();
		ProvjereDAO pd = new ProvjereDAO();
		String provjera = pd.deaktivirajZeton(konfig, korisnik, lozinka, zeton);
		if (provjera.equals("200")) {
			return Response.status(Response.Status.OK).entity("Žeton je deaktiviran.").build();
		} else if (provjera.equals("401")) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity("Korisnik se ne poklapa s korisnikom čiji je žeton.").build();
		} else if (provjera.equals("403")) {
			return Response.status(Response.Status.FORBIDDEN).entity("Žeton nije važeći.").build();
		} else if (provjera.equals("408")) {
			return Response.status(Response.Status.REQUEST_TIMEOUT).entity("Rok trajanja žetona je istekao.").build();
		}
		return Response.status(Response.Status.NOT_FOUND).entity("Žeton nije pronađen.").build();
	}

	/**
	 * Provjeri aktivnost zetona korisnika.
	 *
	 * @param korisnik
	 * @param lozinka
	 * @param korisnik koji se provjerava
	 * @return odgovor
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("korisnik/{korisnik}")
	public Response provjeriAktivnostZetonaKorisnika(@HeaderParam("korisnik") String korisnik,
			@HeaderParam("lozinka") String lozinka, @PathParam("korisnik") String k) {
		ucitajKonfiguraciju();
		ProvjereDAO pd = new ProvjereDAO();
		String provjera = pd.provjeriAktivnostZetonaKorisnika(konfig, korisnik, lozinka, k);
		if (provjera.equals("200")) {
			return Response.status(Response.Status.OK).entity("Korisnikovi žetoni su deaktivirani.").build();
		} else if (provjera.equals("401")) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik nema ovlaštenje za brisanje žetona.")
					.build();
		}
		return Response.status(Response.Status.NOT_FOUND).entity("Nema nijedan aktivan žeton.").build();
	}
}
