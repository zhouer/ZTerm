package org.zhouer.zterm;

import java.awt.BorderLayout;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.zhouer.protocol.Protocol;
import org.zhouer.utils.Convertor;

public class ZTerm extends JFrame implements ActionListener, ChangeListener, KeyEventDispatcher, KeyListener, ComponentListener
{
	private static final long serialVersionUID = 6304594468121008572L;
		
	// 標準選單
	private JMenuBar menuBar;
	private JMenu connectMenu, siteMenu, editMenu, optionMenu, helpMenu;
	private JMenu encodingMenu;
	private JMenuItem openItem, closeItem, reopenItem, quitItem;
	private JMenuItem copyItem, pasteItem, colorCopyItem, colorPasteItem;
	private JMenuItem preferenceItem, siteManagerItem, showToolbarItem;
	private JMenuItem usageItem, faqItem, aboutItem;
	private JMenuItem big5Item, utf8Item;
	private JMenuItem[] favoriteItems;
	
	// popup 選單
	private JPopupMenu popupMenu;
	private JMenuItem popupCopyItem, popupPasteItem, popupColorCopyItem, popupColorPasteItem, popupCopyLinkItem;
	
	// 連線工具列
	private JToolBar connectionToolbar;
	private JButton openButton, closeButton, reopenButton;
	private JButton copyButton, colorCopyButton, pasteButton, colorPasteButton;
	private JButton telnetButton, sshButton;
	
	private DefaultComboBoxModel siteModel;
	private JComboBox siteField;
	private JTextComponent siteText;
	
	// 分頁
	private JTabbedPane tabbedPane;
	
	// 分頁 icon
	private ImageIcon connectedIcon, closedIcon, bellIcon;
	
	private Vector sessions;
	private Clip clip;
	private BufferedImage bi;
	private Resource resource;
	private Convertor conv;
	private String colorText = null;
	
	// 是否顯示工具列
	private boolean showToolbar;
	
	// 按滑鼠右鍵時滑鼠下的連結
	private String tmpLink;
	
	// 目前是否有 dialog 開啟
	private boolean isShowingDialog;
	
	// 避免同時有數個 thread 修改資料用的 lock object
	private Object msglock, menulock;
	
	public static final int ICON_CONNECTED = 1;
	public static final int ICON_CLOSED = 2;
	public static final int ICON_ALERT = 3;
	
	// 建立各種選單，包括 popup 選單也在這裡建立
	private void makeMenu()
	{
		menuBar = new JMenuBar();
		
		connectMenu = new JMenu("連線");
		connectMenu.setMnemonic(KeyEvent.VK_N);
		connectMenu.setToolTipText("meta-n");

		siteMenu = new JMenu("站台");
		siteMenu.setMnemonic(KeyEvent.VK_F);
		siteMenu.setToolTipText("meta-f");
		
		editMenu = new JMenu("編輯");
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.setToolTipText("meta-e");
		
		optionMenu = new JMenu("選項");
		optionMenu.setMnemonic(KeyEvent.VK_O);
		optionMenu.setToolTipText("meta-o");
		
		helpMenu = new JMenu("求助");
		helpMenu.setMnemonic(KeyEvent.VK_H);		
		helpMenu.setToolTipText("meta-h");
		
		encodingMenu = new JMenu("編碼");
		
		popupMenu = new JPopupMenu();
		
		openItem = new JMenuItem("開新連線");
		openItem.setToolTipText("meta-q");
		openItem.addActionListener( this );
		
		closeItem = new JMenuItem("中斷連線");
		closeItem.setToolTipText("meta-w");
		closeItem.addActionListener( this );

		reopenItem = new JMenuItem("重新連線");
		reopenItem.setToolTipText("meta-r");
		reopenItem.addActionListener( this );
		
		quitItem = new JMenuItem("離開");
		quitItem.addActionListener( this );

		copyItem = new JMenuItem("複製");
		copyItem.addActionListener( this );
		copyItem.setToolTipText("meta-c");
		
		pasteItem = new JMenuItem("貼上");
		pasteItem.addActionListener( this );
		pasteItem.setToolTipText("meta-v");

		colorCopyItem = new JMenuItem("彩色複製");
		colorCopyItem.addActionListener( this );
		
		colorPasteItem = new JMenuItem("彩色貼上");
		colorPasteItem.addActionListener( this );
		
		preferenceItem = new JMenuItem("偏好設定");
		preferenceItem.addActionListener( this );
		preferenceItem.setToolTipText("meta-,");
		
		siteManagerItem = new JMenuItem("站台管理");
		siteManagerItem.addActionListener( this );
		siteManagerItem.setToolTipText("meta-.");
		
		showToolbarItem = new JMenuItem( showToolbar ? "隱藏工具列" : "顯示工具列" );
		showToolbarItem.addActionListener( this );
		
		usageItem = new JMenuItem("使用說明");
		usageItem.addActionListener( this );
		
		faqItem = new JMenuItem( "常見問題" );
		faqItem.addActionListener( this );
		
		aboutItem = new JMenuItem("關於 ZTerm");
		aboutItem.addActionListener( this );
		
		big5Item = new JMenuItem("Big5");
		big5Item.addActionListener( this );
		
		utf8Item = new JMenuItem("UTF-8");
		utf8Item.addActionListener( this );
		
		setJMenuBar( menuBar);
		
		menuBar.add( connectMenu );
		menuBar.add( siteMenu );
		menuBar.add( editMenu );
		menuBar.add( optionMenu );
		menuBar.add( helpMenu );
		
		connectMenu.add( openItem );
		connectMenu.add( closeItem );
		connectMenu.add( reopenItem );
		connectMenu.add( quitItem );
		
		updateFavoriteMenu();
		
		encodingMenu.add( big5Item );
		encodingMenu.add( utf8Item );
		
		editMenu.add( copyItem );
		editMenu.add( pasteItem );
		editMenu.add( colorCopyItem );
		editMenu.add( colorPasteItem );
		editMenu.addSeparator();
		editMenu.add( encodingMenu );
		
		optionMenu.add( preferenceItem );
		optionMenu.add( siteManagerItem );
		optionMenu.add( showToolbarItem );
		
		helpMenu.add( usageItem );
		helpMenu.add( faqItem );
		helpMenu.add( aboutItem );
	}

