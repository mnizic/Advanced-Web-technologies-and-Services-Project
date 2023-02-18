package org.foi.nwtis.mnizic.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.mnizic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;

import com.google.gson.Gson;

// TODO: Auto-generated Javadoc
/**
 * @author Mario Nižić
 * Singleton klasa Status
 */
class Status {
    private static Status instanca = null;
    
    public int vrijednost = 0;
  
    private Status()
    {
    	
    }

    public static Status dohvatiInstancu()
    {
        if (instanca == null)
            instanca = new Status();
  
        return instanca;
    }
    
    public int getVrijednost() {
    	return vrijednost;
    }
    
    public void setVrijednost(int vrijednost) {
    	this.vrijednost = vrijednost;
    }
}

/**
 * Klasa poslužitelja.
 */
public class ServerGlavni {
	int port = 0;
	int brojDretve = 0;
	int maksCekaca = -1;
	Socket veza;
	PostavkeBazaPodataka konfig = null;
	static List<Aerodrom> aerodromi = null;

	/**
	 * Main funkcija.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("ERROR 14 - Niste unijeli samo jedan argument!");
			return;
		}

		ServerGlavni server = new ServerGlavni();
		String nazivDatoteke = args[0];
		if (!server.ucitajKonfiguraciju(nazivDatoteke))
			return;

		server.inicijalizirajPodatkeIzKonfiguracije();
		if (!server.provjeriPort())
			return;
		server.pokreniDretvu();
	}

	/**
	 * Ucitavanje konfiguracije.
	 *
	 * @param nazivDatoteke the naziv datoteke
	 * @return boolean
	 */
	public boolean ucitajKonfiguraciju(String nazivDatoteke) {
		try {
			konfig = new PostavkeBazaPodataka(nazivDatoteke);
			konfig.ucitajKonfiguraciju();
		} catch (NeispravnaKonfiguracija e) {
			System.out.println("ERROR 14 - Konfiguracijska datoteka ne postoji.");
			return false;
		}

		if (konfig == null) {
			System.out.println("ERROR 14 - Konfiguracija nije uspješno učitana.");
			return false;
		}

		return true;
	}

	/**
	 * Inicijaliziraj podatke iz konfiguracije.
	 */
	private void inicijalizirajPodatkeIzKonfiguracije() {
		this.port = Integer.parseInt(konfig.dajPostavku("port"));
		this.maksCekaca = Integer.parseInt(konfig.dajPostavku("maks.cekaca"));
	}

	/**
	 * Provjeri port.
	 *
	 * @return boolean
	 */
	public boolean provjeriPort() {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(this.port);
			serverSocket.close();
		} catch (IOException ex) {
			System.out.println("ERROR 14 - Pogreška kod spajanja na port.");
			return false;
		}

		if (this.port < 8000 || this.port > 9999) {
			System.out.println("ERROR 14 - Port nije u rasponu od 8000-9999.");
			return false;
		}
		return true;
	}

	/**
	 * Pokreni dretvu.
	 */
	private void pokreniDretvu() {
		try (ServerSocket serverSocket = new ServerSocket(this.port, this.maksCekaca)) {
			while (true) {
				this.veza = serverSocket.accept();
				DretvaZahtjeva dretva = new DretvaZahtjeva(veza, brojDretve++);
				dretva.start();
			}
		} catch (IOException ex) {
			System.out.println("ERROR 49 Port nije slobodan");
		}

	}
}

/**
 * Dretva zahtjeva
 */
class DretvaZahtjeva extends Thread {
	private Socket veza;
	private int redniBrojDretve = 0;
	Status status = Status.dohvatiInstancu();
	
	/**
	 * Konstruktor
	 */
	public DretvaZahtjeva(Socket veza, int redniBrojDretve) {
		super();
		this.veza = veza;
		this.redniBrojDretve = redniBrojDretve;
	}

	@Override
	public synchronized void start() {
		System.out.println("Dretva mnizic_" + redniBrojDretve + " pocinje s radom.");
		super.start();
	}

	@Override
	public synchronized void run() {
		citajZahtjev();
		super.run();
	}
	
	/**
	 * Čitanje zahtjeva
	 */
	private synchronized void citajZahtjev() {
		try (InputStreamReader isr = new InputStreamReader(this.veza.getInputStream(), Charset.forName("UTF-8"));
				OutputStreamWriter osw = new OutputStreamWriter(this.veza.getOutputStream(),
						Charset.forName("UTF-8"));) {
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			this.veza.shutdownInput();

			String odgovor = izvrsiNaredbu(tekst.toString());
			
			osw.write(odgovor);
			osw.flush();
			sleep(300);

			veza.shutdownOutput();
		} catch (IOException ex) {
			System.out.println("Error 14 - Port nije otvoren.");
		} catch (InterruptedException e) {
			System.exit(0);
		}
	}

