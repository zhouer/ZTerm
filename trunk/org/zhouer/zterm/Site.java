package org.zhouer.zterm;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.zhouer.protocol.Protocol;
import org.zhouer.utils.CSV;
import org.zhouer.utils.TextUtils;

public class Site implements Comparable {

	// 識別名稱
	public String name;

	// hostname and port
	public String host;
	public int port;
	
	// 別名
	public String alias;
	
	// 通訊協定 (telnet or ssh)
	public String protocol;
	public final String defProtocol = Protocol.TELNET;
	
	// 文字編碼
	public String encoding;
	public final String defEncoding = "Big5";
	
	// 終端機模擬
	public String emulation;
	public final String defEmulation = "vt100";
	
	// 啟動時自動連線
	public boolean autoconnect;
	
	// 透過 Socks 連線
	public boolean usesocks;
	
	// 使用者帳號
	public String username;

	// 最近連線時間。
	public long lastvisit;

	// 連線總次數
	public int total;
	
	public void update()
	{
		total++;
		lastvisit = new Date().getTime();
	}
	
	public int compareTo( Object o )
	{
		int retval = 0;
		
		if ( o instanceof Site ) {
			Site site = (Site)o;

			// TODO: 找個好一點的演算法吧
			if ( total == site.total ) {
				retval = lastvisit < site.lastvisit ? 1 : ( lastvisit > site.lastvisit ? -1 : 0);
			} else {
				retval = site.total - total;
			}
		}

		return retval;
	}
	
	public boolean equals( Object o )
	{
		if ( o instanceof Site ) {
			Site site = (Site)o;
			if( host.equalsIgnoreCase( site.host ) && protocol.equalsIgnoreCase( site.protocol ) && port == site.port ) {
				return true;
			}
		}
		
		return false;
	}
	
	public String toString()
	{
		Vector v = new Vector();
		
		v.addElement( "name=" + name );
		v.addElement( "host=" + host );
		v.addElement( "port=" + port );
		v.addElement( "username=" + username );
		v.addElement( "protocol=" + protocol );
		v.addElement( "alias=" + alias );
		v.addElement( "encoding=" + encoding );
		v.addElement( "emulation=" + emulation );
		v.addElement( "lastvisit=" + lastvisit );
		v.addElement( "total=" + total );
		
		if( autoconnect ) {
			v.addElement( "autoconnect=true" );
		} else {
			v.addElement( "autoconnect=false" );	
		}
		
		if( usesocks ) {
			v.addElement( "usesocks=true" );
		} else {
			v.addElement( "usesocks=false" );	
		}

		return CSV.generate( v );
	}
	
	public String getURL()
	{
		String url = protocol + "://" + host;
		
		// 當連線 port 不等於該 protocol 預設值時要顯示連線 port
		if( ( protocol.equalsIgnoreCase( Protocol.TELNET ) && port != 23 ) ||
			( protocol.equalsIgnoreCase( Protocol.SSH ) && port != 22 ) ){
			url = url + ":" + port;
		}
		
		return url;
	}
	
	/**
	 * 沒有任何參數的 Site constructor
	 */
	public Site()
	{	
	}
	
	/**
	 * 使用詳細資料建構 Site
	 * @param n 名稱
	 * @param h	hostname
	 * @param po port
	 * @param pr protocol
	 */
	public Site( String n, String h, int po, String pr )
	{
		name = n;
		host = h;
		port = po;
		protocol = pr;
		
		// 以下使用預設值
		alias = "";
		encoding = defEncoding;
		emulation = defEmulation;
		lastvisit = 0;
		total = 0;
		autoconnect = false;
		usesocks = true;
		username = "";
	}
	
	/**
	 * 由 CSV 表示法建構 Site
	 * @param h CSV
	 */
	public Site( String h )
	{
		Map m = TextUtils.getCsvParameters( h );
		
		// name, host, port 是必要的，一定會有
		name = (String)m.get( "name" );
		host = (String)m.get( "host" );
		port = Integer.parseInt( (String)m.get("port") );
		
		if( m.containsKey("protocol") ) {
			protocol = (String)m.get("protocol");
		} else {
			protocol = defProtocol;
		}			
		
		if( m.containsKey("alias") ) {
			alias = (String)m.get("alias");
		} else {
			alias = "";
		}
		
		if( m.containsKey( "encoding" )) {
			encoding = (String)m.get("encoding");
		} else {
			encoding = defEncoding;
		}
		
		if( m.containsKey( "emulation" ) ) {
			emulation = (String)m.get( "emulation" );
		} else {
			emulation = defEmulation;
		}
		
		if( m.containsKey( "lastvisit" ) ) {
			lastvisit = Long.parseLong( (String)m.get( "lastvisit" ) );
		} else {
			lastvisit = 0;
		}
		
		if( m.containsKey( "total" ) ) {
			total = Integer.parseInt( (String)m.get( "total" ) );
		} else {
			total = 0;
		}
		
		if( m.containsKey("autoconnect") ) {
			autoconnect = ((String)m.get("autoconnect")).equalsIgnoreCase("true");
		} else {
			autoconnect = false;	
		}
		
		if( m.containsKey("usesocks") ) {
			usesocks = ((String)m.get("usesocks")).equalsIgnoreCase("true");
		} else {
			usesocks = true;
		}
		
		if( m.containsKey("username") ) {
			username = (String)m.get( "username" );
		} else {
			username = "";
		}
	}
}