	private void makePopupMenu()
	{
		popupCopyItem = new JMenuItem("複製");
		popupCopyItem.addActionListener( this );

		popupColorCopyItem = new JMenuItem("彩色複製");
		popupColorCopyItem.addActionListener( this );
		
		popupPasteItem = new JMenuItem("貼上");
		popupPasteItem.addActionListener( this );
		
		popupColorPasteItem = new JMenuItem("彩色貼上");
		popupColorPasteItem.addActionListener( this );
		
		popupCopyLinkItem = new JMenuItem("複製連結");
		popupCopyLinkItem.addActionListener( this );
		popupCopyLinkItem.setEnabled( false );
		
		popupMenu.add( popupCopyItem );
		popupMenu.add( popupPasteItem );
		popupMenu.add( popupColorCopyItem );
		popupMenu.add( popupColorPasteItem );
		popupMenu.add( popupCopyLinkItem );
	}
	
	private void makeTabbedPane()
	{
		// tab 擺在上面，太多 tab 時使用捲頁的顯示方式
		tabbedPane = new JTabbedPane( JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT );
		tabbedPane.addChangeListener( this );
		getContentPane().add( tabbedPane );
	}
	
	private void makeToolbar()
	{
		connectionToolbar = new JToolBar();
		connectionToolbar.setVisible( showToolbar );
		connectionToolbar.setRollover( true );
		
		closeButton = new JButton( "斷線");
		closeButton.setToolTipText("meta-w");
		closeButton.setFocusable( false );
		closeButton.addActionListener( this );
		
		reopenButton = new JButton( "重連" );
		reopenButton.setToolTipText("meta-r");
		reopenButton.setFocusable( false );
		reopenButton.addActionListener( this );
		
		copyButton = new JButton("複製");
		copyButton.setToolTipText("meta-c");
		copyButton.setFocusable( false );
		copyButton.addActionListener( this );
		
		pasteButton = new JButton("貼上");
		pasteButton.setToolTipText("meta-v");
		pasteButton.setFocusable( false );
		pasteButton.addActionListener( this );
		
		colorCopyButton = new JButton("彩色複製");
		colorCopyButton.setFocusable( false );
		colorCopyButton.addActionListener( this );
		
		colorPasteButton = new JButton("彩色貼上");
		colorPasteButton.setFocusable( false );
		colorPasteButton.addActionListener( this );
		
		telnetButton = new JButton("Telnet");
		telnetButton.setToolTipText("meta-t");
		telnetButton.setFocusable( false );
		telnetButton.addActionListener( this );
		
		sshButton = new JButton("SSH");
		sshButton.setToolTipText("meta-s");
		sshButton.setFocusable( false );
		sshButton.addActionListener( this );

		// XXX: JComboBox has a bug in the Mac OS X Aqua Look & Feel.
		LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
		}
		
