/**
 * 
 */
package marytts.tools.newinstall.objects;

import java.util.Locale;
import java.util.Observable;

import marytts.tools.newinstall.enums.Status;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;

import org.apache.log4j.Logger;

/**
 * Java object representing marytts components
 * 
 * @author Jonathan
 * 
 */
public class Component extends Observable implements Comparable<Component> {

	private ModuleDescriptor moduleDescriptor;
	protected String name;
	protected String displayName;
	protected String version;
	protected String licenseName;
	protected String licenseShortName;
	protected String description;
	protected Status status;
	protected long size;

	static Logger logger = Logger.getLogger(marytts.tools.newinstall.objects.Component.class.getName());

	public Component(ModuleDescriptor descriptor) {
		this.moduleDescriptor = descriptor;
		setDescription(descriptor.getDescription());
		setLicenseName(descriptor.getLicenses()[0].getName());
		setLicenseShortName(descriptor.getExtraAttribute("license"));
		setVersion(descriptor.getAttribute("revision"));
		setName(descriptor.getExtraAttribute("name"));
		setDisplayNameFromName(descriptor.getExtraAttribute("name"));

		long parsedLong;

		// trying to parse a long number from the String attribute
		try {
			parsedLong = Long.parseLong(descriptor.getAllArtifacts()[0].getExtraAttribute("size"));
		} catch (NumberFormatException nfe) {
			logger.error(descriptor.getAllArtifacts()[0].getExtraAttribute("size") + " could not be parsed.");
			parsedLong = 0L;
		}
		setSize(parsedLong);

	}

	/**
	 * @param version
	 *            the version to set
	 */
	private void setVersion(String version) {

		this.version = version;
	}

	/**
	 * @param licenseName
	 *            the licenseName to set
	 */
	private void setLicenseName(String licenseName) {

		this.licenseName = licenseName;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	private void setDescription(String description) {

		this.description = description;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	private void setName(String name) {

		this.name = name;
	}

	public ModuleDescriptor getModuleDescriptor() {
		return this.moduleDescriptor;
	}

	/**
	 * @return the name
	 */
	public String getName() {

		return this.name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {

		return this.version;
	}

	/**
	 * @return the licenseName
	 */
	public String getLicenseName() {

		return this.licenseName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {

		return this.description;
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
	 * @return the licenseShortName
	 */
	public String getLicenseShortName() {
		return this.licenseShortName;
	}

	/**
	 * @param licenseShortName
	 *            the licenseShortName to set
	 */
	private void setLicenseShortName(String licenseShortName) {
		this.licenseShortName = licenseShortName;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return this.size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	private void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * TODO has to be modified once there are mary components as well
	 * 
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayNameFromName(String name) {
		// if (!(this instanceof VoiceComponent)) {
		// this.displayName = this.getLocale().getDisplayName();
		// } else {
		this.displayName = name;
		// }
	}

	// marytts-lang-en-5.1-beta1.jar
	// voice-cmu-slt-hsmm-5.1-beta1.jar
	public String getArtifactName() {

		Artifact artifact = this.moduleDescriptor.getAllArtifacts()[0];
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
		return "Component [name=" + this.name + ", version=" + this.version + ", licenseName=" + this.licenseName
				+ ", licenseShortName=" + this.licenseShortName + ", description=" + this.description + ", status=" + this.status
				+ ", size=" + this.size + "]";
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
