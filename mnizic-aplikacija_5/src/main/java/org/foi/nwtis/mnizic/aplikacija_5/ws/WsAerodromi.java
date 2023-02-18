package org.foi.nwtis.mnizic.aplikacija_5.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.mnizic.aplikacija_5.rest.AdminKlijent;
import org.foi.nwtis.mnizic.aplikacija_5.wsock.Info;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

/**
 * @author Mario Nižić
 */
@WebService(serviceName = "aerodromi")
public class WsAerodromi {
	@Resource
	private WebServiceContext wsContext;

	/**
	 * Dodaj aerodrom preuzimanje.
	 *
	 * @param korisnik
	 * @param zeton 
	 * @param icao
	 * @return boolean
	 */
	@WebMethod
	public boolean dodajAerodromPreuzimanje(String korisnik, String zeton, String icao) {
		AdminKlijent ak = new AdminKlijent(dajPBP());
		Aerodrom a = ak.dajTrazeniAerodrom(korisnik, zeton, icao);

		boolean provjera = false;
		if (a != null) {
			provjera = ak.dodajAerodrom(korisnik, zeton, a);
			if (provjera) {
				int brojPracenihAerodroma = ak.dajPraceneAerodrome(korisnik, zeton).size();

				long trenutneMilisekunde = System.currentTimeMillis();
				Date datum = new Date(trenutneMilisekunde);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

				Info.informiraj(dateFormat.format(datum) + "," + brojPracenihAerodroma);
			}
		}
		return provjera;
	}

	/**
	 * Daj polaske dan.
	 */
	@WebMethod
	public List<AvionLeti> dajPolaskeDan(String korisnik, String zeton, String icao, String danOd, String danDo) {
		AdminKlijent ak = new AdminKlijent(dajPBP());
		return ak.dajPolaskeDan(korisnik, zeton, icao, danOd, danDo);
	}

	/**
	 * Daj polaske vrijeme.
	 *
	 */
	@WebMethod
	public List<AvionLeti> dajPolaskeVrijeme(String korisnik, String zeton, String icao, String vrijemeOd,
			String vrijemeDo) {
		AdminKlijent ak = new AdminKlijent(dajPBP());
		return ak.dajPolaskeVrijeme(korisnik, zeton, icao, vrijemeOd, vrijemeDo);
	}

	/**
	 * Daj PBP.
	 *
	 * @return postavke baza podataka
	 */
	public PostavkeBazaPodataka dajPBP() {
		ServletContext context = (ServletContext) wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		return pbp;
	}

}
