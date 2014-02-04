package marytts.tools.newinstall;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import marytts.tools.newinstall.enums.LogLevel;
import marytts.tools.newinstall.gui.InstallerGUI;
import marytts.tools.newinstall.objects.Component;
import marytts.tools.newinstall.objects.VoiceComponent;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * class for parsing the CL. <br>
 * TODO refine Exceptions
 * 
 * @author Jonathan, Ingmar
 * 
 */
public class InstallerCLI {

	private Options options;
	private final static String MARYTTSINSTALLER = "marytts-installer";
	private final static String HELP = "help";
	private final static String TARGET = "target";
	private final static String GUI = "gui";
	private final static String DEBUG = "debug";
	private final static String VERBOSE = "verbose";
	private final static String YES = "yes";
	private final static String INSTALL = "install";
	private final static String NOGUI = "nogui";

	private CommandLineParser parser;
	private CommandLine commandLine;
	private HelpFormatter helper;
	private Installer installer;
	private boolean assumeYes;
	static Logger logger = Logger.getLogger(marytts.tools.newinstall.InstallerCLI.class.getName());

	private InstallerCLI() {
	}

	public InstallerCLI(String[] args, Installer installer) {

		this.installer = installer;
		this.assumeYes = false;
		logger.debug("Starting InstallerCLI");

		createOptions();
		this.parser = new BasicParser();
		try {
			this.commandLine = this.parser.parse(this.options, args);
		} catch (ParseException e) {
			logger.error("Could not parse command line arguments: " + e.getMessage());
			this.helper.printHelp(MARYTTSINSTALLER, this.options);
			System.exit(1);
		}
		preEvalCommandLine();
	}

	private void createOptions() {
		this.options = new Options();

		this.options.addOption("h", "help", false, "print help");
		this.options.addOption("y", "yes", false, "always assume yes as an answer to prompts");
		this.options.addOption(OptionBuilder.withLongOpt("target").hasArg().withDescription("target installation directory")
				.create());
		this.options.addOption(OptionBuilder.withLongOpt("debug").withDescription("log in debug mode").create());
		// this.options.addOption(OptionBuilder.withLongOpt("verbose").withDescription("log in verbose debug mode").create());

		// listing options
		this.options.addOption(OptionBuilder.withLongOpt("list").withDescription("lists components").create());
		this.options.addOption("n", "name", true, "only with --list: filter by name (also substrings possible");
		this.options.addOption("l", "locale", true, "only with --list: filter by locale");
		this.options.addOption("g", "gender", true, "only with --list: filter by gender");
		this.options.addOption("t", "type", true, "only with --list: filter by voice type (hsmm/unit-selection)");
		this.options.addOption("s", "status", true, "only with --list: filter by download status");
		this.options.addOption("a", "advanced", false, "only with --list: list language and marytts components as well");

		this.options.addOption(OptionBuilder.withLongOpt("gui").withDescription("starts GUI").create());
		this.options.addOption(OptionBuilder.withLongOpt("nogui").withDescription("Do not use GUI, force usage of command line.")
				.create());
		this.options.addOption(OptionBuilder.withLongOpt("install").hasArg().withDescription("installs <arg> component")
				.create("i"));

		this.helper = new HelpFormatter();
		logger.debug("Created the following options: \n" + this.options.toString());
	}

	public String getTargetDirectory() {
		return this.commandLine.getOptionValue(TARGET);
	}

	/**
	 * Evaluate the commandLine by the means of its options and their arguments. Independent of Installer's instantiation, needs
	 * to be called prior to the instantiation of Installer, as the user might set the mary.base path on the CL which is used for
	 * Ivy functionality in Installer.java
	 */
	private void preEvalCommandLine() {
		logger.debug("Evaluating the configuration options set on command line: " + this.commandLine);
		if (this.commandLine.hasOption(HELP)) {
			logger.debug("CL has option HELP");
			this.helper.printHelp(HELP, this.options);
			System.exit(0);
		} else if (this.commandLine.hasOption(TARGET)) {
			this.installer.setMaryBase(new File(getTargetDirectory()));
		}
		if (this.commandLine.hasOption(YES)) {
			this.assumeYes = true;
		}
	}

