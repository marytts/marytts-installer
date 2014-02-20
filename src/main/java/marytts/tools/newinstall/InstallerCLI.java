package marytts.tools.newinstall;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import marytts.tools.newinstall.enums.LogLevel;
import marytts.tools.newinstall.gui.InstallerGUI;
import marytts.tools.newinstall.objects.Component;

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
	private final static String SILENT = "silent";
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

		// create Options necessary for Apache Commons' CLI. Each option represents a possible argument that can be specified on
		// the CL at start-up of the Installer
		createOptions();
		this.parser = new BasicParser();
		try {
			this.commandLine = this.parser.parse(this.options, args);
		} catch (ParseException e) {
			logger.error("Could not parse command line arguments: " + e.getMessage());
			this.helper.printHelp(MARYTTSINSTALLER, this.options);
			System.exit(1);
		}

		// this is a workaround for a workflow problem regarding start-up of Installer, InstallerGUI and InstallerCLI. When the
		// InstallerCLI is instantiated, the Installer itself has not been set up yet. This is due to the fact that the
		// InstallerCLI is called from within the Installer's constructor. Some parameters however must be evaluated before
		// others. This is the case for parameters doing configurations instead of actually using the Installer's functions (e.g.
		// setting the debug mode, setting the mary base directory, executing the help command etc.). These are parsed within the
		// following method.
		preEvalCommandLine();
	}

	private void createOptions() {
		this.options = new Options();

		this.options.addOption("h", "help", false, "print help");
		this.options.addOption("y", "yes", false, "always assume yes as an answer to prompts");
		this.options.addOption(OptionBuilder.withLongOpt("target").hasArg().withDescription("target installation directory")
				.create());
		this.options.addOption(OptionBuilder.withLongOpt("silent").withDescription("only display error messages").create());
		this.options.addOption(OptionBuilder.withLongOpt("verbose").withDescription("log in verbose mode").create());
		this.options.addOption(OptionBuilder.withLongOpt("debug").withDescription("log in debug mode").create());

		// listing options
		this.options.addOption(OptionBuilder.withLongOpt("list").withDescription("lists components").create());
		this.options.addOption("n", "name", true, "only with --list: filter by name (also substrings possible");
		this.options.addOption("l", "locale", true, "only with --list: filter by locale");
		this.options.addOption("g", "gender", true, "only with --list: filter by gender");
		this.options.addOption("t", "type", true, "only with --list: filter by voice type (hsmm/unit-selection)");
		this.options.addOption("s", "status", true, "only with --list: filter by download status");
		this.options.addOption("a", "all", false, "only with --list: list language and marytts components as well");

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
		if (this.commandLine.hasOption(DEBUG)) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
			this.installer.setLogLevel(LogLevel.debug);
		} else if (this.commandLine.hasOption(VERBOSE)) {
			// TODO this has to be implemented. How to set a verbose mode in Log4j?
			Logger.getRootLogger().setLevel(Level.DEBUG);
			this.installer.setLogLevel(LogLevel.verbose);
		} else if (this.commandLine.hasOption(SILENT)) {
			Logger.getRootLogger().setLevel(Level.ERROR);
			this.installer.setLogLevel(LogLevel.error);
		}
		if (this.commandLine.hasOption(HELP)) {
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

		logger.debug("Evaluating the specified content parameters.");
		if (this.commandLine.hasOption(GUI)) {
			startGUI();
		} else if (this.commandLine.hasOption(NOGUI)) {
			// dummy option used to indicate that no GUI is desired -- may be for testing purposes or in case of headless servers
		}
		// -- list evalutation
		// check for right input syntax (--list has to be present when listing constraints are present)
		else if (((this.commandLine.hasOption("name") || this.commandLine.hasOption("locale")
				|| this.commandLine.hasOption("gender") || this.commandLine.hasOption("type"))
				|| this.commandLine.hasOption("all") || this.commandLine.hasOption("status"))
				&& !this.commandLine.hasOption("list")) {
			logger.error("Invalid syntax. Please use the following syntax");
			this.helper.printHelp(MARYTTSINSTALLER, this.options);
			return;
		}

		// list all components
		if (this.commandLine.hasOption("list")) {
			List<Component> resources;
			// --list --all
			String locale = null, type = null, gender = null, status = null, name = null;
			boolean voiceOnly = true;

			if (this.commandLine.hasOption("all")) {
				// --list --all
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
			printComponents(resources);
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
		logger.debug("Starting installation process of component " + componentName);
		try {
			List<Component> componentInList = this.installer.getAvailableComponents(null, null, null, null, componentName, false);
			Component component = null;
			if (componentInList.isEmpty() || componentInList.size() > 1) {
				logger.error("\"" + componentName + "\""
						+ " is not a valid component name. Use --list to see available components!");
				System.exit(1);
			} else {
				component = componentInList.get(0);
			}
			if (!this.assumeYes) {
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
	 * used to format the component list so as to be put out on in the appropriate format when listing components.
	 * 
	 * @param resources
	 *            holds the voice components that are locally available
	 */
	private static void printComponents(List<Component> resources) {

		StringBuilder sb = new StringBuilder();

		sb.append("\nAvailable components:\n\n");

		for (Component oneComponent : resources) {
			sb.append(oneComponent.toString()).append("\n\n");
		}

		sb.append("Total: " + resources.size() + " components");
		System.out.println(sb.toString());

	}

}
