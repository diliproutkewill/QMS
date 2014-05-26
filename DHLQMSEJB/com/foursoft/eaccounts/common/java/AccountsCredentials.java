/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.eaccounts.common.java;

import java.util.Date;
import java.sql.Timestamp;

/**
 * File			: AccountsCredentials.java
 * sub-module 	: Common
 * module 		: eaccounts
 * 
 * This holds the credentials related to EAccounts
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	30-10-2001
 * 
 * Modified history
 * Amit Parekh		10.05.2002		Two more attributes added to the class
 *									'module' and 'acctYear' as needed in login info
 */
public class AccountsCredentials implements java.io.Serializable
{
	/**
	* default empty constrctor
	*/
	public AccountsCredentials()
	{
	}
	
	/**
	* used to get the Back date allowed
	*/
	public Timestamp getBackDate()
	{
		return backDate;
	}
	
	/**
	* used to set the Back date allowed
	*/
	public void setBackDate(Timestamp backDate)
	{
		this.backDate	= backDate;
	}	
	
	/**
	* used to get the Book Id
	*/
    public long getBookId()
    {
		return bookId;
    }

	/**
	* used to set the Book Id
	*/
	public void setBookId(long bookId)
	{
		this.bookId = bookId;
	} 
	
	/**
	* Used to get the Books Status
	*/
    public String getBooksStatus()
    {
		return booksStatus;
    }

	/**
	* Used to set the Books Status
	*/
	public void setBooksStatus(String booksStatus)
	{
		this.booksStatus = booksStatus;
	}
	
	/**
	* used to get the Financial year end date
	*/
	public Timestamp getFinancialYearTo()
	{
		return financialYearTo;
	}

	/**
	* used to set the Financial year end date
	*/
	public void setFinancialYearTo(Timestamp financialYearTo)
	{
		this.financialYearTo = financialYearTo;
	}
	
	/**
	* Used to get the books begening date
	*/
	
	public Timestamp getBooksBeginingFrom()
	{
		return booksBeginingFrom;
	}
	/**
	* Used to set the books begening date
	*/
	
	public void setBooksBeginingFrom(Timestamp booksBeginingFrom)
	{
		this.booksBeginingFrom = booksBeginingFrom;
	}
	
	/**
	* used to get the no of back days allowed
	*/
	public int getBackDays()
	{
		return backDays;
	}

	/**
	* used to set the no of back days allowed
	*/
	public void setBackDays(int backDays)
	{
		this.backDays = backDays;
	}
	
	/**
	* used to get the Transaction Id Status
	*/
	public String getTransactionIdStatus()
	{
		return transactionIdStatus;
	}

	/**
	* used to set the Transaction Id Status
	*/
	public void setTransactionIdStatus(String transactionIdStatus)
	{
		this.transactionIdStatus = transactionIdStatus;
	}	
	
	/**
	* used to get the Account id Status
	*/
	public String getAcctIdStatus()
	{
		return acctIdStatus;
	}
	
	/**
	* used to set the Account id Status
	*/
	public void setAcctIdStatus(String acctIdStatus)
	{
		this.acctIdStatus = acctIdStatus;
	}	
	
	public String getAcctYear()
	{
		return acctYear;
	}
	public void setAcctYear(String acctYear)
	{
		this.acctYear = acctYear;
	}	

	public String getModule()
	{
		return module;
	}
	public void setModule(String module)
	{
		this.module = module;
	}
	// Used to set the Acct Company Id (TERMINALID OR WHID)	
	public void setAcctCompanyId(String acctCompanyId)
	{
		this.acctCompanyId = acctCompanyId;
	}
	// Used to get the AcctCompanyId
	public String getAcctCompanyId()
	{
		return acctCompanyId;
	}
	/**
	* ovverriding the toString() method
	*/
	public String toString()
	{
		StringBuffer sb = new StringBuffer(100);
		sb.append("Acct CompanyId : "+acctCompanyId+" - ");
		sb.append("Back date : "+backDate+" - ");
		sb.append("Book Id : "+bookId+" - ");
		sb.append("Books status : "+booksStatus+" - ");
		sb.append("Financial year to : "+financialYearTo+" - ");
		sb.append("Books begening : "+booksBeginingFrom+" - ");
		sb.append("Back days : "+backDays+" - ");
		sb.append("TransactionId status : "+transactionIdStatus+" - ");
		sb.append("AcctId Status : "+acctIdStatus+" - ");
		sb.append("acct Year : "+acctYear+"-");
		sb.append("module : "+module);
		
		return sb.toString();
				
	}
		
	private Timestamp	backDate			= null;		//	back date upto which user able to access	
 	private long		bookId				= 0L;		//	book id
	private String		booksStatus			= "";		//	book status
	private Timestamp	financialYearTo		= null;		//	financial year ending date
	private Timestamp	booksBeginingFrom	= null;		//	books begening date
	private int			backDays			= 0;		//	no of back days allowed	
	private String		transactionIdStatus	= "";		//	status	
	private String		acctIdStatus		= "";		//	status of the account id
	private	String		acctYear			= "";		//	The Accounting Year of the Book
	private	String		module				= "";		//	The Module to which this Terminal is related
	private String		acctCompanyId		= "";		// 	The accounting company (WHID or CompanyId for e-Log and 
	private Timestamp	booksbackDate		= null;		//	back date upto which user able to access	
}