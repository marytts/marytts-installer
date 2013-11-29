package guiTest;

import static org.junit.Assert.fail;
import marytts.tools.newinstall.Installer;
import marytts.tools.newinstall.InstallerGUI;

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

		this.frame = GuiActionRunner.execute(new GuiQuery<InstallerGUI>() {
			protected InstallerGUI executeInEDT() {
				return new InstallerGUI(new Installer(testArgs));
			}
		});
		this.testFrame = new FrameFixture(this.frame);
		this.testFrame.show();
	}

	@Test
	public void test() {
		this.testFrame.checkBox("advancedCheckBox").requireNotSelected();
	}

	@After
	public void tearDown() {
		this.testFrame.cleanUp();
	}

}
