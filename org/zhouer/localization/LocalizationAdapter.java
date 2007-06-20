/**
 * @Class: LocalizationAdapter
 *
 * @Version: following subversion 24
 */
package org.zhouer.localization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * LocalizationAdapter reads an external file to get localized texts.
 * 
 * @author h45
 */
public class LocalizationAdapter {

	/**
	 * Description: read the external file and then initialize the language
	 * table with those read.
	 */
	public LocalizationAdapter() {
		
		languageTable = new HashMap();

		try
		{	
			BufferedReader br = new BufferedReader( new InputStreamReader( localizedStream() ) );
			String readline, key, value;
			
			// readline equals to null if the file has reached the end.
			while( true )
			{
				readline = br.readLine();
				if( readline == null ) {
					break;
				}
				StringTokenizer st = new StringTokenizer(readline);

				if (st.hasMoreTokens()) {
					key = st.nextToken();
					if (st.hasMoreTokens()) {
						value = st.nextToken();
						languageTable.put(key, value);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Description: the table stores program requests, said keys, to texts with
	 * certain language, said value.
	 */
	protected Map languageTable;

	/**
	 * Description: the subclass shall implement this method saying which file
	 * should be read.
	 * 
	 * @return the localized file name
	 */
	public String localizedFileName() {
        return null;
    }
    
	public InputStream localizedStream() {
		return LocalizationAdapter.class.getResourceAsStream( localizedFileName() );
	}
	
	public String TEXT_ABOUT_ITEM() {
		return (String) languageTable.get("TEXT_ABOUT_ITEM");
	}
	
	public String TEXT_BIG5_ITEM() {
		return (String) languageTable.get("TEXT_BIG5_ITEM");
	}
	
	public String TEXT_CLOSE_ITEM() {
		return (String) languageTable.get("TEXT_CLOSE_ITEM");
	}
	
	public String TEXT_CLOSE_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_CLOSE_ITEM_TOOLTIP");
	}
	
	public String TEXT_COLOR_COPY_ITEM() {
		return (String) languageTable.get("TEXT_COLOR_COPY_ITEM");
	}
	
	public String TEXT_COLOR_PASTE_ITEM() {
		return (String) languageTable.get("TEXT_COLOR_PASTE_ITEM");
	}
	
	public String TEXT_CONNECT_MENU() {
		return (String) languageTable.get("TEXT_CONNECT_MENU");
	}
	
	public String TEXT_CONNECT_TOOLTIP_MENU() {
		return (String) languageTable.get("TEXT_CONNECT_MENU_TOOLTIP");
	}
	
	public String TEXT_COPY_ITEM() {
		return (String) languageTable.get("TEXT_COPY_ITEM");
	}
	
	public String TEXT_COPY_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_COPY_ITEM_TOOLTIP");
	}
	
	public String TEXT_EDIT_MENU() {
		return (String) languageTable.get("TEXT_EDIT_MENU");
	}
	
	public String TEXT_EDIT_MENU_TOOLTIP	() {
		return (String) languageTable.get("TEXT_EDIT_MENU_TOOLTIP	");
	}
	
	public String TEXT_ENCODING_MENU() {
		return (String) languageTable.get("TEXT_ENCODING_MENU");
	}
	
	public String TEXT_FAQ_ITEM() {
		return (String) languageTable.get("TEXT_FAQ_ITEM");
	}
	
	public String TEXT_HELP_MENU() {
		return (String) languageTable.get("TEXT_HELP_MENU");
	}
	
	public String TEXT_HELP_MENU_TOOLTIP() {
		return (String) languageTable.get("TEXT_HELP_MENU_TOOLTIP");
	}
	
	public String TEXT_HIDE_TOOLBAR_ITEM() {
		return (String) languageTable.get("TEXT_HIDE_TOOLBAR_ITEM");
	}
	
	public String TEXT_OPEN_ITEM() {
		return (String) languageTable.get("TEXT_OPEN_ITEM");
	}
	
	public String TEXT_OPEN_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_OPEN_ITEM_TOOLTIP");
	}
	
	public String TEXT_OPTION_MENU() {
		return (String) languageTable.get("TEXT_OPTION_MENU");
	}
	
	public String TEXT_OPTION_MENU_TOOLTIP() {
		return (String) languageTable.get("TEXT_OPTION_MENU_TOOLTIP");
	}
	
	public String TEXT_PASTE_ITEM() {
		return (String) languageTable.get("TEXT_PASTE_ITEM");
	}
	
	public String TEXT_PASTE_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_PASTE_ITEM_TOOLTIP");
	}
	
	public String TEXT_PREFERENCE_ITEM() {
		return (String) languageTable.get("TEXT_PREFERENCE_ITEM");
	}
	
	public String TEXT_PREFERENCE_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_PREFERENCE_ITEM_TOOLTIP");
	}
	
	public String TEXT_QUIT_ITEM() {
		return (String) languageTable.get("TEXT_QUIT_ITEM");
	}
	
	public String TEXT_QUIT_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_QUIT_ITEM_TOOLTIP");
	}
	
	public String TEXT_REOPEN_ITEM() {
		return (String) languageTable.get("TEXT_REOPEN_ITEM");
	}
	
	public String TEXT_REOPEN_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_REOPEN_ITEM_TOOLTIP");
	}
	
	public String TEXT_SHOW_TOOLBAR_ITEM() {
		return (String) languageTable.get("TEXT_SHOW_TOOLBAR_ITEM");
	}
	
	public String TEXT_SITE_MANAGER_ITEM() {
		return (String) languageTable.get("TEXT_SITE_MANAGER_ITEM");
	}
	
	public String TEXT_SITE_MANAGER_ITEM_TOOLTIP() {
		return (String) languageTable.get("TEXT_SITE_MANAGER_ITEM_TOOLTIP");
	}
	
	public String TEXT_SITE_MENU() {
		return (String) languageTable.get("TEXT_SITE_MENU");
	}
	
	public String TEXT_SITE_MENU_TOOLTIP() {
		return (String) languageTable.get("TEXT_SITE_MENU_TOOLTIP");
	}
	
	public String TEXT_USAGE_ITEM() {
		return (String) languageTable.get("TEXT_USAGE_ITEM");
	}
	
	public String TEXT_UTF8_ITEM() {
		return (String) languageTable.get("TEXT_UTF8_ITEM");
	}
}
