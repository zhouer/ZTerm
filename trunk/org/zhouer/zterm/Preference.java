package org.zhouer.zterm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

class GeneralPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 290521402254313069L;
	
	private Resource resource;
	
	public JLabel browserLabel;
	public JTextField browserField;
	
	public JLabel copyOnSelectLabel, clearAfterCopyLabel, removeManualLabel, linebreakLabel;
	public JCheckBox copyOnSelectCheckBox, clearAfterCopyCheckBox, removeManualCheckBox, linebreakCheckBox;
	
	public JLabel breaklengthLabel;
	public JSpinner breaklengthSpinner;
	public SpinnerNumberModel breaklengthModel;
	
	public JLabel customBellLabel;
	public JCheckBox customBellCheckBox;
	
	public JLabel bellPathLabel;
	public JTextField bellPathField;
	public JButton bellPathButton;
	
	private JFileChooser jfc;
	private String parentDirectory;
	private File selectedFile;
	
	public void actionPerformed(ActionEvent ae)
	{
		if( ae.getSource() == linebreakCheckBox ) {
			breaklengthSpinner.setEnabled( linebreakCheckBox.isSelected() );
		} else if( ae.getSource() == customBellCheckBox ) {
			bellPathField.setEnabled( customBellCheckBox.isSelected() );
			bellPathButton.setEnabled( customBellCheckBox.isSelected() );
		} else if( ae.getSource() == bellPathButton ) {
			
			if( parentDirectory != null ) {
				jfc = new JFileChooser( parentDirectory );
			} else {
				jfc = new JFileChooser();
			}
			
			if ( jfc.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
				selectedFile = jfc.getSelectedFile();
				parentDirectory = selectedFile.getParent();
				bellPathField.setText( selectedFile.getAbsolutePath() );
			}
		}
	}
	
	public GeneralPanel( Resource r )
	{
		super();
		resource = r;
		
		browserLabel = new JLabel(Messages.getString("Preference.BrowserCommand_Label_Text")); //$NON-NLS-1$
		browserField = new JTextField( resource.getStringValue(Resource.EXTERNAL_BROWSER), 20 );
		
		copyOnSelectLabel = new JLabel(Messages.getString("Preference.CopyOnSelect_Label_Text")); //$NON-NLS-1$
		copyOnSelectCheckBox = new JCheckBox();
		copyOnSelectCheckBox.setSelected( resource.getBooleanValue(Resource.COPY_ON_SELECT) );
		
		clearAfterCopyLabel = new JLabel(Messages.getString("Preference.ClearAfterCopy_Label_Text")); //$NON-NLS-1$
		clearAfterCopyCheckBox = new JCheckBox();
		clearAfterCopyCheckBox.setSelected( resource.getBooleanValue(Resource.CLEAR_AFTER_COPY) );
		
		removeManualLabel = new JLabel(Messages.getString("Preference.RemoveManual_Label_Text")); //$NON-NLS-1$
		removeManualCheckBox = new JCheckBox();
		removeManualCheckBox.setSelected( resource.getBooleanValue(Resource.REMOVE_MANUAL_DISCONNECT) );
		
		linebreakLabel = new JLabel(Messages.getString("Preference.LineBreak_Label_Text")); //$NON-NLS-1$
		linebreakCheckBox = new JCheckBox();
		linebreakCheckBox.setSelected( resource.getBooleanValue(Resource.AUTO_LINE_BREAK) );
		linebreakCheckBox.addActionListener( this );
		
		breaklengthLabel = new JLabel(Messages.getString("Preference.BreakLength_Label_Text")); //$NON-NLS-1$
		breaklengthModel = new SpinnerNumberModel( resource.getIntValue(Resource.AUTO_LINE_BREAK_LENGTH), 1, 512, 1);
		breaklengthSpinner = new JSpinner( breaklengthModel );
		breaklengthSpinner.setEnabled( resource.getBooleanValue(Resource.AUTO_LINE_BREAK) );
		
		customBellLabel = new JLabel(Messages.getString("Preference.CustomBell_Label_Text")); //$NON-NLS-1$
		customBellCheckBox = new JCheckBox();
		customBellCheckBox.setSelected( resource.getBooleanValue( Resource.USE_CUSTOM_BELL) );
		customBellCheckBox.addActionListener( this );
		
		bellPathLabel = new JLabel(Messages.getString("Preference.BellPath_Label_Text")); //$NON-NLS-1$
		bellPathField = new JTextField( resource.getStringValue(Resource.CUSTOM_BELL_PATH), 8 );
		bellPathField.setEnabled( resource.getBooleanValue( Resource.USE_CUSTOM_BELL) );
		bellPathButton = new JButton(Messages.getString("Preference.BellPath_Button_Text")); //$NON-NLS-1$
		bellPathButton.setEnabled( resource.getBooleanValue(Resource.USE_CUSTOM_BELL) );
		bellPathButton.addActionListener( this );
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		add( browserLabel, c );
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		add( browserField, c );
		
		c.gridwidth = 1;
		
		c.gridx = 0;
		c.gridy = 2;
		add( copyOnSelectLabel, c );
		c.gridx = 1;
		add( copyOnSelectCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 3;
		add( clearAfterCopyLabel, c );
		c.gridx = 1;
		add( clearAfterCopyCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 4;
		add( removeManualLabel, c );
		c.gridx = 1;
		add( removeManualCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 5;
		add( linebreakLabel, c );
		c.gridx = 1;
		add( linebreakCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 6;
		add( breaklengthLabel, c );
		c.gridx = 1;
		add( breaklengthSpinner, c );
		
		c.gridx = 0;
		c.gridy = 7;
		add( customBellLabel, c );
		c.gridx = 1;
		add( customBellCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 8;
		add( bellPathLabel, c );
		c.gridx = 1;
		add( bellPathField, c );
		c.gridx = 2;
		add( bellPathButton, c );
	}
}

class ConnectionPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 5706390056762240339L;
	
	private Resource resource;
	
	public JLabel autoReconnectLabel, reconnectTimeLabel, reconnectIntervalLabel;
	public JCheckBox autoReconnectCheckBox; 
	public JSpinner reconnectTimeSpinner, reconnectIntervalSpinner;
	public SpinnerNumberModel reconnectTimeModel, reconnectIntervalModel;
	
	// chitsaou.070726: 防閒置字串
	public JLabel antiIdleLabel, antiIdleTimeLabel, antiIdleStringLabel;
	public JTextField antiIdleStringField;
	public JCheckBox antiIdleCheckBox;
	public JSpinner antiIdleTimeSpinner;
	public SpinnerNumberModel antiIdleModel;
	
	public JLabel socksLabel, socksHostLabel, socksPortLabel;
	public JCheckBox socksCheckBox;
	public JTextField socksHostField, socksPortField;
	
	public void actionPerformed(ActionEvent ae) {
		if( ae.getSource() == autoReconnectCheckBox ) {
			reconnectTimeSpinner.setEnabled( autoReconnectCheckBox.isSelected() );
			reconnectIntervalSpinner.setEnabled( autoReconnectCheckBox.isSelected() );
		} else if( ae.getSource() == antiIdleCheckBox ) {
			antiIdleTimeSpinner.setEnabled( antiIdleCheckBox.isSelected() );
		}
	}
	
	public ConnectionPanel( Resource r )
	{
		super();
		resource = r;
		
		boolean autoReconnect = resource.getBooleanValue(Resource.AUTO_RECONNECT);
		autoReconnectLabel = new JLabel(Messages.getString("Preference.AutoReconnect_Label_Text")); //$NON-NLS-1$
		autoReconnectCheckBox = new JCheckBox();
		autoReconnectCheckBox.setSelected( autoReconnect );
		autoReconnectCheckBox.addActionListener( this );

		reconnectTimeLabel = new JLabel(Messages.getString("Preference.ReconnectTime_Label_Text")); //$NON-NLS-1$
		reconnectTimeModel = new SpinnerNumberModel( resource.getIntValue( Resource.AUTO_RECONNECT_TIME ), 0, 3600, 1);
		reconnectTimeSpinner = new JSpinner( reconnectTimeModel );
		reconnectTimeSpinner.setEnabled( autoReconnect );
		
		reconnectIntervalLabel = new JLabel(Messages.getString("Preference.ReconnectInterval_Label_Text")); //$NON-NLS-1$
		reconnectIntervalModel = new SpinnerNumberModel( resource.getIntValue( Resource.AUTO_RECONNECT_INTERVAL ), 0, 60000, 1);
		reconnectIntervalSpinner = new JSpinner( reconnectIntervalModel);
		reconnectIntervalSpinner.setEnabled( autoReconnect );
		
		antiIdleLabel = new JLabel(Messages.getString("Preference.AntiIdle_Label_Text")); //$NON-NLS-1$
		antiIdleCheckBox = new JCheckBox();
		antiIdleCheckBox.setSelected( resource.getBooleanValue(Resource.ANTI_IDLE) );
		antiIdleCheckBox.addActionListener( this );
		
		antiIdleTimeLabel = new JLabel(Messages.getString("Preference.AntiIdleTime_Label_Text")); //$NON-NLS-1$
		antiIdleModel = new SpinnerNumberModel( resource.getIntValue(Resource.ANTI_IDLE_INTERVAL), 0, 3600, 1);
		antiIdleTimeSpinner = new JSpinner( antiIdleModel );
		antiIdleTimeSpinner.setEnabled( resource.getBooleanValue(Resource.ANTI_IDLE) );
		
		// chitsaou.070726: 防閒置字串
		antiIdleStringLabel = new JLabel(Messages.getString("Preference.AntiIdleString_Label_Text")); //$NON-NLS-1$
		antiIdleStringField = new JTextField( resource.getStringValue(Resource.ANTI_IDLE_STRING), 15 );
		
		socksLabel = new JLabel( Messages.getString("Preference.UsingSocks_Label_Text") );
		socksHostLabel = new JLabel( Messages.getString("Preference.SocksHost_Label_Text") );
		socksPortLabel = new JLabel( Messages.getString("Preference.SocksPort_Label_Text") );
		socksCheckBox = new JCheckBox();
		socksCheckBox.setSelected( resource.getBooleanValue( Resource.USING_SOCKS) );
		socksHostField = new JTextField( resource.getStringValue(Resource.SOCKS_HOST), 20 );
		socksPortField = new JTextField( resource.getStringValue(Resource.SOCKS_PORT), 5 );
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		c.gridx = 0;
		c.gridy = 0;
		add( autoReconnectLabel,c );
		c.gridx = 1;
		add( autoReconnectCheckBox,c );
		
		c.gridx = 0;
		c.gridy = 1;
		add( reconnectTimeLabel,c );
		c.gridx = 1;
		add( reconnectTimeSpinner,c );
		
		c.gridx = 0;
		c.gridy = 2;
		add( reconnectIntervalLabel,c );
		c.gridx = 1;
		add( reconnectIntervalSpinner,c );
		
		c.gridx = 0;
		c.gridy = 3;
		add( antiIdleLabel,c );
		c.gridx = 1;
		add( antiIdleCheckBox,c );
		
		c.gridx = 0;
		c.gridy = 4;
		add( antiIdleTimeLabel,c );
		c.gridx = 1;
		add( antiIdleTimeSpinner,c );
		
		// chitsaou.070726: 防閒置字串
		c.gridx = 0;
		c.gridy = 5;
		add( antiIdleStringLabel, c );
		c.gridx = 1;
		add( antiIdleStringField, c );
		
		c.gridx = 0;
		c.gridy = 6;
		add( socksLabel, c);
		c.gridx = 1;
		add( socksCheckBox, c);

		c.gridx = 0;
		c.gridy = 7;
		add( socksHostLabel, c);
		c.gridx = 1;
		add( socksHostField, c);
		
		c.gridx = 0;
		c.gridy = 8;
		add( socksPortLabel, c);
		c.gridx = 1;
		add( socksPortField, c);
		
	}
}

class ApperancePanel extends JPanel
{
	private static final long serialVersionUID = -2051345281384271839L;
	
	private Resource resource;
	
	// chitsaou.070726: 分頁編號	
	// chitsaou.070726: 顯示捲軸
	public JLabel tabNumberLabel, showScrollBarLabel; 
	public JCheckBox tabNumberCheckBox, showScrollBarCheckBox;
	
	public JLabel systemLookFeelLabel, showToolbarLabel, cursorBlinkLabel;
	public JCheckBox systemLookFeelCheckBox, showToolbarCheckBox, cursorBlinkCheckBox;
	
	public JLabel widthLabel, heightLabel;
	public JSpinner widthSpinner, heightSpinner;
	public SpinnerNumberModel widthModel, heightModel;
	
	public JLabel scrollLabel, terminalRowsLabel, terminalColumnsLabel;
	public JSpinner scrollSpinner, terminalRowsSpinner, terminalColumnsSpinner;
	public SpinnerNumberModel scrollModel, terminalRowsModel, terminalColumnsModel;
	
	public ApperancePanel( Resource r )
	{
		super();
		resource = r;
		
		systemLookFeelLabel = new JLabel(Messages.getString("Preference.SystemLookFeel_Label_Text")); //$NON-NLS-1$
		systemLookFeelCheckBox = new JCheckBox();
		systemLookFeelCheckBox.setSelected( resource.getBooleanValue(Resource.SYSTEM_LOOK_FEEL) );
		
		showToolbarLabel = new JLabel(Messages.getString("Preference.ShowToolbar_Label_Text")); //$NON-NLS-1$
		showToolbarCheckBox = new JCheckBox();
		showToolbarCheckBox.setSelected( resource.getBooleanValue(Resource.SHOW_TOOLBAR) );
		
		cursorBlinkLabel = new JLabel(Messages.getString("Preference.CursorBlink_Label_Text")); //$NON-NLS-1$
		cursorBlinkCheckBox = new JCheckBox();
		cursorBlinkCheckBox.setSelected( resource.getBooleanValue(Resource.CURSOR_BLINK) );
		
		widthLabel = new JLabel(Messages.getString("Preference.WindowWidth_Label_Text")); //$NON-NLS-1$
		widthModel = new SpinnerNumberModel( resource.getIntValue(Resource.GEOMETRY_WIDTH), 0, 4096, 1);
		widthSpinner = new JSpinner( widthModel );
		
		heightLabel = new JLabel(Messages.getString("Preference.WindowHeight_Label_Text")); //$NON-NLS-1$
		heightModel = new SpinnerNumberModel( resource.getIntValue(Resource.GEOMETRY_HEIGHT), 0, 4096, 1);
		heightSpinner = new JSpinner( heightModel );
		
		scrollLabel = new JLabel(Messages.getString("Preference.Scroll_Label_Text")); //$NON-NLS-1$
		scrollModel = new SpinnerNumberModel( resource.getIntValue(Resource.TERMINAL_SCROLLS), 0, 10000, 1);
		scrollSpinner = new JSpinner( scrollModel );
		
		terminalColumnsLabel = new JLabel(Messages.getString("Preference.TerminalColumns_Label_Text")); //$NON-NLS-1$
		terminalColumnsModel = new SpinnerNumberModel( resource.getIntValue(Resource.TERMINAL_COLUMNS), 80, 200, 1);
		terminalColumnsSpinner = new JSpinner( terminalColumnsModel );
		terminalColumnsSpinner.setEnabled( false );
		
		terminalRowsLabel = new JLabel(Messages.getString("Preference.TerminalRows_Label_Text")); //$NON-NLS-1$
		terminalRowsModel = new SpinnerNumberModel( resource.getIntValue(Resource.TERMINAL_ROWS), 24, 200, 1);
		terminalRowsSpinner = new JSpinner( terminalRowsModel );
		terminalRowsSpinner.setEnabled( false );
		
		// chitsaou.070726: 分頁編號
		tabNumberLabel = new JLabel(Messages.getString("Preference.TabNumber_Label_Text")); //$NON-NLS-1$
		tabNumberCheckBox = new JCheckBox();
		tabNumberCheckBox.setSelected( resource.getBooleanValue(Resource.TAB_NUMBER) );
		
		// chitsaou.070726: 顯示捲軸
		// TODO: no restart
		showScrollBarLabel = new JLabel(Messages.getString("Preference.ShowScrollBar_Label_Text")); //$NON-NLS-1$
		showScrollBarCheckBox = new JCheckBox();
		showScrollBarCheckBox.setSelected( resource.getBooleanValue(Resource.SHOW_SCROLL_BAR) );
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		c.gridx = 0;
		c.gridy = 0;
		add( systemLookFeelLabel, c );
		c.gridx = 1;
		add( systemLookFeelCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 1;
		add( showToolbarLabel, c );
		c.gridx = 1;
		add( showToolbarCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 2;
		add( cursorBlinkLabel, c );
		c.gridx = 1;
		add( cursorBlinkCheckBox, c );
		
		c.gridx = 0;
		c.gridy = 3;
		add( widthLabel, c );
		c.gridx = 1;
		add( widthSpinner, c );
		
		c.gridx = 0;
		c.gridy = 4;
		add( heightLabel, c );
		c.gridx = 1;
		add( heightSpinner, c );
		
		c.gridx = 0;
		c.gridy = 5;
		add( scrollLabel, c );
		c.gridx = 1;
		add( scrollSpinner, c );
		
		c.gridx = 0;
		c.gridy = 6;
		add( terminalColumnsLabel, c );
		c.gridx = 1;
		add( terminalColumnsSpinner, c );
		
		c.gridx = 0;
		c.gridy = 7;
		add( terminalRowsLabel, c );
		c.gridx = 1;
		add( terminalRowsSpinner, c );
		
		// chitsaou.070726: 分頁編號
		c.gridx = 0;
		c.gridy = 8;
		add( tabNumberLabel, c );
		c.gridx = 1;
		add( tabNumberCheckBox, c );
		
		// chitsaou.070726: 顯示捲軸
		c.gridx = 0;
		c.gridy = 9;
		add( showScrollBarLabel, c );
		c.gridx = 1;
		add( showScrollBarCheckBox, c );
	}
}

class FontPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1511310874988772350L;
	
	private Resource resource;
	
	public JLabel familyLabel;
	public JComboBox familyCombo;
	
	public JLabel sizeLabel;
	public JSpinner sizeSpinner;
	public SpinnerNumberModel sizeModel;
	
	public JLabel boldLabel, italyLabel, aaLabel;
	public JCheckBox boldCheck, italyCheck, aaCheck;
	
	public JLabel fontVerticalGapLabel, fontHorizontalGapLabel, fontDescentAdjustLabel;
	public JSpinner fontVerticalGapSpinner, fontHorizontalGapSpinner, fontDescentAdjustSpinner;
	public SpinnerNumberModel fontVerticalGapModel, fontHorizontalGapModel, fontDescentAdjustModel;
	
	public void actionPerformed(ActionEvent e)
	{
	}
	
	public FontPanel( Resource r )
	{
		super();
		resource = r;
		
		familyLabel = new JLabel(Messages.getString("Preference.FontFamily_Label_Text")); //$NON-NLS-1$
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] familyList = ge.getAvailableFontFamilyNames();
		familyCombo = new JComboBox( familyList );
		familyCombo.setSelectedItem( resource.getStringValue( Resource.FONT_FAMILY ) );
		
		sizeLabel = new JLabel(Messages.getString("Preference.FontSize_Label_Text")); //$NON-NLS-1$
		sizeModel = new SpinnerNumberModel( resource.getIntValue(Resource.FONT_SIZE), 0, 64, 1);
		sizeSpinner = new JSpinner( sizeModel );
		
		boldLabel = new JLabel(Messages.getString("Preference.FontBold_Label_Text")); //$NON-NLS-1$
		boldCheck = new JCheckBox();
		boldCheck.setSelected( resource.getBooleanValue( Resource.FONT_BOLD ) );
		
		italyLabel = new JLabel(Messages.getString("Preference.FontItaly_Label_Text")); //$NON-NLS-1$
		italyCheck = new JCheckBox();
		italyCheck.setSelected( resource.getBooleanValue( Resource.FONT_ITALY ) );
		
		aaLabel = new JLabel(Messages.getString("Preference.FontAntiAliasing_Label_Text")); //$NON-NLS-1$
		aaCheck = new JCheckBox();
		aaCheck.setSelected( resource.getBooleanValue( Resource.FONT_ANTIALIAS ) );
		
		fontVerticalGapLabel = new JLabel(Messages.getString("Preference.FontVerticalGap_Label_Text")); //$NON-NLS-1$
		fontVerticalGapModel = new SpinnerNumberModel( resource.getIntValue(Resource.FONT_VERTICLAL_GAP), -10, 10, 1);
		fontVerticalGapSpinner = new JSpinner( fontVerticalGapModel );
		
		fontHorizontalGapLabel = new JLabel(Messages.getString("Preference.FontHorizontalGap_Label_Text")); //$NON-NLS-1$
		fontHorizontalGapModel = new SpinnerNumberModel( resource.getIntValue(Resource.FONT_HORIZONTAL_GAP), -10, 10, 1);
		fontHorizontalGapSpinner = new JSpinner( fontHorizontalGapModel );
		
		fontDescentAdjustLabel = new JLabel(Messages.getString("Preference.FontDescentAdjust_Label_Text")); //$NON-NLS-1$
		fontDescentAdjustModel = new SpinnerNumberModel( resource.getIntValue(Resource.FONT_DESCENT_ADJUST), -10, 10, 1);
		fontDescentAdjustSpinner = new JSpinner( fontDescentAdjustModel );
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		
		c.gridx = 0;
		c.gridy = 0;
		add( familyLabel, c );
		c.gridx = 1;
		add( familyCombo, c );
		
		c.gridx = 0;
		c.gridy = 1;
		add( sizeLabel, c );
		c.gridx = 1;
		add( sizeSpinner, c );
		
		c.gridx = 0;
		c.gridy = 2;
		add( boldLabel, c );
		c.gridx = 1;
		add( boldCheck, c );
		
		c.gridx = 0;
		c.gridy = 3;
		add( italyLabel, c );
		c.gridx = 1;
		add( italyCheck, c );
		
		c.gridx = 0;
		c.gridy = 4;
		add( aaLabel, c );
		c.gridx = 1;
		add( aaCheck, c );
		
		c.gridx = 0;
		c.gridy = 5;
		add( fontVerticalGapLabel, c );
		c.gridx = 1;
		add( fontVerticalGapSpinner, c );
		
		c.gridx = 0;
		c.gridy = 6;
		add( fontHorizontalGapLabel, c );
		c.gridx = 1;
		add( fontHorizontalGapSpinner, c );
		
		c.gridx = 0;
		c.gridy = 7;
		add( fontDescentAdjustLabel, c );
		c.gridx = 1;
		add( fontDescentAdjustSpinner, c );
	}

}

public class Preference extends JDialog implements ActionListener, TreeSelectionListener
{
	private static final long serialVersionUID = -1892496769315626958L;
	
	private ZTerm parent;
	private Resource resource;
	
	private JSplitPane jsp;
	private GeneralPanel gp;
	private ConnectionPanel cp;
	private ApperancePanel ap;
	private FontPanel fp;
	private JPanel welcome;
	
	private JTree categoryTree;
	private DefaultMutableTreeNode rootNode, generalNode, connectionNode, appearanceNode, fontNode;
	
	private JButton okButton, cancelButton, applyButton;
	private JPanel controlPanel;
	
	public void submit()
	{
		resource.setValue( Resource.EXTERNAL_BROWSER, gp.browserField.getText() );
		resource.setValue( Resource.COPY_ON_SELECT, gp.copyOnSelectCheckBox.isSelected() );
		resource.setValue( Resource.CLEAR_AFTER_COPY, gp.clearAfterCopyCheckBox.isSelected() );
		resource.setValue( Resource.REMOVE_MANUAL_DISCONNECT, gp.removeManualCheckBox.isSelected() );
		resource.setValue( Resource.AUTO_LINE_BREAK, gp.linebreakCheckBox.isSelected() );
		resource.setValue( Resource.AUTO_LINE_BREAK_LENGTH, gp.breaklengthModel.getValue().toString() );
		resource.setValue( Resource.USE_CUSTOM_BELL, gp.customBellCheckBox.isSelected() );
		resource.setValue( Resource.CUSTOM_BELL_PATH, gp.bellPathField.getText() );
		
		resource.setValue( Resource.AUTO_RECONNECT, cp.autoReconnectCheckBox.isSelected() );
		resource.setValue( Resource.AUTO_RECONNECT_TIME, cp.reconnectTimeModel.getValue().toString() );
		resource.setValue( Resource.AUTO_RECONNECT_INTERVAL, cp.reconnectIntervalModel.getValue().toString() );
		
		resource.setValue( Resource.ANTI_IDLE, cp.antiIdleCheckBox.isSelected() );
		resource.setValue( Resource.ANTI_IDLE_INTERVAL, cp.antiIdleModel.getValue().toString() );
		resource.setValue( Resource.ANTI_IDLE_STRING, cp.antiIdleStringField.getText() );
		
		resource.setValue( Resource.USING_SOCKS, cp.socksCheckBox.isSelected() );
		resource.setValue( Resource.SOCKS_HOST, cp.socksHostField.getText() );
		resource.setValue( Resource.SOCKS_PORT, cp.socksPortField.getText() );
		
		resource.setValue( Resource.SYSTEM_LOOK_FEEL, ap.systemLookFeelCheckBox.isSelected() );
		resource.setValue( Resource.SHOW_TOOLBAR, ap.showToolbarCheckBox.isSelected() );
		resource.setValue( Resource.CURSOR_BLINK, ap.cursorBlinkCheckBox.isSelected() );
		resource.setValue( Resource.GEOMETRY_WIDTH, ap.widthModel.getValue().toString() );
		resource.setValue( Resource.GEOMETRY_HEIGHT, ap.heightModel.getValue().toString() );
		resource.setValue( Resource.TERMINAL_SCROLLS, ap.scrollModel.getValue().toString() );
		resource.setValue( Resource.TERMINAL_COLUMNS, ap.terminalColumnsModel.getValue().toString() );
		resource.setValue( Resource.TERMINAL_ROWS, ap.terminalRowsModel.getValue().toString() );
		resource.setValue( Resource.TAB_NUMBER, ap.tabNumberCheckBox.isSelected() );
		resource.setValue( Resource.SHOW_SCROLL_BAR, ap.showScrollBarCheckBox.isSelected() );
		
		resource.setValue( Resource.FONT_FAMILY, fp.familyCombo.getSelectedItem().toString() );
		resource.setValue( Resource.FONT_SIZE, fp.sizeModel.getValue().toString() );
		resource.setValue( Resource.FONT_BOLD, fp.boldCheck.isSelected() );
		resource.setValue( Resource.FONT_ITALY, fp.italyCheck.isSelected() );
		resource.setValue( Resource.FONT_ANTIALIAS, fp.aaCheck.isSelected() );
		resource.setValue( Resource.FONT_VERTICLAL_GAP, fp.fontVerticalGapModel.getValue().toString() );
		resource.setValue( Resource.FONT_HORIZONTAL_GAP, fp.fontHorizontalGapModel.getValue().toString() );
		resource.setValue( Resource.FONT_DESCENT_ADJUST, fp.fontDescentAdjustModel.getValue().toString() );
		
		// 將修改寫回設定檔
		resource.writeFile();
		
		// XXX: 只有在啟動時會設定 look and feel
		// parent.updateLookAndFeel();
		
		// TODO: 修改後才需要更新
		parent.updateBounds();
		parent.updateToolbar();
		parent.updateSize();
		parent.updateAntiIdleTime();
	}

	private void makeCategoryTree()
	{
		rootNode = new DefaultMutableTreeNode(Messages.getString("Preference.Tree_RootNode_Text")); //$NON-NLS-1$
		generalNode = new DefaultMutableTreeNode(Messages.getString("Preference.Tree_GeneralNode_Text")); //$NON-NLS-1$
		connectionNode = new DefaultMutableTreeNode(Messages.getString("Preference.Tree_ConnectionNode_Text")); //$NON-NLS-1$
		appearanceNode = new DefaultMutableTreeNode(Messages.getString("Preference.Tree_AppearanceNode_Text")); //$NON-NLS-1$
		fontNode = new DefaultMutableTreeNode(Messages.getString("Preference.Tree_FontNode_Text")); //$NON-NLS-1$
		
		rootNode.add( generalNode );
		rootNode.add( connectionNode );
		rootNode.add( appearanceNode );
		rootNode.add( fontNode );
		
		categoryTree = new JTree( rootNode );
		categoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		categoryTree.addTreeSelectionListener( this );
	}
	
	public void actionPerformed(ActionEvent ae ) {
		if( ae.getSource() == okButton ) {
			submit();
			dispose();
		} else if( ae.getSource() == cancelButton ) {
			dispose();
		} else if( ae.getSource() == applyButton ) {
			submit();
		}
	}
	
	public void valueChanged( TreeSelectionEvent tse ) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)categoryTree.getLastSelectedPathComponent();

		if (node == null) return;

		if( node == generalNode ) {
			jsp.setRightComponent( gp );
		} else if( node == connectionNode ) {
			jsp.setRightComponent( cp );
		} else if( node == appearanceNode ) {
			jsp.setRightComponent( ap );
		} else if( node == fontNode ) {
			jsp.setRightComponent( fp );
		} else {
			jsp.setRightComponent( welcome );
		}
		jsp.setDividerLocation( 120 );
	}
	
	public Preference( Resource re, ZTerm pa )
	{
		super( pa, "偏好設定", true ); //$NON-NLS-1$
	
		parent = pa;
		resource = re;
		
		// 因為調整視窗位置、大小不會動態更新 resource, 因此這裡先取得目前大小
		Rectangle r = parent.getBounds();
		resource.setValue( Resource.GEOMETRY_X, (int)r.getX() );
		resource.setValue( Resource.GEOMETRY_Y, (int)r.getY() );
		resource.setValue( Resource.GEOMETRY_WIDTH, (int)r.getWidth() );
		resource.setValue( Resource.GEOMETRY_HEIGHT, (int)r.getHeight() );
		
		makeCategoryTree();
		
		welcome = new JPanel();
		welcome.add( new JLabel(Messages.getString("Preference.Welcome_Label_Text")) ); //$NON-NLS-1$
		
		jsp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, categoryTree, welcome );
		jsp.setOneTouchExpandable(true);
		jsp.setDividerLocation( 120 );
		getContentPane().add( jsp, BorderLayout.CENTER );
		
		gp = new GeneralPanel( resource );
		cp = new ConnectionPanel( resource );
		ap = new ApperancePanel( resource );
		fp = new FontPanel( resource );
		
		okButton = new JButton(Messages.getString("Preference.OK_Button_Text")); //$NON-NLS-1$
		okButton.addActionListener( this );
		
		cancelButton = new JButton(Messages.getString("Preference.Cancel_Button_Text")); //$NON-NLS-1$
		cancelButton.addActionListener( this );
		
		applyButton = new JButton(Messages.getString("Preference.Apply_Button_Text")); //$NON-NLS-1$
		applyButton.addActionListener( this );
		
		controlPanel = new JPanel();
		controlPanel.setLayout( new FlowLayout() );
		controlPanel.add( okButton );
		controlPanel.add( cancelButton );
		controlPanel.add( applyButton );
		
		getContentPane().add( controlPanel, BorderLayout.SOUTH );

		// 處理 Esc 關閉視窗
		KeyStroke escape = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 4476536128384114721L;
			public void actionPerformed( ActionEvent ae ) {
				dispose();
			}};
		getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( escape, "ESCAPE" ); //$NON-NLS-1$
		getRootPane().getActionMap().put( "ESCAPE", escapeAction ); //$NON-NLS-1$
		
		setSize( 620, 450 );
		setLocationRelativeTo( null );
		setVisible( true );
	}
}
