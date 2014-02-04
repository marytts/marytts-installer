package marytts.tools.newinstall;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import marytts.tools.newinstall.objects.Component;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class InstallerIT extends TestCase {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	public InstallerIT(String name) {
		super(name);
	}

	@Test
	public void testInstallerInstallTe() {

		File maryBase = tempFolder.newFolder("maryBaseInstallTe");
		String[] clArgs = new String[] { "--target", maryBase.toString(), "--install", "te", "--yes" };

		Installer installer = new Installer(clArgs);
		List<Component> components = installer.getAvailableComponents(null, null, null, null, "te", false);
		Component teComp = components.get(0);
		String artifactName = teComp.getArtifactName();
		assertTrue(new File(maryBase + "/lib").exists());
		assertTrue(new File(maryBase + "/installed").exists());
		assertTrue(new File(maryBase + "/download").exists());

		assertTrue(new File(maryBase + "/lib/" + artifactName).exists());
	}
}
