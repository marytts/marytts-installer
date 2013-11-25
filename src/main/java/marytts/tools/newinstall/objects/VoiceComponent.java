package marytts.tools.newinstall.objects;

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;

import com.google.common.collect.ComparisonChain;

public class VoiceComponent extends Component {

	private String gender;
	private String type;

	public VoiceComponent(ModuleDescriptor descriptor) {
		super(descriptor);
		setGender(descriptor.getExtraAttribute("gender"));
		setType(descriptor.getExtraAttribute("type"));
	}

	/**
	 * @return the gender
	 */
	public String getGender() {

		return this.gender;
	}

	/**
	 * @return the type
	 */
	public String getType() {

		return this.type;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	private void setGender(String gender) {

		this.gender = gender;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	private void setType(String type) {

		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VoiceComponent [gender=" + this.gender + ", type=" + this.type + ", name=" + this.name + ", locale="
				+ this.locale + ", version=" + this.version + ", licenseName=" + this.licenseName + ", licenseShortName="
				+ this.licenseShortName + ", description=" + this.description + ", status=" + this.status + ", size=" + this.size
				+ "]";
	}

	@Override
	public int compareTo(Component o) {

		VoiceComponent newO = (VoiceComponent) o;
		return ComparisonChain.start().compare(super.locale.toString(), newO.getLocale().toString())
				.compare(this.gender, newO.getGender()).compare(super.name, newO.getName()).result();
	}

}
