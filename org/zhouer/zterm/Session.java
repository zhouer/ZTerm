package org.zhouer.zterm;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.zhouer.protocol.Protocol;
import org.zhouer.protocol.SSH2;
import org.zhouer.protocol.Telnet;
import org.zhouer.utils.Convertor;
import org.zhouer.vt.Application;
import org.zhouer.vt.Config;
import org.zhouer.vt.ZVT;

public class Session extends JPanel implements Runnable, Application
{
	private static final long serialVersionUID = 2180544188833033537L;

	private ZTerm parent;
	private Resource resource;
	private BufferedImage bi;
	private Convertor conv;
	private Site site;
	private ZVT zvt;
	
	private String iconname;
	private String windowtitle;
	
	// 與遠端溝通用的物件
	private Protocol network;
	private InputStream is;
	private OutputStream os;
	
	// 自動重連用
	private long startTime;
	
	// 防閒置用
	private boolean antiidle;
	private Timer ti;
	private long lastInputTime, antiIdleInterval;
	
	// 這個 session 是否擁有一個 tab, 可能 session 還沒結束，但 tab 已被關閉。
	private boolean hasTab;
	
	/*
	 * 送往上層的
	 */
	
	public void setTabIcon( int icon )
	{
		parent.setIcon( icon, this );
	}
	
	public boolean isTabForeground()
	{
		return parent.isTabForeground( this );
	}
	
	public void bell()
	{
		parent.bell();
		
		// 如果 tab 不在最前則改變 icon
		if( !isTabForeground() ) {
			setTabIcon( ZTerm.ICON_ALERT );
		}
	}
	
	public void copy()
	{
		parent.copy();
	}
	
	public void colorCopy()
	{
		parent.colorCopy();
	}
	
	public void paste()
	{
		parent.paste();
	}
	
	public void colorPaste()
	{
		parent.colorPaste();
	}
	
	/*
	 * 送往下層的
	 */
	
	public void updateSize()
	{
		zvt.updateSize();
		zvt.updateFont();
		zvt.updateScreen();
	}
	
	public void updateFont()
	{
		zvt.updateFont();
	}
	
	public void updateScreen()
	{
		zvt.updateScreen();
	}
	
	public void updateImage()
	{
		zvt.updateImage();
	}
	
	public BufferedImage getImage()
	{
		return bi;
	}
	
	public void resetSelected()
	{
		zvt.resetSelected();
	}
	
	public String getSelectedText()
	{
		return zvt.getSelectedText();
	}
	
	public String getSelectedColorText()
	{
		return zvt.getSelectedColorText();
	}
	
	public void pasteText( String str )
	{
		zvt.pasteText( str );
	}
	
	public void pasteColorText( String str )
	{
		zvt.pasteColorText( str );
	}
	
	/*
	 * 送到 network 的 
	 */
	
	public int readBytes( byte[] buf )
	{
		try {
			return is.read( buf );
		} catch (IOException e) {
			// e.printStackTrace();
			// 可能是正常中斷，也可能是異常中斷，在下層沒有區分
			close( true );
			return -1;
		}
	}
	
	public void writeByte( byte b )
	{
		lastInputTime = new Date().getTime();
		try {
			os.write( b );
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println( "Caught IOException in Session::writeByte(...)" );
			close( true );
		}
	}
	
	public void writeBytes( byte[] buf, int offset, int len )
	{
		lastInputTime = new Date().getTime();
		try {
			os.write( buf, offset, len );
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println( "Caught IOException in Session::writeBytes(...)" );
			close( true );
		}
	}
	
	public void writeChar( char c )
	{
		byte[] buf;

		buf = conv.charToBytes( c, site.encoding );
		
		writeBytes( buf, 0, buf.length );
	}
	
	public void writeChars( char[] buf, int offset, int len )
	{
		int count = 0;
		// FIXME: magic number
		byte[] tmp = new byte[len * 4];
		byte[] tmp2;
		
		for(int i = 0; i < len; i++) {
			tmp2 = conv.charToBytes( buf[offset + i], site.encoding );
			for(int j = 0; j < tmp2.length; j++ ) {
				tmp[count++] = tmp2[j];
			}
		}
		
		writeBytes( tmp, 0, count );
	}
	
	public boolean isConnected()
	{
		// 如果 network 尚未建立則也當成尚未 connect.
		if( network == null ) {
			return false;
		}
		return network.isConnected();		
	}
	
	public boolean isClosed()
	{
		// 如果 network 尚未建立則也當成 closed.
		if( network == null ) {
			return true;
		}
		return network.isClosed();
	}
	
	/*
	 *
	 */
	
	public Site getSite()
	{
		return site;
	}
	
	public void setEncoding( String enc )
	{
		site.encoding = enc;
		zvt.setEncoding( site.encoding );
	}
	
	public void setEmulation( String emu )
	{
		site.emulation = emu;
		
		// 通知遠端 terminal type 已改變
		network.setTerminalType( emu );
		
		zvt.setEmulation( emu );
	}
	
	public String getEmulation()
	{
		return site.emulation;
	}
	
	public String getURL()
	{
		return site.getURL();
	}
	
	public void setIconName( String in )
	{
		iconname = in;
		parent.updateTab();
	}
	
