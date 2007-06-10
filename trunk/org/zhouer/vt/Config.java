package org.zhouer.vt;

public interface Config
{
	public static final String GEOMETRY_X = "geometry.x";
	public static final String GEOMETRY_Y = "geometry.y";
	public static final String GEOMETRY_WIDTH = "geometry.width";
	public static final String GEOMETRY_HEIGHT = "geometry.height";
	
	public static final String TERMINAL_ROWS = "terminal.rows";
	public static final String TERMINAL_COLUMNS = "terminal.columns";
	public static final String TERMINAL_SCROLLS = "terminal.scrolls";
	
	public static final String ANTI_IDLE = "connect.anti-idle";
	public static final String ANTI_IDLE_INTERVAL = "connect.anti-idle-interval";
	public static final String ANTI_IDLE_STRING = "connect.anti-idle-string";
	
	public static final String AUTO_RECONNECT = "connect.auto-reconnect";
	public static final String AUTO_RECONNECT_TIME = "connect.autoreconnect-time";
	public static final String AUTO_RECONNECT_INTERVAL = "connect.auto-reconnect-interval";

	public static final String FONT_FAMILY = "font.family";
	public static final String FONT_SIZE = "font.size";
	public static final String FONT_BOLD = "font.bold";
	public static final String FONT_ITALY = "font.italy";
	public static final String FONT_ANTIALIAS = "font.antialias";
	public static final String FONT_VERTICLAL_GAP = "font.vertical-gap";
	public static final String FONT_HORIZONTAL_GAP = "font.hoizontal-gap";
	public static final String FONT_DESCENT_ADJUST = "font.descent-adjust";
	
	public static final String EXTERNAL_BROWSER = "external-browser-command";
	public static final String SYSTEM_LOOK_FEEL = "use-system-look-and-feel";
	public static final String COPY_ON_SELECT = "copy-on-select";
	public static final String CLEAR_AFTER_COPY = "clear-after-copy";
	public static final String REMOVE_MANUAL_DISCONNECT = "remove-manual-disconnect";
	public static final String AUTO_LINE_BREAK = "auto-line-break";
	public static final String AUTO_LINE_BREAK_LENGTH = "auto-line-break-length";
	public static final String USE_CUSTOM_BELL = "use-custom-bell";
	public static final String CUSTOM_BELL_PATH = "custom-bell-path";
	public static final String SHOW_TOOLBAR = "show-toolbar";
	
	public boolean getBooleanValue( String key );
	public int getIntValue( String key );
	public String getStringValue( String key );
}
