package org.foi.nwtis.mnizic.aplikacija_5.slusaci;

import java.io.File;

import org.foi.nwtis.mnizic.aplikacija_5.wsock.Info;
import org.foi.nwtis.mnizic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * 
 * @author Mario Nižić
 * Slušač aplikacije wa1
 *
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

	static PostavkeBazaPodataka konfig = null;

	public SlusacAplikacije() {

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		String nazivDatotekeKonfiguracije = context.getInitParameter("konfiguracija");
		String nazivDatoteke = dohvatiPutanju(context, nazivDatotekeKonfiguracije);
		konfig = new PostavkeBazaPodataka(nazivDatoteke);
		ucitajKonfiguraciju(context);
		ServletContextListener.super.contextInitialized(sce);
	}

	private void ucitajKonfiguraciju(ServletContext context) {
		try {
			konfig.ucitajKonfiguraciju();
			context.setAttribute("Postavke", konfig);
			System.out.println("Postavke učitane!");
		} catch (NeispravnaKonfiguracija e) {
			System.out.println("Konfiguracija nije učitana.");
		}
	}

	private String dohvatiPutanju(ServletContext context, String nazivDatoteke) {
		String putanja = context.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;
		return nazivDatoteke;
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("Postavke");

		
		ServletContextListener.super.contextDestroyed(sce);
	}

	public static PostavkeBazaPodataka dajPBP() {
		return konfig;
	}
}
