package org.zhouer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils
{
	public static final int FILE = 1;
	public static final int DIRECTORY = 2;
	
	public static boolean copyFile( File oldfile, File newfile )
	{
		FileInputStream fis;
		FileOutputStream fos;
		int len;
		byte[] buf;

		try {
			if( !newfile.exists() ) {
				newfile.createNewFile();
			}

			if( !oldfile.canRead() || !newfile.canWrite() ) {
				return false;
			}

			fis = new FileInputStream( oldfile );
			fos = new FileOutputStream( newfile );

			buf = new byte[4096];

			while( (len = fis.read( buf )) > 0 ) {
				fos.write( buf, 0, len );
			}

			fis.close();
			fos.close();

		} catch ( IOException e ) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static void moveFile( File oldfile, File newfile )
	{
		copyFile( oldfile, newfile );
		oldfile.delete();
	}
	
	public static void checkFile( File f, int type )
	{
		try {
			switch( type ) {
			case FILE:
				if( !f.isFile() ) {
					f.deleteOnExit();
					f.createNewFile();
				}
				break;
			case DIRECTORY:
				if( !f.isDirectory() ) {
					f.deleteOnExit();
					f.mkdir();
				}
				break;
			default:
				System.err.println( "Unknown file type!" );
				break;
			}
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
