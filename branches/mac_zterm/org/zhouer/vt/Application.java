package org.zhouer.vt;

import java.awt.Dimension;

public interface Application
{
	public void showMessage( String msg );
	public void showPopup( int x, int y );
	public void openExternalBrowser( String url );
	
	public boolean isConnected();
	public boolean isClosed();
	
	public boolean isTabForeground();
	
	public Dimension getSize();
	
	public void bell();
	
	public void paste();
	public void colorPaste();
	public void copy();
	public void colorCopy();
	
	public void setIconName( String name );
	public void setWindowTitle( String title );
	
	public void scroll( int lines );
	
	public int readBytes( byte[] buf );
	
	public void writeByte( byte b );
	public void writeBytes( byte[] buf, int offset, int len );
	public void writeChar( char c );
	public void writeChars( char[] buf, int offset, int len );
}
