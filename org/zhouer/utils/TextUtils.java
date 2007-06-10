package org.zhouer.utils;

import java.util.HashMap;
import java.util.Iterator;

public class TextUtils
{
	public static String fmt( String str, int width )
	{
		char c, c2;
		int count = 0;
		StringBuffer sb = new StringBuffer();
		
		for( int i = 0; i < str.length(); i++ ) {
			c = str.charAt( i );
			
			if( c > 127 ) {
				count += 2;
				sb.append( c );
			} else if( c == 0x09 ) {
				// 遇到 tab 時加四
				// FIXME: magic number
				count += 4;
				sb.append( c );
			} else if( c == 0x0a || c == 0x0d ) {
				// 遇到換行
				c = 0x0d;
				count = 0;
				sb.append( c );
			} else {
				count++;
				sb.append( c );
			}
			
			if( count > width ) {

				// TODO: 斷行的演算法要再加強，要避頭點之類的
				// FIXME: 用 c > 127 判斷是否為中文
				// 這裡的作法類似於 fmt - width
				// 中文到處皆可換行，英文只在空白處換行
				for( int j = sb.length() - 1; j >= 0; j-- ) {
					c2 = sb.charAt( j );
					if( c2 > 127 ) {
						// 在這之前都沒有遇到中文字，
						// 找到後用目前的寬度，減去中文字元後面的字元寬度，
						// 如果剩下寬度大於 width，就在中文前換行。
						// （注意處理中文時 count 要多算一）
						if ( count - (sb.length() - j - 1) > width ) {
							count = sb.length() - j + 1;
							sb.insert( j, (char)0x0d );
						} else {
							count = sb.length() - j - 1;
							sb.insert( j + 1, (char)0x0d );
						}
						break;
					} else if( c2 == ' ' ) {
						// 把遇到的第一個空白取代為換行。
						sb.setCharAt( j, (char)0x0d );
						count = sb.length() - j;
						break;
					} else if( c2 == (char)0x0d ) {
						// 整行都遇不到中文或空白，沒辦法處理。
						break;
					}
				}
			}
		}
		
		return new String( sb );
	}
	
	public static String BSStringToString( String s )
	{
		char c;
		StringBuffer result = new StringBuffer();
		String digits = "0123456789abcdef";
		
		for( int i = 0; i < s.length(); i++ ) {
			if( s.charAt(i) == '\\' ) {
				if( (i + 1) < s.length() && s.charAt(i + 1) == '\\' ) {
					c = '\\';
					i += 1;
				} else if( (i + 3) < s.length() && s.charAt(i + 1) == 'x' ) {
					c = (char) (digits.indexOf( s.charAt(i + 2) ) * 16 + digits.indexOf( s.charAt(i + 3) ));
					i += 3;
				} else {
					// XXX: 暫時先這樣
					c = '\\';
				}
			} else {
				c = s.charAt(i);
			}
			result.append( c );
		}
		
		return new String(result);
	}
	
	public static HashMap getCsvParameters( String s )
	{
		HashMap hm = new HashMap();
		Iterator iter = CSV.parse( s ).iterator();
		
		String ss;
		int i;
		
		while( iter.hasNext() ) {
			ss = (String)iter.next();
			i = ss.indexOf( '=' );
			hm.put( ss.substring( 0, i ), ss.substring( i + 1 ) );
		}
		
		return hm;
	}
}
