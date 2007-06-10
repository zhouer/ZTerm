package org.zhouer.vt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JScrollBar;

import org.zhouer.utils.Convertor;

public class ZVT extends JComponent implements AdjustmentListener
{
	private static final long serialVersionUID = 2378589039395982558L;
	
	private VT100 vt;
	private JScrollBar scrollbar;
	private User user;
	
	private BufferedImage bi;
	private Config resource;
	private Convertor conv;
	private Application parent;
	
	// 捲頁緩衝區的行數
	private int scrolllines;
	
	/*
	 * 往上送到 parent 的
	 */
	
	public void showMessage( String msg )
	{
		parent.showMessage( msg );
	}
	
	public void showPopup( int x, int y )
	{
		parent.showPopup( x, y );
	}
	
	public void openExternalBrowser( String url ) {
		parent.openExternalBrowser( url );
	}
	
	public boolean isTabForeground()
	{
		return parent.isTabForeground();
	}
	
	public void setIconName( String name )
	{
		parent.setIconName( name );
	}
	
	public void setWindowTitle( String title )
	{
		parent.setWindowTitle( title );
	}
	
	public boolean isConnected()
	{
		return parent.isConnected();
	}
	
	public boolean isClosed()
	{
		return parent.isClosed();
	}
	
	public void bell()
	{
		parent.bell();
	}
	
	public int readBytes( byte[] buf )
	{
		return parent.readBytes( buf );
	}
	
	public void writeByte( byte b )
	{
		parent.writeByte( b );
	}
	
	public void writeBytes( byte[] buf, int offset, int len )
	{
		parent.writeBytes(buf, offset, len);
	}
	
	public void writeChar( char c )
	{
		parent.writeChar( c );
	}
	
	public void writeChars( char[] buf, int offset, int len )
	{
		parent.writeChars(buf, offset, len);
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
	 * 送到 vt 的
	 */
	
	public boolean coverURL( int x, int y )
	{
		return vt.coverURL( x, y );
	}
	
	public String getURL( int x, int y )
	{
		return vt.getURL( x, y);
	}
	
	public void resetSelected()
	{
		vt.resetSelected();
	}
	
	public void selectConsequtive( int x, int y )
	{
		vt.selectConsequtive( x, y);
	}
	
	public void selectEntireLine( int x, int y )
	{
		vt.selectEntireLine( x, y );
	}
	
	public void setSelected( int x1, int y1, int x2, int y2 )
	{
		vt.setSelected( x1, y1, x2, y2 );
	}
	
	public String getSelectedText()
	{
		return vt.getSelectedText();
	}
	
	public String getSelectedColorText()
	{
		return vt.getSelectedColorText();
	}
	
	public void pasteText( String str )
	{
		vt.pasteText( str );
	}
	
	public void pasteColorText( String str )
	{
		vt.pasteColorText( str );
	}
	
	public int getKeypadMode()
	{
		return vt.getKeypadMode();
	}
	
	public void updateImage()
	{
		bi = parent.getImage();
		if( vt != null ) {
			vt.updateImage( bi );
		}
	}
	
	public void updateSize()
	{
		if( vt != null ) {
			vt.updateSize();
			vt.updateFont();
			vt.updateCurrentScreen();
		}
	}
	
	public void updateFont()
	{
		if( vt != null ) {
			vt.updateFont();
		}		
	}
	
	public void updateScreen()
	{
		if( vt != null ) {
			vt.updateCurrentScreen();
		}
	}
	
	public void setEncoding( String enc )
	{
		vt.setEncoding( enc );
	}
	
	public String getEncoding()
	{
		return vt.getEncoding();
	}
	
	public void setEmulation( String emu )
	{
		vt.setEmulation( emu );
	}
	
	public String getEmulation()
	{
		return vt.getEmulation();
	}
	
	/*
	 * 自己的
	 */
	
	public void close()
	{
		// 斷線後應停止處理來自使用者的訊息
		removeKeyListener( user );
		removeMouseListener( user );
		removeMouseWheelListener( user );
		removeMouseMotionListener( user );
		
		// 通知 vt 停止運作
		if( vt != null ) {
			vt.close();
		}
	}
	
	public void scroll( int amount )
	{
		scrollbar.setValue( scrollbar.getValue() + amount );
	}
	
	public Dimension getPreferredSize()
	{
		// FIXME: magic number
		return new Dimension( 800, 600 );
	}
	
	public void adjustmentValueChanged( AdjustmentEvent ae )
	{
		vt.setScrollUp( scrollbar.getMaximum() - scrollbar.getValue() - scrollbar.getVisibleAmount() );
	}
	
	public void run()
	{
		// XXX: 因為是 ZVT addKeyListener，所以在這裡 requestFocus。
		requestFocusInWindow();
		
		vt.run();
	}
	
	public ZVT( Config c, Convertor cv, BufferedImage b, Application pa )
	{
		super();
		
		resource = c;
		conv = cv;
		bi = b;
		parent = pa;
		
		// 取消  focus traversal key, 這樣才能收到 tab.
		setFocusTraversalKeysEnabled( false );
		
		// Input Method Framework, set passive-client
		enableInputMethods( true );
		
		// VT100
		vt = new VT100( this, resource, conv, bi );
		
		// scrollbar
		scrolllines = resource.getIntValue( Config.TERMINAL_SCROLLS );
		// FIXME: magic number
		scrollbar = new JScrollBar( JScrollBar.VERTICAL, scrolllines - 1, 24, 0, scrolllines + 23 );
		scrollbar.addAdjustmentListener( this );
		
		// 設定 layout 並把 vt 及 scrollbar 放進去，
		setLayout( new BorderLayout() );
		add( vt, BorderLayout.CENTER );
		add( scrollbar, BorderLayout.EAST );
		
		// User
		user = new User( this, resource );
		addKeyListener( user );
		addMouseListener( user );
		addMouseWheelListener( user );
		addMouseMotionListener( user );
	}
}
