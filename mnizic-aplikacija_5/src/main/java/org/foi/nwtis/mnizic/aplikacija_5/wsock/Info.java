package org.foi.nwtis.mnizic.aplikacija_5.wsock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.foi.nwtis.mnizic.aplikacija_5.rest.AdminKlijent;
import org.foi.nwtis.mnizic.aplikacija_5.rest.Zeton;
import org.foi.nwtis.mnizic.aplikacija_5.slusaci.SlusacAplikacije;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.inject.Singleton;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/info")
public class Info {

	static Set<Session> sesije = new HashSet<>();

	public Info() {

	}

	public static void informiraj(String poruka) {
		for (Session s : sesije) {
			if (s.isOpen()) {
				try {
					s.getBasicRemote().sendText(poruka);
				} catch (IOException e) {
					System.out.println("Sesija: " + s.getId() + " greska: " + e.getMessage());
				}
			}
		}
	}

	@OnOpen
	public void otvori(Session sesija, EndpointConfig konfig) {
		sesije.add(sesija);
		System.out.println("Otvorena veza: " + sesija.getId());
	}

	@OnClose
	public void zatvori(Session sesija, CloseReason razlog) {
		System.out.println("Zatvorena veza: " + sesija.getId() + " Razlog: " + razlog.getReasonPhrase());
		sesije.remove(sesija);
	}

	@OnMessage
	public void stiglaPoruka(Session sesija, String poruka) {
		System.out.println("Veza: " + sesija.getId() + " Poruka: " + poruka);
		if (poruka.equalsIgnoreCase("info")) {
			PostavkeBazaPodataka konfig = SlusacAplikacije.dajPBP();
			AdminKlijent ak = new AdminKlijent(konfig);
			String korisnik = konfig.dajPostavku("sustav.korisnik");
			String lozinka = konfig.dajPostavku("sustav.lozinka");
			Zeton z = ak.dohvatiZeton(korisnik, lozinka);
			String zeton = String.valueOf(z.getT());
			int brojPracenihAerodroma = ak.dajPraceneAerodrome(korisnik, zeton).size();

			long trenutneMilisekunde = System.currentTimeMillis();
			Date datum = new Date(trenutneMilisekunde);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

			Info.informiraj(dateFormat.format(datum) + "," + brojPracenihAerodroma);
		}
	}

	@OnError
	public void pogreska(Session sesija, Throwable iznimka) {
		System.out.println("Veza: " + sesija.getId() + " Pogreška: " + iznimka.getMessage());
	}
}
