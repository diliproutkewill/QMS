package com.foursoft.etrans.setup.currency.dob;

import java.io.Serializable;
import java.sql.Timestamp;

public class CurrencyConversionDOB implements Serializable
{
	public String convFrom     = null;
	public String convTo       = null;
	public double exchangeBuy  = 0.0;
	public double exchangeSell = 0.0;
	public Timestamp date      = null;
	
	
	public void setConvFrom(String convFrom)
	{
		this.convFrom= convFrom;
	}
	public void setConvTo(String convTo)
	{
       this.convTo= convTo;
	}
	public void setExchangeBuy(double exchangeBuy)
	{
		this.exchangeBuy = exchangeBuy;
	}
	public void setExchangeSell(double exchangeSell)
	{
		this.exchangeSell = exchangeSell;
	}
	public void setDate(Timestamp date)
	{
		this.date = date;
	}

	public String getConvFrom()
	{
		return convFrom;
	}
	public String getConvTo()
	{
		return convTo;
	}
	public double getExchangeBuy()
	{
		return exchangeBuy;
	}
	public double getExchangeSell()
	{
		return exchangeSell;
	}
	public Timestamp getDate()
	{
		return date;
	}
}

