import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.Application;
import com.apple.cocoa.application.NSApplication;

import javax.swing.JFrame;

import org.zhouer.zterm.Session;
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
		
		public void handleAbout(ApplicationEvent event)
		{
			event.setHandled(true);
			callback.showAbout();
		}
		
		public void handlePreferences(ApplicationEvent event)
		{
			event.setHandled(true);
			callback.showPreference();
		}
		
		public void handleQuit(ApplicationEvent event)
		{
			callback.quit();
		}
	}

	public MacOSQuitHandler( MacZTerm callback )
	{
		addApplicationListener(new OSXHandler(callback));
	}

}

public class MacZTerm extends ZTerm
{
	public void bell( Session s )
	{
		super.bell(s);

		// 跳動 icon
		NSApplication app = NSApplication.sharedApplication();      
		app.requestUserAttention( NSApplication.UserAttentionRequestCritical );
	}
	
	public MacZTerm()
	{        
		super();

		// 處理 meta-q
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
