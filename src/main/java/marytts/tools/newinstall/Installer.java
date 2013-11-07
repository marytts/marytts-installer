package marytts.tools.newinstall;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import marytts.tools.newinstall.objects.Component;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.install.InstallOptions;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorParser;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;

import com.google.common.io.Resources;

public class Installer {

  private List<Component> resources;
  private IvySettings ivySettings;


  public Installer() {

    try {
      this.resources = new ArrayList<Component>();
      this.ivySettings = new IvySettings();
      this.ivySettings.load(Resources.getResource("ivysettings.xml"));
      parseIvyResources(this.ivySettings);
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * @param ivySettings
   * @throws ParseException
   * @throws IOException
   */
  private void parseIvyResources(IvySettings ivySettings)
      throws ParseException, IOException {

    String resourcesPath =
      "/Users/Jonathan/marytts/marytts-installer/src/main/resources";
    File[] resourcesList = new File(resourcesPath).listFiles();

    for (File oneFile : resourcesList) {
      if (oneFile.getName().startsWith("marytts-voice")) {
        URL oneResource = Resources.getResource(oneFile.getName());
        ModuleDescriptor descriptor =
          XmlModuleDescriptorParser.getInstance().parseDescriptor(
            ivySettings, oneResource, true);
        Component oneComponent = new Component(descriptor);
        this.resources.add(oneComponent);
      }
      else {
        continue;
      }
    }

  }


  /**
   * @return
   */
  public List<Component> getAvailableVoices() {

    return this.resources;
  }


  /**
   * @param feature
   * @param value
   * @return
   */
  public Collection<Component> getAvailableVoices(String feature, String value) {

    // TODO
    return null;
  }


  /**
   * Test Installer <br>
   * <b>Note:</b> must currently run with -Dmary.base=/path/to/marytts
   * 
   * @param args
   * @throws IOException
   * @throws ParseException
   */
  public static void main(String[] args)
      throws ParseException, IOException {

    // load ivy settings
    IvySettings ivySettings = new IvySettings();
    ivySettings.load(Resources.getResource("ivysettings.xml"));

    // as a test, parse module descriptor for lang-de component
    URL resource =
      Resources.getResource("marytts-voice-cmu-slt-hsmm-5.1-SNAPSHOT.xml");
    ModuleDescriptor descriptor =
      XmlModuleDescriptorParser.getInstance().parseDescriptor(ivySettings,
        resource, true);

    // instantiate ivy
    Ivy ivy = Ivy.newInstance(ivySettings);
    // set ivy loglevel to DEBUG
    ivy.getLoggerEngine().pushLogger(
      new DefaultMessageLogger(Message.MSG_DEBUG));
    ResolveOptions resolveOptions = new ResolveOptions();
    ivy.resolve(descriptor, resolveOptions);
    // retrieve options; retrieved artifacts go into MARYBASE/lib
    RetrieveOptions retrieveOptions = new RetrieveOptions();
    retrieveOptions
      .setDestIvyPattern("${mary.base}/installed/[module]-[revision].xml");
    retrieveOptions
      .setDestArtifactPattern("${mary.base}/lib/[module]-[revision].[ext]");
    ivy.retrieve(descriptor.getModuleRevisionId(), retrieveOptions);

    InstallOptions installOptions = new InstallOptions();
    installOptions.setTransitive(true);
    installOptions.setOverwrite(true);
    String from = "remote";
    String to = "marytts-installed";
    ivy.install(descriptor.getModuleRevisionId(), from, to, installOptions);
  }
}
