package marytts.tools.newinstall.gui;

import static org.junit.Assert.assertTrue;
import marytts.tools.newinstall.Installer;
import marytts.tools.newinstall.gui.InstallerGUI;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InstallerGUITest {

	private FrameFixture testFrame;
	private InstallerGUI frame;
	private Installer installer;

	@BeforeClass
	public static void setUpOnce() {
		FailOnThreadViolationRepaintManager.install();
	}

	@Before
	public void setUp() throws Exception {

		final String[] testArgs = { "--gui" };
		this.installer = new Installer(testArgs);

		this.frame = GuiActionRunner.execute(new GuiQuery<InstallerGUI>() {
			protected InstallerGUI executeInEDT() {
				return new InstallerGUI(InstallerGUITest.this.installer);
			}
		});
		this.testFrame = new FrameFixture(this.frame);
		this.testFrame.show();
	}

	public void setUpTest() {
		this.testFrame.checkBox("advancedCheckBox").requireNotSelected();
	}

	public void requirePopulated() {
		// needs update with arguments
		if (this.installer.getAvailableComponents(null, null, null, null, null, false) != null) {
			assertTrue(this.testFrame.panel("voicesGroupPanel").component().getComponentCount() != 0
					|| this.testFrame.panel("languagesGroupPanel").component().getComponentCount() != 0);
		}
	}

	@After
	public void tearDown() {
		this.testFrame.cleanUp();
	}

}
