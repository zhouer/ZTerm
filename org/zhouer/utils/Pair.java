package org.zhouer.utils;

public class Pair {
	
	protected Object first, second;

	
	public Object getFirst()
	{
		return first;
	}
	
	public Object getSecond()
	{
		return second;
	}
	
	public void setFirst( Object f )
	{
		first = f;
	}
	
	public void setSecond( Object s )
	{
		second = s;
	}
	
	public Pair()
	{
		first = second = null;
	}
	
	public Pair( Object f, Object s )
	{
		first = f;
		second = s;
	}
}
