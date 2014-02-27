package marytts.tools.newinstall;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import marytts.tools.newinstall.objects.Component;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class InstallerIT {

	@ClassRule
	public static TemporaryFolder tempFolder = new TemporaryFolder();

	private static File maryBase;

	@BeforeClass
	public static void setUp() throws IOException {
		maryBase = tempFolder.newFolder();
	}

	@Test
	public void testInstallerInstallTe() {

		String[] clArgs = new String[] { "--target", maryBase.toString(), "--install", "lang-te", "--yes" };

		Installer installer = new Installer(clArgs);
		List<Component> components = installer.getAvailableComponents(null, null, null, null, "lang-te", false);
		Component teComp = components.get(0);
		String artifactName = teComp.getArtifactName();
		assertTrue(new File(maryBase + "/lib").exists());
		// TODO since we don't currently install, there is no MARYBASE/installed
		// assertTrue(new File(maryBase + "/installed").exists());
		assertTrue(new File(maryBase + "/download").exists());

		// TODO artifact names are not determined correctly
		// assertTrue(new File(maryBase + "/lib/" + artifactName).exists());
	}
}
