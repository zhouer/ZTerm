package org.zhouer.zterm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import org.zhouer.vt.Config;

public class Resource implements Config
{
	public static final String GEOMETRY_X = "geometry.x";
	public static final String GEOMETRY_Y = "geometry.y";
	public static final String GEOMETRY_WIDTH = "geometry.width";
	public static final String GEOMETRY_HEIGHT = "geometry.height";
	
	public static final String ANTI_IDLE = "connect.anti-idle";
	public static final String ANTI_IDLE_INTERVAL = "connect.anti-idle-interval";
	public static final String ANTI_IDLE_STRING = "connect.anti-idle-string";
	
	public static final String AUTO_RECONNECT = "connect.auto-reconnect";
	public static final String AUTO_RECONNECT_TIME = "connect.autoreconnect-time";
	public static final String AUTO_RECONNECT_INTERVAL = "connect.auto-reconnect-interval";
	
	public static final String USING_SOCKS = "connect.using-socks";
	public static final String SOCKS_HOST = "connect.socks-host";
	public static final String SOCKS_PORT = "connect.socks-port";
	
	public static final String EXTERNAL_BROWSER = "external-browser-command";
	public static final String SYSTEM_LOOK_FEEL = "use-system-look-and-feel";
	public static final String REMOVE_MANUAL_DISCONNECT = "remove-manual-disconnect";
	public static final String USE_CUSTOM_BELL = "use-custom-bell";
	public static final String CUSTOM_BELL_PATH = "custom-bell-path";
	public static final String SHOW_TOOLBAR = "show-toolbar";
	
	// chitsaou.070726: 分頁編號
	public static final String TAB_NUMBER = "tab-number";
	// chitsaou.070726: 顯示捲軸
	public static final String SHOW_SCROLL_BAR = "show-scroll-bar";
	
	private HashMap defmap, map;
	
	private void parseLine( String line )
	{
		String[] argv;
		
		// 用 "::" 隔開參數名與值
		argv = line.split("::");
		
		if( argv.length != 2 ) {
			return;
		}
		
		if( argv[0].length() > 0 ) {
			map.put( argv[0], argv[1] );
		}
	}
	
	private void loadDefault()
	{
		// 設定視窗相關資訊
		defmap.put( GEOMETRY_X, "0");
		defmap.put( GEOMETRY_Y, "0");
		defmap.put( GEOMETRY_WIDTH, "980");
		defmap.put( GEOMETRY_HEIGHT, "720");
		defmap.put( TAB_NUMBER, "true");
		defmap.put( SHOW_TOOLBAR, "true");
		defmap.put( SHOW_SCROLL_BAR, "true" );
		
		// 設定模擬終端機大小
		defmap.put( TERMINAL_COLUMNS, "80");
		defmap.put( TERMINAL_ROWS, "24");
		defmap.put( TERMINAL_SCROLLS, "200");
		
		// 防閒置設定
		defmap.put( ANTI_IDLE, "true");
		defmap.put( ANTI_IDLE_INTERVAL, "120");
		defmap.put( ANTI_IDLE_STRING, "\\x1bOA\\x1bOB");
		
		// 自動重連設定
		defmap.put( AUTO_RECONNECT, "true" );
		defmap.put( AUTO_RECONNECT_TIME, "10");
		defmap.put( AUTO_RECONNECT_INTERVAL, "500" );
		
		// Socks 相關設定
		defmap.put( USING_SOCKS, "false" );
		defmap.put( SOCKS_HOST, "localhost" );
		defmap.put( SOCKS_PORT, "2222" );
		
		// 字型設定
		defmap.put( FONT_FAMILY, "Monospaced" );
		defmap.put( FONT_SIZE, "0" );
		defmap.put( FONT_BOLD, "false" );
		defmap.put( FONT_ITALY, "false" );
		defmap.put( FONT_ANTIALIAS, "false" );
		defmap.put( FONT_VERTICLAL_GAP, "0");
		defmap.put( FONT_HORIZONTAL_GAP, "0");
		defmap.put( FONT_DESCENT_ADJUST, "0");
		
		// 游標設定
		defmap.put( CURSOR_BLINK, "true" );
		defmap.put( CURSOR_SHAPE, "block" );
		
		// 一般設定
		defmap.put( EXTERNAL_BROWSER, "explorer \"%u\"");
		defmap.put( SYSTEM_LOOK_FEEL, "false");
		defmap.put( COPY_ON_SELECT, "false");
		defmap.put( CLEAR_AFTER_COPY, "true");
		defmap.put( REMOVE_MANUAL_DISCONNECT, "true");
		defmap.put( AUTO_LINE_BREAK, "false");
		defmap.put( AUTO_LINE_BREAK_LENGTH, "72");
		defmap.put( USE_CUSTOM_BELL, "false");
		defmap.put( CUSTOM_BELL_PATH, "");
	}
	
