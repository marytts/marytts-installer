package marytts.tools.newinstall.objects;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Resources;

public class LangComponentTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private LangComponent component;

	@Before
	public void setUp() throws ParseException, IOException {
		IvySettings ivySettings = new IvySettings();
		ivySettings.setVariable("mary.base", tempFolder.newFolder(this.getClass().getName()).getAbsolutePath());
		ivySettings.load(Resources.getResource("ivysettings.xml"));

		URL langComponentResource = Resources.getResource("marytts-voice-cmu-slt-hsmm-5.1-beta1.xml");
		ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings, langComponentResource,
				true);
		component = new LangComponent(descriptor);
	}

	@Test
	@Ignore
	public void hasLocale() {
		assertNotNull(component.getLocale());
	}

}
