package marytts.tools.newinstall.objects;

import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;

public class LangComponent extends Component {

	private Locale locale;

	public LangComponent(ModuleDescriptor descriptor) {
		super(descriptor);
		setLocale(new Locale(descriptor.getExtraAttribute("locale")));
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return this.locale;
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

	@Override
	public String getDisplayName() {
		return getLocale().getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Language component: ").append(this.name).append("\n");
		sb.append("locale: ").append(this.locale).append("\n");
		sb.append("version: ").append(this.version).append("; status: ").append(this.status).append("; size: ")
				.append(FileUtils.byteCountToDisplaySize(this.size)).append("\n");
		sb.append("license name: ").append(this.licenseName).append("\n");
		sb.append("description: ").append(this.description.replaceAll("[\\t\\n]", " ").replaceAll("( )+", " "));

		return sb.toString();
	}

	@Override
	protected String toComparisonString() {
		return this.getClass().getName().concat(getLocale().getDisplayName()).concat(getDisplayName());
	}

}
