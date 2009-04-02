package org.zhouer.zterm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.zhouer.protocol.Protocol;

class ParameterPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -3479511727147739169L;
	
	// 名稱, 位置, 埠號, 通訊協定, 別名, 自動連線
	private JLabel nameLabel, hostLabel, portLabel, protocolLabel, aliasLabel, autoConnectLabel;
	private JTextField nameField, hostField, portField, aliasField;
	private ButtonGroup protocolGroup;
	private JRadioButton sshButton, telnetButton;
	private JCheckBox autoConnectCheckBox;

	private JLabel usernameLabel;
	private JTextField usernameField;

	private JLabel updateLabel;
	private JButton updateButton;

	private JLabel encodingLabel, emulationLabel;
	private JComboBox encodingCombo, emulationCombo;

	private String[] encodingList = { "Big5", "UTF-8" };
	private String[] emulationList = { "vt100", "xterm", "xterm-color", "ansi" };
	
	private JLabel useSocksLabel;
	private JCheckBox useSocksCheckBox;

	private SiteManager parent;
	
	public void updateParameter( Site s )
	{
		nameField.setText( s.name );
		hostField.setText( s.host );
		portField.setText( Integer.toString( s.port ) );
		aliasField.setText( s.alias );
		usernameField.setText( s.username );
		
		if( s.protocol.equalsIgnoreCase( Protocol.TELNET ) ) {
			telnetButton.setSelected( true );
		} else if( s.protocol.equalsIgnoreCase( Protocol.SSH ) ) {
			sshButton.setSelected( true );
		}
		
		encodingCombo.setSelectedItem( s.encoding );
		emulationCombo.setSelectedItem( s.emulation );
		
		autoConnectCheckBox.setSelected( s.autoconnect );
		useSocksCheckBox.setSelected( s.usesocks );
		
		// username 只對 ssh2 連線有效
		usernameField.setEnabled( s.protocol.equalsIgnoreCase( Protocol.SSH ) );
	}
	
	public void actionPerformed( ActionEvent ae ) {
		if( ae.getSource() == updateButton ) {
			
			Site s = new Site();
			s.name = nameField.getText();
			s.host = hostField.getText();
			s.port = Integer.parseInt( portField.getText() );
			s.alias = aliasField.getText();
			s.username = usernameField.getText();
			
			if( telnetButton.isSelected() ) {
				s.protocol = Protocol.TELNET;
			} else if( sshButton.isSelected() ) {
				s.protocol = Protocol.SSH;	
			}
			
			s.encoding = encodingCombo.getSelectedItem().toString();
			s.emulation = emulationCombo.getSelectedItem().toString();
			
			s.autoconnect = autoConnectCheckBox.isSelected();
			s.usesocks = useSocksCheckBox.isSelected();
			s.username = usernameField.getText();
			
			parent.updateFavorite( s );
		} else if( ae.getSource() == telnetButton ) {
			portField.setText( Integer.toString( 23 ) );
			usernameField.setEnabled( sshButton.isSelected() );
		} else if( ae.getSource() == sshButton ) {
			portField.setText( Integer.toString( 22 ) );
			usernameField.setEnabled( sshButton.isSelected() );
		}
	}
	
	public ParameterPanel( SiteManager p )
	{
		super();
		parent = p;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		nameLabel = new JLabel("名稱");
		hostLabel = new JLabel("位置");
		portLabel = new JLabel("埠號");
		aliasLabel = new JLabel("別名");
		protocolLabel = new JLabel("通訊協定");
		encodingLabel = new JLabel("文字編碼");
		emulationLabel = new JLabel("終端機模擬");
		autoConnectLabel = new JLabel("啟動時連線");
		useSocksLabel = new JLabel("Socks 連線");
		usernameLabel = new JLabel("自動送出帳號");
		updateLabel = new JLabel("修改設定後請記得按「更新」");
		
		nameField = new JTextField( 15 );
		hostField = new JTextField( 15 );
		portField = new JTextField( 15 );
		aliasField = new JTextField( 15 );
		usernameField = new JTextField( 15 );
		
		telnetButton = new JRadioButton( "Telnet" );
		telnetButton.addActionListener( this );
		sshButton = new JRadioButton( "SSH" );
		sshButton.addActionListener( this );
		
		protocolGroup = new ButtonGroup();
		protocolGroup.add( telnetButton );
		protocolGroup.add( sshButton );
		
		
		encodingCombo = new JComboBox( encodingList );
		encodingCombo.addActionListener( this );
		
		emulationCombo = new JComboBox( emulationList );
		emulationCombo.addActionListener( this );
		
		autoConnectCheckBox = new JCheckBox();
		useSocksCheckBox = new JCheckBox();
		
		updateButton = new JButton("更新");
		updateButton.addActionListener( this );
		
		c.ipadx = c.ipady = 3; 
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		add( nameLabel, c );
		c.gridx = 1;
		c.gridwidth = 2;
		add( nameField, c );
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		add( hostLabel, c );
		c.gridx = 1;
		c.gridwidth = 2;
		add( hostField, c );
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		add( portLabel, c );
		c.gridx = 1;
		c.gridwidth = 2;
		add( portField, c );
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		add( aliasLabel, c );
		c.gridx = 1;
		c.gridwidth = 2;
		add( aliasField, c );

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		add( protocolLabel, c );
		c.gridx = 1;
		add( telnetButton, c );
		c.gridx = 2;
		add( sshButton, c );
				
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		add( encodingLabel, c );
		c.gridx = 1;
		add( encodingCombo, c );
		
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		add( emulationLabel, c );
		c.gridx = 1;
		add( emulationCombo, c );
		
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		add( autoConnectLabel, c );
		c.gridx = 1;
		add( autoConnectCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 1;
		add( useSocksLabel, c );
		c.gridx = 1;
		add( useSocksCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 9;
		add( usernameLabel, c );
		c.gridx = 1;
		add( usernameField, c );
		
		c.gridx = 0;
		c.gridy = 10;
		add( updateButton, c );
		c.gridx = 1;
		add( updateLabel, c );
	}
}

class SitePanel extends JPanel implements ActionListener, ListSelectionListener
{
	private static final long serialVersionUID = 6399807179665067907L;
	
	private JList siteList;
	private DefaultListModel siteListModel;
	
	private JPanel modifyPanel;
	private JButton addButton, removeButton, upButton, downButton;
	
	private SiteManager parent;
	private Vector favorites;
	
	private void makeList()
	{
		Iterator iter = favorites.iterator();
		siteListModel = new DefaultListModel();

		while( iter.hasNext() ) {
			siteListModel.addElement( ((Site)iter.next()).name );
		}

		siteList = new JList( siteListModel );
		siteList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		siteList.addListSelectionListener( this );
		siteList.setSelectedIndex( 0 );
	}
	
	private void makeModify()
	{
		modifyPanel = new JPanel();
		modifyPanel.setLayout( new GridLayout( 0, 2, 3, 3 ) );
		
		addButton = new JButton("新增");
		addButton.addActionListener( this );
		removeButton = new JButton("移除");
		removeButton.addActionListener( this );
		upButton = new JButton("上移");
		upButton.addActionListener( this );
		downButton = new JButton("下移");
		downButton.addActionListener( this );
		
		modifyPanel.add( addButton );
		modifyPanel.add( removeButton );
		modifyPanel.add( upButton );
		modifyPanel.add( downButton );
	}
	
	public SitePanel( SiteManager p, Vector f )
	{
		super();
		
		parent = p;
		favorites = f;
		
		makeList();
		makeModify();
		
		setLayout( new BorderLayout());
		add( new JScrollPane(siteList), BorderLayout.CENTER );
		add( modifyPanel, BorderLayout.SOUTH );
	}

	public void updateFavorite( Site f )
	{
		int index = siteList.getSelectedIndex();
		if( index != -1 ) {
			siteListModel.setElementAt( f.name, index );
			favorites.setElementAt( f, index );
		}		
	}
	
	public void valueChanged(ListSelectionEvent lse ) {
		int index = siteList.getSelectedIndex();
		if( index != -1 ) {
			parent.updateParameter( (Site)favorites.elementAt(index) );
		}
	}
	
	public void actionPerformed( ActionEvent ae )
	{
		if( ae.getSource() == addButton ) {
			siteListModel.addElement("新站台");
			favorites.add( new Site( "新站台", "hostname", 23, Protocol.TELNET ) );
			siteList.setSelectedIndex( siteListModel.getSize() - 1 );
		} else if( ae.getSource() == removeButton ) {
			int i = siteList.getSelectedIndex();
			if( i != -1 ) {
				siteListModel.removeElementAt( i );
				favorites.removeElementAt( i );
				siteList.setSelectedIndex( i - 1 );
			}
		} else if( ae.getSource() == upButton ) {
			int i = siteList.getSelectedIndex();
			if( i > 0 ) {
				Object tmp;
				
				tmp = favorites.elementAt( i );
				favorites.removeElementAt( i );
				favorites.insertElementAt( tmp, i - 1 );
				
				tmp = siteListModel.elementAt( i );
				siteListModel.removeElementAt( i );
				siteListModel.insertElementAt( tmp, i - 1 );
				
				siteList.setSelectedIndex( i - 1 );
			}
		} else if( ae.getSource() == downButton ) {
			int i = siteList.getSelectedIndex();
			if( i < siteListModel.size() - 1 ) {
				Object tmp;
				
				tmp = favorites.elementAt( i );
				favorites.removeElementAt( i );
				favorites.insertElementAt( tmp, i + 1 );
				
				tmp = siteListModel.elementAt( i );
				siteListModel.removeElementAt( i );
				siteListModel.insertElementAt( tmp, i + 1 );
				
				siteList.setSelectedIndex( i + 1 );
			}
		}
	}
}

public class SiteManager extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 3644901803388220764L;
	
	private ZTerm parent;
	private Resource resource;
	private Vector favorites;
	
	private JSplitPane jsp;
	private ParameterPanel parameterPanel;
	private SitePanel sitePanel;
	
	private JButton okButton, cancelButton;
	private JPanel controlPanel;
	
	private void submit()
	{
		// 將修改更新後寫回設定檔
		resource.setFavorites( favorites );
		resource.writeFile();
		
		parent.updateFavoriteMenu();
	}
	
	public void updateParameter( Site s )
	{
		parameterPanel.updateParameter( s );
	}
	
	public void updateFavorite( Site s )
	{
		sitePanel.updateFavorite( s );
	}
	
	public void actionPerformed(ActionEvent ae )
	{
		if( ae.getSource() == okButton ) {
			submit();
			dispose();
		} else if( ae.getSource() == cancelButton ) {
			dispose();
		}
	}
	
	public SiteManager( Resource re, ZTerm pa )
	{
		super( pa, "站台管理", true );
		
		parent = pa;
		resource = re;
		favorites = resource.getFavorites();
		
		parameterPanel = new ParameterPanel( this );
		sitePanel = new SitePanel( this, favorites );
		
		jsp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, sitePanel, parameterPanel );
		getContentPane().add( jsp, BorderLayout.CENTER );

		okButton = new JButton("確定");
		okButton.addActionListener( this );
		
		cancelButton = new JButton("取消");
		cancelButton.addActionListener( this );
		
		controlPanel = new JPanel();
		controlPanel.setLayout( new FlowLayout() );
		controlPanel.add( okButton );
		controlPanel.add( cancelButton );
		getContentPane().add( controlPanel, BorderLayout.SOUTH );
		
		jsp.setDividerLocation( 160 );

		// 處理 Esc 關閉視窗
		KeyStroke escape = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 7303000774467918993L;
			public void actionPerformed( ActionEvent ae ) {
				dispose();
			}};
		getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( escape, "ESCAPE" );
		getRootPane().getActionMap().put( "ESCAPE", escapeAction );
		
		setSize( 500, 375 );
		setLocationRelativeTo( null );
		setVisible( true );
	}
}
