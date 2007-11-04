package org.zhouer.zterm;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "org.zhouer.zterm.lang.zh_TW"; //$NON-NLS-1$
	
	private static final String EN_BUNDLE_NAME = "org.zhouer.zterm.lang.en_US";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);
	
	private static final ResourceBundle EN_RESOURCE_BUNDLE = ResourceBundle
	.getBundle(EN_BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			if (Locale.getDefault().equals(Locale.TAIWAN)) {
				return RESOURCE_BUNDLE.getString(key);
			} else {
				return EN_RESOURCE_BUNDLE.getString(key);
			}
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
