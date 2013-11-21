package marytts.tools.newinstall;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import marytts.tools.newinstall.objects.Component;
import marytts.tools.newinstall.objects.VoiceComponent;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

/**
 * 
 * 
 * @author Jonathan
 * 
 */
public class Installer {

	private List<Component> resources;
	private IvySettings ivySettings;

	/**
	 * constructor for Installer
	 */
	public Installer() {

		try {
			this.resources = new ArrayList<Component>();
			this.ivySettings = new IvySettings();
			this.ivySettings.load(Resources.getResource("ivysettings.xml"));
			parseIvyResources(this.ivySettings);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse list of voice descriptors from JSON array in resource. The resource is generated at compile time by the <a
	 * href="http://numberfour.github.io/file-list-maven-plugin/list-mojo.html">file-list-maven-plugin</a>.
	 * 
	 * @return List of voice descriptor resources
	 * @throws IOException
	 */
	public List<String> readComponentDescriptorList() throws IOException {
		URL componentListResource = Resources.getResource("component-list.json");
		String componentListJson = Resources.toString(componentListResource, Charsets.UTF_8);
		String[] componentDescriptors = new Gson().fromJson(componentListJson, String[].class);
		return Arrays.asList(componentDescriptors);
	}

	/**
	 * retrieves the voice component names from the {@link #readComponentDescriptorList()} and creates {@link Component} objects.
	 * TODO remove repeated code
	 * 
	 * @param ivySettings
	 * @throws ParseException
	 * @throws IOException
	 */
	private void parseIvyResources(IvySettings ivySettings) throws ParseException, IOException {
		List<String> resourcesList = readComponentDescriptorList();
		for (String oneFileName : resourcesList) {
			if (oneFileName.startsWith("marytts-voice")) {

				URL oneResource = Resources.getResource(oneFileName);
				ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings, oneResource,
						true);
				VoiceComponent oneComponent = new VoiceComponent(descriptor);
				this.resources.add(oneComponent);
			} else if (oneFileName.startsWith("marytts-lang")) {

				URL oneResource = Resources.getResource(oneFileName);
				ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings, oneResource,
						true);
				Component oneComponent = new Component(descriptor);
				this.resources.add(oneComponent);
			} else {
				continue;
			}
		}

	}

	/**
	 * @return component list
	 */
	public List<Component> getAvailableComponents() {

		return this.resources;
	}

	/**
	 * filters the available voice components by a certain attribute-value pair. Iterates over list of components and removes
	 * those that match the given attribute-value pair
	 * 
	 * @param resToBeFiltered
	 *            the resources to be filtered
	 * @param attribute
	 *            the attribute that the component list should be filtered by (i.e., "gender", "locale", "name" and "type")
	 * @param attributeValue
	 *            the value of the attribute
	 * @return the filtered list
	 * @throws Exception
	 */
	public List<Component> filterResources(List<Component> resToBeFiltered, String attribute, String attributeValue)
			throws Exception {

		// stores the size of the voice component list before filtering.
		int sizeBefore = resToBeFiltered.size();

		// in order to modify the list while iterating over it, an iterator is needed to call the Iterator.remove() method.
		Iterator<Component> it = resToBeFiltered.iterator();

		if (attribute.equals("locale")) {
			System.out.println("filtering by " + attribute + "=" + attributeValue);
			for (it = it; it.hasNext();) {
				Component oneComponent = it.next();
				if (!oneComponent.getLocale().toString().equalsIgnoreCase(attributeValue)) {
					it.remove();
				}
			}
		} else if (attribute.equals("type")) {
			System.out.println("filtering by " + attribute + "=" + attributeValue);
			for (it = it; it.hasNext();) {
				VoiceComponent oneComponent = (VoiceComponent) it.next();
				if (!oneComponent.getType().equalsIgnoreCase(attributeValue)) {
					it.remove();
				}
			}
		} else if (attribute.equals("gender")) {
			System.out.println("filtering by " + attribute + "=" + attributeValue);
			for (it = it; it.hasNext();) {
				VoiceComponent oneComponent = (VoiceComponent) it.next();
				if (!oneComponent.getGender().equalsIgnoreCase(attributeValue)) {
					it.remove();
				}
			}
		} else if (attribute.equals("name")) {
			System.out.println("filtering by " + attribute + "=" + attributeValue);
			for (it = it; it.hasNext();) {
				Component oneComponent = it.next();
				if (!oneComponent.getName().equalsIgnoreCase(attributeValue)) {
					it.remove();
				}
			}
		}

		int sizeAfter = resToBeFiltered.size();

		// if list hasn't been empty before, but is afterwards, the attr-value pair filtered out everything that remained by using
		// an non-existing attribute value or a value that doesn't occur in the list.
		if (sizeBefore > 0 && sizeAfter == 0) {
			System.out.println(attribute + "=" + attributeValue + " is not valid or filtered out all remaining components.");
			throw new Exception(" No more filtering possible. Component list empty.");
			// if list size didn't change, the attr-value pair can be discarded as well as it doesn't have affect.
		} else if (sizeBefore == sizeAfter) {
			System.out.println(attribute + "=" + attributeValue + " doesn't affect filtering.");
		}

		return resToBeFiltered;
	}

	/**
	 * 
	 * TODO not used at the moment, remove. <br>
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
		URL resource = Resources.getResource("marytts-voice-cmu-slt-hsmm-5.1-SNAPSHOT.xml");
		ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings, resource, true);

		// instantiate ivy
		Ivy ivy = Ivy.newInstance(ivySettings);
		// set ivy loglevel to DEBUG
		ivy.getLoggerEngine().pushLogger(new DefaultMessageLogger(Message.MSG_DEBUG));
		ResolveOptions resolveOptions = new ResolveOptions();
		ivy.resolve(descriptor, resolveOptions);
		// retrieve options; retrieved artifacts go into MARYBASE/lib
		RetrieveOptions retrieveOptions = new RetrieveOptions();
		retrieveOptions.setDestIvyPattern("${mary.base}/installed/[module]-[revision].xml");
		retrieveOptions.setDestArtifactPattern("${mary.base}/lib/[module]-[revision].[ext]");
		ivy.retrieve(descriptor.getModuleRevisionId(), retrieveOptions);

		InstallOptions installOptions = new InstallOptions();
		installOptions.setTransitive(true);
		installOptions.setOverwrite(true);
		String from = "remote";
		String to = "marytts-installed";
		ivy.install(descriptor.getModuleRevisionId(), from, to, installOptions);
	}

	/**
	 * returns - if present - the Component with nameValue as name. If not presents, returns null
	 * 
	 * @param nameValue
	 *            the value of the name to be searched for
	 * @return the component with nameValue, null if not present
	 */
	public Component getComponentByName(String nameValue) {

		for (Component oneComponent : this.resources) {
			if (oneComponent.getName().equalsIgnoreCase(nameValue)) {
				return oneComponent;
			}
		}
		return null;
	}

	/**
	 * checks if component list contains a {@link Component} with the name equal to the one passed along to this method.
	 * 
	 * @param nameValue
	 *            the value of the name to be searched for
	 * @return true if nameValue was found, false otherwise
	 */
	public boolean isNamePresent(String nameValue) {

		for (Component oneComponent : this.resources) {
			if (oneComponent.getName().equalsIgnoreCase(nameValue)) {
				return true;
			}
		}
		return false;
	}
}
