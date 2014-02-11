/**
 * 
 */
package marytts.tools.newinstall.objects;

import java.util.Locale;
import java.util.Observable;

import marytts.tools.newinstall.enums.Status;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.License;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.log4j.Logger;

/**
 * Java object representing marytts components
 * 
 * @author Jonathan
 * 
 */
public class Component extends Observable implements Comparable<Component> {

	private ModuleDescriptor descriptor;

	private Status status;

	static Logger logger = Logger.getLogger(marytts.tools.newinstall.objects.Component.class.getName());

	public Component(ModuleDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public ModuleDescriptor getModuleDescriptor() {
		return this.descriptor;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return descriptor.getExtraAttribute("name");
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return getName();
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return descriptor.getAttribute("revision");
	}

	/**
	 * @return the licenseName
	 */
	public String getLicenseName() {
		License[] licenses = descriptor.getLicenses();
		License license = licenses[0];
		return license.getName();
	}

	/**
	 * @return the licenseShortName
	 */
	public String getLicenseShortName() {
		return descriptor.getExtraAttribute("license");
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return descriptor.getDescription();
	}

	public Status getStatus() {
		return this.status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		// if status has not previously been set, set it
		if (this.status == null) {
			this.status = status;
			// if it is a reset of Status due to Installation of Components, notify GUI to update the appropriate Component Panel
			// with the new Status
		} else if (this.status != status) {
			this.status = status;
			setChanged();
			notifyObservers();
		}

	}

	/**
	 * @return the size
	 */
	public long getSize() {
		long size = 0;
		for (Artifact artifact : descriptor.getAllArtifacts()) {
			String sizeAttribute = artifact.getExtraAttribute("size");
			// trying to parse a long number from the String attribute
			try {
				long parsedSize = Long.parseLong(sizeAttribute);
				size += parsedSize;
			} catch (NumberFormatException nfe) {
				logger.error(descriptor.getAllArtifacts()[0].getExtraAttribute("size") + " could not be parsed.");
			}
		}
		return size;
	}

	// marytts-lang-en-5.1-beta1.jar
	// voice-cmu-slt-hsmm-5.1-beta1.jar
	public String getArtifactName() {

		Artifact artifact = this.descriptor.getAllArtifacts()[0];
		StringBuilder sb = new StringBuilder();
		sb.append(artifact.getAttribute("module")).append("-").append(artifact.getAttribute("revision")).append(".")
				.append(artifact.getExt());

		return sb.toString();
	}

	// public String getDependencyArtifact() {
	//
	// DependencyDescriptor dependencyDescriptor = this.moduleDescriptor.getDependencies()[0];
	// DependencyArtifactDescriptor depArtDesc = dependencyDescriptor.getAllDependencyArtifacts()[0];
	//
	// StringBuilder sb = new StringBuilder();
	// sb.append(depArtDesc.getAttribute("module")).append("-").append(depArtDesc.getAttribute("revision")).append(".")
	// .append(depArtDesc.getExt());
	//
	// return sb.toString();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append("Component: ").append(getName()).append("\n");
		sb.append("version: ").append(getVersion()).append("; status: ").append(getStatus()).append("; size: ")
				.append(FileUtils.byteCountToDisplaySize(getSize())).append("\n");
		sb.append("license name: ").append(getLicenseName()).append("\n");
		sb.append("description: ").append(getDescription().replaceAll("[\\t\\n]", " ").replaceAll("( )+", " "));

		return sb.toString();
	}

	protected String toComparisonString() {
		return this.getClass().getName().concat(getDisplayName());
	}

	public int compareTo(Component o) {
		return this.toComparisonString().compareTo(o.toComparisonString());
	}

	public Locale getLocale() {
		// TODO Auto-generated method stub
		return Locale.getDefault();
	}

}
