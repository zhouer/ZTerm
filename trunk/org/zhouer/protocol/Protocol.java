package org.zhouer.protocol;

import java.io.InputStream;
import java.io.OutputStream;

public interface Protocol
{	
	public static final String TELNET = "telnet";
	public static final String SSH = "ssh";

	public void setTerminalType( String tt );
	public String getTerminalType();
	
	public boolean connect();
	public void disconnect();
	
	public boolean isConnected();
	public boolean isClosed();
	
	public InputStream getInputStream();
	public OutputStream getOutputStream();
}
