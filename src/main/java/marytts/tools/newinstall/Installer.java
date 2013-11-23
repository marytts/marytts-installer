package marytts.tools.newinstall;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import marytts.tools.newinstall.objects.Component;
import marytts.tools.newinstall.objects.VoiceComponent;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

/**
 * 
 * 
 * @author Jonathan, Ingmar
 * 
 */
public class Installer {

	private Ivy ivy;
	private IvySettings ivySettings;
	private ResolveOptions resolveOptions;
	private InstallOptions installOptions;

	private List<Component> resources;
	private HashMap<String, HashSet<String>> attributeValues;
	private InstallerCLI cli;
	private String maryBasePath;

	/**
	 * constructor for Installer
	 */
	public Installer() {

		try {
			this.resources = new ArrayList<Component>();
			this.ivySettings = new IvySettings();
			this.ivySettings.load(Resources.getResource("ivysettings.xml"));
			this.attributeValues = new HashMap<String, HashSet<String>>();
			// this.attributeValues.put("name", new HashSet<String>());
			this.attributeValues.put("locale", new HashSet<String>());
			this.attributeValues.put("status", new HashSet<String>());
			this.attributeValues.put("type", new HashSet<String>());
			this.attributeValues.put("gender", new HashSet<String>());
			parseIvyResources(this.ivySettings);

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Installer(String[] args) {
		cli = new InstallerCLI(args);
		setMaryBase();

		// setup ivy
		IvySettings ivySettings = new IvySettings();
		ivySettings.setVariable("mary.base", maryBasePath);
		try {
			ivySettings.load(Resources.getResource("ivysettings.xml"));
		} catch (IOException ioe) {
			System.err.printf("Could not access settings file: %s", ioe.getMessage());
		} catch (ParseException pe) {
			System.err.printf("Could not parse settings file: %s", pe.getMessage());
		}
		Ivy.newInstance(ivySettings);
		resolveOptions = new ResolveOptions().setOutputReport(false);
		installOptions = new InstallOptions().setOverwrite(true).setTransitive(true);
	}

	private void setMaryBase() {
		String userSelection = cli.getTargetDirectory();
		File maryBase = null;
		try {
			maryBase = new File(userSelection);
		} catch (NullPointerException npe) {
			// no target directory selected;
			// fall back to location of this class/jar
			// from http://stackoverflow.com/a/320595
			URL location = Installer.class.getProtectionDomain().getCodeSource().getLocation();
			try {
				maryBase = new File(location.toURI().getPath());
			} catch (URISyntaxException use) {
				// TODO Auto-generated catch block
				System.err.printf("Could not parse %s: %s\n", location, use.getMessage());
			}
		}
		setMaryBase(maryBase);
	}

	private void setMaryBase(File maryBase) {
		try {
			maryBase = maryBase.getCanonicalFile();
		} catch (IOException ioe) {
			System.err.printf("Could not determine path to directory %s: %s\n", maryBase, ioe);
		}
		// if this is running from the jar file, back off to directory containing it
		if (maryBase.isFile()) {
			maryBase = maryBase.getParentFile();
		}
		// create directory (with parents, if required)
		try {
			FileUtils.forceMkdir(maryBase);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		try {
			maryBasePath = maryBase.getCanonicalPath();
		} catch (IOException ioe) {
			System.err.printf("Could not determine path to directory %s: %s\n", maryBase, ioe);
		}
	}

	public void install(Component component) throws ParseException, IOException {
		ivy.resolve(component.getModuleDescriptor(), resolveOptions);
		ivy.install(component.getModuleDescriptor().getModuleRevisionId(), "remote", "installed", installOptions);
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
				storeAttributeValues(oneComponent);
			} else if (oneFileName.startsWith("marytts-lang")) {

				URL oneResource = Resources.getResource(oneFileName);
				ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings, oneResource,
						true);
				Component oneComponent = new Component(descriptor);
				this.resources.add(oneComponent);
				storeAttributeValues(oneComponent);
			} else {
				continue;
			}
		}

	}

	private void storeAttributeValues(Component oneComponent) {

		// this.attributeValues.get("name").add(oneComponent.getName());
		this.attributeValues.get("locale").add(oneComponent.getLocale().toString());
		this.attributeValues.get("status").add(oneComponent.getStatus().toString());
		if (oneComponent instanceof VoiceComponent) {
			VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
			this.attributeValues.get("type").add(oneVoiceComponent.getType());
			this.attributeValues.get("gender").add(oneVoiceComponent.getGender());
		}
	}

	/**
	 * @return component list
	 */
	public List<Component> getAvailableComponents() {

		return this.resources;
	}

	/**
	 * @return the attributeValues
	 */
	public HashMap<String, HashSet<String>> getAttributeValues() {
		return this.attributeValues;
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

	/**
	 * 
	 * TODO not used at the moment, remove. <br>
	 * Test Installer <br>
	 * <b>Note:</b> must currently run with -Dmary.base=/path/to/marytts
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Installer installer = new Installer(args);
	}
}
