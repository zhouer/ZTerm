package org.zhouer.vt;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// add by ericsk 2007/11/12
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;


public class User implements KeyListener, MouseListener, MouseMotionListener, InputMethodListener, InputMethodRequests
{
	private Application parent;
	private VT100 vt;
	private Config config;
	private int pressX, pressY, dragX, dragY;
	private boolean isDefaultCursor;
	
	public void keyPressed( KeyEvent e )
	{
		int len;
		byte[] buf = new byte[4];
		
		/*
		System.out.println( "key presses: " + e );
		System.out.println( "key modifier: " + e.getModifiers() );
		*/
		
		// 其他功能鍵
		switch( e.getKeyCode() ) {
		case KeyEvent.VK_UP:
			if( vt.getKeypadMode() == VT100.NUMERIC_KEYPAD ) {
				buf[0] = 0x1b;
				buf[1] = 0x5b;
				buf[2] = 'A';
				len = 3;
			} else {
				buf[0] = 0x1b;
				buf[1] = 0x4f;
				buf[2] = 'A';
				len = 3;
			}
			break;
		case KeyEvent.VK_DOWN:
			if( vt.getKeypadMode() == VT100.NUMERIC_KEYPAD ) {
				buf[0] = 0x1b;
				buf[1] = 0x5b;
				buf[2] = 'B';
				len = 3;
			} else {
				buf[0] = 0x1b;
				buf[1] = 0x4f;
				buf[2] = 'B';
				len = 3;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if( vt.getKeypadMode() == VT100.NUMERIC_KEYPAD ) {
				buf[0] = 0x1b;
				buf[1] = 0x5b;
				buf[2] = 'C';
				len = 3;
			} else {
				buf[0] = 0x1b;
				buf[1] = 0x4f;
				buf[2] = 'C';
				len = 3;
			}
			break;
		case KeyEvent.VK_LEFT:
			if( vt.getKeypadMode() == VT100.NUMERIC_KEYPAD ) {
				buf[0] = 0x1b;
				buf[1] = 0x5b;
				buf[2] = 'D';
				len = 3;
			} else {
				buf[0] = 0x1b;
				buf[1] = 0x4f;
				buf[2] = 'D';
				len = 3;
			}
			break;
		case KeyEvent.VK_INSERT:
			buf[0] = 0x1b;
			buf[1] = 0x5b;
			buf[2] = '2';
			buf[3] = '~';
			len = 4;
			break;
		case KeyEvent.VK_HOME:
			if( vt.getKeypadMode() == VT100.NUMERIC_KEYPAD ) {
				buf[0] = 0x1b;
				buf[1] = 0x5b;
				buf[2] = '1';
				buf[3] = '~';
				len = 4;
			} else {
				buf[0] = 0x1b;
				buf[1] = 0x4f;
				buf[2] = 'H';
				len = 3;					
			}
			break;
		case KeyEvent.VK_PAGE_UP:
			buf[0] = 0x1b;
			buf[1] = 0x5b;
			buf[2] = '5';
			buf[3] = '~';
			len = 4;
			break;
		case KeyEvent.VK_DELETE:
			buf[0] = 0x1b;
			buf[1] = 0x5b;
			buf[2] = '3';
			buf[3] = '~';
			len = 4;
			break;
		case KeyEvent.VK_END:
			if( vt.getKeypadMode() == VT100.NUMERIC_KEYPAD ) {
				buf[0] = 0x1b;
				buf[1] = 0x5b;
				buf[2] = '4';
				buf[3] = '~';
				len = 4;
			} else {
				buf[0] = 0x1b;
				buf[1] = 0x4f;
				buf[2] = 'F';
				len = 3;
			}
			break;
		case KeyEvent.VK_PAGE_DOWN:
			buf[0] = 0x1b;
			buf[1] = 0x5b;
			buf[2] = '6';
			buf[3] = '~';
			len = 4;
			break;
		default:
			len = 0;
		}

		if( len != 0 ) {
			parent.writeBytes( buf, 0, len );
			return;
		}
		
		if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
			// XXX: 在 Mac 上 keyTyped 似乎收不到 esc
			parent.writeByte( (byte) 0x1b );
			return;
		}
		
		if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
			// XXX: ptt 只吃 0x0d, 只送 0x0a 沒用
			parent.writeByte( (byte) 0x0d );
			return;
		}
		
	}
	
	public void keyReleased( KeyEvent e )
	{
	}
	
	public void keyTyped( KeyEvent e )
	{
		// System.out.println( "key typed: " + e );
		
		// 功能鍵，不理會
		if( e.isAltDown() || e.isMetaDown() ) {
			return;
		}
		
		// delete, enter, esc 會在 keyPressed 被處理 
		if( e.getKeyChar() == KeyEvent.VK_DELETE ||
			e.getKeyChar() == KeyEvent.VK_ENTER || 
			e.getKeyChar() == KeyEvent.VK_ESCAPE ) {
			return;
		}
		
		// 一般按鍵，直接送出
		parent.writeChar( e.getKeyChar() );
		e.consume();
	}
	
	public void mouseClicked( MouseEvent e )
	{
		// System.out.println( e );
		
		do {
			if( e.getButton() == MouseEvent.BUTTON1 ) {
				// 左鍵
				if( vt.coverURL( e.getX(), e.getY() ) ) {
					// click
					// 開啟瀏覽器
					String url = vt.getURL( e.getX(), e.getY() );

					if( url.length() != 0 ) {
						parent.openExternalBrowser( url );
					}
					break;
				} else if( e.getClickCount() == 2 ) {
					// double click
					// 選取連續字元
					vt.selectConsequtive( e.getX(), e.getY() );
					vt.repaint();
					break;
				} else if( e.getClickCount() == 3 ) {
					// triple click
					// 選取整行
					vt.selectEntireLine( e.getX(), e.getY() );
					vt.repaint();
					break;
				}
			} else if( e.getButton() == MouseEvent.BUTTON2 ) { 
				// 中鍵
				// 貼上
				if( e.isControlDown() ) {
					// 按下 ctrl 則彩色貼上
					parent.colorPaste();
				} else {
					parent.paste();
				}
				break;
			} else if( e.getButton() == MouseEvent.BUTTON3 ) {
				// 右鍵
				// 跳出 popup menu
				parent.showPopup( e.getX(), e.getY() );
				break;
			}
			
			vt.requestFocus();
			vt.resetSelected();
			vt.repaint();
			
		} while( false );
	}
	
	public void mousePressed( MouseEvent e )
	{
		pressX = e.getX();
		pressY = e.getY();
	}
	
	public void mouseReleased( MouseEvent e )
	{
		boolean meta, ctrl;
		
		// 只處理左鍵
		if( e.getButton() != MouseEvent.BUTTON1 ) {
			return;
		}
		
		meta = e.isAltDown() || e.isMetaDown();
		ctrl = e.isControlDown();

		// select 時按住 meta 表反向，即：
		// 若有 copy on select 則按住 meta 代表不複製，若沒有 copy on select 則按住 meta 代表要複製。
		if( config.getBooleanValue( Config.COPY_ON_SELECT ) == meta ) {
			return;
		}
		
		// ctrl 代表複製時包含色彩。
		if( ctrl ) {
			parent.colorCopy();
		} else {
			parent.copy();
		}
	}
	
	public void mouseEntered( MouseEvent e )
	{
	}
	public void mouseExited( MouseEvent e )
	{
	}
	
	public void mouseMoved( MouseEvent e )
	{
		boolean cover = vt.coverURL( e.getX(), e.getY());
		
		// 只有滑鼠游標需改變時才 setCursor
		if( isDefaultCursor && cover ) {
			vt.setCursor( new Cursor(Cursor.HAND_CURSOR) );
			isDefaultCursor = false;
		} else if ( !isDefaultCursor && !cover ) {
			vt.setCursor( new Cursor(Cursor.DEFAULT_CURSOR) );
			isDefaultCursor = true;
		}
	}
	
	public void mouseDragged( MouseEvent e )
	{
		dragX = e.getX();
		dragY = e.getY();
		
		vt.setSelected( pressX, pressY, dragX, dragY );
		vt.repaint();
	}
	
	// add by ericsk 2007/11/12
	private AttributedString composedTextString = null;
	private AttributedCharacterIterator composedText = null;
	private TextHitInfo caret = null;
	private static final Attribute[] IM_ATTRIBUTES = { TextAttribute.INPUT_METHOD_HIGHLIGHT };	
	private int textOriginX = 10;
    private int textOriginY = 40;
	private transient TextLayout textLayout = null;
    private transient boolean validTextLayout = false;
	private static final AttributedCharacterIterator EMPTY_TEXT = (new AttributedString("")).getIterator();
	
	public void inputMethodTextChanged( InputMethodEvent e) {
		int committedCharacterCount = e.getCommittedCharacterCount();
		AttributedCharacterIterator text = e.getText();
		composedText = null;
		char c;
		if ( text != null ) {
			int toCopy = committedCharacterCount;
			c = text.first();
			while ( toCopy-- > 0 ) {
				parent.writeChar(c);
				c = text.next();
			}
			
			if (text.getEndIndex() - (text.getBeginIndex() + committedCharacterCount) > 0) {
				composedTextString = new AttributedString(text,
			                        text.getBeginIndex() + committedCharacterCount, // skip over committed text
			                        text.getEndIndex(), IM_ATTRIBUTES);
			    // add font information because TextLayout requires it
			    composedTextString.addAttribute(TextAttribute.FONT, vt.getFont());
			    composedText = composedTextString.getIterator();
			}
		}
		e.consume();
	}
	
	public void caretPositionChanged(InputMethodEvent event) {
		caret = event.getCaret();
		event.consume();
	}
	
	public TextHitInfo getCaret() {
		if (composedText == null) {
			return TextHitInfo.trailing(committedText.length() - 1);
		} else if (caret == null) {
		    return null;
		} else {
		    // the caret provided by the input method is relative
		    // to the composed text, so translate it to the entire text
		    return caret.getOffsetHit(getCommittedTextLength());
		}
	}
	
	public Rectangle getCaretRectangle() {
		TextHitInfo caret = getCaret();
	    if (caret == null) {
	    	return null;
	    }
	    return getCaretRectangle(caret);
	}

	public AttributedCharacterIterator getDisplayText() {
		AttributedString string = new AttributedString(committedText.toString());
		if (committedText.length() > 0) {
			string.addAttribute(TextAttribute.FONT, vt.getFont());
		}
		return string.getIterator();
	}
	
	public synchronized TextLayout getTextLayout() {
		if (!validTextLayout) {
			textLayout = null;
		    AttributedCharacterIterator text = getDisplayText();
		    if (text.getEndIndex() > text.getBeginIndex()) {
		    	FontRenderContext context = ((Graphics2D) vt.getGraphics()).getFontRenderContext();
		        textLayout = new TextLayout(text, context);
		    }
		}
		validTextLayout = true;
		return textLayout;
	}
	
	public Rectangle getCaretRectangle(TextHitInfo caret) {
		TextLayout textLayout = getTextLayout();
	    int caretLocation;
	    if (textLayout != null) {
	    	caretLocation = Math.round(textLayout.getCaretInfo(caret)[0]);
	    } else {
	    	caretLocation = 0;
	    }
	    FontMetrics metrics = vt.getGraphics().getFontMetrics();
	    return new Rectangle(textOriginX + caretLocation,
	                         textOriginY - metrics.getAscent(),
	                         0, metrics.getAscent() + metrics.getDescent());
	}
	
	public Rectangle getTextLocation(TextHitInfo offset) {
		// determine the text location in component coordinates
		Rectangle rectangle;
		if (offset == null) {
			// no composed text: return caret for committed text
		    rectangle = getCaretRectangle();
		} else {
		    // composed text: return caret within composed text
		    TextHitInfo globalOffset = offset.getOffsetHit(getCommittedTextLength());
		    rectangle = getCaretRectangle(globalOffset);
		}

		// translate to screen coordinates
		Point location = vt.getLocationOnScreen();
		rectangle.translate(location.x, location.y);

		return rectangle;
	}
	
	public Point getTextOrigin() {
		return new Point(textOriginX, textOriginY);
	}

	public TextHitInfo getLocationOffset(int x, int y) {
		// translate from screen coordinates to coordinates in the text layout
	    Point location = vt.getLocationOnScreen();
	    Point textOrigin = getTextOrigin();
	    x -= location.x + textOrigin.x;
	    y -= location.y + textOrigin.y;

	    // TextLayout maps locations far outside its bounds to locations within.
	    // To avoid false hits, we use it only if it actually contains the location.
	    // We also have to translate the TextHitInfo to be relative to composed text.
	    TextLayout textLayout = getTextLayout();
	    if (textLayout != null && textLayout.getBounds().contains(x, y)) {
	    	return textLayout.hitTestChar(x, y).getOffsetHit(-getCommittedTextLength());
		} else {
			return null;
	    }
	}

	public int getInsertPositionOffset() {
        return getCommittedTextLength();
	}
	
	public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex) {
		AttributedString string = new AttributedString(committedText.toString());
	    return string.getIterator(null, beginIndex, endIndex);
	}

	public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, Attribute[] attributes) {
	    return getCommittedText(beginIndex, endIndex);
	}

	private StringBuffer committedText = new StringBuffer();

	public int getCommittedTextLength() {
		return committedText.length();
	}

	public AttributedCharacterIterator cancelLatestCommittedText(Attribute[] attributes) {
		return null;
	}
	
	public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
		return EMPTY_TEXT;
	}
	// END of adding by ericsk
	
	public User( Application p, VT100 v, Config c )
	{
		parent = p;
		vt = v;
		config = c;
		isDefaultCursor = true;
	}
}
