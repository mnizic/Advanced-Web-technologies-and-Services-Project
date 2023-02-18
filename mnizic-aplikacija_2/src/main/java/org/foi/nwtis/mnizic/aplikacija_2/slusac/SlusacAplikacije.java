package org.foi.nwtis.mnizic.aplikacija_2.slusac;

import java.io.File;
import java.sql.SQLException;

import org.foi.nwtis.mnizic.aplikacija_2.dretva.DretvaPreuzimanja;
import org.foi.nwtis.mnizic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Slusac aplikacije
 *
 *@author Mario Nižić
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
	DretvaPreuzimanja dp;
	PostavkeBazaPodataka konfig;
	
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
			dp = new DretvaPreuzimanja(konfig);
			dp.start();
		} catch (NeispravnaKonfiguracija e) {
			System.out.println("Konfiguracija nije učitana.");
		}		
	}
	
	/**
	 * Dohvaćanje putanje datoteke konfiguracije.
	 *
	 *@param context
	 *@param naziv datoteke
	 *@return string putanje
	 */
	private String dohvatiPutanju(ServletContext context, String nazivDatoteke) {
		String putanja = context.getRealPath("/WEB-INF") + File.separator;
		nazivDatoteke = putanja + nazivDatoteke;
		return nazivDatoteke;
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("Postavke");
		System.out.println("Postavke obrisane!");
		
		ServletContextListener.super.contextDestroyed(sce);
	}
	
	
}
