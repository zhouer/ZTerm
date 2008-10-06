package org.zhouer.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ForwardThread extends Thread
{
	InputStream is;
	OutputStream os;
	
	public void run()
	{	
		while( true ) {
			try {
				os.write( is.read() );
			} catch (IOException e) {
				break;
			}
		}
	}
	
	public ForwardThread( InputStream is, OutputStream os )
	{
		this.is = is;
		this.os = os;
	}
}

public class Standalone
{
	public static void main( String args[] )
	{
		Protocol p;
		Thread a, b;
		int port;
		
		// 保證至少有 protocol 與 host
		if( args.length < 2) {
			System.out.println("Not enough parameter!");
			return;
		}

		if( args[0].equalsIgnoreCase( "telnet" ) ) {
			if( args.length == 2 ) {
				port = 23;
			} else {
				port = Integer.parseInt( args[2] );
			}
			p = new Telnet( args[1], port );
		} else if ( args[0].equalsIgnoreCase( "ssh2" ) ) {
			if( args.length == 2 ) {
				port = 22;
			} else {
				port = Integer.parseInt( args[2] );
			}
			p = new SSH2( args[1], port, null );
			p.setTerminalType( "vt100" );
		} else {
			System.out.println("Unknown protocol!");
			return;
		}
		
		if( p.connect() == false ) {
			System.out.println("Connection error!");
			return;
		}
		
		a = new ForwardThread( p.getInputStream(), System.out );
		b = new ForwardThread( System.in, p.getOutputStream() );
		a.start();
		b.start();
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
