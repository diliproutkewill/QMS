package com.foursoft.esupply.common.util;
import java.util.*;

public class Test 
{
	public Test()
	{
	}
	
	public static void main(String[] args)
	{
		Date dd = new Date();
		Timer t = new Timer();
		TestTimer tt = new TestTimer(); 
		t.schedule(tt,dd,2000);
		
		
	}
	
}