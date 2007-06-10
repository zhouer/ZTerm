package org.zhouer.utils;

import java.io.IOException;
import java.io.InputStream;

public class Convertor
{
	private byte[] big5bytes;
	private byte[] ucs2bytes;
	private char[] ucs2chars;
	
	public static boolean isWideChar( char c )
	{
		// TODO: 應該改用更好的寫法判斷是否為寬字元，暫時假設非 ASCII 字元皆為寬字元。
		if( c > 127 )
			return true;
		return false;
	}
	
	/**
	 *  把 jar 中的檔案讀進 byte array
	 *  @param	name 檔名
	 *  @param	b 目地 array
	 *  @return	檔案長度
	 */
	public static int readFile( String name, byte[] b )
	{
		int size = 0, len;
		InputStream is = Convertor.class.getResourceAsStream( name );
		
		try {
			// XXX: 本來應該 is.read( b ) 就可以才對，
			// 但是我發現在包裝成 jar 以後就會讀不完整，一定要這樣讀。
			while( true ) {
				len = is.read( b, size, b.length - size );
				if( len == -1 ) {
					break;
				}
				size += len;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return size;
	}
	
	public char bytesToChar( byte[] b, int from, int limit, String encoding )
	{
		// FIXME: magic number
		if( encoding.equalsIgnoreCase("Big5") ) {
			return big5BytesToChar( b, from, limit );
		} else if( encoding.equalsIgnoreCase("UTF-8") ) {
			return utf8BytesToChar( b, from, limit );
		} else {
			// TODO: 其他的編碼
			System.out.println("Unknown Encoding: " + encoding );
			return 0;
		}
	}
	
	public byte[] charToBytes( char c, String encoding )
	{
		if( encoding.equalsIgnoreCase("Big5") ) {
			return charToBig5Bytes( c );
		} else if( encoding.equalsIgnoreCase("UTF-8") ) {
			return charToUTF8Bytes( c );
		} else {
			// TODO: 其他的編碼
			System.out.println("Unknown Encoding: " + encoding );
			return null;			
		}
	}
	
	public boolean isValidMultiBytes( byte[] b, int from, int limit, String encoding )
	{
		// FIXME: magic number
		if( encoding.equalsIgnoreCase("Big5") ) {
			return Convertor.isValidBig5( b, from, limit );
		} else if( encoding.equalsIgnoreCase("UTF-8") ) {
			return Convertor.isValidUTF8( b, from, limit );
		} else {
			// TODO: 其他的編碼
			System.out.println("Unknown Encoding: " + encoding );
			return true;
		}
	}
	
	public static boolean isValidBig5( byte[] b, int offset, int limit )
	{
		// TODO: 改為較嚴謹的 Big5 規格
		if( b[offset] >= 0 ) {
			return limit == 1;
		} else {
			return limit == 2;
		}
	}
	
	public char big5BytesToChar( byte[] buf, int offset, int limit )
	{
		if( limit == 1 ) {
			return (char)buf[offset];
		}
		
		// signed to unsigned
		int i1 = (buf[offset] < 0 ? 256 : 0) + (int)buf[offset];
		int i2 = (buf[offset + 1] < 0 ? 256 : 0) + (int)buf[offset + 1];

		// 表是從 big5 0x8140 開始建的
		int shift = ((i1 << 8) | i2) - 0x8140;
		
		// 超過 big5 的範圍
		if( shift < 0 || shift * 2 + 1 >= ucs2bytes.length ) {
			return '?';
		}
		
		i1 = (ucs2bytes[shift * 2] < 0 ? 256 : 0) + (int)ucs2bytes[shift * 2];
		i2 = (ucs2bytes[shift * 2 + 1] < 0 ? 256 : 0) + (int)ucs2bytes[shift * 2 + 1];
		
		return (char) (i1 << 8 | i2);
	}
	
	public byte[] charToBig5Bytes( char c )
	{
		byte[] b;

		// XXX: 假設非 ASCII 都是 2 bytes
		if( c < 0x80 ) {
			b = new byte[1];
			b[0] = (byte) c;
			return b;
		} else {
			b = new byte[2];
			// 表是從 Unicode 0x80 開始建的
			b[0] = big5bytes[ (c - 0x80) * 2 ];
			b[1] = big5bytes[ (c - 0x80) * 2 + 1 ];
		}
		
		return b;
	}
	
	public byte[] StringToBig5Bytes( String str )
	{
		int count = 0;
		byte[] buf;
		byte[] tmp = new byte[str.length() * 2];
		byte[] result;
		
		for( int i = 0; i < str.length(); i++ ) {
			buf = charToBig5Bytes( str.charAt(i) );
			for( int j = 0; j < buf.length; j++ ) {
				tmp[count++] = buf[j];
			}
		}
		
		result = new byte[count];
		for( int i = 0; i < count; i++ ) {
			result[i] = tmp[i];
		}
		
		return result;
	}
	
	public String big5BytesToString( byte[] buf, int offset, int limit )
	{
		StringBuffer sb = new StringBuffer();
		
		for( int i = 0; i < limit; i++ ) {
			if( i + 1 < limit && buf[offset + i] < 0 ) {
				sb.append( big5BytesToChar( buf, offset + i, 2) );
				i += 1;
			} else {
				sb.append( (char)buf[offset + i] );
			}
		}
		
		return new String(sb);
	}
	
	public static boolean isValidUTF8( byte[] b, int offset, int limit )
	{
		// TODO: 改為較嚴謹的 UTF-8 規格
		if( b[offset] >= 0 ) {
			return limit == 1;
		} else if( b[offset] >= -64 && b[offset] <= -33 ) {
			return limit == 2;
		} else if( b[offset] >= -32 && b[offset] <= -17 ) {
			return limit == 3;
		} else if( b[offset] >= -16 && b[offset] <= -9 ) {
			return limit == 4;
		} else {
			// XXX: 不是合法的 UTF-8
			return true;
		}
	}
	
	public char utf8BytesToChar( byte[] buf, int offset, int limit )
	{
		char c;
		
		if( limit == 1 ) {
			c = (char)buf[0];
		} else if( limit == 2 ) {
			c = (char)(buf[0] & 0x1f);
			c <<= 6;
			c |= (char)(buf[1] & 0x3f);
		} else if( limit == 3 ) {
			c = (char) (buf[0] & 0xf);
			c <<= 6;
			c |= (char)(buf[1] & 0x3f);
			c <<= 6;
			c |= (char)(buf[2] & 0x3f);
		} else {
			c = (char) (buf[0] & 0x7);
			c <<= 6;
			c |= (char)(buf[1] & 0x3f);
			c <<= 6;
			c |= (char)(buf[2] & 0x3f);
			c <<= 6;
			c |= (char)(buf[3] & 0x3f);
		}
		
		return c;
	}
	
	public byte[] charToUTF8Bytes( char c )
	{
		byte[] b;
		
		if( c >= 0 && c <= 0x7f ) {
			b = new byte[1];
			b[0] = (byte)c;
		} else if( c >= 0x80 && c <= 0x7ff ) {
			b = new byte[2];
			b[0] = (byte) (0xc0 | (c >> 6));
			b[1] = (byte) (0x80 | (c & 0x3f));
		} else if( c >= 0x800 && c <= 0xffff ) {
			b = new byte[3];
			b[0] = (byte) (0xe0 | (c >> 12));
			b[1] = (byte) (0x80 | ((c >> 6) & 0x3f));
			b[2] = (byte) (0x80 | (c & 0x3f));
		} else if( c >= 0x10000 && c <= 0x10ffff ) {
			b = new byte[4];
			b[0] = (byte) (0xf0 | (c >> 18));
			b[1] = (byte) (0x80 | ((c >> 12) & 0x3f));
			b[2] = (byte) (0x80 | ((c>> 6) & 0x3f));
			b[3] = (byte) (0x80 | (c & 0x3f));
		} else {
			System.out.println("Error converting char to UTF-8 bytes.");
			b = null;
		}
		
		return b;
	}
	
	public Convertor()
	{
		int i1, i2;
		
		ucs2bytes = new byte[64 * 1024];
		big5bytes = new byte[128 * 1024];
		
		readFile( "conv/ucs2.txt", ucs2bytes );
		readFile( "conv/big5.txt", big5bytes );
		
		ucs2chars = new char[ ucs2bytes.length / 2 ];
		
		// 把讀進來的 ucs2 bytes 處理成標準的 (ucs2) char
		for( int i = 0; i < ucs2bytes.length; i += 2) {
			i1 = (ucs2bytes[i] < 0 ? 256 : 0) + (int)ucs2bytes[i];
			i2 = (ucs2bytes[i + 1] < 0 ? 256 : 0) + (int)ucs2bytes[i + 1];
			ucs2chars[i / 2] = (char)( i1 << 8 | i2 );
		}
	}
}