	/**
	 * public eval command line method. Is called upon completion of Installer construction
	 */
	protected void mainEvalCommandLine() {

		if (this.commandLine.hasOption(DEBUG)) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
			this.installer.setIvyLoggingLevel(LogLevel.debug);
		} else if (this.commandLine.hasOption(VERBOSE)) {
			this.installer.setIvyLoggingLevel(LogLevel.verbose_debug);
		}
		if (this.commandLine.hasOption(GUI)) {
			startGUI();
		} else if (this.commandLine.hasOption(NOGUI)) {
			// dummy option used to indicate that no GUI is desired -- may be for testing purposes or in case of headless servers
		}
		// -- list evalutation
		// check for right input syntax (--list has to be present when listing constraints are present)
		else if (((this.commandLine.hasOption("name") || this.commandLine.hasOption("locale")
				|| this.commandLine.hasOption("gender") || this.commandLine.hasOption("type"))
				|| this.commandLine.hasOption("advanced") || this.commandLine.hasOption("status"))
				&& !this.commandLine.hasOption("list")) {
			logger.error("Invalid syntax. Please use the following syntax");
			this.helper.printHelp(MARYTTSINSTALLER, this.options);
			return;
		}

		// list all components
		if (this.commandLine.hasOption("list")) {
			List<Component> resources;
			// --list --advanced
			String locale = null, type = null, gender = null, status = null, name = null;
			boolean voiceOnly = true;

			if (this.commandLine.hasOption("advanced")) {
				// --list --advanced
				voiceOnly = false;
			}
			if (this.commandLine.hasOption("locale") || this.commandLine.hasOption('l')) {
				// --list --locale
				locale = this.commandLine.getOptionValue("locale");
			}
			if (this.commandLine.hasOption("type") || this.commandLine.hasOption('t')) {
				// --list --type
				type = this.commandLine.getOptionValue("type");
			}
			if (this.commandLine.hasOption("gender") || this.commandLine.hasOption('g')) {
				// --list --gender
				gender = this.commandLine.getOptionValue("gender");
			}
			if (this.commandLine.hasOption("status") || this.commandLine.hasOption('s')) {
				// --list --status
				status = this.commandLine.getOptionValue("status");
			}
			if (this.commandLine.hasOption("name") || this.commandLine.hasOption('n')) {
				// --list --name
				name = this.commandLine.getOptionValue("name");
			}
			resources = this.installer.getAvailableComponents(locale, type, gender, status, name, voiceOnly);
			printSortedComponents(resources);
		} else if (this.commandLine.hasOption(INSTALL)) {
			installComponents();
		}

