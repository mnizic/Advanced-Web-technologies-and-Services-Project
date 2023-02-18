package org.foi.nwtis.mnizic.vjezba_06.konfiguracije.bazaPodataka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import org.foi.nwtis.mnizic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostavkeBazaPodatakaTest {
	private PostavkeBazaPodataka pbp;

	@BeforeEach
	void setUp() throws Exception {
		String nazivDatoteke = "NWTiS.db.config_1.xml";
		pbp = new PostavkeBazaPodataka(nazivDatoteke);
		try {
			pbp.ucitajKonfiguraciju();
		} catch (NeispravnaKonfiguracija e) {
			e.printStackTrace();
			fail("Problem učitvanja!");
		}
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetDriverDatabase() {
		assertEquals("org.hsqldb.jdbcDriver", pbp.getDriverDatabase());
	}

	@Test
	void testGetDriverDatabaseString() {
		assertEquals("com.mysql.jdbc.Driver", pbp.getDriverDatabase("jdbc:mysql://localhost/"));
	}

	@Test
	void testGetDriversDatabase() {
		Properties result = pbp.getDriversDatabase();

		Properties expResult = new Properties();
		expResult.setProperty("jdbc.mysql", "com.mysql.jdbc.Driver");
		expResult.setProperty("jdbc.derby", "org.apache.derby.jdbc.ClientDriver");
		expResult.setProperty("jdbc.hsqldb.hsql", "org.hsqldb.jdbcDriver");

		assertEquals(expResult, result);
	}

}
