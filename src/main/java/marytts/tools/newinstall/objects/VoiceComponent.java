package marytts.tools.newinstall.objects;

import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.log4j.Logger;

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
		StringBuilder sb = new StringBuilder();

		sb.append("Voice component: ").append(this.name).append("\n");
		sb.append("locale: ").append(this.locale).append("; gender: ").append(this.gender).append("; type: ").append(this.type)
				.append("\n");
		sb.append("version: ").append(this.version).append("; status: ").append(this.status).append("; size: ")
				.append(FileUtils.byteCountToDisplaySize(this.size)).append("\n");
		sb.append("license name: ").append(this.licenseName).append("\n");
		sb.append("description: ").append(this.description.replaceAll("[\\t\\n]", " ").replaceAll("( )+", " "));

		return sb.toString();
	}

	@Override
	protected String toComparisonString() {
		return "_".concat(getLocale().getDisplayName()).concat(getGender()).concat(getDisplayName());
	}

}
