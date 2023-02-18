package org.foi.nwtis.mnizic.aplikacija_3.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.List;

import org.foi.nwtis.mnizic.aplikacija_3.podaci.Status;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;

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

// TODO: Auto-generated Javadoc
/**
 * Rest servis servera
 */
@Path("serveri")
public class RestServeri {

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
	 * Daj status.
	 *
	 * @param korisnik
	 * @param zeton
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajStatus(@HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton) {
		ucitajKonfiguraciju();
		String adresa = konfig.dajPostavku("a");
		int port = Integer.parseInt(konfig.dajPostavku("p"));
		String odgovor = posaljiKomandu("STATUS", adresa, port);
		if(odgovor.startsWith("OK")) {
			Status s = new Status();
			s.setAdresa(adresa);
			s.setPort(port);

			if(odgovor.substring(3).equals("0")) {
				return Response.status(1).entity(s).build();
			} else if (odgovor.substring(3).equals("1")) {
				return Response.status(2).entity(s).build();
			} else if (odgovor.substring(3).equals("2")){
				return Response.status(3).entity(s).build();
			}
		}
		return Response.status(Response.Status.BAD_REQUEST).entity(odgovor).build();
	}
	
	/**
	 * Daj odgovor na komandu.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param komanda
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{komanda}")
	public Response dajOdgovorNaKomandu(@HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton, @PathParam("komanda") String komanda) {
		ucitajKonfiguraciju();
		String adresa = konfig.dajPostavku("a");
		int port = Integer.parseInt(konfig.dajPostavku("p"));
		String odgovor = null;
		if(komanda.equals("QUIT") || komanda.equals("INIT") || komanda.equals("CLEAR")) {
			odgovor = posaljiKomandu(komanda, adresa, port);
		}
		
		if(odgovor.startsWith("OK")) {
			return Response.status(Response.Status.OK).entity("Komanda uspješna.").build();	
		}
		
		return Response.status(Response.Status.BAD_REQUEST).entity(odgovor).build();
	}
	
	/**
	 * Ucitaj aerodrome.
	 *
	 * @param korisnik
	 * @param zeton
	 * @param aerodromi
	 * @return odgovor
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("LOAD")
	public Response ucitajAerodrome(@HeaderParam("korisnik") String korisnik,
			@HeaderParam("zeton") String zeton, List<Aerodrom> aerodromi) {
		ucitajKonfiguraciju();
		String adresa = konfig.dajPostavku("a");
		int port = Integer.parseInt(konfig.dajPostavku("p"));
		System.out.println("TU SAM");
		Gson gson = new Gson();
	    String listaString = gson.toJson(aerodromi);
	    String komanda = "LOAD " + listaString;
	    String odgovor = posaljiKomandu(komanda, adresa, port);
	    if(odgovor.startsWith("OK")) {
	    	return Response.status(Response.Status.OK).entity("Broj poslanih aerodroma: " + odgovor.substring(3)).build();	
	    }
		return Response.status(Response.Status.CONFLICT).entity(odgovor).build();
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