	private File getRcFile()
	{
		File f;
		String home = System.getProperty("user.home");
		String rcfile = ".ztermrc";
		f = new File( home + File.separator + rcfile );
		// System.out.println( f );
		return f;
	}
	
	public void readFile()
	{
		File rc = getRcFile();
		BufferedReader br;
		String buf;
		
		try {
			br = new BufferedReader( new InputStreamReader( new FileInputStream( rc ), "UTF8" ) );
			
			while( (buf = br.readLine()) != null ) {
				parseLine( buf );
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeFile()
	{
		File rc = getRcFile();
		TreeSet ts;
		Iterator iter;
		String str;
		PrintWriter pw;
		
		// TreeSet 才會排序
		ts = new TreeSet( map.keySet() );
		iter = ts.iterator();
		
		try {
			pw = new PrintWriter( new OutputStreamWriter( new FileOutputStream( rc ), "UTF8") );
			
			while( iter.hasNext() ) {
				str = iter.next().toString();
				pw.println( str + "::" + map.get( str ) );
				// System.out.println( "Setting: " + str + " -> " + settings.get( str ) );
			}
			
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public boolean getBooleanValue( String key )
	{
		return getValue(key).equalsIgnoreCase("true");
	}
	
	public int getIntValue( String key )
	{
		return Integer.parseInt( getValue(key) );
	}
	
	public String getStringValue( String key )
	{
		return getValue(key);
	}
	
	private synchronized String getValue( String key )
	{
		if( map.get( key ) != null ) {
			return (String)map.get( key );
		}

		// 若沒有設定，則使用預設值
		return (String)defmap.get( key );
	}
		
	public void setValue( String key, boolean value )
	{
		if( value ) {
			setValue( key, "true" );
		} else {
			setValue( key, "false" );
		}
	}
	
	public void setValue( String key, int value )
	{
		setValue( key, Integer.toString(value) );
	}
	
	public synchronized void setValue( String key, String value )
	{
		map.put( key, value );
	}
	
	public synchronized Vector getArray( String name )
	{
		Vector v = new Vector();
		String s;
		
		// 應該是連號的，找不到就結束
		for( int count = 0;; count++ ){
			s = getStringValue( name + "." + count );
			if( s != null ) {
				v.addElement( s );
			} else {
				break;
			}
		}
		
		return v;
	}
	
	public synchronized void setArray( String name, Vector strs )
	{
		String tmp;
		Iterator mapiter = map.keySet().iterator();
		Iterator iter = strs.iterator();
		
		// remove original data
		while( mapiter.hasNext() ) {
			tmp = mapiter.next().toString();
			if( tmp.startsWith( name + "." ) ) {
				mapiter.remove();
			}
		}
		
		// add new data
		for( int count = 0; iter.hasNext(); count++ ) {
			setValue( name + "." + count, iter.next().toString() );
		}
	}
	
	public void addFavorite( Site site )
	{
		int index;
		Vector f = getFavorites();
		
		index = f.indexOf( site );
		
		if( index == -1 ) {
			f.addElement( site );
		} else {
			((Site)f.elementAt( index )).update();
		}
		
		setFavorites( f );
	}
	
	public Site getFavorite( String id )
	{
		Site fa;
		Vector f = getFavorites();
		Iterator iter = f.iterator();
		
		while( iter.hasNext() ) {
			fa = (Site)iter.next();
			// 尋找時可用 name 或是 alias
			if( id.equalsIgnoreCase( fa.name ) || id.equalsIgnoreCase( fa.alias ) ) {
				return fa;
			}
		}
		
		return null;
	}
	
	public Vector getFavorites()
	{
		Vector favorites = getArray( "favorite" );
		
		for( int i = 0; i < favorites.size(); i++ ) {
			favorites.setElementAt( new Site( favorites.elementAt(i).toString() ), i );
		}
		
		return favorites;
	}
	
	public void setFavorites( Vector favorites )
	{
		setArray( "favorite", favorites );
	}
	
	public Resource()
	{
		File rc = getRcFile();
		map = new HashMap();
		defmap = new HashMap();
		
		// 載入預設值
		loadDefault();
		
		// 從設定檔讀取設定，若不存在則新建設定檔
		if( rc.exists() ) {
			readFile();
		} else {
			try {
				rc.createNewFile();
				// System.out.println("rcfile: " + rc.getName() + " created.");
			} catch (IOException e) {
				System.out.println("catch IOException when create new rcfile.");
			}
		}
	}
}
