/**
 * 
 */
package marytts.tools.newinstall;

/**
 * @author Jonathan
 * 
 */
public class InstallerRun {

	/**
	 * main class of Installer
	 * 
	 * @param args
	 *            command line arguments used for the command line parser of {@link InstallerCLI}
	 */
	public static void main(String[] args) {

		try {
			InstallerCLI installerCLI = new InstallerCLI();
			installerCLI.run(args);
		} catch (Exception e) {
			System.err.println("Unexpected exception: " + e.getMessage());
		}

	}

}
