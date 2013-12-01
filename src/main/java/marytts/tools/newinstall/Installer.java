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
import java.util.Map;

import marytts.tools.newinstall.objects.Component;
import marytts.tools.newinstall.objects.VoiceComponent;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.DownloadStatus;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;
import org.apache.log4j.Logger;

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
	// private IvySettings ivySettings;
	private ResolveOptions resolveOptions;
	private InstallOptions installOptions;

	private List<Component> resources;
	private HashMap<String, HashSet<String>> attributeValues;
	private InstallerCLI cli;
	private String maryBasePath;

	static Logger logger = Logger.getLogger(marytts.tools.newinstall.Installer.class.getName());

	// /**
	// * constructor for Installer
	// */
	// public Installer() {
	//
	// try {
	// this.resources = new ArrayList<Component>();
	// this.ivySettings = new IvySettings();
	// this.ivySettings.load(Resources.getResource("ivysettings.xml"));
	// initAttributeValues();
	// parseIvyResources(this.ivySettings);
	//
	// } catch (ParseException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * initializes the attribute values to be stored in Installer for later usage in GUI and CLI
	 */
	private void initAttributeValues() {
		this.attributeValues = new HashMap<String, HashSet<String>>();
		this.attributeValues.put("locale", new HashSet<String>());
		this.attributeValues.put("status", new HashSet<String>());
		this.attributeValues.put("type", new HashSet<String>());
		this.attributeValues.put("gender", new HashSet<String>());
	}

	public Installer(String[] args) {

		logger.debug("Loading installer.");
		this.resources = new ArrayList<Component>();
		cli = new InstallerCLI(args, this);
		setMaryBase();
		logger.debug("Set mary base path to: " + maryBasePath);

		// setup ivy

		IvySettings ivySettings = new IvySettings();
		ivySettings.setVariable("mary.base", maryBasePath);
		try {
			logger.debug("Loading ivysettings.xml");
			ivySettings.load(Resources.getResource("ivysettings.xml"));
			initAttributeValues();
			logger.debug("Starting ivy resource parse");
			parseIvyResources(ivySettings);
		} catch (IOException ioe) {
			logger.error("Could not access settings file: " + ioe.getMessage());
		} catch (ParseException pe) {
			logger.error("Could not access settings file: " + pe.getMessage());
		}
		logger.info("Creating new Ivy file");
		this.ivy = Ivy.newInstance(ivySettings);
		resolveOptions = new ResolveOptions().setOutputReport(false);
		installOptions = new InstallOptions().setOverwrite(true).setTransitive(true);
	}

	private void setMaryBase() {
		logger.debug("Setting mary base directory");
		String userSelection = cli.getTargetDirectory();
		File maryBase = null;
		try {
			maryBase = new File(userSelection);
		} catch (NullPointerException npe) {
			// no target directory selected;
			// fall back to location of this class/jar
			// from http://stackoverflow.com/a/320595
			logger.warn("No directory specified on the command line");
			URL location = Installer.class.getProtectionDomain().getCodeSource().getLocation();
			try {
				logger.debug("Trying to use directory Installer is run from.");
				maryBase = new File(location.toURI().getPath());
			} catch (URISyntaxException use) {
				// TODO Auto-generated catch block
				logger.error("Could not parse " + location + ": " + use.getMessage() + "\n");
			}
		}
		setMaryBase(maryBase);
	}

	private void setMaryBase(File maryBase) {
		try {
			maryBase = maryBase.getCanonicalFile();
		} catch (IOException ioe) {
			logger.error("Could not determine path to directory " + maryBase + ": " + ioe + "\n");
		}
		// if this is running from the jar file, back off to directory containing it
		if (maryBase.isFile()) {
			logger.debug("Installer is running from jar. Creating directory for setting mary base path");
			maryBase = maryBase.getParentFile();
		}
		// create directory (with parents, if required)
		try {
			FileUtils.forceMkdir(maryBase);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		}
		try {
			maryBasePath = maryBase.getCanonicalPath();
		} catch (IOException ioe) {
			logger.error("Could not determine path to directory " + maryBase + ": " + ioe + "\n");
		}
	}

	/**
	 * @return the maryBasePath
	 */
	public String getMaryBasePath() {
		return this.maryBasePath;
	}

	public DownloadStatus install(Component component) throws ParseException, IOException {
		logger.info("Resolving and installing component " + component.getName());
		ResolveReport resolve = ivy.resolve(component.getModuleDescriptor(), resolveOptions);
		ResolveReport install = ivy.install(component.getModuleDescriptor().getModuleRevisionId(), "remote", "installed",
				installOptions);

		ArtifactDownloadReport[] dependencyReports = resolve.getAllArtifactsReports();
		logger.debug("Resolve reports of dependencies");
		for (int i = 0; i < resolve.getAllArtifactsReports().length; i++) {
			logger.debug(dependencyReports[i]);
		}

		ArtifactDownloadReport[] installReports = install.getAllArtifactsReports();
		logger.debug("Resolve reports of target installation");
		if (installReports.length > 1) {
			logger.error("There are " + installReports.length
					+ " artifacts. There should not be more than one target artifact to be resolved.");
		}
		logger.debug(installReports[0]);
		return installReports[0].getDownloadStatus();
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
		logger.debug("reading component descriptor list component-list.json from resources");
		String componentListJson = Resources.toString(componentListResource, Charsets.UTF_8);
		String[] componentDescriptors = new Gson().fromJson(componentListJson, String[].class);
		return Arrays.asList(componentDescriptors);
	}

	/**
	 * retrieves the voice component names from the {@link #readComponentDescriptorList()} and creates {@link Component} objects.<br>
	 * Those Components then are added to the list holding all Components and the {{@link #storeAttributeValues(Component)} method
	 * takes care of storing possible attribute values in a HashMap<br>
	 * TODO remove repeated code
	 * 
	 * @param ivySettings
	 * @throws ParseException
	 * @throws IOException
	 */
	private void parseIvyResources(IvySettings ivySettings) {

		try {
			List<String> resourcesList = readComponentDescriptorList();
			for (String oneFileName : resourcesList) {
				logger.debug("Parsing " + oneFileName);
				if (oneFileName.startsWith("marytts-voice")) {

					URL oneResource = Resources.getResource(oneFileName);
					ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings,
							oneResource, true);
					VoiceComponent oneComponent = new VoiceComponent(descriptor);
					this.resources.add(oneComponent);
					storeAttributeValues(oneComponent);
					logger.info((oneComponent.getClass().getSimpleName().equals("VoiceComponent") ? "VoiceComponent "
							: "Component ") + oneComponent.getName() + " added to resource list.");
				} else if (oneFileName.startsWith("marytts-lang")) {

					URL oneResource = Resources.getResource(oneFileName);
					ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings,
							oneResource, true);
					Component oneComponent = new Component(descriptor);
					this.resources.add(oneComponent);
					storeAttributeValues(oneComponent);
					logger.info((oneComponent.getClass().getSimpleName().equals("VoiceComponent") ? "VoiceComponent "
							: "Component ") + oneComponent.getName() + " added to resource list.");
				}
			}
		} catch (IOException ioe) {
			logger.error("Problem reading in file: " + ioe.getMessage());
		} catch (ParseException pe) {
			logger.error("Problem parsing component file: " + pe.getMessage());
		}
	}

	/**
	 * When a {@link Component} or {@link VoiceComponent} is parsed, it's attribute values are extracted and added to a hashMap in
	 * order for the GUI and the CLI to access these values easily later.
	 * 
	 * @param oneComponent
	 */
	private void storeAttributeValues(Component oneComponent) {

		logger.debug("Adding component's attribute values to attributeList");
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
	 * filters the available components by one or many attribute-value pairs. Iterates over list of components and removes those
	 * that match the given attribute-value pair
	 * 
	 * @param filterMap
	 * @return
	 * @throws Exception
	 */
	public List<Component> filterGlobal(HashMap<String, String> filterMap) {

		logger.info("Filtering resources by " + filterMap.toString());
		List<Component> resourcesToBeFiltered = new ArrayList<Component>(this.resources);

		// stores the size of the voice component list before filtering.
		int sizeBefore = resourcesToBeFiltered.size();
		logger.debug("Resource list size before filtering: " + sizeBefore);

		String attribute = "", attributeValue = "";
		for (Map.Entry<String, String> oneEntry : filterMap.entrySet()) {
			// in order to modify the list while iterating over it, an iterator is needed to call the Iterator.remove() method.
			Iterator<Component> it = resourcesToBeFiltered.iterator();

			if (oneEntry.getValue().equals("")) {
				continue;
			}
			if (resourcesToBeFiltered.isEmpty()) {
				logger.warn("List is empty!");
				break;
			}
			attribute = oneEntry.getKey();
			attributeValue = oneEntry.getValue();
			if (attribute.equalsIgnoreCase("locale")) {
				logger.info("filtering by " + attribute + "=" + attributeValue);
				for (it = it; it.hasNext();) {
					Component oneComponent = it.next();
					if (!oneComponent.getLocale().toString().equalsIgnoreCase(attributeValue)) {
						logger.debug("Removed " + oneComponent + " as its locale = " + attributeValue);
						it.remove();
					}
				}
			} else if (attribute.equals("type")) {
				logger.info("filtering by " + attribute + "=" + attributeValue);
				for (it = it; it.hasNext();) {
					Component oneComponent = it.next();
					if (!(oneComponent instanceof VoiceComponent)) {
						logger.debug("Removed " + oneComponent + " as it is not a VoiceComponent");
						it.remove();
						continue;
					}
					VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
					if (!oneVoiceComponent.getType().equalsIgnoreCase(attributeValue)) {
						logger.debug("Removed " + oneComponent + " as its type = " + attributeValue);
						it.remove();
					}
				}
			} else if (attribute.equals("gender")) {
				logger.info("filtering by " + attribute + "=" + attributeValue);
				for (it = it; it.hasNext();) {
					Component oneComponent = it.next();
					if (!(oneComponent instanceof VoiceComponent)) {
						logger.debug("Removed " + oneComponent + " as it is not a VoiceComponent");
						it.remove();
						continue;
					}
					VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
					if (!oneVoiceComponent.getType().equalsIgnoreCase(attributeValue)) {
						logger.debug("Removed " + oneComponent + " as its gender = " + attributeValue);
						it.remove();
					}
				}
			} else if (attribute.equals("status")) {
				logger.info("filtering by " + attribute + "=" + attributeValue);
				for (it = it; it.hasNext();) {
					Component oneComponent = it.next();
					if (!oneComponent.getStatus().toString().equalsIgnoreCase(attributeValue)) {
						logger.debug("Removed " + oneComponent + " as its status = " + attributeValue);
						it.remove();
					}
				}
			}
			int sizeAfterTMP = resourcesToBeFiltered.size();
			if (sizeBefore == sizeAfterTMP) {
				logger.warn(attribute + "=" + attributeValue + " doesn't affect filtering.");
			}
		}
		int sizeAfter = resourcesToBeFiltered.size();
		logger.debug("Resource list size after filtering: " + sizeAfter);

		// if list hasn't been empty before, but is afterwards, the attr-value pair filtered out everything that remained by using
		// an non-existing attribute value or a value that doesn't occur in the list.
		// if (sizeBefore > 0 && sizeAfter == 0) {
		// System.out.println(attribute + "=" + attributeValue + " is not valid or filtered out all remaining components.");
		// throw new Exception(" No more filtering possible. Component list empty.");
		// // if list size didn't change, the attr-value pair can be discarded as well as it doesn't have affect.
		// }
		if (sizeBefore != sizeAfter) {
			logger.info("Successfully filtered resource list");
		}

		return resourcesToBeFiltered;

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
	// public List<Component> filterResources(List<Component> resToBeFiltered, String attribute, String attributeValue)
	// throws Exception {
	//
	// // stores the size of the voice component list before filtering.
	// int sizeBefore = resToBeFiltered.size();
	//
	// // in order to modify the list while iterating over it, an iterator is needed to call the Iterator.remove() method.
	// Iterator<Component> it = resToBeFiltered.iterator();
	//
	// if (attribute.equals("locale")) {
	// System.out.println("filtering by " + attribute + "=" + attributeValue);
	// for (it = it; it.hasNext();) {
	// Component oneComponent = it.next();
	// if (!oneComponent.getLocale().toString().equalsIgnoreCase(attributeValue)) {
	// it.remove();
	// }
	// }
	// } else if (attribute.equals("type")) {
	// System.out.println("filtering by " + attribute + "=" + attributeValue);
	// for (it = it; it.hasNext();) {
	// Component oneComponent = it.next();
	// if (!(oneComponent instanceof VoiceComponent)) {
	// it.remove();
	// }
	// VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
	// if (!oneVoiceComponent.getType().equalsIgnoreCase(attributeValue)) {
	// it.remove();
	// }
	// }
	// } else if (attribute.equals("gender")) {
	// System.out.println("filtering by " + attribute + "=" + attributeValue);
	// for (it = it; it.hasNext();) {
	// Component oneComponent = it.next();
	// if (!(oneComponent instanceof VoiceComponent)) {
	// it.remove();
	// }
	// VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
	// if (!oneVoiceComponent.getType().equalsIgnoreCase(attributeValue)) {
	// it.remove();
	// }
	// }
	// } else if (attribute.equals("name")) {
	// System.out.println("filtering by " + attribute + "=" + attributeValue);
	// for (it = it; it.hasNext();) {
	// Component oneComponent = it.next();
	// if (!oneComponent.getName().equalsIgnoreCase(attributeValue)) {
	// it.remove();
	// }
	// }
	// }
	//
	// int sizeAfter = resToBeFiltered.size();
	//
	// // if list hasn't been empty before, but is afterwards, the attr-value pair filtered out everything that remained by using
	// // an non-existing attribute value or a value that doesn't occur in the list.
	// if (sizeBefore > 0 && sizeAfter == 0) {
	// System.out.println(attribute + "=" + attributeValue + " is not valid or filtered out all remaining components.");
	// throw new Exception(" No more filtering possible. Component list empty.");
	// // if list size didn't change, the attr-value pair can be discarded as well as it doesn't have affect.
	// } else if (sizeBefore == sizeAfter) {
	// System.out.println(attribute + "=" + attributeValue + " doesn't affect filtering.");
	// }
	//
	// return resToBeFiltered;
	// }

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
