package marytts.tools.newinstall.gui;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class SwingAppender extends AppenderSkeleton {

	private LogViewFrame logViewFrame;

	public SwingAppender(LogViewFrame logViewFrame) {

		this.logViewFrame = logViewFrame;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {

		// String[] logOutput = event.getThrowableStrRep();
		String logOutput = this.layout.format(event);
		// String logOutput = ((String) event.getMessage()).concat("\n");
		this.logViewFrame.log(logOutput);

	}

}
