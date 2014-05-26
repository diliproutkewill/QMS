/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
 
/**
 * @author ramakumar
 */

import java.util.StringTokenizer;
import java.util.Calendar;

public final class FourSoftExpiry  
{

	// Date string is in  "mm/dd/yyyy"  format.
	private static final String expiryDate = com.foursoft.esupply.common.java.FoursoftWebConfig.EXP_DATE;
	
	public FourSoftExpiry()
	{
	}
	
	public static boolean isLicenseValid()
	{
		if(expiryDate != null && expiryDate.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(expiryDate,"/");
			int mon = Integer.parseInt(st.nextToken());
			int day = Integer.parseInt(st.nextToken());
			int year = Integer.parseInt(st.nextToken());
			
			Calendar expDate = Calendar.getInstance();
			expDate.set(year,mon-1,day);
			Calendar currentDate = Calendar.getInstance();
			
			if(expDate.before(currentDate)) {
				return false;
			}
    	    return true;
		} else {
		  return true;
		}
    }
}

