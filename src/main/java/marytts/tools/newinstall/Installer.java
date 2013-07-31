package marytts.tools.newinstall;

import java.io.IOException;
import java.text.ParseException;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;

import com.google.common.io.Resources;

public class Installer {

	/**
	 * Test Installer <br>
	 * <b>Note:</b> must currently run with -Dmary.base=/path/to/marytts
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		// load ivy settings
		IvySettings ivySettings = new IvySettings();
		ivySettings.load(Resources.getResource("ivysettings.xml"));

		// as a test, parse module descriptor for lang-de component
		ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings,
				Resources.getResource("marytts-lang-de/ivy-5.1-SNAPSHOT.xml"), true);

		// instantiate ivy
		Ivy ivy = Ivy.newInstance(ivySettings);
		// set ivy loglevel to DEBUG
		ivy.getLoggerEngine().pushLogger(new DefaultMessageLogger(Message.MSG_DEBUG));
		// retrieve options; retrieved artifacts go into MARYBASE/lib
		RetrieveOptions retrieveOptions = new RetrieveOptions()
				.setDestArtifactPattern("${mary.base}/lib/[artifact]-[revision].[ext]");
		ivy.retrieve(descriptor.getModuleRevisionId(), retrieveOptions);
	}

}
