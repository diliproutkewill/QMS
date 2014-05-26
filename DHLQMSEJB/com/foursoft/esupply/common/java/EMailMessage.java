/*
 * Copyright ©.
 */
package com.foursoft.esupply.common.java;
import java.util.Vector;

public class EMailMessage implements java.io.Serializable
{
	//Variable Declaration
	private String fromAddress;
	private String toAddress;
	private String subject;
	private String message;
//	private Vector attachments;
	private boolean needRollback;
	//Constructor with no arguments
	public EMailMessage()
	{
		needRollback	= false;
	}

//user defined setter/getter methods
	public void setFromAddress(String fromAddress)
	{
		this.fromAddress=fromAddress;
		System.out.println(" EmailMessage.java  Here setted from Address is "+fromAddress);
	}
	public String getFromAddress()
	{
		return this.fromAddress;
	}
	public void setToAddress(String toAddress)
	{
		this.toAddress=toAddress;
		System.out.println("EmailMessage.java Here Setted to Address is "+toAddress);
	}
	public String getToAddress()
	{
		return this.toAddress;
	}
	public void setSubject(String subject)
	{
		this.subject=subject;
	}
	public String getSubject()
	{
		return this.subject;
	}

	public void setMessage(String message)
	{
		this.message=message;
	}
	public String getMessage()
	{
		return this.message;
	}
/*
	public void setAttachments(Vector attachments)
	{
		this.attachments=attachments;
	}
	public Vector getAttachments()
	{
		return this.attachments;
	}
*/
	public void setNeedRollback(boolean neeadRollback)
	{
		this.needRollback = neeadRollback;
	}
	public boolean getNeedRollback()
	{
		return this.needRollback;
	}

	/**
	 * getMessage Details
	 *
	 * @return
	 */
	public String toString() {
/*	    int aCount = 0;
	    if (attachments != null) {
	        aCount = attachments.size();
	    }
*/
	    return new String("Message Details:\n"
	        + "\tTo '" + toAddress + "'\n"
	    	+ "\tFrom '" + fromAddress + "'\n"
	    	+ "\tSubject '" + subject + "'\n"
	        + "\tMessage '" + message);
//	        + "\tWith " + aCount + " attachments.");
	} /* messageDetails() */

}