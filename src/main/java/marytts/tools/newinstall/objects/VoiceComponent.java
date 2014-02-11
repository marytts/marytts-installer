package marytts.tools.newinstall.objects;

import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.log4j.Logger;

public class VoiceComponent extends Component {

	private Locale locale;

	static Logger logger = Logger.getLogger(marytts.tools.newinstall.objects.VoiceComponent.class.getName());

	public VoiceComponent(ModuleDescriptor descriptor) {
		super(descriptor);
		setLocale(new Locale(descriptor.getExtraAttribute("locale")));
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
		return descriptor.getExtraAttribute("gender");
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return descriptor.getExtraAttribute("type");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Voice component: ").append(getName()).append("\n");
		sb.append("locale: ").append(getLocale()).append("; gender: ").append(getGender()).append("; type: ").append(getType())
				.append("\n");
		sb.append("version: ").append(getVersion()).append("; status: ").append(getStatus()).append("; size: ")
				.append(FileUtils.byteCountToDisplaySize(this.getSize())).append("\n");
		sb.append("license name: ").append(getLicenseName()).append("\n");
		sb.append("description: ").append(getDescription().replaceAll("[\\t\\n]", " ").replaceAll("( )+", " "));

		return sb.toString();
	}

	@Override
	protected String toComparisonString() {
		return "_".concat(getLocale().getDisplayName()).concat(getGender()).concat(getDisplayName());
	}

}
