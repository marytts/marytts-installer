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
		options.addOption("l", "list", false, "list all available voice components");

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("list")) {
				// --list: list all components
				List<Component> resources = installer.getAvailableVoices();
				listAllComponents(resources);

			}
		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}

	}

	private static void listAllComponents(List<Component> resources) {

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
