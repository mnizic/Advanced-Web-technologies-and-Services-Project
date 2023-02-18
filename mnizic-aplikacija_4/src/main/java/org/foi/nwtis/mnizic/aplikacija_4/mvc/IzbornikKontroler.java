package org.foi.nwtis.mnizic.aplikacija_4.mvc;

import jakarta.enterprise.context.RequestScoped;
import jakarta.mvc.Controller;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Controller
@Path("izbornik")
@RequestScoped
public class IzbornikKontroler {
	
	@GET
	@View("index.jsp")
	public void izbornik() {
	}
}
