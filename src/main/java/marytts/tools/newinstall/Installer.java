package marytts.tools.newinstall;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import marytts.tools.newinstall.enums.LogLevel;
import marytts.tools.newinstall.enums.Status;
import marytts.tools.newinstall.objects.Component;
import marytts.tools.newinstall.objects.LangComponent;
import marytts.tools.newinstall.objects.VoiceComponent;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.descriptor.DependencyArtifactDescriptor;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ArtifactRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;
import org.apache.ivy.plugins.resolver.RepositoryResolver;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.filter.ArtifactTypeFilter;
import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

/**
 * Main class of the component installer. Both holds the main method and holds data and functionality methods on the component
 * data
 * 
 * @author Jonathan, Ingmar
 * 
 */
public class Installer {

	// Ivy instance, used for installation and resolving purposes
	private Ivy ivy;

	// Java representation of the ivysettings.xml file that holds information about repository structure and location
	private IvySettings ivySettings;

	// Options passed on to the resolve/install methods of the Ivy object
	private ResolveOptions resolveOptions;
	private InstallOptions installOptions;

	private ArtifactTypeFilter jarFilter;

	// holds all currently available components
	private Set<Component> resources;

	// the instance of the command line interface which is created once the installer is started
	private InstallerCLI cli;

	// holds the directory where marytts should be (preferably: is) installed. This location will be used to put downloaded and
	// installed components in
	private String maryBasePath;

	// holds the logLevel currently used. Used to set Ivy logging once Ivy is started.
	private LogLevel logLevel;

	static Logger logger = Logger.getLogger(marytts.tools.newinstall.Installer.class.getName());

	public Installer(String[] args) {

		logger.debug("Loading installer.");
		this.resources = Sets.newTreeSet();
		jarFilter = new ArtifactTypeFilter(Lists.newArrayList("jar"));
		// default value for logging. may be overwritten by InstallerCLI
		this.logLevel = LogLevel.info;
		this.cli = new InstallerCLI(args, this);

		// test if user has specified mary path on command line. If not, determine directory Installer is run from
		if (this.maryBasePath == null) {
			// this method will also loadIvySettings(), loadIvy() and parseIvyResources()
			setMaryBase();
		}
		logger.debug("Set mary base path to: " + this.maryBasePath);

		// setup ivy

		// try {
		// // loads ivy settings from resources ivysettings.xml file
		// loadIvySettings();
		//
		// // creating a new ivy instance as well as sets necessary options
		// loadIvy();
		//
		// logger.debug("Starting ivy resource parse");
		// // parses component descriptors, creates Component objects from them and stores them in this.resources
		// parseIvyResources();

		// once the resources are parsed, Installer passes the workflow on to the command line interface which evaluates
		// parameters that have been passed on to the Installer
		this.cli.mainEvalCommandLine();

		// } catch (IOException ioe) {
		// logger.error("Could not access settings file: " + ioe.getMessage());
		// } catch (ParseException pe) {
		// logger.error("Could not access settings file: " + pe.getMessage());
		// }
	}

	/**
	 * creates an Ivy instance and sets necessary options
	 */
	public void loadIvy() {
		logger.info("Starting Ivy ...");
		this.ivy = Ivy.newInstance(this.ivySettings);
		logger.debug("Setting log level to " + this.logLevel.toString());
		DefaultMessageLogger defaultLogger = new DefaultMessageLogger(this.logLevel.ordinal());
		defaultLogger.setShowProgress(true);
		this.ivy.getLoggerEngine().setDefaultLogger(defaultLogger);

		this.resolveOptions = new ResolveOptions();
		this.installOptions = new InstallOptions().setOverwrite(true).setTransitive(true);
	}

	public void loadIvySettings() throws ParseException, IOException {
		this.ivySettings = new IvySettings();
		this.ivySettings.setVariable("mary.base", this.maryBasePath);
		logger.debug("Loading ivysettings.xml ...");
		this.ivySettings.load(Resources.getResource("ivysettings.xml"));

	}

