package com.foursoft.esupply.accesscontrol.util;

import sun.misc.*;
import java.io.UnsupportedEncodingException;

public class Encryptor
{
	public static String encrypt(String encString) throws java.io.UnsupportedEncodingException
	{
		byte[] b	= encString.getBytes("UTF8");
		
		BASE64Encoder	encoder				= new BASE64Encoder();
		String 			encryptedString		= encoder.encode(b);
		return encryptedString;
	}
	
}