	public void setWindowTitle( String wt )
	{
		windowtitle = wt;
		parent.updateTab();
	}
	
	public String getIconName()
	{
		return iconname;
	}
	
	public String getWindowTitle()
	{
		return windowtitle;
	}
	
	public void close( boolean fromRemote )
	{
		if( isClosed() ) {
			return;
		}
		
		//上層來的中斷連線命令，也就是來自使用者
		network.disconnect();
		
		// 停止防閒置用的 timer
		if( ti != null ) {
			ti.stop();
		}

		// 將連線 icon 改為斷線
		setTabIcon( ZTerm.ICON_CLOSED );
		
		// 若遠端 server 主動斷線則判斷是否需要重連
		boolean autoreconnect = resource.getBooleanValue( Config.AUTO_RECONNECT );
		if( autoreconnect && fromRemote ) {	
			long reopenTime = resource.getIntValue( Config.AUTO_RECONNECT_TIME );
			long reopenInterval = resource.getIntValue( Config.AUTO_RECONNECT_INTERVAL );
			long now = new Date().getTime();

			// 判斷連線時間距現在時間是否超過自動重連時間
			// 若設定自動重連時間為 0 則總是自動重連
			if( (now - startTime <= reopenTime * 1000) || reopenTime == 0 ) {
				try {
					Thread.sleep( reopenInterval );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				parent.reopenSession( this );	
			}
		}
	}
	
	public void remove()
	{
		// 設定分頁被移除了
		hasTab = false;
	}
	
	public void updateAntiIdleTime()
	{
		// 更新是否需要啟動防閒置
		antiidle = resource.getBooleanValue( Config.ANTI_IDLE );
		
		// 防閒置的作法是定時檢查距上次輸入是否超過 interval,
		// 所以這裡只要設定 antiIdleTime 就自動套用新的值了。
		antiIdleInterval = resource.getIntValue( Config.ANTI_IDLE_INTERVAL ) * 1000 ;
	}
	public void showMessage( String msg )
	{
		// 當分頁仍存在時才會顯示訊息
		if( hasTab ) {
			parent.showMessage( msg );
		}
	}
	
	public void showPopup( int x, int y )
	{
		Point p = zvt.getLocationOnScreen();
		String link;
		
		if( zvt.coverURL(x, y) ) {
			link = zvt.getURL( x, y );
		} else {
			link = null;
		}
		
		parent.showPopup( p.x + x, p.y + y, link );
	}
	
	public void openExternalBrowser( String url )
	{
		parent.openExternalBrowser( url );
	}
	
	class AntiIdleTask implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			// 如果超過 antiIdelInterval milliseconds 沒有送出東西，
			// lastInputTime 在 writeByte, writeBytes 會被更新。
			long now = new Date().getTime();
			if( antiidle && isConnected() && (now - lastInputTime > antiIdleInterval) ) {
				// System.out.println( "Sent antiidle char" );
				// TODO: 設定 antiidle 送出的字元
				if( site.protocol.equalsIgnoreCase( Protocol.TELNET ) ) {
					writeByte( Telnet.IAC );
					writeByte( Telnet.NOP );
				}
			}
		}
	}
	
	public void run()
	{
		zvt.run();
	}
	
	public Session( Site s, Resource r, Convertor c, BufferedImage b, ZTerm pa )
	{
		super();
		
		site = s;
		resource = r;
		conv = c;
		bi = b;
		parent = pa;
		
		// 設定擁有一個分頁
		hasTab = true;
		
		// XXX: 預設成 host
		windowtitle = site.host;
		iconname = site.host;
		
		// 新建連線
		if( site.protocol.equalsIgnoreCase( Protocol.TELNET ) ) {
			network = new Telnet( site.host, site.port );
			network.setTerminalType( site.emulation );
		} else if( site.protocol.equalsIgnoreCase( Protocol.SSH ) ) {
			network = new SSH2( site.host, site.port );
			network.setTerminalType( site.emulation );
		} else {
			System.err.println( "Unknown protocol: " + site.protocol );
		}
		
		if( network.connect() == false ) {
			showMessage( "連線失敗！" );
			return;
		}
		
		is = network.getInputStream();
		os = network.getOutputStream();
		
		// TODO: 如果需要 input filter or trigger 可以在這邊套上
		
		zvt = new ZVT( resource, conv, bi, this );
		
		// 把 zvt 放進 JPanel 中
		setLayout( new BorderLayout() );
		add( zvt, BorderLayout.CENTER );
		
		// FIXME: 是否應該在這邊設定？
		zvt.setEncoding( site.encoding );
		zvt.setEmulation( site.emulation );
		
		// 設定 icon 為 connected icon
		setTabIcon( ZTerm.ICON_CONNECTED );
		
		// 連線成功，更新或新增連線紀錄
		resource.addFavorite( site );
		parent.updateFavoriteMenu();
		
		// 防閒置用的 Timer
		updateAntiIdleTime();
		lastInputTime = new Date().getTime();
		ti = new Timer( 1000, new AntiIdleTask() );
		ti.start();
		
		// 記錄連線開始的時間
		startTime = new Date().getTime();
	}
}
