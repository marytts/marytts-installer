/**
 * 
 */
package marytts.tools.newinstall;

import java.util.Collections;
import java.util.List;

import marytts.tools.newinstall.objects.Component;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * 
 * 
 * @author Jonathan
 * 
 */
public class InstallerCLI {

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
		// options.addOption("l", "list", false, "list all available voice components");
		options.addOption("n", "name", true, "filter by name (also substrings possible");
		options.addOption("l", "locale", true, "filter by locale");
		options.addOption("g", "gender", true, "filter by gender");
		options.addOption("t", "type", true, "filter by type");
		options.addOption("i", "install", true, "install <name> component");
		options.addOption("y", "yes", true, "always assume yes as an answer to prompts");
		options.addOption(OptionBuilder.withLongOpt("gui").withDescription("starts GUI").create());

		List<Component> resources = installer.getAvailableVoices();

		evalCommandLine(args, parser, options, resources);
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
	 * @param resources
	 *            holds the voice components that are locally available
	 * @throws Exception
	 */
	private void evalCommandLine(String[] args, CommandLineParser parser, Options options, List<Component> resources)
			throws Exception {
		// parse the command line arguments
		CommandLine line = parser.parse(options, args);

		// --list: list all components
		if (line.hasOption("locale")) {
			// --list --locale
			String localeValue = line.getOptionValue("locale");
			resources = Installer.filterResources(resources, "locale", localeValue);
		}
		if (line.hasOption("type")) {
			// --list --type
			String typeValue = line.getOptionValue("type");
			resources = Installer.filterResources(resources, "type", typeValue);
		}
		if (line.hasOption("gender")) {
			// --list --gender
			String genderValue = line.getOptionValue("gender");
			resources = Installer.filterResources(resources, "gender", genderValue);
		}
		if (line.hasOption("name")) {
			// --list --name
			String nameValue = line.getOptionValue("name");
			resources = Installer.filterResources(resources, "name", nameValue);
		}
		printSortedComponents(resources);
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
	private void printComponents(List<Component> resources) {

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
