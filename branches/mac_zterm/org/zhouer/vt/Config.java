package org.zhouer.vt;

public interface Config
{
	public static final String TERMINAL_ROWS = "terminal.rows";
	public static final String TERMINAL_COLUMNS = "terminal.columns";
	public static final String TERMINAL_SCROLLS = "terminal.scrolls";
	
	public static final String FONT_FAMILY = "font.family";
	public static final String FONT_SIZE = "font.size";
	public static final String FONT_BOLD = "font.bold";
	public static final String FONT_ITALY = "font.italy";
	public static final String FONT_ANTIALIAS = "font.antialias";
	public static final String FONT_VERTICLAL_GAP = "font.vertical-gap";
	public static final String FONT_HORIZONTAL_GAP = "font.hoizontal-gap";
	public static final String FONT_DESCENT_ADJUST = "font.descent-adjust";
	
	public static final String CURSOR_BLINK = "cursor.blink";
	public static final String CURSOR_SHAPE = "cursor.shape";
	
	public static final String COPY_ON_SELECT = "copy-on-select";
	public static final String CLEAR_AFTER_COPY = "clear-after-copy";
	public static final String AUTO_LINE_BREAK = "auto-line-break";
	public static final String AUTO_LINE_BREAK_LENGTH = "auto-line-break-length";
	
	public boolean getBooleanValue( String key );
	public int getIntValue( String key );
	public String getStringValue( String key );
}