		if (this.commandLine.getOptions().length == 0) {
			logger.info("no options were given, starting GUI");
			startGUI();
		}
	}

	private void installComponents() {

		String componentName = this.commandLine.getOptionValue(INSTALL);
		try {
			List<Component> componentInList = this.installer.getAvailableComponents(null, null, null, null, componentName, false);
			Component component = componentInList.get(0);
			if (componentInList.isEmpty() || componentInList.size() > 1) {
				logger.error("\"" + componentName + "\""
						+ " is not a valid component name. Use --list to see available components!");
				System.exit(1);
			} else if (!this.assumeYes) {
				List<String> dependencies = this.installer.retrieveDependencies(component);

				StringBuilder sb = new StringBuilder();
				sb.append("\nAre you sure you want to install the following components (yes or y to confirm):\n");
				int ctr = 1;
				sb.append(ctr++ + ". ");
				sb.append(componentName);
				sb.append(" (" + FileUtils.byteCountToDisplaySize(this.installer.getSizeOfComponentByName(componentName)) + ")");
				sb.append("\n");

				for (String oneDep : dependencies) {
					String oneDepAsResource = formatDescriptorToResourceName(oneDep);
					sb.append(ctr++ + ". ");
					sb.append(oneDepAsResource);
					sb.append(" (" + FileUtils.byteCountToDisplaySize(this.installer.getSizeOfComponentByName(oneDepAsResource))
							+ ")");
					sb.append("\n");
					if (dependencies.size() - 1 != dependencies.indexOf(oneDep)) {
						sb.append(", ");
					}
				}
				System.out.println(sb.toString());
				Scanner scanner = new Scanner(System.in);
				if (scanner.hasNext()) {
					if (!(scanner.next().trim().matches("yes||y||YES"))) {
						System.out.println("Ok, ending installer!");
						return;
					}
					System.out.println("Ok, installing the specified component(s) ...");
				}
			}
			this.installer.install(component);
		} catch (java.text.ParseException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * @param oneDep
	 * @return
	 */
	private static String formatDescriptorToResourceName(String ivyComponentName) {
		Pattern p = Pattern.compile("(marytts-lang-|marytts-voice-)(.*)");
		Matcher m = p.matcher(ivyComponentName);
		String componentName = null;
		if (m.find()) {
			componentName = m.group(2);
		}
		// TODO workaround for ivy naming issues
		else {
			componentName = ivyComponentName;
		}
		return componentName;
	}

	private void startGUI() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InstallerGUI gui = new InstallerGUI(InstallerCLI.this.installer);
					gui.setVisible(true);
				} catch (HeadlessException he) {
					logger.warn("Cannot start GUI, please use the command line interface instead!");
				}
			}
		});
	}

	/**
	 * Starting point of the parse. Initialized the needed objects and then parses the input with
	 * {@link #evalCommandLine(String[], CommandLineParser, Options, List)}
	 * 
	 * @param args
	 *            the command line arguments, passed on to the {@link CommandLineParser}
	 * @throws Exception
	 */
	// public void run(String[] args) throws Exception {
	//
	// // create the command line parser
	// parser = new BasicParser();
	//
	// // create the Options
	// options = new Options();
	// OptionGroup optionGroup = new OptionGroup();
	//
	// optionGroup.addOption(OptionBuilder.withLongOpt("list").withDescription("lists components").create());
	// optionGroup.addOption(OptionBuilder.withLongOpt("install").hasArg().isRequired()
	// .withDescription("installs <arg> component").create("i"));
	// optionGroup.addOption(OptionBuilder.withLongOpt("gui").withDescription("starts GUI").create());
	// options.addOptionGroup(optionGroup);
	//
	// // list options
	// options.addOption("n", "name", true, "only with --list: filter by name (also substrings possible");
	// options.addOption("l", "locale", true, "only with --list: filter by locale");
	// options.addOption("g", "gender", true, "only with --list: filter by gender");
	// options.addOption("t", "type", true, "only with --list: filter by type");
	//
	// options.addOption("y", "yes", false, "always assume yes as an answer to prompts");
	// }

	/**
	 * Parses the input command line arguments and selects the proper action to take.
	 * 
	 * @param args
	 *            command line arguments to parse
	 * @param parser
	 *            {@link CommandLineParser used to parse the command line arguments}
	 * @param options
	 *            {stores the {@link Option} arguments that define the CLI parameters
	 * @param installer
	 *            the installer object, holds the
	 * @throws Exception
	 */
	// private void evalCommandLine(String[] args, CommandLineParser parser, Options options) {
	// // the voice components
	// List<Component> resources = installerInstance.getAvailableComponents();
	//
	// // parse the command line arguments
	// CommandLine line = parser.parse(options, args);
	//
	// // for formatting the help when wrong input was given
	// HelpFormatter hf = new HelpFormatter();
	//
	// // TODO specify installer usage syntax
	// final String installerSyntax = "installer syntax";
	//
	// // check for right input syntax
	// if ((line.hasOption("n") || line.hasOption("l") || line.hasOption("g") || line.hasOption("t")) && !line.hasOption("list"))
	// {
	// // TODO fix sync problem (with flush() it doesn't seem to work)
	// System.err.println("Invalid syntax. Please use the following syntax");
	// hf.printHelp(installerSyntax, options);
	// }
	//
	// // --list: list all components
	// else if (line.hasOption("list") || line.hasOption("")) {
	// if (line.hasOption("locale") || line.hasOption('l')) {
	// // --list --locale
	// String localeValue = line.getOptionValue("locale");
	// resources = installerInstance.filterResources(resources, "locale", localeValue);
	// }
	// if (line.hasOption("type") || line.hasOption('t')) {
	// // --list --type
	// String typeValue = line.getOptionValue("type");
	// resources = installerInstance.filterResources(resources, "type", typeValue);
	// }
	// if (line.hasOption("gender") || line.hasOption('g')) {
	// // --list --gender
	// String genderValue = line.getOptionValue("gender");
	// resources = installerInstance.filterResources(resources, "gender", genderValue);
	// }
	// if (line.hasOption("name") || line.hasOption('n')) {
	// // --list --name
	// String nameValue = line.getOptionValue("name");
	// resources = installerInstance.filterResources(resources, "name", nameValue);
	// }
	// printSortedComponents(resources);
	// // --install || -i
	// } else if (line.hasOption("yes") || line.hasOption('y')) {
	// this.assumeYes = true;
	// } else if (line.hasOption("install") || line.hasOption('i')) {
	// String nameValue = line.getOptionValue("i");
	//
	// // TODO implement installer object's resources to also hold lang and marytts-runtime-with-dependencies components.
	//
	// // might also be done using the installer.getComponentByName method (when trying to avoid code repetition)
	// // if (installer.getComponentByName(nameValue) != null) {
	//
	// // checks if <name> to be installed is a valid component
	// if (installerInstance.isNamePresent(nameValue)) {
	//
	// // if --yes option is set, installs without further asking.
	// if (this.assumeYes) {
	// System.out.println("TODO implement installation");
	// } else {
	// // TODO add size
	// System.out.println("Are you sure that you want to install the following component " + nameValue
	// + "(here should be the sizes" + ")?\n");
	// System.out.println("Insert \"yes\" to install and \"no\" to abort");
	//
	// // Really ugly, but don't know how commons.cli can handle user prompts...
	// Scanner scanner = new Scanner(System.in);
	// String input = scanner.nextLine();
	//
	// if (input.equalsIgnoreCase("yes")) {
	// System.out.println("TODO implement installation");
	// } else if (input.equalsIgnoreCase("no")) {
	// System.out.println("Ok. This installation has been aborted.");
	// }
	// }
	// } else {
	// throw new Exception(nameValue + " is not a valid component. Please try again.");
	// }
	// } else if (line.hasOption("gui")) {
	// System.out.println("Start gui");
	//
	// new InstallerGUI(installerInstance);
	// } else {
	// System.err.println("Invalid syntax. Please use the following syntax");
	// hf.printHelp(installerSyntax, options);
	// }
	//
	// }

	/**
	 * sorts components in resources list by their natural ordering as specified by {@link Component#compareTo(Component)}.
	 * 
	 * @param resources
	 *            holds the voice components that are locally available.
	 */
	private void printSortedComponents(List<Component> resources) {

		List<Component> voiceResources = new ArrayList<Component>();
		List<Component> otherResources = new ArrayList<Component>();

		for (Component oneComponent : resources) {
			if (oneComponent instanceof VoiceComponent) {
				voiceResources.add(oneComponent);
			} else {
				otherResources.add(oneComponent);
			}
		}

		Collections.sort(voiceResources);
		System.out.println("");

		if (!voiceResources.isEmpty()) {
			System.out.println("Listing voice components:");
			printComponents(voiceResources);
			System.out.println("===========================");
		}

		if (!otherResources.isEmpty()) {
			Collections.sort(otherResources);
			System.out.println("Listing other components:");
			printComponents(otherResources);
		}

		if (otherResources.isEmpty() && voiceResources.isEmpty()) {
			System.out.println("No components to display!");
		}
	}

	/**
	 * used to format the component list so as to be put out on in the appropriate format when listing components.
	 * 
	 * @param resources
	 *            holds the voice components that are locally available
	 */
	private static void printComponents(List<Component> resources) {

		StringBuilder sb = new StringBuilder();

		String prevLang = "";
		for (Component oneComp : resources) {

			if (oneComp instanceof VoiceComponent) {
				VoiceComponent voiceOneComp = (VoiceComponent) oneComp;
				if (!prevLang.equals(voiceOneComp.getLocale().toString())) {
					sb.append("##" + voiceOneComp.getLocale().toString() + " - " + voiceOneComp.getLocale().getDisplayLanguage()
							+ "##\n");
				}
				sb.append("\t" + "Voice component: " + voiceOneComp.getName() + "\n");
				sb.append("\t" + "gender: " + voiceOneComp.getGender() + "; ");
				sb.append("" + "type: " + voiceOneComp.getType() + "; ");
				sb.append("" + "version: " + voiceOneComp.getVersion() + "; ");
				sb.append("" + "status: " + voiceOneComp.getStatus() + "; ");
				sb.append("" + "size: " + FileUtils.byteCountToDisplaySize(voiceOneComp.getSize()) + "; ");
				sb.append("" + "license name: " + voiceOneComp.getLicenseName() + "\n");
				sb.append("\t" + "description: "
						+ voiceOneComp.getDescription().replaceAll("[\\t\\n]", " ").replaceAll("( )+", " ") + "");
				sb.append("\n\n");
				prevLang = voiceOneComp.getLocale().toString();
			} else {
				if (!prevLang.equals(oneComp.getLocale().toString())) {
					sb.append("##" + oneComp.getLocale().toString() + " - " + oneComp.getLocale().getDisplayLanguage() + "##\n");
				}
				sb.append("\t" + "Component: " + oneComp.getName() + "\n");
				sb.append("\t" + "version: " + oneComp.getVersion() + "; ");
				sb.append("" + "license name: " + oneComp.getLicenseName() + "\n");
				sb.append("\t" + "description: " + oneComp.getDescription().replaceAll("[\\t\\n]", " ").replaceAll("( )+", " ")
						+ "");
				sb.append("\n\n");
				prevLang = oneComp.getLocale().toString();
			}
		}

		sb.append("Total: " + resources.size() + " components");
		System.out.println(sb.toString());

	}

}
