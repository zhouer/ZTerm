package org.zhouer.zterm;

import java.io.IOException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

public class HtmlDialog extends JDialog
{
	private static final long serialVersionUID = -2801813211379571475L;

	public HtmlDialog( ZTerm pa, String title, URL url )
	{
		super( pa, title );
		
		try {
			JEditorPane htmlPane = new JEditorPane( url );
			htmlPane.setEditable( false );
			getContentPane().add( new JScrollPane( htmlPane ) );

		} catch (IOException e) {
			e.printStackTrace();
		}

		setSize( 640, 480 );
		setLocationRelativeTo( null );
		setVisible( true );
	}

}
