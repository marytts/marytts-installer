package marytts.tools.newinstall.objects;

import java.util.Locale;

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.log4j.Logger;

import com.google.common.collect.ComparisonChain;

public class VoiceComponent extends Component {

	private String gender;
	private String type;
	private Locale locale;

	static Logger logger = Logger.getLogger(marytts.tools.newinstall.objects.VoiceComponent.class.getName());

	public VoiceComponent(ModuleDescriptor descriptor) {
		super(descriptor);
		setLocale(new Locale(descriptor.getExtraAttribute("locale")));
		setGender(descriptor.getExtraAttribute("gender"));
		setType(descriptor.getExtraAttribute("type"));
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return this.locale;
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
	 * @param locale
	 *            the locale to set
	 */
	private void setLocale(Locale locale) {

		// if (locale.toString().equalsIgnoreCase("en-us")) {
		// locale = Locale.US;
		// } else if (locale.toString().equalsIgnoreCase("en-gb")) {
		// locale = Locale.UK;
		// }
		this.locale = locale;
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
		return "VoiceComponent [gender=" + this.gender + ", type=" + this.type + ", locale=" + this.locale + ", name="
				+ this.name + ", displayName=" + this.displayName + ", version=" + this.version + ", licenseName="
				+ this.licenseName + ", licenseShortName=" + this.licenseShortName + ", description=" + this.description
				+ ", status=" + this.status + ", size=" + this.size + "]";
	}

	@Override
	public int compareTo(Component o) {
		
		

		VoiceComponent newO = (VoiceComponent) o;
		return ComparisonChain.start().compare(this.locale.toString(), newO.getLocale().toString())
				.compare(this.gender, newO.getGender()).compare(super.name, newO.getName()).result();
	}

}
