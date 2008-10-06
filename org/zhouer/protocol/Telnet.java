package org.zhouer.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

class TelnetInputStream extends InputStream
{
	private Telnet telnet;
	
	public int read() throws IOException
	{
		return telnet.readByte();
	}

	public int read( byte[] buf ) throws IOException
	{
		return telnet.readBytes( buf );
	}
	
	public int read( byte[]buf, int offset, int length ) throws IOException
	{
		return telnet.readBytes( buf, offset, length );
	}
	
	public TelnetInputStream( Telnet tel )
	{
		telnet = tel;
	}
}

class TelnetOutputStream extends OutputStream
{
	private Telnet telnet;
	
	public void write( int b ) throws IOException
	{
		// java doc: The 24 high-order bits of b are ignored.
		telnet.writeByte( (byte)b );
	}

	public void write( byte[] buf ) throws IOException
	{
		telnet.writeBytes( buf );
	}
	
	public void write( byte[] buf, int offset, int length ) throws IOException
	{
		telnet.writeBytes( buf, offset, length );
	}
	
	public TelnetOutputStream( Telnet tel )
	{
		telnet = tel;
	}
}

public class Telnet implements Protocol
{
	private String host;
	private int port;
	
	private String socks_host;
	private int socks_port;
	private boolean using_socks;
	
	private Socket sock;
	private InputStream is;
	private OutputStream os;
	
	private String terminal_type;
	
	public final static byte ECHO =	1;	// Echo
	public final static byte SGA =	3;	// Supress Go Ahead
//	public final static byte ST =		5;	// Status
//	public final static byte TM =		6;	// Terminal Mark
	public final static byte TT =		24;	// Terminal Type
	public final static byte WS =		31;	// Window Size
	public final static byte TS =		32;	// Terminal Speed
//	public final static byte RFC =	33;	// Remote Flow Control
//	public final static byte LM =		34;	// Linemode
//	public final static byte XDL =	35;	// X Display Location
//	public final static byte EV =		36;	// Environment Variables
//	public final static byte NEO =	39;	// New Environment Option

	public final static byte SE =         (byte)240;
	public final static byte NOP =        (byte)241;
//	public final static byte DM =         (byte)242;
//	public final static byte BRK =        (byte)243;
//	public final static byte IP =         (byte)244;
//	public final static byte AO =         (byte)245;
//	public final static byte AYT =        (byte)246;
//	public final static byte EC =         (byte)247;
//	public final static byte EL =         (byte)248;
//	public final static byte GA =         (byte)249;
	public final static byte SB =         (byte)250;
	public final static byte WILL =       (byte)251;
	public final static byte WONT =       (byte)252;
	public final static byte DO =         (byte)253;
	public final static byte DONT =       (byte)254;
	public final static byte IAC =        (byte)255;
	
	// 從下層來的資料暫存起來
	private byte[] buf;
	private int bufpos, buflen;

	private void proc_sb( ) throws IOException
	{
		byte b, buf1, buf2;

		b = read();
		buf2 = read();

		while( true ) {
			buf1 = buf2;
			buf2 = read();
			if ( buf1 == IAC && buf2 == SE ) {
				break;
			}
		}

		switch ( b ) {
			case TT :
				byte[] ttbuf = terminal_type.getBytes();
				send_sb( b, (byte)0, ttbuf, ttbuf.length );
				break;
			case TS:
				// FIXME: magic number
				byte[] tsbuf = { '3', '8', '4', '0', '0', ',', '3', '8', '4', '0', '0'};
				send_sb( b, (byte)0, tsbuf, tsbuf.length );
				break;
			default:
				break;
		}
	}

	private void proc_will( ) throws IOException
	{
		byte b;

		b = read();
		// System.out.println("Will " + b );

		if( b == ECHO || b == SGA ) {
			send_command( DO, b );
		} else {
			send_command( DONT, b );
		}

	}

	private void proc_wont() throws IOException
	{
		byte b;

		b = read();
		// DONT is the only valid response.
		send_command( DONT, b );
	}

	private void proc_do() throws IOException
	{
		byte b;

		b = read();
		// System.out.println("Do " + b );
		if( b == WS ) {
			send_command( WILL, b );
			// FIXME: Magic number (80x24)
			byte[] ws = { 0x50, 0x00, 0x18 };
			send_sb( b, (byte)0, ws, 3);
		} else if( b == TT || b == TS ) {
			send_command( WILL, b );
		} else if( b == ECHO ) {
			// XXX: 為什麼？
			send_command( WONT, b );
		} else {
			send_command( WONT, b );
		}
	}

	private void proc_dont() throws IOException
	{
		byte b;

		b = read();
		// WONT is the only valid responce.
		send_command( WONT, b );
	}

	private void proc_iac() throws IOException
	{
		byte b;

		b = read();
		switch( b ) {
			case SB:
				proc_sb();
				break;
			case WILL:
				proc_will();
				break;
			case WONT:
				proc_wont();
				break;
			case DO:
				proc_do();
				break;
			case DONT:
				proc_dont();
				break;
			default:
				break;
		}
	}

	private void send_sb( byte opt1, byte opt2, byte[] opt3, int size ) throws IOException
	{
		int i;
		byte[] buf = new byte[size + 6];
		
		buf[0] = IAC;
		buf[1] = SB;
		buf[2] = opt1;
		buf[3] = opt2;

		for(i = 0; i < size; i++) {
			buf[4 + i] = opt3[i];
		}

		buf[4 + size] = IAC;
		buf[5 + size] = SE;

		writeBytes( buf, 0, size + 6 );
	}

	private void send_command( byte comm, byte opt ) throws IOException
	{
		byte[] buf = new byte[3];

		buf[0] = IAC;
		buf[1] = comm;
		buf[2] = opt;

		writeBytes( buf, 0, 3 );
	}
	