	/**
	 * Slanje naredbe na izvršavanje
	 * 
	 * @return odgovor
	 */
	private String izvrsiNaredbu(String tekst) {
		if (tekst.equals("STATUS")) {
			return izvrsiStatus();
		} else if (tekst.equals("QUIT")) {
			return izvrsiQuit();
		} else if (tekst.equals("INIT")) {
			return izvrsiInit();
		} else if (tekst.startsWith("LOAD")) {
			return izvrsiLoad(tekst);
		} else if (tekst.startsWith("DISTANCE")) {
			return izvrsiDistance(tekst);
		} else if (tekst.equals("CLEAR")){
			return izvrsiClear();	
		}
		
		return "ERROR 14 - Naredba ne postoji.";
	}
	
	/**
	 * Izvršavanje clear naredbe
	 * 
	 * @return odgovor
	 */
	private String izvrsiClear() {
		int vrijednostStatusa = status.getVrijednost();

		if(vrijednostStatusa == 0) {
			return "ERROR 01 - Poslužitelj hibernira tj. nije aktivan.";
		} else if(vrijednostStatusa == 1) {
			return "ERROR 02 - Poslužitelj je inicijaliziran tj. nije aktivan";
		} 

		ServerGlavni.aerodromi = null;
		status.setVrijednost(0);
		return "OK";
	}

	/**
	 * Izvršavanje distance naredbe
	 * 
	 * @return odgovor
	 */
	private String izvrsiDistance(String tekst) {
		int vrijednostStatusa = status.getVrijednost();
		
		if(vrijednostStatusa == 0) {
			return "ERROR 01 - Poslužitelj hibernira tj. nije aktivan.";
		} else if(vrijednostStatusa == 1) {
			return "ERROR 02 - Poslužitelj je inicijaliziran tj. nije aktivan";
		} 
		
		String[] komanda = tekst.split(" ");
		String prviIcao = komanda[1];
		String drugiIcao = komanda[2];
		
		Aerodrom prvi = null;
		Aerodrom drugi = null;
		if(ServerGlavni.aerodromi.size() < 2) return "ERROR 14 - Ne postoji barem dva aerodroma";
		for(Aerodrom a : ServerGlavni.aerodromi) {
			if(prviIcao.equals(a.getIcao())) {
				prvi = a;
			}
			
			if(drugiIcao.equals(a.getIcao())) {
				drugi = a;
			}
		}
		
		if(prvi == null && drugi == null) {
			return "ERROR 13 - Ne postoje oba unesena aerodroma.";
		}
		
		if(prvi == null) {
			return "ERROR 11 - Ne postoji prvi uneseni aerodrom.";
		}
		
		if(drugi == null) {
			return "ERROR 12 - Ne postoji drugi uneseni aerodrom.";
		}
		
		return "OK " + Math.round(distance(Double.parseDouble(prvi.getLokacija().getLatitude()), 
				Double.parseDouble(drugi.getLokacija().getLatitude()),
				Double.parseDouble(prvi.getLokacija().getLongitude()), 
				Double.parseDouble(drugi.getLokacija().getLongitude())));
	}
	
	
	/**
	 * Metoda za računanje Haversinove formule
	 * 
	 * @return udaljenost
	 */
	public double distance(double lat1, double lat2, double lon1, double lon2) {
		double R = 6372.8;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	/**
	 * Izvršavanje load naredbe
	 * 
	 * @return odgovor
	 */
	private String izvrsiLoad(String tekst) {
		int vrijednostStatusa = status.getVrijednost();
		if(vrijednostStatusa == 0) {
			return "ERROR 01 - Poslužitelj hibernira tj. nije inicijaliziran.";
		} else if (vrijednostStatusa == 2) {
			return "ERROR 03 - Poslužitelj je već aktivan.";
		}
		if(tekst.length() > 5) {
			String jsonString = tekst.substring(5);
			Gson gson = new Gson();
			ServerGlavni.aerodromi = new ArrayList<Aerodrom>();
			ServerGlavni.aerodromi.addAll(Arrays.asList(gson.fromJson(jsonString, Aerodrom[].class)));
			
			if(ServerGlavni.aerodromi.size() >= 2) {
				status.setVrijednost(2);
				return "OK " + ServerGlavni.aerodromi.size();
			}
		}
		return "ERROR 14 - LOAD ne sadrži ispravan format";
	}

	/**
	 * Izvršavanje init naredbe
	 * 
	 * @return odgovor
	 */
	private String izvrsiInit() {
		int vrijednostStatusa = status.getVrijednost();
		if(vrijednostStatusa == 1) {
			return "ERROR 02 - Poslužitelj je već inicijaliziran";
		} else if (vrijednostStatusa == 2) {
			return "ERROR 03 - Poslužitelj je aktivan i nije ga moguće opet inicijalizirati"; 
		}
		status.setVrijednost(1);
		return "OK";
	}

	/**
	 * Izvršavanje quit naredbe
	 * 
	 * @return odgovor
	 */
	private String izvrsiQuit() {
		interrupt();
		return "OK";
	}

	/**
	 * Izvršavanje status naredbe
	 * 
	 * @return odgovor
	 */
	private String izvrsiStatus() {
		return "OK " + status.getVrijednost();
	}

	@Override
	public synchronized void interrupt() {
		System.out.println("Poslužitelj ugašen.");
		super.interrupt();
	}

}
