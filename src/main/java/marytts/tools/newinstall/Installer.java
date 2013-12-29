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
import org.apache.commons.io.FilenameUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ArtifactRevisionId;
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

	private Installer() {

	}

	public Installer(String[] args) {

		logger.debug("Loading installer.");
		this.resources = new ArrayList<Component>();
		this.cli = new InstallerCLI(args, this);

		// test if user has specified mary path on command line. If not, determine directory Installer is run from
		if (this.maryBasePath == null) {
			setMaryBase();
		}
		logger.debug("Set mary base path to: " + this.maryBasePath);

		// setup ivy

		IvySettings ivySettings = new IvySettings();
		ivySettings.setVariable("mary.base", this.maryBasePath);
		try {
			logger.debug("Loading ivysettings.xml");
			ivySettings.load(Resources.getResource("ivysettings.xml"));
			initAttributeValues();
			logger.debug("Starting ivy resource parse");
			parseIvyResources(ivySettings);
			logger.info("Creating new Ivy file");
			this.ivy = Ivy.newInstance(ivySettings);
			this.resolveOptions = new ResolveOptions().setOutputReport(false);
			this.installOptions = new InstallOptions().setOverwrite(true).setTransitive(true);

			//
			this.cli.mainEvalCommandLine();

		} catch (IOException ioe) {
			logger.error("Could not access settings file: " + ioe.getMessage());
		} catch (ParseException pe) {
			logger.error("Could not access settings file: " + pe.getMessage());
		}
	}

	private void setMaryBase() {
		logger.debug("Setting mary base directory");
		// String userSelection = cli.getTargetDirectory();
		File maryBase = null;
		// try {
		// maryBase = new File(userSelection);
		// } catch (NullPointerException npe) {
		// no target directory selected;
		// fall back to location of this class/jar
		// from http://stackoverflow.com/a/320595
		// logger.warn("No directory specified on the command line");
		URL location = Installer.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			logger.debug("Trying to use directory Installer is run from.");
			maryBase = new File(location.toURI().getPath());
		} catch (URISyntaxException use) {
			// TODO Auto-generated catch block
			logger.error("Could not parse " + location + ": " + use.getMessage() + "\n");
		}
		// }
		setMaryBase(maryBase);
	}

	public void setMaryBase(File maryBase) {
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
			this.maryBasePath = maryBase.getCanonicalPath();
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
		ResolveReport resolve = this.ivy.resolve(component.getModuleDescriptor(), this.resolveOptions);
		ResolveReport install = this.ivy.install(component.getModuleDescriptor().getModuleRevisionId(), "remote", "installed",
				this.installOptions);

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

					ArtifactRevisionId artifactRevisionId = descriptor.getAllArtifacts()[0].getId();
					String artifactName = artifactRevisionId.getAttribute("organisation") + "-" + artifactRevisionId.getName()
							+ "-" + artifactRevisionId.getRevision() + "." + artifactRevisionId.getExt();
					oneComponent.setStatus(getResourceStatus(artifactName));
					this.resources.add(oneComponent);
					storeAttributeValues(oneComponent);
					logger.info((oneComponent.getClass().getSimpleName().equals("VoiceComponent") ? "VoiceComponent "
							: "Component ") + oneComponent.getName() + " added to resource list.");
				} else if (oneFileName.startsWith("marytts-lang")) {

					URL oneResource = Resources.getResource(oneFileName);
					ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings,
							oneResource, true);
					Component oneComponent = new Component(descriptor);
					String componentJarName = FilenameUtils.removeExtension(oneFileName) + ".jar";
					oneComponent.setStatus(getResourceStatus(componentJarName));
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

	private Status getResourceStatus(String componentName) {

		if (new File(this.maryBasePath + "/installed/" + componentName).exists()) {
			return Status.INSTALLED;
		}
		if (new File(this.maryBasePath + "/download/" + componentName).exists()) {
			return Status.DOWNLOADED;
		}
		return Status.AVAILABLE;
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
	 * filters the available components by one or many attribute-value pairs. Iterates over list of components and removes those
	 * that match the given attribute-value pair. if all attributeValues are null, the method simply returns all components.
	 * 
	 * @param locale
	 * @param type
	 * @param gender
	 * @param status
	 * @param name
	 * @return component list
	 */
	public List<Component> getAvailableComponents(String locale, String type, String gender, String status, String name,
			boolean voiceOnly) {

//		/* @formatter:off */
//		logger.info("Filtering resources by " + ((locale == null) ? "" : "locale=" + locale + ";")
//										      + ((type == null) ? "" : "locale=" + type + ";") 
//										      + ((gender== null) ? "" : "locale=" + gender + ";")
//										      + ((status == null) ? "" : "locale=" + status + ";")
//										      + ((name == null) ? "" : "locale=" + name + ";"));
//		/* @formatter:on */

		List<Component> resourcesToBeFiltered = new ArrayList<Component>(this.resources);

		// stores the size of the voice component list before filtering.
		int sizeBefore = resourcesToBeFiltered.size();
		logger.debug("Resource list size before filtering: " + sizeBefore);

		// in order to modify the list while iterating over it, an iterator is needed to call the Iterator.remove() method.
		Iterator<Component> it;

		if (resourcesToBeFiltered.isEmpty()) {
			logger.warn("List is empty!");
			return resourcesToBeFiltered;
		}

		// int sizeAfter = resourcesToBeFiltered.size();
		if (locale != null) {
			logger.info("filtering by " + "locale=" + locale);
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!oneComponent.getLocale().toString().equalsIgnoreCase(locale)) {
					// logger.debug("Removed " + oneComponent + " as its locale=" + locale);
					it.remove();
				}
			}
			// sizeAfter = resourcesToBeFiltered.size();
			// logger.debug("Resource list size after filtering: " + sizeAfter);
			// if (sizeBefore == sizeAfter) {
			// logger.info("Locale didn't affect filtering");
			// }
			// sizeBefore = sizeAfter;
		}
		if (type != null) {
			logger.info("filtering by " + "type=" + type);
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!(oneComponent instanceof VoiceComponent)) {
					logger.debug("Removed " + oneComponent + " as it is not a VoiceComponent");
					it.remove();
					continue;
				}
				VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
				if (!oneVoiceComponent.getType().equalsIgnoreCase(type)) {
					it.remove();
				}
			}
		}
		if (gender != null) {
			logger.info("filtering by " + "gender=" + gender);
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!(oneComponent instanceof VoiceComponent)) {
					logger.debug("Removed " + oneComponent + " as it is not a VoiceComponent");
					it.remove();
					continue;
				}
				VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
				if (!oneVoiceComponent.getGender().equalsIgnoreCase(gender)) {
					it.remove();
				}
			}
		}
		if (status != null) {
			logger.info("filtering by " + "status=" + status);
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!oneComponent.getStatus().toString().equalsIgnoreCase(status)) {
					it.remove();
				}
			}
		}
		if (name != null) {
			logger.info("filtering by " + "name=" + name);
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!oneComponent.getName().equalsIgnoreCase(locale)) {
					it.remove();
				}
			}
		}
		if (voiceOnly) {
			logger.info("filtering by component type=" + (voiceOnly ? "voice " : " ") + "component");
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!(oneComponent instanceof VoiceComponent)) {
					it.remove();
				}
			}
		}

		return resourcesToBeFiltered;
	}

	/**
	 * @return the attributeValues
	 */
	public HashMap<String, HashSet<String>> getAttributeValues() {
		return this.attributeValues;
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

	// /**
	// * @return
	// */
	// public List<Component> getAvailableComponents() {
	// return getAvailableComponents(null, null, null, null, null, false);
	// }

	/**
	 * 
	 * Installer Main Method<br>
	 * <b>Note:</b> must currently run with -Dmary.base=/path/to/marytts
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Installer installer = new Installer(args);
	}

}