		siteModel = new DefaultComboBoxModel();
		siteField = new JComboBox( siteModel );
		siteField.setToolTipText("meta-d");
		siteField.setEditable( true );

		// XXX: Restore Look & Feel.
		try {
			UIManager.setLookAndFeel(currentLookAndFeel);
		} catch (Exception e) {
		}

		siteText = (JTextComponent)siteField.getEditor().getEditorComponent();
		siteText.addKeyListener( this );
		
		openButton = new JButton( "連線" );
		openButton.setFocusable( false );
		openButton.addActionListener( this );
		
		connectionToolbar.add(closeButton);
		connectionToolbar.add(reopenButton);
		
		connectionToolbar.add( new JToolBar.Separator() );
		
		connectionToolbar.add(copyButton);
		connectionToolbar.add(pasteButton);
		connectionToolbar.add(colorCopyButton);
		connectionToolbar.add(colorPasteButton);
		
		connectionToolbar.add( new JToolBar.Separator() );
		
		connectionToolbar.add(telnetButton);
		connectionToolbar.add(sshButton);
		
		connectionToolbar.add( new JToolBar.Separator() );
		
		connectionToolbar.add( siteField );
		
		connectionToolbar.add( new JToolBar.Separator() );
		
		connectionToolbar.add( openButton );
		
