package marytts.tools.newinstall;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
	private final static String INSTALLER = "marytts-installer";
	private final static String HELP = "help";
	private final static String TARGET = "target";
	private final static String GUI = "gui";

	private CommandLineParser parser;
	private CommandLine commandLine;
	private HelpFormatter helper;

	static Logger logger = Logger.getLogger(marytts.tools.newinstall.InstallerCLI.class.getName());

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 */
	public InstallerCLI() throws Exception {
	}

	public InstallerCLI(String[] args, Installer installerInstance) {
		createOptions();
		parser = new BasicParser();
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Could not parse command line arguments: " + e.getMessage());
			helper.printHelp(INSTALLER, options);
			System.exit(1);
		}
		evalCommandLine(installerInstance);
	}

	private void createOptions() {
		options = new Options();
		options.addOption("h", HELP, false, "print help");
		options.addOption("t", TARGET, true, "target installation directory");
		options.addOption(OptionBuilder.withLongOpt("gui").withDescription("starts GUI").create());
		helper = new HelpFormatter();
	}

	public String getTargetDirectory() {
		return commandLine.getOptionValue(TARGET);
	}

	/**
	 * Evaluate the commandLine by the means of its options and their arguments
	 * 
	 * @param installerInstance
	 */
	private void evalCommandLine(final Installer installerInstance) {
		if (this.commandLine.hasOption(HELP)) {
			this.helper.printHelp(HELP, this.options);
			System.exit(0);
		} else if (this.commandLine.hasOption(GUI)) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					InstallerGUI gui = new InstallerGUI(installerInstance);
					gui.setVisible(true);
				}
			});
		}
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
	// List<Component> resources = installer.getAvailableComponents();
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
	// resources = installer.filterResources(resources, "locale", localeValue);
	// }
	// if (line.hasOption("type") || line.hasOption('t')) {
	// // --list --type
	// String typeValue = line.getOptionValue("type");
	// resources = installer.filterResources(resources, "type", typeValue);
	// }
	// if (line.hasOption("gender") || line.hasOption('g')) {
	// // --list --gender
	// String genderValue = line.getOptionValue("gender");
	// resources = installer.filterResources(resources, "gender", genderValue);
	// }
	// if (line.hasOption("name") || line.hasOption('n')) {
	// // --list --name
	// String nameValue = line.getOptionValue("name");
	// resources = installer.filterResources(resources, "name", nameValue);
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
	// if (installer.isNamePresent(nameValue)) {
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
	// new InstallerGUI(installer);
	// } else {
	// System.err.println("Invalid syntax. Please use the following syntax");
	// hf.printHelp(installerSyntax, options);
	// }
	//
	// }
	//
	// /**
	// * sorts components in resources list by their natural ordering as specified by {@link Component#compareTo(Component)}.
	// *
	// * @param resources
	// * holds the voice components that are locally available.
	// */
	// private void printSortedComponents(List<Component> resources) {
	//
	// System.out.println("listing voice components:\n\n");
	//
	// Collections.sort(resources);
	//
	// printComponents(resources);
	//
	// }
	//
	// /**
	// * used to format the component list so as to be put out on in the appropriate format when listing components.
	// *
	// * @param resources
	// * holds the voice components that are locally available
	// */
	// private static void printComponents(List<Component> resources) {
	//
	// StringBuilder sb = new StringBuilder();
	//
	// String prevLang = "";
	// for (Component oneComp : resources) {
	//
	// if (oneComp instanceof VoiceComponent) {
	// VoiceComponent voiceOneComp = (VoiceComponent) oneComp;
	// if (!prevLang.equals(voiceOneComp.getLocale().toString())) {
	// sb.append("##" + voiceOneComp.getLocale().toString() + " - " + voiceOneComp.getLocale().getDisplayLanguage()
	// + "##\n");
	// }
	// sb.append("\t" + voiceOneComp.getName() + "\n");
	// sb.append("\t" + "gender: " + voiceOneComp.getGender() + "; ");
	// sb.append("" + "type: " + voiceOneComp.getType() + "; ");
	// sb.append("" + "version: " + voiceOneComp.getVersion() + "; ");
	// sb.append("" + "license name: " + voiceOneComp.getLicenseName() + "\n");
	// sb.append("\t" + "description: "
	// + voiceOneComp.getDescription().replaceAll("[\\t\\n]", " ").replaceAll("( )+", " ") + "");
	// sb.append("\n\n");
	// prevLang = voiceOneComp.getLocale().toString();
	// }
	// }
	//
	// sb.append("Total: " + resources.size() + " components");
	// System.out.println(sb.toString());
	//
	// }
	//
}
