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
import org.apache.commons.cli.ParseException;

/**
 * @author Jonathan
 * 
 */
public class InstallerCLI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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
		try {
			List<Component> resources = installer.getAvailableVoices();
			
			parseCommandLine(args, parser, options, resources);

		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}

	}

	//TODO make unstatic
	private static void parseCommandLine(String[] args, CommandLineParser parser, Options options, List<Component> resources)
			throws ParseException {
		// parse the command line arguments
		CommandLine line = parser.parse(options, args);

			// --list: list all components
			if (line.hasOption("locale")) {
				// --list --locale
				String localeValue = line.getOptionValue("locale");
				resources = filterResources(resources, "locale", localeValue);
			}
			if (line.hasOption("type")) {
				// --list --type
				String typeValue = line.getOptionValue("type");
				resources = filterResources(resources, "type", typeValue);
			}
			if (line.hasOption("gender")) {
				// --list --gender
				String genderValue = line.getOptionValue("gender");
				resources = filterResources(resources, "gender", genderValue);
			}
			if (line.hasOption("name")) {
				// --list --name
				String nameValue = line.getOptionValue("name");
				resources = filterResources(resources, "name", nameValue);
			}
			printSortedComponents(resources);
	}

	private static List<Component> filterResources(List<Component> resources, String attribute, String attributeValue) {

		for (Component oneComponent : resources) {
			if (attribute.equals("locale")) {
				if (!oneComponent.getLocale().toString().equalsIgnoreCase(attributeValue)) {
					resources.remove(oneComponent);
				}
			} else if (attribute.equals("type")) {
				if (!oneComponent.getType().equalsIgnoreCase(attributeValue)) {
					resources.remove(oneComponent);
				}
			}
			if (attribute.equals("gender")) {
				if (!oneComponent.getGender().equalsIgnoreCase(attributeValue)) {
					resources.remove(oneComponent);
				}
			}
			if (attribute.equals("name")) {
				if (!oneComponent.getName().equalsIgnoreCase(attributeValue)) {
					resources.remove(oneComponent);
				}
			}
		}

		return resources;
	}

	private static void printSortedComponents(List<Component> resources) {

		StringBuilder sb = new StringBuilder();
		sb.append("listing all voice components:").append("\n\n");

		Collections.sort(resources);

		String prevLang = "";
		for (Component oneComp : resources) {

			if (!prevLang.equals(oneComp.getLocale().toString())) {
				sb.append("##" + oneComp.getLocale().toString() + " - " + oneComp.getLocale().getDisplayLanguage() + "##\n");
			}
			sb.append("\t" + oneComp.getName() + "\n");
			sb.append("\t\t" + "gender: " + oneComp.getGender() + "\n");
			sb.append("\t\t" + "type: " + oneComp.getType() + "\n");
			sb.append("\t\t" + "version: " + oneComp.getVersion() + "\n");
			sb.append("\t\t" + "license name: " + oneComp.getLicenseName() + "\n");
			sb.append("\t\t" + "description: " + oneComp.getDescription() + "\n");
			sb.append("\n");
			prevLang = oneComp.getLocale().toString();
		}
		System.out.println(sb.toString());

	}

}