		getContentPane().add( connectionToolbar, BorderLayout.NORTH );
	}
	
	public void updateFavoriteMenu()
	{
		synchronized( menulock ) {
			Site fa;
			Vector f = resource.getFavorites();
			favoriteItems = new JMenuItem[ f.size() ];
			
			siteMenu.removeAll();
			
			// 顯示目前我的最愛內容
			for( int i = 0; i < f.size(); i++ ) {
				fa = (Site)f.elementAt(i);
				favoriteItems[i] = new JMenuItem( fa.name );
				favoriteItems[i].setToolTipText( fa.host + ":" + fa.port );
				favoriteItems[i].addActionListener( this );
				siteMenu.add( favoriteItems[i] );
			}
		}
	}
	
	public void updateToolbar()
	{
		synchronized( menulock ) {
			showToolbar = resource.getBooleanValue( Resource.SHOW_TOOLBAR );
			showToolbarItem.setText( showToolbar ? "隱藏工具列" : "顯示工具列" );
			connectionToolbar.setVisible( showToolbar );
			validate();
		}
	}
	
	public void updateBounds()
	{
		int locationx, locationy, width, height;
		locationx = resource.getIntValue( Resource.GEOMETRY_X );
		locationy = resource.getIntValue( Resource.GEOMETRY_Y );
		width = resource.getIntValue( Resource.GEOMETRY_WIDTH );
		height = resource.getIntValue( Resource.GEOMETRY_HEIGHT );

		setBounds( locationx, locationy, width, height );
		validate();
	}
	
	public void updateSize()
	{
		Session s;
		
		// 產生跟主視窗一樣大的 image
		bi = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
		
		// 視窗大小調整時同步更新每個 session 的大小
		for( int i = 0; i < sessions.size(); i++ ) {
			s = (Session)sessions.elementAt( i );
			s.validate();
			s.updateImage( bi );
			s.updateSize();
		}
	}
	
	public void updateAntiIdleTime()
	{
		for( int i = 0; i < sessions.size(); i++ ) {
			((Session)sessions.elementAt( i )).updateAntiIdleTime();
		}
	}
	
	public void updateLookAndFeel()
	{
		try {
			// 使用系統的 Look and Feel
			if( resource.getBooleanValue( Resource.SYSTEM_LOOK_FEEL ) ) {
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			} else {
				UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
			}
			SwingUtilities.updateComponentTreeUI( this );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void updateTab()
	{
		// 為了下面 invokeLater 的關係，這邊改成 final
		final Session s = (Session)tabbedPane.getSelectedComponent();
		
		if( s != null ) {
			
			// 修改視窗標題列
			setTitle( "ZTerm - " + s.getWindowTitle() );
			
			// 修改位置列
			siteText.setText( s.getURL() );
			siteText.select( 0, 0 );
			siteField.hidePopup();
			
			// 切換到已連線的 session 時設定 icon 為 connected, 以取消  bell icon.
			if( s.isClosed() ) {
				setIcon( ICON_CLOSED, s );
			} else {
				setIcon( ICON_CONNECTED, s );
			}
			
			// 因為全部的連線共用一張 BufferedImage, 切換分頁時需重繪內容。
			s.updateScreen();
			
			// 讓所選的 session 取得 focus
			s.requestFocus();
		} else {
			// FIXME: magic number
			setTitle("ZTerm");
			siteText.setText("");
		}
	}

	public void updateTabTitle()
	{
		for( int i = 0; i < tabbedPane.getTabCount(); i++) {
			tabbedPane.setTitleAt( i, (i + 1) + ". " + ((Session)sessions.elementAt(i)).getSite().name );
		}
	}
	
	public void updateCombo()
	{
		int dotPos = siteText.getCaretPosition();
		String text = siteText.getText();
		Iterator iter = getCandidateSites( text ).iterator();
		
		siteModel.removeAllElements();
		siteModel.addElement( text );
		while( iter.hasNext() ) {
			// TODO: 可以考慮一下顯示甚麼比較好，name or url
			siteModel.addElement( ( (Site)iter.next() ).getURL() );
		}
		
		// 還原輸入游標的位置，否則每次輸入一個字就會跑到最後面
		siteText.setCaretPosition( dotPos );
		
		// FIXME: 增加 item 時重繪會有問題
		// 超過一個選項時才顯示 popup
		if( siteModel.getSize() > 1 ) {
			siteField.showPopup();
		} else {
			siteField.hidePopup();
		}
	}
	
	public void updateEncoding( String enc )
	{
		Session s = (Session)tabbedPane.getSelectedComponent();
		if( s != null ) {
			s.setEncoding( enc );
		}
	}
	
	private Vector getCandidateSites( String term )
	{
		Vector candi = new Vector();

		// 如果關鍵字是空字串，那就什麼都不要回
		if (term.length() == 0)
			return candi;

		Iterator iter;
		Site site;
		
		// 加入站台列表中符合的
		iter = resource.getFavorites().iterator();
		while( iter.hasNext() ) {
			site = (Site)iter.next();
			if( site.name.indexOf( term ) != -1 || site.alias.indexOf( term ) != -1 ||	site.getURL().indexOf( term ) != -1 ) {
				candi.addElement( site );
			}
		}
		
		// 把結果排序後再輸出
		Collections.sort( candi );
		
		return candi;
	}
	
	public void changeSession( int index )
	{
		if( 0 <= index && index < tabbedPane.getTabCount() ) {
			tabbedPane.setSelectedIndex( index );
			// System.out.println("Change to session: " + index );
		} else {
			// System.out.println("Change to session: " + index + ", error range!");
		}
	}
	
	public int showConfirm( String msg, String title, int option )
	{
		int result;
		
		isShowingDialog = true;
		result = JOptionPane.showConfirmDialog( null, msg, title, option );
		isShowingDialog = false;
		
		return result;
	}
	
	public void showMessage( String msg )
	{
		// 用了一個 lock 避免數個 thread 同時顯示 message dialog
		synchronized( msglock )
		{
			isShowingDialog = true;
			JOptionPane.showMessageDialog( null, msg );
			isShowingDialog = false;
		}
	}
	
	public void showPopup( int x, int y, String link )
	{
		Point p = getLocationOnScreen();
		
		// TODO: 用 tmpLink 的作法蠻笨的，但暫時想不到好作法
		popupCopyLinkItem.setEnabled( link != null );
		tmpLink = link;
		
		// 傳進來的是滑鼠相對於視窗左上角的座標，減去主視窗相對於螢幕左上角的座標，可得滑鼠相對於主視窗的座標。
		popupMenu.show( this, x - p.x , y - p.y );
	}
	
	private void autoconnect()
	{
		Vector f = resource.getFavorites();
		Iterator iter = f.iterator();
		Site s;
		
		while( iter.hasNext() ) {
			s = (Site)iter.next();
			if( s.autoconnect ) {
				connect( s, -1 );
			}
		}
	}
	
	private void connect( String h )
	{
		Site si;
		String host;
		int port, pos;
		String prot;

   		// 如果開新連線時按了取消則傳回值為 null
		if( h == null || h.length() == 0 ) {
			return;
		}
        
		do {
			// 透過 name or alias 連線
			si = resource.getFavorite( h );
			if( si != null ) {
				break;
			}

			pos = h.indexOf( "://" );
			// Default 就是 telnet
			prot = Protocol.TELNET;
			if( pos != -1 ) {
				if( h.substring( 0, pos).equalsIgnoreCase( Protocol.SSH ) ) {
					prot = Protocol.SSH;
				} else if( h.substring( 0, pos).equalsIgnoreCase( Protocol.TELNET ) ) {
					prot = Protocol.TELNET;
				} else {
					showMessage("通訊協定錯誤！");
					return;
				}
				// 將 h 重設為 :// 後的東西
				h = h.substring( pos + 3 );
			}
			
			// 取得 host:port, 或 host(:23)
			pos = h.indexOf(':');
			if( pos == -1 ) {
				host = h;
				if( prot.equalsIgnoreCase( Protocol.TELNET ) ) {
					port = 23;
				} else {
					port = 22;
				}
			} else {
				host = h.substring( 0, pos );
				port = Integer.parseInt( h.substring( pos + 1) );
			}
			si = new Site( host, host, port, prot );
		} while( false );
		
		// host 長度為零則不做事
		if( h.length() == 0 ) {
			return;
		}
		
		connect( si, -1 );
	}

	private void connect( Site si, int index )
	{
		Session s;

		s = new Session( si, resource, conv, bi, this );
		
		// index 為連線後放在第幾個分頁，若為 -1 表開新分頁。
		if( index == -1 ) {
			sessions.add( s );
			// 分頁 title 會顯示分頁編號加站台名稱，tip 會顯示 hostname.
			tabbedPane.addTab( (tabbedPane.getTabCount() + 1) + ". " + si.name, closedIcon, s, si.host );
			tabbedPane.setSelectedIndex( tabbedPane.getTabCount() - 1);
		} else {
			sessions.setElementAt( s, index );
			tabbedPane.setComponentAt( index, s);
		}

		// 每個 session 都是一個 thread, 解決主程式被 block 住的問題。
		new Thread( s ).start();
	}
	
	public void openExternalBrowser( String url )
	{
		int pos;
		
		String cmd = resource.getStringValue( Resource.EXTERNAL_BROWSER );
		if( cmd == null ) {
			showMessage( "瀏覽器啟動命令錯誤！" );
			return;
		}
		
		// 把 %u 置換成給定的 url
		pos = cmd.indexOf( "%u" );
		if( pos == -1 ) {
			showMessage( "瀏覽器啟動命令錯誤！" );
			return;
		}
		cmd = cmd.substring( 0, pos ) + url + cmd.substring( pos + 2 );
		// System.out.println( "browser command: " + cmd );
		
		runExternal( cmd );
	}
	
	public void runExternal( String cmd )
	{
		try {
			Runtime.getRuntime().exec( cmd );
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void bell()
	{	
		if( resource.getBooleanValue( Resource.USE_CUSTOM_BELL) ) {
			try {
				java.applet.Applet.newAudioClip( new File( resource.getStringValue( Resource.CUSTOM_BELL_PATH ) ).toURI().toURL() ).play();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			java.awt.Toolkit.getDefaultToolkit().beep();
		}
		
		// TODO: 該讓使用者知道這個視窗有 bell
	}
	
	public void open()
	{
		String host = JOptionPane.showInputDialog( this, "請輸入連線位置：");
		connect(host);
	}
	
	public void quit()
	{
		for( int i = 0; i < sessions.size(); i++) {
			if( !((Session)sessions.elementAt(i)).isClosed() ) {
				if( showConfirm( "還有連線尚未中斷，您確定要關閉程式嗎？", "連線中", JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION ) {
					return;
				} else {
					break;
				}
			}
		}

		for( int i = 0; i < sessions.size(); i++) {
			((Session)sessions.elementAt(i)).close( false );
		}
		sessions.removeAllElements();

		// 紀錄結束時的視窗大小
		Rectangle r = getBounds();
		resource.setValue( Resource.GEOMETRY_X, r.x );
		resource.setValue( Resource.GEOMETRY_Y, r.y );
		resource.setValue( Resource.GEOMETRY_WIDTH, r.width );
		resource.setValue( Resource.GEOMETRY_HEIGHT, r.height );
		
		// 程式結束，把目前設定值寫回設定檔。
		resource.writeFile();
		
		System.exit( 0 );
	}
	
	public void copy()
	{
		Session s = (Session)tabbedPane.getSelectedComponent();
		
		if( s != null ) {
			String str = s.getSelectedText();
			if( str.length() != 0 ) {
				clip.setContent( str );
			}
			if( resource.getBooleanValue( Resource.CLEAR_AFTER_COPY) ) {
				s.resetSelected();
				s.repaint();
			}
		}
	}
	
	public void colorCopy()
	{
		Session s = (Session)tabbedPane.getSelectedComponent();
		
		if( s != null ) {
			String str = s.getSelectedColorText();
			if( str.length() != 0 ) {
				colorText = str;
			}
			if( resource.getBooleanValue( Resource.CLEAR_AFTER_COPY) ) {
				s.resetSelected();
				s.repaint();
			}
		}
	}

	public void copyLink()
	{
		if( tmpLink != null ) {
			clip.setContent( tmpLink );
		}
	}
	
	public void paste()
	{
		Session s = (Session)tabbedPane.getSelectedComponent();
		if( s != null ) {
			s.pasteText( clip.getContent() );
		}
	}
	
	public void colorPaste()
	{
		Session s = (Session)tabbedPane.getSelectedComponent();
		if( s != null && colorText != null ) {
			s.pasteColorText( colorText );
		}
	}
	
	public void setIcon( int icon, Session s )
	{
		int index;
		ImageIcon ii;
		
		switch( icon ) {
			case ICON_CONNECTED:
				ii = connectedIcon;
				break;
			case ICON_CLOSED:
				ii = closedIcon;
				break;
			case ICON_ALERT:
				ii = bellIcon;
				break;
			default:
				ii = null;
		}
		
		index = tabbedPane.indexOfComponent( s );
		if( index != -1 ) {
			tabbedPane.setIconAt( index, ii );
		}
	}
	
	public boolean isTabForeground( Session s )
	{
		return (tabbedPane.indexOfComponent(s) == tabbedPane.getSelectedIndex());
	}
	
	public void openNewTab()
	{
		String host = siteText.getText();
		siteModel.removeAllElements();
		connect( host );
	}
	
	public void closeCurrentTab()
	{
		Session s = (Session)tabbedPane.getSelectedComponent();

		if( s != null ) {
			
			// 連線中則詢問是否要斷線
			if( !s.isClosed() ) {
				if( showConfirm( "您確定要結束連線嗎？", "連線中", JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION ) {
					return;
				}
				
				// 通知 session 要中斷連線了
				s.close( false );
				
				if( !resource.getBooleanValue( Resource.REMOVE_MANUAL_DISCONNECT ) ) {
					return;
				}
			}

			// 通知 session 要被移除了
			s.remove();
			
			tabbedPane.remove( s );
			sessions.remove( s );
			
			// 刪除分頁會影響分頁編號
			updateTabTitle();
			
			// 讓現在被選取的分頁取得 focus.
			updateTab();
		} else {
			// 沒有分頁了，關閉程式
			quit();
		}
	}
	
	public void reopenSession( Session s )
	{
		if( s != null ) {
			// 若連線中則開新分頁，已斷線則重連。
			if( s.isClosed() ) {
				connect( s.getSite(), tabbedPane.indexOfComponent(s) );
			} else {
				connect( s.getSite(), -1 );
			}
		}
	}
	
	private void showPreference()
	{
		isShowingDialog = true;
		new Preference( resource, this );
		isShowingDialog = false;
	}
	
	private void showSiteManager()
	{
		isShowingDialog = true;
		new SiteManager( resource, this );
		isShowingDialog = false;
	}
	
	private void showUsage()
	{
		new HtmlDialog( this, "使用說明", ZTerm.class.getResource( "docs/usage.html" ) );
	}
	
	private void showFAQ()
	{
		new HtmlDialog( this, "常見問題", ZTerm.class.getResource( "docs/faq.html" ) );
	}
	
	private void showAbout()
	{
		// FIXME: magic number
		showMessage( "ZTerm current 版\n專案網頁：http://zhouer.org/ZTerm/" );
	}
	
	public void actionPerformed( ActionEvent ae )
	{	
		Object source = ae.getSource();
		
		if( source == openItem ) {
			open();
		} else if( source == openButton ) {
			openNewTab();
		} else if( source == closeItem || source == closeButton ) {
			closeCurrentTab();
		} else if( source == reopenItem || source == reopenButton ) {
			reopenSession( (Session)tabbedPane.getSelectedComponent() );
		} else if( source == quitItem ) {
			quit();
		} else if( source == copyItem || source == copyButton || source == popupCopyItem ) {
			copy();
		} else if( source == colorCopyItem || source == colorCopyButton || source == popupColorCopyItem ) {
			colorCopy();
		} else if( source == pasteItem || source == pasteButton || source == popupPasteItem ) {
			paste();
		} else if( source == colorPasteItem || source == colorPasteButton || source == popupColorPasteItem ) {
			colorPaste();
		} else if( source == popupCopyLinkItem ) {
			copyLink();
		} else if( source == telnetButton ) {
			siteText.setText( "telnet://" );
			siteField.requestFocusInWindow();
		} else if( source == sshButton ) {
			siteText.setText( "ssh://" );
			siteField.requestFocusInWindow();
		} else if( source == preferenceItem ) {
			showPreference();
		} else if( source == siteManagerItem ) {
			showSiteManager();
		} else if( source == showToolbarItem ) {
			showToolbar = !showToolbar;
			resource.setValue( Resource.SHOW_TOOLBAR, showToolbar);
			updateToolbar();
			updateSize();
		} else if( source == usageItem ) {
			showUsage();
		} else if( source == faqItem ) {
			showFAQ();
		} else if( source == aboutItem ) {
			showAbout();
		} else if( source == big5Item ) {
			updateEncoding( "Big5" );
		} else if( source == utf8Item ) {
			updateEncoding( "UTF-8" );
		}
		
		// 我的最愛列表
		for( int i = 0; i < favoriteItems.length; i++ ) {
			if( source == favoriteItems[i] ) {
				Site f = resource.getFavorite(favoriteItems[i].getText());
				connect( f, -1 );
				break;
			}
		}
	}
	
	public void stateChanged(ChangeEvent e)
	{
		// 切換分頁，更新視窗標題、畫面
		if( e.getSource() == tabbedPane ) {
			updateTab();
		}
	}
	
	public boolean dispatchKeyEvent( KeyEvent ke )
	{
		// 只處理按下的狀況
		if( ke.getID() != KeyEvent.KEY_PRESSED ) {
			return false;
		}
		
		// 如果正在顯示 dialog 則不要處理
		// 原本是期望 modal dialog 會 block 住 keyboard event 的，不過看來還是得自己判斷
		if( isShowingDialog ) {
			return false;
		}
		
		if( ke.isAltDown() || ke.isMetaDown() ) {
		
			if( ke.getKeyCode() == KeyEvent.VK_C ) {
				copy();
			} else if( ke.getKeyCode() == KeyEvent.VK_D ) {
				siteText.selectAll();
				siteField.requestFocusInWindow();
			} else if( ke.getKeyCode() == KeyEvent.VK_Q ) {
				open();
			} else if( ke.getKeyCode() == KeyEvent.VK_R ) {
				reopenSession( (Session)tabbedPane.getSelectedComponent() );
			} else if( ke.getKeyCode() == KeyEvent.VK_S ) {
				siteText.setText( "ssh://" );
				siteField.requestFocusInWindow();
			} else if( ke.getKeyCode() == KeyEvent.VK_T ) {
				siteText.setText("telnet://");
				siteField.requestFocusInWindow();
			} else if( ke.getKeyCode() == KeyEvent.VK_V ) {
				paste();
			} else if( ke.getKeyCode() == KeyEvent.VK_W ) {
				closeCurrentTab();
			} else if( KeyEvent.VK_0 <= ke.getKeyCode() && ke.getKeyCode() <= KeyEvent.VK_9 ) {
				// 切換 tab 快速建
				// 可用 alt-N 或是 cmd-N 切換
				// XXX: 其實可以考慮 alt-0 不是快速建，不然多一個判斷很不舒服。
			
				if( KeyEvent.VK_0 == ke.getKeyCode() ) {
					changeSession( 9 );
				} else {
					changeSession( ke.getKeyCode() - KeyEvent.VK_1 );
				}
			} else if(
					ke.getKeyCode() == KeyEvent.VK_LEFT ||
					ke.getKeyCode() == KeyEvent.VK_UP ||
					ke.getKeyCode() == KeyEvent.VK_Z ) {
				// meta-left,up,z 切到上一個連線視窗
				// index 是否合法在 changeSession 內會判斷
				
				changeSession( tabbedPane.getSelectedIndex() - 1 );
			} else if(
					ke.getKeyCode() == KeyEvent.VK_RIGHT ||
					ke.getKeyCode() == KeyEvent.VK_DOWN ||
					ke.getKeyCode() == KeyEvent.VK_X ) {
				// meta-right,up,x 切到下一個連線視窗
				// index 是否合法在 changeSession 內會判斷
				
				changeSession( tabbedPane.getSelectedIndex() + 1 );
			} else if( ke.getKeyCode() == KeyEvent.VK_HOME ) {
				changeSession( 0 );
			} else if( ke.getKeyCode() == KeyEvent.VK_END ) {
				changeSession( tabbedPane.getTabCount() - 1 );
			} else if( ke.getKeyCode() == KeyEvent.VK_COMMA ) {
				// alt-, 開啟偏好設定
				showPreference();
			} else if( ke.getKeyCode() == KeyEvent.VK_PERIOD ) {
				// alt-. 開啟站台管理
				showSiteManager();
			} else {
				// 雖然按了 alt 或 meta, 但不認識，
				// 繼續往下送。
				return false;
			}

			// 功能鍵不再往下送
			return true;
		}
		
		// 一般鍵，繼續往下送。
		return false;
	}
	
	public void keyPressed(KeyEvent e)
	{
		// XXX: Mac 下 keyTyped 收不到 ESCAPE 一定要在這裡處理
		if( e.getSource() == siteText ) {
			if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
				updateTab();
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e)
	{
		if( e.getSource() == siteText ) {
			
			// 不對快速鍵起反應
			if( e.isMetaDown() || e.isAltDown() ) {
				return;
			}
			
			if( e.getKeyChar() == KeyEvent.VK_ENTER ) {
				openNewTab();
			} else if( e.getKeyChar() == KeyEvent.VK_ESCAPE ) {
				// ignore escape
			} else {
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateCombo();
					}
				});
			}
		}
	}
	
	public void componentResized(ComponentEvent ce)
	{
		validate();
		updateSize();
	}

	public void componentMoved(ComponentEvent ce) {}
	public void componentShown(ComponentEvent ce) {}
	public void componentHidden(ComponentEvent ce) {}
	
	public ZTerm()
	{
		// FIXME: magic number
		super("ZTerm");
		
		// 初始化 lock object
		msglock = new Object();
		menulock = new Object();
		
		// 各個連線
		sessions = new Vector();
		
		// 各種設定
		resource = new Resource();

		// 轉碼用
		conv = new Convertor();
		
		// 與系統剪貼簿溝通的橋樑
		clip = new Clip();
		
		// 設定 Look and Feel
		updateLookAndFeel();
		
		// 初始化各種 icon
		connectedIcon = new ImageIcon( ZTerm.class.getResource( "icon/connected.png" ) );
		closedIcon = new ImageIcon( ZTerm.class.getResource( "icon/closed.png" ) );
		bellIcon = new ImageIcon( ZTerm.class.getResource( "icon/bell.png" ) );
		
		// 是否要顯示工具列
		showToolbar = resource.getBooleanValue( Resource.SHOW_TOOLBAR );
		
		// 設定主畫面 Layout
		getContentPane().setLayout( new BorderLayout() );
		
		makeMenu();
		makePopupMenu();
		makeTabbedPane();
		makeToolbar();
		
		// 設定視窗位置、大小
		updateBounds();
		// 設定好視窗大小後才知道 image 大小
		bi = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
		
		setVisible( true );
		setResizable( true );
		
		// 一開始沒有任何 dialog
		isShowingDialog = false;
		
		// 按下關閉視窗按鈕時只執行 quit()
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		
		// 接收視窗大小改變的事件
		addComponentListener( this );
		
		// 攔截鍵盤 event 以處理快速鍵
		DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
		
		// 自動連線
		autoconnect();
	}
	
	public static void main( String args[] ) {
		new ZTerm();
	}
}
