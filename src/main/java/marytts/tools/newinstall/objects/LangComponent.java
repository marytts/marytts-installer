package marytts.tools.newinstall.objects;

import java.util.Locale;

import org.apache.ivy.core.module.descriptor.ModuleDescriptor;

import com.google.common.collect.ComparisonChain;

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

	/**
	 * 
	 * @param displayName
	 *            the displayName to set
	 */
	@Override
	public void setDisplayNameFromName(String name) {
		this.displayName = this.getLocale().getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LangComponent [locale=" + this.locale + ", name=" + this.name + ", displayName=" + this.displayName
				+ ", version=" + this.version + ", licenseName=" + this.licenseName + ", licenseShortName="
				+ this.licenseShortName + ", description=" + this.description + ", status=" + this.status + ", size=" + this.size
				+ "]";
	}

	@Override
	public int compareTo(Component o) {

		LangComponent newO = (LangComponent) o;
		return ComparisonChain.start().compare(this.locale.toString(), newO.getLocale().toString())
				.compare(super.name, newO.getName()).result();
	}

}
