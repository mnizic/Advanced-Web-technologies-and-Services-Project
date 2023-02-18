package org.foi.nwtis.mnizic.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Mario Nižić
 * KorisnikGlavni za slanje zahtjeva serveru.
 */
public class KorisnikGlavni {
	String adresa = "localhost";
	int port = 8003;
	
	/**
	 * Main funkcija
	 *
	 * @param argumenti
	 */
	public static void main(String[] args) {
		if(provjeriIspravnostArgumenata(args))
            System.out.println(obradaUnesenogZahtjeva(args));
	}

	/**
	 * Obrada unesenog zahtjeva.
	 *
	 * @param argumenti
	 * @return string
	 */
	private static String obradaUnesenogZahtjeva(String[] args) {
		KorisnikGlavni kg = new KorisnikGlavni();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("STATUS")) {
                return kg.posaljiKomandu("STATUS");
            } else if (args[i].equals("QUIT")) {
                return kg.posaljiKomandu("QUIT");
            } else if (args[i].equals("INIT")) {
                return kg.posaljiKomandu("INIT");
            } else if (args[i].equals("LOAD")) {
                return izvrsiLoad(args, kg);
            } else if (args[i].equals("DISTANCE")) {
            	return izvrsiDistance(args, kg);
            } else if (args[i].equals("CLEAR")) {
            	return kg.posaljiKomandu("CLEAR");
            }
        }
        return "ERROR 14 - Problem kod obrade unesenog zahtjeva.";
	}

	/**
	 * Izvrsi naredbu distance.
	 *
	 * @param argumenti
	 * @param instanca objekta korisnika
	 * @return string odgovor
	 */
	private static String izvrsiDistance(String[] args, KorisnikGlavni kg) {
		String zahtjev = "DISTANCE " + args[1] + " " + args[2];
		return kg.posaljiKomandu(zahtjev);
	}

	/**
	 * Izvrsi naredbu load load.
	 *
	 * @param argumenti
	 * @param instanca objekta korisnika
	 * @return string odgovor
	 */
	private static String izvrsiLoad(String[] args, KorisnikGlavni kg) {
		String zahtjev = "LOAD ";
		for(int i = 1; i < args.length; i++) {
			zahtjev += args[i] + " ";
		}
		return kg.posaljiKomandu(zahtjev);
	}

	/**
	 * Posalji komandu.
	 *
	 * @param komanda
	 * @return odgovor
	 */
	private String posaljiKomandu(String komanda) {
		try (Socket vezaServerGlavni = new Socket("localhost", 8003);
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

	/**
	 * Provjeri ispravnost argumenata.
	 *
	 * @param argumenti
	 * @return boolean
	 */
	private static boolean provjeriIspravnostArgumenata(String[] args) {
        String zahtjev = "";
		for (int i = 0; i < args.length; i++) {
            zahtjev += args[i] + " ";
        }
		
		String regex = "(^(STATUS)|(QUIT)|(INIT)|(LOAD \\[(\\{\"icao\":"
				+ "\"[A-Z]{4}\",\"naziv\":\".+\",\"drzava\":\".+\",\""
				+ "lokacija\":\\{\"latitude\":\\d+((.)|(.\\d+)?),\"longitude"
				+ "\":\\d+((.)|(.\\d+)?)\\}\\})+,*\\])|(DISTANCE ([A-Z]{4}) "
				+ "([A-Z]{4}))|(CLEAR))$";
		
		Pattern pattern = Pattern.compile(regex);
        Matcher matcherTest = pattern.matcher(zahtjev.trim());
        System.out.println(zahtjev);
        if (!matcherTest.matches()) {
            System.out.println("ERROR 14 - Pogrešno unesen zahtjev.");
            return false;
        } else {
            return true;
        }
	}
}
