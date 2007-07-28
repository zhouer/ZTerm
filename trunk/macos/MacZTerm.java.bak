import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.Application;

import javax.swing.JFrame;

import org.zhouer.zterm.ZTerm;

class MacOSQuitHandler extends Application
{
	class OSXHandler extends ApplicationAdapter
	{
		MacZTerm callback;

		public OSXHandler( MacZTerm cbin )
		{
			callback = cbin;
		}

		public void handleQuit(ApplicationEvent event)
		{
			callback.quit();
		}

		public void handleAbout(ApplicationEvent event)
		{
			event.setHandled(true);
			callback.showAbout();
		}
	}

	public MacOSQuitHandler( MacZTerm callback )
	{
		addApplicationListener(new OSXHandler(callback));
	}

}

public class MacZTerm extends ZTerm
{
	public MacZTerm()
	{        
		super();

		// Meta+Q
		new MacOSQuitHandler(this);
	}

	public static void main( String args[] )
	{
		// MacOSX-Style MenuBar
		if (System.getProperty("os.name").indexOf("Mac") != -1) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		new MacZTerm();
	} 
}
