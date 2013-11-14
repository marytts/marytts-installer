/**
 * 
 */
package marytts.tools.newinstall;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import marytts.tools.newinstall.objects.Component;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * class for parsing the CL. TODO refine Exceptions
 * 
 * @author Jonathan
 * 
 */
public class InstallerCLI {

	private boolean assumeYes;

	/**
	 * Constructor
	 */
	public InstallerCLI() {
		this.assumeYes = false;
	}

	/**
	 * Starting point of the parse. Initialized the needed objects and then parses the input with
	 * {@link #evalCommandLine(String[], CommandLineParser, Options, List)}
	 * 
	 * @param args
	 *            the command line arguments, passed on to the {@link CommandLineParser}
	 * @throws Exception
	 */
	public void run(String[] args) throws Exception {

		// create installer object containing a collection of Component (voice)
		// objects
		Installer installer = new Installer();

		// create the command line parser
		CommandLineParser parser = new BasicParser();

		// create the Options
		Options options = new Options();
		OptionGroup optionGroup = new OptionGroup();

		// TODO
		// options.addOption("l", "list", false, "list all available voice components");
		optionGroup.addOption(OptionBuilder.withLongOpt("list").withDescription("lists components").create());
		optionGroup.addOption(OptionBuilder.withLongOpt("install").hasArg().isRequired()
				.withDescription("installs <arg> component").create("i"));
		optionGroup.addOption(OptionBuilder.withLongOpt("gui").withDescription("starts GUI").create());
		options.addOptionGroup(optionGroup);

		// list options
		options.addOption("n", "name", true, "only with --list: filter by name (also substrings possible");
		options.addOption("l", "locale", true, "only with --list: filter by locale");
		options.addOption("g", "gender", true, "only with --list: filter by gender");
		options.addOption("t", "type", true, "only with --list: filter by type");

		options.addOption("y", "yes", false, "always assume yes as an answer to prompts");

		evalCommandLine(args, parser, options, installer);
	}

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
	private void evalCommandLine(String[] args, CommandLineParser parser, Options options, Installer installer) throws Exception {
		// the voice components
		List<Component> resources = installer.getAvailableVoices();

		// parse the command line arguments
		CommandLine line = parser.parse(options, args);
		
		// for formatting the help when wrong input was given
		HelpFormatter hf = new HelpFormatter();
		
		// TODO specify installer usage syntax
		final String installerSyntax = "installer syntax";

		// check for right input syntax
		if ((line.hasOption("n") || line.hasOption("l") || line.hasOption("g") || line.hasOption("t")) && !line.hasOption("list")) {
			// TODO fix sync problem (with flush() it doesn't seem to work)
			System.err.println("Invalid syntax. Please use the following syntax");
			hf.printHelp(installerSyntax, options);
		}

		// --list: list all components
		else if (line.hasOption("list") || line.hasOption("")) {
			if (line.hasOption("locale") || line.hasOption('l')) {
				// --list --locale
				String localeValue = line.getOptionValue("locale");
				resources = installer.filterResources(resources, "locale", localeValue);
			}
			if (line.hasOption("type") || line.hasOption('t')) {
				// --list --type
				String typeValue = line.getOptionValue("type");
				resources = installer.filterResources(resources, "type", typeValue);
			}
			if (line.hasOption("gender") || line.hasOption('g')) {
				// --list --gender
				String genderValue = line.getOptionValue("gender");
				resources = installer.filterResources(resources, "gender", genderValue);
			}
			if (line.hasOption("name") || line.hasOption('n')) {
				// --list --name
				String nameValue = line.getOptionValue("name");
				resources = installer.filterResources(resources, "name", nameValue);
			}
			printSortedComponents(resources);
			// --install || -i
		} else if (line.hasOption("yes") || line.hasOption('y')) {
			this.assumeYes = true;
		} else if (line.hasOption("install") || line.hasOption('i')) {
			String nameValue = line.getOptionValue("i");

			// TODO implement installer object's resources to also hold lang and marytts-runtime-with-dependencies components.
			// checks if <name> to be installed is a valid component
			if (installer.isNamePresent(nameValue)) {

				// if --yes option is set, installs without further asking.
				if (this.assumeYes) {
					System.out.println("TODO implement installation");
				} else {
					// TODO add size
					System.out.println("Are you sure that you want to install the following component " + nameValue
							+ "(here should be the sizes" + ")?\n");
					System.out.println("Insert \"yes\" to install and \"no\" to abort");

					// Really ugly, but don't know how commons.cli can handle user prompts...
					Scanner scanner = new Scanner(System.in);
					String input = scanner.nextLine();

					if (input.equalsIgnoreCase("yes")) {
						System.out.println("TODO implement installation");
					} else if (input.equalsIgnoreCase("no")) {
						System.out.println("Ok. This installation has been aborted.");
					}
				}
			} else {
				throw new Exception(nameValue + " is not a valid component. Please try again.");
			}
		} else if (line.hasOption("gui")) {
			System.out.println("Start gui");
			// TODO start gui
		} else {
			System.err.println("Invalid syntax. Please use the following syntax");
			hf.printHelp(installerSyntax, options);
		}

	}

	/**
	 * sorts components in resources list by their natural ordering as specified by {@link Component#compareTo(Component)}.
	 * 
	 * @param resources
	 *            holds the voice components that are locally available.
	 */
	private void printSortedComponents(List<Component> resources) {

		System.out.println("listing voice components:\n\n");

		Collections.sort(resources);

		printComponents(resources);

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

			if (!prevLang.equals(oneComp.getLocale().toString())) {
				sb.append("##" + oneComp.getLocale().toString() + " - " + oneComp.getLocale().getDisplayLanguage() + "##\n");
			}
			sb.append("\t" + oneComp.getName() + "\n");
			sb.append("\t" + "gender: " + oneComp.getGender() + "; ");
			sb.append("" + "type: " + oneComp.getType() + "; ");
			sb.append("" + "version: " + oneComp.getVersion() + "; ");
			sb.append("" + "license name: " + oneComp.getLicenseName() + "\n");
			sb.append("\t" + "description: " + oneComp.getDescription().replaceAll("[\\t\\n]", " ").replaceAll("( )+", " ") + "");
			sb.append("\n\n");
			prevLang = oneComp.getLocale().toString();
		}

		sb.append("Total: " + resources.size() + " components");
		System.out.println(sb.toString());

	}

}
