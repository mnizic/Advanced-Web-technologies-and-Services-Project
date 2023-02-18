package org.foi.nwtis.mnizic.aplikacija_3.slusaci;

import java.io.File;

import org.foi.nwtis.mnizic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Autor:Mario Nižić
 * Slusac aplikacije
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
	
	/** Konfiguracija */
	PostavkeBazaPodataka konfig;
	
	/**
	 * Inicijalizacija konteksta
	 *
	 * @param
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		String nazivDatotekeKonfiguracije = context.getInitParameter("konfiguracija");
		String nazivDatoteke = dohvatiPutanju(context, nazivDatotekeKonfiguracije);
		konfig = new PostavkeBazaPodataka(nazivDatoteke);
		ucitajKonfiguraciju(context);
		ServletContextListener.super.contextInitialized(sce);
	}

	/**
	 * Ucitaj konfiguraciju.
	 *
	 * @param kontekst
	 */
	private void ucitajKonfiguraciju(ServletContext context) {
		try {
			konfig.ucitajKonfiguraciju();
			context.setAttribute("Postavke", konfig);
			System.out.println("Postavke učitane!");
		} catch (NeispravnaKonfiguracija e) {
			System.out.println("Konfiguracija nije učitana.");
		}
	}

	/**
	 * Unistavanje konteksta
	 *
	 * @param
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();

		context.removeAttribute("Postavke");
		System.out.println("Postavke obrisane!");

		ServletContextListener.super.contextDestroyed(sce);
	}

	/**
	 * Dohvati putanju.
	 *
	 * @param context the context
	 * @param nazivDatoteke the naziv datoteke
	 * @return the string
	 */
	private String dohvatiPutanju(ServletContext context, String nazivDatoteke) {
		String putanja = context.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;
		return nazivDatoteke;
	}

}
