package marytts.tools.newinstall;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import marytts.tools.newinstall.objects.Component;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class InstallerTest {

	@ClassRule
	public static TemporaryFolder tempFolder = new TemporaryFolder();

	private static File maryBaseDir;

	static Installer installer = new Installer(new String[] { "--nogui" });

	@BeforeClass
	public static void setUp() throws IOException {
		maryBaseDir = tempFolder.newFolder();
	}

	@Ignore	
	@Test
	public void testSetMaryBase() {

		Component voiceCompToTest = installer.getAvailableComponents(null, null, null, null, "cmu-slt-hsmm", false).get(0);

		try {
			System.out.println(maryBaseDir.getAbsolutePath());
			assertTrue(installer.setMaryBase(maryBaseDir));
			System.out.println(installer.getMaryBasePath());
			installer.install(voiceCompToTest);
			assertTrue(new File(maryBaseDir + "/lib").exists());
			assertTrue(new File(maryBaseDir + "/installed").exists());
			assertTrue(new File(maryBaseDir + "/download").exists());

			String voiceArtifactName = voiceCompToTest.getArtifactName();
			// don't yet know how to retrieve artifact of dependency (so far, can only reach to the DependencyDescriptor. The
			// artifact of the dependency itself is null.
			// String voiceArtifactDepName = voiceCompToTest.getDependencyArtifact();
			assertTrue(new File(maryBaseDir + "/lib/" + voiceArtifactName).exists());
			assertTrue(new File(maryBaseDir + "/download/" + voiceArtifactName).exists());
			// assertTrue(new File(maryBaseDirClean + "/lib/" + voiceArtifactDepName).exists());

			Artifact artifactDescriptor = voiceCompToTest.getModuleDescriptor().getMetadataArtifact();
			String descriptorName = artifactDescriptor.getAttribute("module") + "-" + artifactDescriptor.getAttribute("revision")
					+ "." + artifactDescriptor.getExt();
			assertTrue(new File(maryBaseDir + "/installed/" + descriptorName).exists());
			assertTrue(new File(maryBaseDir + "/download/" + descriptorName).exists());
		} catch (IOException e) {
			assertTrue(true);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
