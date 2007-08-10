import org.zhouer.zterm.Session;
import org.zhouer.zterm.ZTerm;

public class WindowsZTerm extends ZTerm
{
	public void bell( Session s )
	{
		super.bell(s);
		new WindowsUtils().flash( this, true );
	}
	
	public static void main( String args[] )
	{
		new WindowsZTerm();
	} 
}