	// 使用 buffer
	// fillBuf(), read(), readByes( byte[] )

	private void fillBuf() throws IOException
	{
		buflen = is.read( buf );
		if( buflen == -1 ) {
			throw new IOException();
		}
		bufpos = 0;
	}

	private byte read() throws IOException
	{
		// cache 用完了，再跟下層要一次。
		while( bufpos == buflen ) {
			fillBuf();
		}

		return buf[bufpos++];
	}

	public int readByte() throws IOException
	{
		return read();
	}
	
	public int readBytes( byte[] b ) throws IOException
	{
		return readBytes( b, 0, b.length );
	}
	
	public int readBytes( byte[] b, int offset, int length ) throws IOException
	{
		int len = 0;

		while( true ) {

			// 把 buf 都用完了，但還沒有裝東西到 b 去，就把 buf 重新裝滿。
			if( bufpos == buflen && len == 0 ) {
				// 會從這邊拿到 IOException
				fillBuf();
				continue;
			}

			// 當 buf 用完了，或是裝了 length bytes 後就結束。
			if( bufpos == buflen || len == length ) {
				break;
			}

			if( buf[bufpos] == IAC ) {
				// 把控制字元處理掉
				bufpos++;
				proc_iac();
			} else {
				// 把 buf 的資料裝進 b
				b[offset + len++] = buf[bufpos++];
			}
		}

		return len;
	}
    
	public void writeByte( byte b ) throws IOException
	{
		os.write( b );
		os.flush();
	}
	
	public void writeBytes( byte[] buf ) throws IOException
	{
		os.write( buf );
		os.flush();
	}
	
	public void writeBytes( byte[] buf, int offset, int size ) throws IOException
	{
		os.write( buf, offset, size );
		os.flush();
	}
	
	private Socket doConnect() throws IOException
	{
		Socket s;
		
		if( using_socks ) {
			
			s = new Socket( socks_host, socks_port );
			
			InetAddress inet = InetAddress.getByName( host );
			
			DataInputStream dis = new DataInputStream( s.getInputStream() );
			DataOutputStream dos = new DataOutputStream( s.getOutputStream() );
			
		    dos.writeByte( 0x05 );	// socks version (must be 0x05)
		    dos.writeByte( 0x01 );	// number of supported authentication methods
		    dos.writeByte( 0x00 );	// using ``no auth''

		    dis.readByte();	// server should return 0x05
		    dis.readByte();	// server should return 0x00
		    
		    dos.writeByte( 0x05 );	// socks version (must be 0x05)
		    dos.writeByte( 0x01 );	// 0x01: TCP connect
		    dos.writeByte( 0x00 );	// reserved
		    dos.writeByte( 0x01 );	// 0x01: IPv4 address

		    dos.write( inet.getAddress() );			// send host
		    dos.writeByte( (byte)((port >> 8) & 0xff) );		// send port
		    dos.writeByte( (byte)(port & 0xff) );

		} else {
			// System.out.println("Connecting to host: " + host + ", port: " + port + " ...");
			s = new Socket( host, port );
		}
		
		return s;
	}
	
	public boolean connect()
	{
		try {
			
			sock = doConnect();
			
			// disable Nagle's algorithm
			// 不要把很多小封包包裝成一個大封包再一次送出，
			// 這非常重要，連續顯示的游標是使用者評斷顯示速度的重要依據。
			sock.setTcpNoDelay( true );
			
			// 設定 keep alive
			sock.setKeepAlive( true );
			
			is = sock.getInputStream();
			os = sock.getOutputStream();
		} catch ( UnknownHostException e ) {
			// 可能是未連線或連線位置錯誤
			// e.printStackTrace();
			System.out.println("Caught UnknownHostException in Telnet::connect()");
			return false;
		} catch ( IOException e ) {
			// e.printStackTrace();
			System.out.println("Caught IOException in Telnet::connect()");
			return false;
		}

		return true;
	}
	
	public void disconnect()
	{
		// 如果跟本沒連線成功或是連線已被關閉則不做任何事。
		if( sock == null || sock.isClosed() ) {
			return;
		}
		
		try {
			is.close();
			os.close();
			sock.close();				
		} catch( IOException e ) {
			// e.printStackTrace();
			System.out.println( "Caught IOException in Network::disconnect()" );
		}
		
		if( sock.isClosed() ) {
			// System.out.println( "Connection closed!");
		} else {
			System.out.println( "Disconnect failed!");
		}
	}
	
	public boolean isConnected()
	{
		if( sock == null ) {
			return false;
		}
		return sock.isConnected();
	}
	
	public boolean isClosed()
	{
		// XXX: 連線建立失敗也當成已關閉連線。
		if( sock == null ) {
			return true;
		}

		return sock.isClosed();
	}
	
	public InputStream getInputStream()
	{
		return new TelnetInputStream( this );
	}
	
	public OutputStream getOutputStream()
	{
		return new TelnetOutputStream( this );
	}
	
	public void setTerminalType( String tt )
	{
		// TODO: 現在還不能動態改變，只有連線前就設定好才有用。
		terminal_type = tt;
	}
	
	public String getTerminalType()
	{
		return terminal_type;
	}
	
	public Telnet(String h, int p, String sh, int sp )
	{
		this( h, p );
		
		socks_host = sh;
		socks_port = sp;
		using_socks = true;
	}
	
	public Telnet( String h, int p )
	{
		host = h;
		port = p;
		using_socks = false;
		
		// 預設的 terminal type 是 vt100
		terminal_type = "vt100";
		
		// 使用 buffer
		buf = new byte[4096];
		bufpos = buflen = 0;
	}
}
