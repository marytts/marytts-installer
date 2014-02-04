package marytts.tools.newinstall;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import junit.framework.TestCase;
import marytts.tools.newinstall.objects.Component;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

public class InstallerTest extends TestCase {

	public InstallerTest(String name) {
		super(name);
	}

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	static Installer installer = new Installer(new String[] { "--nogui" });

	// @After
	// public void tearDown() {
	//
	// this.tempFolder.delete();
	// }

	@Test
	public void testSetMaryBase() {

		Component voiceCompToTest = installer.getAvailableComponents(null, null, null, null, "cmu-slt-hsmm", false).get(0);
		// Component compToTest = installer.getAvailableComponents(null, null, null, null, "te", false).get(0);

		try {
			File maryBaseDirClean = this.tempFolder.newFolder("maryBaseDirClean");
			System.out.println(maryBaseDirClean.getAbsolutePath());
			assertTrue(installer.setMaryBase(maryBaseDirClean));
			System.out.println(installer.getMaryBasePath());
			installer.install(voiceCompToTest);
			assertTrue(new File(maryBaseDirClean + "/lib").exists());
			assertTrue(new File(maryBaseDirClean + "/installed").exists());
			assertTrue(new File(maryBaseDirClean + "/download").exists());

			String voiceArtifactName = voiceCompToTest.getArtifactName();
			// don't yet know how to retrieve artifact of dependency (so far, can only reach to the DependencyDescriptor. The
			// artifact of the dependency itself is null.
			// String voiceArtifactDepName = voiceCompToTest.getDependencyArtifact();
			assertTrue(new File(maryBaseDirClean + "/lib/" + voiceArtifactName).exists());
			assertTrue(new File(maryBaseDirClean + "/download/" + voiceArtifactName).exists());
			// assertTrue(new File(maryBaseDirClean + "/lib/" + voiceArtifactDepName).exists());

			Artifact artifactDescriptor = voiceCompToTest.getModuleDescriptor().getMetadataArtifact();
			String descriptorName = artifactDescriptor.getAttribute("module") + "-" + artifactDescriptor.getAttribute("revision")
					+ "." + artifactDescriptor.getExt();
			assertTrue(new File(maryBaseDirClean + "/installed/" + descriptorName).exists());
			assertTrue(new File(maryBaseDirClean + "/download/" + descriptorName).exists());
		} catch (IOException e) {
			assertTrue(true);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
