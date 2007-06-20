/**
 * @Class: LocalizationTW
 *
 * @Version: following subversion 24
 */
package org.zhouer.localization;

/**
 * LocalizationTW defines the file name of localization in TW.
 *
 * @author h45 
 */
public class LocalizationTW extends LocalizationAdapter {

	private final static String LOCALIZATION_TW = "localization.tw"; 
	
	/* (non-Javadoc)
	 * @see org.zhouer.definition.LocalizationAdapter#localizedFileName()
	 */
	public String localizedFileName() {
		return LOCALIZATION_TW;
	}

}