	protected void setLogLevel(LogLevel logLevel) {

		this.logLevel = logLevel;
	}

	/**
	 * method to set the maryBasePath variable. Is only called if user didn't manually set a path on the command line and thus
	 * uses instead the location where the Installer.jar is run from
	 */
	private void setMaryBase() {
		logger.debug("Setting mary base directory ...");
		File maryBase = null;
		// fall back to location of this class/jar
		// from http://stackoverflow.com/a/320595
		URL location = Installer.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			maryBase = new File(location.toURI().getPath());
			logger.debug("Setting mary base directory - Trying to use directory Installer is run from - " + maryBase);
		} catch (URISyntaxException use) {
			logger.error("Setting mary base directory - Could not parse " + location + ": " + use.getMessage() + "\n");
		}
		setMaryBase(maryBase);

		// try {
		// this.installer.loadIvySettings();
		// this.installer.loadIvy();
		// } catch (IOException ioe) {
		// logger.error("Could not access settings file: " + ioe.getMessage());
		// } catch (ParseException pe) {
		// logger.error("Could not access settings file: " + pe.getMessage());
		// }
		// this.installer.parseIvyResources();
	}

	/**
	 * sets a new file path for the marytts base directory.
	 * 
	 * @param maryBase
	 * @return true if mary path was successfully set, false otherwise
	 */
	public boolean setMaryBase(File maryBase) {
		boolean isSuccessful;
		try {
			maryBase = maryBase.getCanonicalFile();
			isSuccessful = true;
		} catch (IOException ioe) {
			logger.error("Setting mary base directory - Could not determine path to directory " + maryBase + ": " + ioe + "\n");
			isSuccessful = false;
		}
		// if this is running from the jar file, back off to directory containing it
		if (maryBase.isFile()) {
			logger.debug("Setting mary base directory - Installer is running from jar. Creating directory for setting mary base path");
			maryBase = maryBase.getParentFile();
			isSuccessful = true;
		}
		// create directory (with parents, if required)
		try {
			FileUtils.forceMkdir(maryBase);
			isSuccessful = true;
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
			isSuccessful = false;
		}
		try {
			this.maryBasePath = maryBase.getCanonicalPath();
			isSuccessful = true;
		} catch (IOException ioe) {
			logger.error("Setting mary base directory - Could not determine path to directory " + maryBase + ": " + ioe + "\n");
			isSuccessful = false;
		}

		if (isSuccessful) {

			logger.debug("(Re)loading Ivy, IvySettings and (re)parsing IvyResources ...");
			reloadIvy();
		}

		return isSuccessful;
	}

	public void reloadIvy() {
		try {
			loadIvySettings();
			loadIvy();
			parseIvyResources();
		} catch (IOException ioe) {
			logger.error("Could not access settings file: " + ioe.getMessage());
		} catch (ParseException pe) {
			logger.error("Could not access settings file: " + pe.getMessage());
		}
	}

	/**
	 * @return the maryBasePath
	 */
	public String getMaryBasePath() {
		return this.maryBasePath;
	}

	/**
	 * Installs given component using the ivy instance of this class
	 * 
	 * @param component
	 * @throws ParseException
	 * @throws IOException
	 */
	public void install(Component component) throws ParseException, IOException {

		logger.info("Ivy is installing component " + component.getName() + " and resolving its dependencies ...");
		// ResolveReport resolveAllDependencies = this.ivy.resolve(component.getModuleDescriptor(), this.resolveOptions);
		ResolveReport resolveReport = this.ivy.resolve(component.getModuleDescriptor(), this.resolveOptions);
		// RetrieveReport retrieveReport = this.ivy.retrieve(component.getModuleDescriptor().getModuleRevisionId(),
		// new RetrieveOptions());
		RepositoryResolver resolver = (RepositoryResolver) ivy.getSettings().getResolver("installed");
		String ivyPattern = (String) resolver.getIvyPatterns().get(0);
		String artifactPattern = (String) resolver.getArtifactPatterns().get(0);
		RetrieveOptions retrieveOptions = new RetrieveOptions();
		retrieveOptions.setDestIvyPattern(ivyPattern).setDestArtifactPattern(artifactPattern);

		// do not install zip, but leave them in download and unpack them from there
		retrieveOptions.setArtifactFilter(jarFilter);

		this.ivy.retrieve(component.getModuleDescriptor().getModuleRevisionId(), retrieveOptions);
		logger.debug("The ModulDescriptor for the selected component is: " + component.getModuleDescriptor());

		// TODO: unzip the zip artifacts

		// ArtifactDownloadReport[] dependencyReports = resolveAllDependencies.getAllArtifactsReports();

		// for (int i = 0; i < dependencyReports.length; i++) {
		// install resolved dependencies
		// ArtifactDownloadReport artifactDownloadReport = dependencyReports[i];
		// ModuleRevisionId mrid = artifactDownloadReport.getArtifact().getModuleRevisionId();
		// ResolveReport installDependencies = this.ivy.install(mrid, "downloaded", "installed", this.installOptions);
		// logging?
		// ArtifactDownloadReport[] installReports = installDependencies.getAllArtifactsReports();
		// }

		// finally install the component itself
		// ResolveReport install = this.ivy.install(component.getModuleDescriptor().getModuleRevisionId(), "remote", "installed",
		// this.installOptions);
		logger.info("HERE SHOULD BE LOGGING FOR THE INSTALLATION AND RESOLUTION OF COMPONENTS");
	}

	/**
	 * helper method to get information about dependencies of component prior to its resolution
	 * 
	 * @param component
	 * @return
	 */
	public List<String> retrieveDependencies(Component component) {

		List<String> toReturn = new ArrayList<String>();
		DependencyDescriptor[] dependencies = component.getModuleDescriptor().getDependencies();
		for (DependencyDescriptor oneDep : dependencies) {
			for (DependencyArtifactDescriptor oneDepArtifact : oneDep.getAllDependencyArtifacts()) {
				String depArtName = oneDepArtifact.getName();
				String depArtClassifier = oneDepArtifact.getExtraAttribute("classifier");
				if (depArtClassifier != null) {
					depArtName = depArtName.concat("-").concat(depArtClassifier);
				}
				toReturn.add(depArtName);
			}
		}
		return toReturn;
	}

	/**
	 * @param componentName
	 * @return
	 */
	public long getSizeOfComponentByName(String componentName) {

		for (Component oneComponent : this.resources) {
			if (componentName.equalsIgnoreCase(oneComponent.getName())) {
				return oneComponent.getSize();
			}
		}
		return 0L;
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
		logger.debug("Reading component descriptor list component-list.json from resources");
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
	 * @throws ParseException
	 * @throws IOException
	 */
	public void parseIvyResources() {

		try {
			List<String> resourcesList = readComponentDescriptorList();

			// as this method can be used to reparse the components, clear the existing ones first
			this.resources.clear();

			for (String oneFileName : resourcesList) {
				URL oneResource = Resources.getResource(oneFileName);
				ModuleDescriptor descriptor = XmlModuleDescriptorParser.getInstance().parseDescriptor(this.ivySettings,
						oneResource, true);
				logger.debug("Parsing " + oneFileName + " into moduleDescriptor: " + descriptor.toString());
				Component oneComponent = new Component(descriptor);
				if (oneFileName.startsWith("marytts-voice")) {
					oneComponent = new VoiceComponent(descriptor);
				} else if (oneFileName.startsWith("marytts-lang")) {
					oneComponent = new LangComponent(descriptor);
				}
				ArtifactRevisionId artifactRevisionId = descriptor.getAllArtifacts()[0].getId();
				String artifactName = /* artifactRevisionId.getAttribute("organisation") + "-" + */artifactRevisionId.getName()
						+ "-" + artifactRevisionId.getRevision() + "." + artifactRevisionId.getExt();
				logger.debug("The artifact name is calulated to be: " + artifactName + " and has the following resource status: "
						+ getResourceStatus(artifactName));
				oneComponent.setStatus(getResourceStatus(artifactName));
				this.resources.add(oneComponent);
				logger.debug((oneComponent.getClass().getSimpleName().equals("VoiceComponent") ? "VoiceComponent " : "Component ")
						+ oneComponent.getName() + " added to resource list.");
			}
		} catch (IOException ioe) {
			logger.error("Problem reading in file: " + ioe.getMessage());
		} catch (ParseException pe) {
			logger.error("Problem parsing component file: " + pe.getMessage());
		}
	}

	public Status getResourceStatus(String componentName) {

		if (new File(this.maryBasePath + "/lib/" + componentName).exists()) {
			return Status.INSTALLED;
		}
		if (new File(this.maryBasePath + "/download/" + componentName).exists()) {
			return Status.DOWNLOADED;
		}
		return Status.AVAILABLE;
	}

	public void updateResourceStatuses() {

		logger.debug("Updating all resource statuses ... ");
		for (Component oneComponent : this.resources) {
			ArtifactRevisionId ari = oneComponent.getModuleDescriptor().getAllArtifacts()[0].getId();
			String artifactName = /* ari.getAttribute("organisation") + "-" + */ari.getName() + "-" + ari.getRevision() + "."
					+ ari.getExt();
			oneComponent.setStatus(getResourceStatus(artifactName));
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

		List<Component> resourcesToBeFiltered = new ArrayList<Component>(this.resources);
		logger.debug("Fetching component list with the following parameters: "
				+ ((locale != null) ? ("locale=" + locale + " ") : "") + ((type != null) ? ("type=" + type + " ") : "")
				+ ((gender != null) ? ("gender=" + gender + " ") : "") + ((status != null) ? ("status=" + status + " ") : "")
				+ ((name != null) ? ("name=" + name + " ") : ""));

		// stores the size of the voice component list before filtering.
		int sizeBefore = resourcesToBeFiltered.size();
		logger.debug("Resource list size before filtering: " + sizeBefore);

		// in order to modify the list while iterating over it, an iterator is needed to call the Iterator.remove() method.
		Iterator<Component> it;

		if (resourcesToBeFiltered.isEmpty()) {
			logger.warn("List is empty!");
			return resourcesToBeFiltered;
		}

		if (locale != null && !locale.equals("all")) {
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!(oneComponent instanceof VoiceComponent || oneComponent instanceof LangComponent)) {
					logger.debug("Removed " + oneComponent + " as it is not a VoiceComponent or LangComponent");
					it.remove();
					continue;
				}
				if (oneComponent instanceof VoiceComponent) {
					VoiceComponent oneVoiceComponent = (VoiceComponent) oneComponent;
					if (!oneVoiceComponent.getLocale().toString().equalsIgnoreCase(locale)) {
						it.remove();
					}
				}
				if (oneComponent instanceof LangComponent) {
					LangComponent oneLangComponent = (LangComponent) oneComponent;
					if (!oneLangComponent.getLocale().toString().equalsIgnoreCase(locale)) {
						it.remove();
					}
				}
			}
		}
		if (type != null && !type.equals("all")) {
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
		if (gender != null && !gender.equals("all")) {
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
		if (status != null && !status.equals("all")) {
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!oneComponent.getStatus().toString().equalsIgnoreCase(status)) {
					it.remove();
				}
			}
		}
		if (name != null && !name.equals("all")) {
			for (it = resourcesToBeFiltered.iterator(); it.hasNext();) {
				Component oneComponent = it.next();
				if (!oneComponent.getName().equalsIgnoreCase(name)) {
					it.remove();
				}
			}
		}
		if (voiceOnly) {
			logger.debug("filtering by component type=" + (voiceOnly ? "voice " : " ") + "component");
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
	 * Installer Main Method<br>
	 * <b>Note:</b> must currently run with -Dmary.base=/path/to/marytts
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Installer installer = new Installer(args);
	}

}
