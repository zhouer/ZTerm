package win32;

import java.awt.Component;

public class WindowsUtils
{
	static {
		System.loadLibrary("WindowsUtils");
	}
	
	public native void flash( Component c, boolean f );
}

