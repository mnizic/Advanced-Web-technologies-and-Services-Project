package org.foi.nwtis.mnizic.aplikacija_5.ws;

import org.foi.nwtis.mnizic.aplikacija_5.rest.AdminKlijent;
import org.foi.nwtis.mnizic.aplikacija_5.rest.Zeton;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

@WebService(serviceName = "meteo")
public class WsMeteo {
	@Resource
	private WebServiceContext wsContext;
	
	PostavkeBazaPodataka konfig;

	String apikey;
	
	OWMKlijent owmKlijent;

	@WebMethod
	public MeteoPodaci dajMeteo(String icao) {
		inicijalizacijaPodataka();
		AdminKlijent ak = new AdminKlijent(dajPBP());
		Zeton z = ak.dohvatiZeton(konfig.dajPostavku("sustav.korisnik"), konfig.dajPostavku("sustav.lozinka"));
		Aerodrom a = ak.dajTrazeniAerodrom(konfig.dajPostavku("sustav.korisnik"), String.valueOf(z.getT()), icao);
		Lokacija lokacija = a.getLokacija();
		try {
			return owmKlijent.getRealTimeWeather(lokacija.getLatitude(), lokacija.getLongitude());
		} catch (NwtisRestIznimka e) {
			System.out.println("Nisu dohvaćeni meteo podaci.");
		}
		return null;
	}
	
	private void inicijalizacijaPodataka() {
		konfig = dajPBP();
		apikey = konfig.dajPostavku("OpenWeatherMap.apikey");
		owmKlijent = new OWMKlijent(apikey);
	}

	public PostavkeBazaPodataka dajPBP() { 
		ServletContext context = (ServletContext) wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke"); 
		return pbp;
	}


}
