package org.foi.nwtis.mnizic.aplikacija_3.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.List;

import org.foi.nwtis.mnizic.aplikacija_3.podaci.AerodromiDolasciDAO;
import org.foi.nwtis.mnizic.aplikacija_3.podaci.AerodromiPolasciDAO;
import org.foi.nwtis.mnizic.aplikacija_3.podaci.AerodromiPraceniDAO;
import org.foi.nwtis.mnizic.aplikacija_3.podaci.AirportsDAO;
import org.foi.nwtis.mnizic.aplikacija_3.podaci.Distanca;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Rest servis aerodromi
 * @author Mario Nižić
 */
@Path("aerodromi")
public class RestAerodromi {

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
	 * Daj sve aerodrome.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param preuzimanje
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajSveAerodrome(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton,
			@QueryParam("preuzimanje") String preuzimanje) {
		ucitajKonfiguraciju();
		if (preuzimanje != null) {
			AerodromiPraceniDAO apd = new AerodromiPraceniDAO();
			List<Aerodrom> aerodromi = apd.dohvatiIzBazeAerodromPraceni(konfig);
			if (aerodromi != null) {
				return Response.status(Response.Status.OK).entity(aerodromi).build();
			}
			return Response.status(Response.Status.NOT_FOUND).entity("Nisu pronađeni aerodromi.").build();
		}
		AirportsDAO aerodromiDAO = new AirportsDAO();
		List<Aerodrom> aerodromi = aerodromiDAO.dohvatiSveAerodrome(konfig);
		if (aerodromi != null) {
			return Response.status(Response.Status.OK).entity(aerodromi).build();
		}
		return Response.status(Response.Status.NOT_FOUND).entity("Nisu pronađeni aerodromi.").build();
	}

	/**
	 * Dodaj aerodrom za pratiti.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param aerodrom
	 * @return odgovor
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response dodajAerodromZaPratiti(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton,
			Aerodrom airport) {
		ucitajKonfiguraciju();
		AerodromiPraceniDAO ap = new AerodromiPraceniDAO();
		return ap.dodavanjeAerodroma(konfig, airport);
	}

	/**
	 * Daj aerodrom.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}")
	public Response dajAerodrom(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton,
			@PathParam("icao") String icao) {
		ucitajKonfiguraciju();
		AirportsDAO aerodromiDAO = new AirportsDAO();
		Aerodrom a = aerodromiDAO.dohvatiAerodrom(konfig, icao);
		if (a != null) {
			return Response.status(Response.Status.OK).entity(a).build();
		}
		return Response.status(Response.Status.OK).entity("Prazna lista aerodroma.").build();
	}

	/**
	 * Daj polaske aerodoma.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param icao
	 * @param format vremena
	 * @param vrijeme od
	 * @param vrijeme do
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/polasci")
	public Response dajPolaskeAerodoma(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton,
			@PathParam("icao") String icao, @QueryParam("vrsta") String x, @QueryParam("od") String vrijemeOd,
			@QueryParam("do") String vrijemeDo) {
		ucitajKonfiguraciju();
		AerodromiPolasciDAO apd = new AerodromiPolasciDAO();
		List<AvionLeti> aerodromi = apd.dajPolaskeAerodroma(konfig, icao, x, vrijemeOd, vrijemeDo);
		if (aerodromi != null) {
			return Response.status(Response.Status.OK).entity(aerodromi).build();
		}
		return Response.status(Response.Status.OK).entity("Prazna lista aerodroma.").build();
	}

	/**
	 * Daj dolaske aerodoma.
	 *
	 * @param korisnik
	 * @param zeton 
	 * @param icao
	 * @param format vremena
	 * @param vrijeme od
	 * @param vrijeme do
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/dolasci")
	public Response dajDolaskeAerodoma(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String zeton,
			@PathParam("icao") String icao, @QueryParam("vrsta") String x, @QueryParam("od") String vrijemeOd,
			@QueryParam("do") String vrijemeDo) {
		ucitajKonfiguraciju();
		AerodromiDolasciDAO add = new AerodromiDolasciDAO();
		List<AvionLeti> aerodromi = add.dajDolaskeAerodroma(konfig, icao, x, vrijemeOd, vrijemeDo);
		if (aerodromi != null) {
			return Response.status(Response.Status.OK).entity(aerodromi).build();
		}
		return Response.status(Response.Status.NOT_FOUND).entity("Prazna lista aerodroma.").build();
	}

	/**
	 * Daj udaljenost dva aerodroma.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param icao1
	 * @param icao2
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao1}/{icao2}")
	public Response dajUdaljenostDvaAerodroma(@HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton, @PathParam("icao1") String icao1, @PathParam("icao2") String icao2) {
		ucitajKonfiguraciju();
		String adresa = konfig.dajPostavku("a");
		int port = Integer.parseInt(konfig.dajPostavku("p"));

		String komanda = "DISTANCE " + icao1 + " " + icao2;
		String odgovor = posaljiKomandu(komanda, adresa, port);

		if (odgovor.startsWith("OK")) {
			Distanca d = new Distanca();
			d.setUdaljenost(Integer.parseInt(odgovor.substring(3)));
			return Response.status(Response.Status.OK).entity(d).build();
		}

		return Response.status(Response.Status.NOT_FOUND).entity(odgovor).build();
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
