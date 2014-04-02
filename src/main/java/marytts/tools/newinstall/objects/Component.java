/**
 * 
 */
package marytts.tools.newinstall.objects;

import java.util.Locale;
import java.util.Observable;

import marytts.tools.newinstall.enums.Status;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.module.descriptor.DependencyArtifactDescriptor;
import org.apache.ivy.core.module.descriptor.DependencyDescriptor;
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

	protected ModuleDescriptor descriptor;

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

		String extraNameAttribute = this.descriptor.getExtraAttribute("name");
		if (extraNameAttribute != null) {
			return extraNameAttribute;
		}
		return this.descriptor.getModuleRevisionId().getName();
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
		return this.descriptor.getAttribute("revision");
	}

	/**
	 * @return the licenseName
	 */
	public String getLicenseName() {
		License[] licenses = this.descriptor.getLicenses();
		License license = licenses[0];
		return license.getName();
	}

	/**
	 * @return the licenseShortName
	 */
	public String getLicenseShortName() {
		return this.descriptor.getExtraAttribute("license");
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.descriptor.getDescription();
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

		DependencyDescriptor[] dependencies = this.descriptor.getDependencies();

		DependencyDescriptor depDescriptor = dependencies[0];
		DependencyArtifactDescriptor[] dependencyArtifacts = depDescriptor.getAllDependencyArtifacts();
		for (DependencyArtifactDescriptor oneDepArt : dependencyArtifacts) {
			String artifactSize = oneDepArt.getExtraAttribute("size");
			try {
				long parsedSize = Long.parseLong(artifactSize);
				size += parsedSize;
			} catch (NumberFormatException nfe) {
				logger.error(this.descriptor.getAllArtifacts()[0].getExtraAttribute("size") + " could not be parsed.");
			}
		}
		return size;
	}

	public String getExt() {
		return this.descriptor.getAllArtifacts()[0].getAttribute("ext");
	}

	// marytts-lang-en-5.1-beta1.jar
	// voice-cmu-slt-hsmm-5.1-beta1.jar
	// marytts-runtime-5.1-beta1-jar-with-dependencies.jar
	public String getArtifactName() {

		StringBuilder sb = new StringBuilder();
		if (!(this instanceof VoiceComponent)) {
			sb.append("marytts-");
		}
		sb.append(getName()).append("-").append(getVersion());

		// if a component like marytts-runtime-5.1-beta1-jar-with-dependencies.jar has a classifier in its name, it has to be
		// added to the artifactName
		String classifier = getModuleDescriptor().getDependencies()[0].getAllDependencyArtifacts()[0]
				.getExtraAttribute("classifier");
		if (classifier != null) {
			sb.append("-").append(classifier);
		}
		sb.append(".").append(getExt());

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
		sb.append("version: ").append(getVersion()).append("; status: ").append(getStatus())/*
																							 * .append("; size: ")
																							 * .append(FileUtils
																							 * .byteCountToDisplaySize(getSize()))
																							 */.append("\n");
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
