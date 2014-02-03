package marytts.tools.newinstall;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import junit.framework.TestCase;
import marytts.tools.newinstall.objects.Component;

import org.junit.Before;
import org.junit.Test;

public class InstallerTest extends TestCase {

	public InstallerTest(String name) {
		super(name);
	}

	static Installer installer = new Installer(new String[] { "--test" });

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSetMaryBase() {

		// assertTrue(true);
		String baseDirClean = "/Users/Jonathan/marytts/marytts-installer/target/classes";
		assertTrue(installer.setMaryBase(new File(baseDirClean)));

		String baseDirUnk = "/Users/Jonathan/marytts/marytts-installer/target/classe";
		File baseDirUnkFile = new File(baseDirUnk);
		baseDirUnkFile.setReadOnly();

		assertTrue(installer.setMaryBase(baseDirUnkFile));
		Component comp = installer.getAvailableComponents(null, null, null, null, "cmu-slt-hsmm", false).get(0);
		try {
			installer.install(comp);
			assertFalse(new File(baseDirUnk + "/lib/voice-cmu-slt-hsmm-5.1-beta1.jar").exists());
		} catch (IOException e) {
			assertTrue(true);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
