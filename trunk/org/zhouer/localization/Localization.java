/**
 * @Class: Localization
 */
package org.zhouer.localization;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Localization reads an external file to get localized texts.
 */
public class Localization
{
	/**
	 * Description: the table stores program requests, said keys, to texts with
	 * certain language, said value.
	 */
	protected Properties mapping;
	
	/**
	 * Description: read the external file and then initialize the language
	 * table with those read.
	 */
	public Localization()
	{
		mapping = new Properties();
		InputStream is = Localization.class.getResourceAsStream( localizedFileName() );
		
		try {
			mapping.load( is );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Description: determine the localized file name base on default locale
	 * @return the localized file name
	 */
	private String localizedFileName()
	{
		Locale local = Locale.getDefault();
		
		if( local.equals(Locale.TAIWAN) ) {
			return "localization.tw";
		}
		
		return "localization.us"; 
	}
	
	public String TEXT_ABOUT_ITEM() {
		return mapping.getProperty("TEXT_ABOUT_ITEM");
	}
	
	public String TEXT_BIG5_ITEM() {
		return mapping.getProperty("TEXT_BIG5_ITEM");
	}
	
	public String TEXT_CLOSE_ITEM() {
		return mapping.getProperty("TEXT_CLOSE_ITEM");
	}
	
	public String TEXT_CLOSE_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_CLOSE_ITEM_TOOLTIP");
	}
	
	public String TEXT_COLOR_COPY_ITEM() {
		return mapping.getProperty("TEXT_COLOR_COPY_ITEM");
	}
	
	public String TEXT_COLOR_PASTE_ITEM() {
		return mapping.getProperty("TEXT_COLOR_PASTE_ITEM");
	}
	
	public String TEXT_CONNECT_MENU() {
		return mapping.getProperty("TEXT_CONNECT_MENU");
	}
	
	public String TEXT_CONNECT_TOOLTIP_MENU() {
		return mapping.getProperty("TEXT_CONNECT_MENU_TOOLTIP");
	}
	
	public String TEXT_COPY_ITEM() {
		return mapping.getProperty("TEXT_COPY_ITEM");
	}
	
	public String TEXT_COPY_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_COPY_ITEM_TOOLTIP");
	}
	
	public String TEXT_COPY_LINK_ITEM() {
		return mapping.getProperty("TEXT_COPY_LINK_ITEM");
	}
	
	public String TEXT_EDIT_MENU() {
		return mapping.getProperty("TEXT_EDIT_MENU");
	}
	
	public String TEXT_EDIT_MENU_TOOLTIP	() {
		return mapping.getProperty("TEXT_EDIT_MENU_TOOLTIP	");
	}
	
	public String TEXT_ENCODING_MENU() {
		return mapping.getProperty("TEXT_ENCODING_MENU");
	}
	
	public String TEXT_FAQ_ITEM() {
		return mapping.getProperty("TEXT_FAQ_ITEM");
	}
	
	public String TEXT_HELP_MENU() {
		return mapping.getProperty("TEXT_HELP_MENU");
	}
	
	public String TEXT_HELP_MENU_TOOLTIP() {
		return mapping.getProperty("TEXT_HELP_MENU_TOOLTIP");
	}
	
	public String TEXT_HIDE_TOOLBAR_ITEM() {
		return mapping.getProperty("TEXT_HIDE_TOOLBAR_ITEM");
	}
	
	public String TEXT_OPEN_ITEM() {
		return mapping.getProperty("TEXT_OPEN_ITEM");
	}
	
	public String TEXT_OPEN_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_OPEN_ITEM_TOOLTIP");
	}
	
	public String TEXT_OPTION_MENU() {
		return mapping.getProperty("TEXT_OPTION_MENU");
	}
	
	public String TEXT_OPTION_MENU_TOOLTIP() {
		return mapping.getProperty("TEXT_OPTION_MENU_TOOLTIP");
	}
	
	public String TEXT_PASTE_ITEM() {
		return mapping.getProperty("TEXT_PASTE_ITEM");
	}
	
	public String TEXT_PASTE_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_PASTE_ITEM_TOOLTIP");
	}
	
	public String TEXT_PREFERENCE_ITEM() {
		return mapping.getProperty("TEXT_PREFERENCE_ITEM");
	}
	
	public String TEXT_PREFERENCE_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_PREFERENCE_ITEM_TOOLTIP");
	}
	
	public String TEXT_QUIT_ITEM() {
		return mapping.getProperty("TEXT_QUIT_ITEM");
	}
	
	public String TEXT_QUIT_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_QUIT_ITEM_TOOLTIP");
	}
	
	public String TEXT_REOPEN_ITEM() {
		return mapping.getProperty("TEXT_REOPEN_ITEM");
	}
	
	public String TEXT_REOPEN_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_REOPEN_ITEM_TOOLTIP");
	}
	
	public String TEXT_SHOW_TOOLBAR_ITEM() {
		return mapping.getProperty("TEXT_SHOW_TOOLBAR_ITEM");
	}
	
	public String TEXT_SITE_MANAGER_ITEM() {
		return mapping.getProperty("TEXT_SITE_MANAGER_ITEM");
	}
	
	public String TEXT_SITE_MANAGER_ITEM_TOOLTIP() {
		return mapping.getProperty("TEXT_SITE_MANAGER_ITEM_TOOLTIP");
	}
	
	public String TEXT_SITE_MENU() {
		return mapping.getProperty("TEXT_SITE_MENU");
	}
	
	public String TEXT_SITE_MENU_TOOLTIP() {
		return mapping.getProperty("TEXT_SITE_MENU_TOOLTIP");
	}
	
	public String TEXT_USAGE_ITEM() {
		return mapping.getProperty("TEXT_USAGE_ITEM");
	}
	
	public String TEXT_UTF8_ITEM() {
		return mapping.getProperty("TEXT_UTF8_ITEM");
	}
}
