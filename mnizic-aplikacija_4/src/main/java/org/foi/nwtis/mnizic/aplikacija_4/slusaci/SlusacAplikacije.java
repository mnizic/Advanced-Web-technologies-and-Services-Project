package org.foi.nwtis.mnizic.aplikacija_4.slusaci;

import java.io.File;

import org.foi.nwtis.mnizic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {
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
		System.out.println("Postavke obrisane!");
		
		ServletContextListener.super.contextDestroyed(sce);
	}
    


    public SlusacAplikacije() {
       
    }

}
