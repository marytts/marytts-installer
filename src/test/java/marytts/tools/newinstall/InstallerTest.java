package marytts.tools.newinstall;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class InstallerTest extends TestCase {
	
	public InstallerTest(String name) {
		super(name);
	}
	
	static Installer installer = new Installer(new String[] {});

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSetMaryBase() {
		
		String baseDirClean = "/Users/Jonathan/marytts/marytts-installer/target/classes";
		assertTrue(installer.setMaryBase(new File(baseDirClean)));		
		
	}

}
