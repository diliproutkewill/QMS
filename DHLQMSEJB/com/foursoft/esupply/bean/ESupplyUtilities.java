package com.foursoft.esupply.bean;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Date;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Calendar;
import java.text.DateFormat;
//import com.foursoft.etrans.common.bean.Address;
//import com.foursoft.eaccounts.invoice.bean.InvoiceMasterDataObject;
// com.foursoft.esupply.bean.ESupplyUtilities

public class ESupplyUtilities implements java.io.Serializable {

  
  
  	// Method returns 'Date String' in DD/MM/YYYY format after taking a 'Timestamp' as an argument
	public String getCurrentDateInDDMMYYYY() {

		DateFormat dateformat=java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.UK);
		Calendar calendar = dateformat.getCalendar();
		
		// Start Formating Voucher Date
		calendar.setTime(new java.util.Date());
		String dd="",mm="", strTodaysDate="";
			
		int d = calendar.get(java.util.Calendar.DAY_OF_MONTH);
		int m = calendar.get(java.util.Calendar.MONTH) + 1;
		int yyyy = calendar.get(java.util.Calendar.YEAR);
			
		if(d < 10) {
			dd = "0" + d;
		} else {	
			dd = d + "";
		}

		if(m < 10) {	
			mm = "0" + m;
		} else {
			mm = m + "";
		}

		strTodaysDate = dd+"/"+mm+"/"+yyyy;
		// Finish Formating  Date
		return strTodaysDate;		
		
	} // end getCurrentDateInDDMMYYYY()




  	// Method returns a 'Timestamp' object after taking a 'Date String' in DD/MM/YYYY format as an argument
	// Its also take a flag as the hour of the day in which the date is to be formatted. START or END
	public Timestamp getTimestampFromDDMMYYYY(String strDateInDDMMYYYY, String strTimeOfTheDay) {
		
		DateFormat dateformat=java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.UK);
		Calendar calendar = dateformat.getCalendar();
		int dd=0, mm=0, yyyy=0;
	
		try {
			if(strDateInDDMMYYYY.length() == 10) {	// Its a valid date
				dd   = Integer.parseInt(strDateInDDMMYYYY.substring(0,2));
				mm   = Integer.parseInt(strDateInDDMMYYYY.substring(3,5));
				yyyy = Integer.parseInt(strDateInDDMMYYYY.substring(6,10));
			}
		} catch(NumberFormatException nfe) {
			System.out.println("Error formatting date in UtilityBean. Date is not OK. Returning the current system date as Timestamp");
			System.out.println("Description : "+ nfe);
			return new java.sql.Timestamp(new java.util.Date().getTime());
		}
		
		calendar.clear();

		if(strTimeOfTheDay.equals("START")) {
			calendar.set(yyyy, mm-1, dd,  0,  0,  0);
		}
		if(strTimeOfTheDay.equals("END")) {
			calendar.set(yyyy, mm-1, dd, 23, 59, 59);
		}
		
		java.sql.Timestamp timestamp = new java.sql.Timestamp( calendar.getTime().getTime() );
		return timestamp;
		
	} // end getTimestampFromDDMMYYYY()
		

	
 	// Method returns a 'Date String' after taking a 'java.util.Date' OR A 'java.sql.Timestamp' object
	// as an argument. It OMITS the time part of the date and only return the string in 'DD/MM/YYYY' format.

	public String getDDMMYYYYDateFromDateObj(java.util.Date date) {

		DateFormat dateformat = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT,java.util.Locale.UK);
		Calendar calendar = dateformat.getCalendar();
		int d=0, m=0, yyyy=0;
		String dd="", mm="";
		
		long lMilliseconds = 0L;
		
		if(date instanceof java.util.Date) {
			calendar.setTime(date);
		}
		if(date instanceof java.sql.Timestamp) {
			calendar.setTime( new java.util.Date(date.getTime()) );
		}
			
		d = calendar.get(java.util.Calendar.DAY_OF_MONTH);
		m = calendar.get(java.util.Calendar.MONTH) + 1;
		yyyy = calendar.get(java.util.Calendar.YEAR);
			
		if(d < 10) {
			dd = "0" + d;
		} else {	
			dd = d + "";
		}

		if(m < 10) {	
			mm = "0" + m;
		} else {
			mm = m + "";
		}

		String datestring = dd+"/"+mm+"/"+yyyy;
		return datestring;

	} // end method getDDMMYYYYDateFromDateObj()
		
		

	// This method returns the difference in number of days between the two dates. The dates are passed
	// in DD/MM/YYYY format and the diference is returned ans a java 'long' type

	public long getDaysDifference(String strFromDate, String strToDate) {

		long lMilliSecondsInOneDay = 86400000;

		java.sql.Timestamp tsFromDate = getTimestampFromDDMMYYYY(strFromDate, "START");
		java.sql.Timestamp tsToDate   = getTimestampFromDDMMYYYY(strToDate, "START");

		long lDifferenceInMilliseconds = tsToDate.getTime() - tsFromDate.getTime();

		long iDifferenceInNumOfDays    = lDifferenceInMilliseconds / lMilliSecondsInOneDay;

		return iDifferenceInNumOfDays;

	} // end method getDaysDifference
	
	
	// This method is for FORMATTING THE ACCOUNT NAME according to INDENTATIONS
	// based on the length of the  ACCTID and finally to be displayed in a Select Box.
	// ATTENTION USERS : As the name suggests, this methods expects a valid account id to be present
	// for calculating the spaces. Please check your Acct Id falg, and then only call this method for
	// formatting. This mathod counts 10 spaces for the acctid and put one more empty space at the end
	// before appending the acctname

	public String formatAcctNameWithAcctId(String strAcctId, String strAcctName) {
	
		//Length of AcctId is to be fixed maximum at 10 (in words TEN) characters;

		String strFormattedAcctName = "";
		StringBuffer sbSpaces = new StringBuffer("&nbsp;");	// Initialize with one blank space
	
		int iLengthOfAcctId = strAcctId.length();
		int iNumOfSpacesAtBackOfAcctId = 10 - iLengthOfAcctId;
		
		for(int i=1; i <= iNumOfSpacesAtBackOfAcctId; i++) {	// SPACES for
			sbSpaces.append("&nbsp;");
		} // end SPACES for
		
		strFormattedAcctName = strAcctId + sbSpaces.toString() + strAcctName;

		return strFormattedAcctName;

	} // end method formatAcctNameWithAcctId()
		
	
		
	public Timestamp processModifiedTransactionDate( String orgDate,	String newDate ) {
		
		Timestamp	tsTransactionDate	=	null;
		
			if( newDate.equals( orgDate )==false ) {
				
				tsTransactionDate	=	getTimestampFromDDMMYYYY( newDate, "START" );

				long	timeOnDayStart	=	( getTimestampFromDDMMYYYY( getCurrentDateInDDMMYYYY(), "START") ).getTime();
				long	currentTime		=	new java.util.Date().getTime();
				long	timeDiff		=	currentTime - timeOnDayStart;

				tsTransactionDate		=	new Timestamp( tsTransactionDate.getTime() + timeDiff );
			} else {
				tsTransactionDate		=	null;
			}
		
		
		return tsTransactionDate;
		
	}


	// This method takes a valid address object of the class
	// Address and returns a string that is formatted to be displayed in only
	// a Table Data or HTML paragraph. It cannot be used in a TextArea.

	public String getFormattedAddressFromAddressObj(	String	name,
														String	addressLine1,
														String	addressLine2,
														String	city,
														String	zipcode,
														String	state,
														String	country,
														String	phone,
														String	fax,
														String	emailId,
														String	format
													) {
		
		String	lineBreak	=	"";
		String	space		=	"";
		String	bold1			=	"";
		String	bold2			=	"";
		
		if(format.equalsIgnoreCase("HTML")) {
			lineBreak	=	"<BR>";
			space		=	"&nbsp;";
			bold1		=	"<b>";
			bold2		=	"</b>";
		} else if(format.equalsIgnoreCase("TEXT")) {
			lineBreak	=	"\n";
			space		=	" ";
		} else {
			lineBreak	=	" ";
			space		=	" ";
		}	
		
		StringBuffer sbFormattedAddress	=	new StringBuffer("");

		if(name != null && !name.equals(""))
			sbFormattedAddress.append( space + bold1 + name + bold2 + lineBreak );

		if(addressLine1 != null && !addressLine1.equals(""))
			sbFormattedAddress.append( space + addressLine1 + "," );

		if(addressLine2 != null && !addressLine2.equals(""))
			sbFormattedAddress.append( lineBreak + space + addressLine2 + "," + space );

		if(city!=null && !city.equals(""))
			sbFormattedAddress.append( lineBreak + space + city );

		if(zipcode!=null && !zipcode.equals(""))
			sbFormattedAddress.append( space + "-" + space + zipcode + ","  );
		else
			sbFormattedAddress.append( "," );

		if(state!=null && !state.equals("")) {
			sbFormattedAddress.append( lineBreak + space + state + ","  );
		}

		if(country!=null && !country.equals("")) {
			sbFormattedAddress.append( space + country + "." );
		} else {
			sbFormattedAddress.append( "." );
		}

		if(phone!=null && !phone.equals(""))
			sbFormattedAddress.append( lineBreak + space + "Ph.: "+ phone + "," );

		if(fax!=null && !fax.equals(""))
			sbFormattedAddress.append( lineBreak + space + "Fax: "+ fax );

		if(emailId!=null && !emailId.equals(""))
			sbFormattedAddress.append( lineBreak + space + "Email: "+ emailId );

		return sbFormattedAddress.toString();
	}
	
	// This method is used in Invoice Allocation (AP and AR) of the 'Transactions module
	// to make a vaild name for a HTML input (text) object which can be accessed via javascript.
	 
	public String makeProperName(String strNumAndType) {
		
		// This is used in JavaScript so if there are any space characters AND ':'
		// it will give JavaScript error. Hence.....do the following
		// REPLACE ALL SPACES WITH 's' AND THE TOKEN LIMITER ':' WITH '$'

		StringBuffer temp = new StringBuffer("");
		String character  = "";
		
		for(int x=0; x < strNumAndType.length(); x++) {
			character = strNumAndType.substring(x,x+1);
		
			if(character.equals(" ")) {
				temp.append("s");
			} else if(character.equals(":")) {
				temp.append("$");
			} else if(character.equals("-")) {
				temp.append("h");
			} else if(character.equals("/")) {
				temp.append("l");
			} else if(character.equals("\\")) {
				temp.append("b");
			} else {
				temp.append(character);
			}
		}
		strNumAndType = "n"+temp.toString();
		return strNumAndType;
		// This names will be reverse engineerd in the Process JSP
	}

	// This method is used in Invoice Allocation (AP and AR) of the 'Transactions module
	// to get the Voucher No and Voucher Type from a valid name (formaatted via method)
	// of a HTML input (text) object which was be accessed via javascript.

	public String reverseEnginnerName(String strName) {
		
		// Remove the 'n' t the starting point.
		strName = strName.substring(1, strName.length());

		// RESTORE ALL SPACE CHARACTERS and the TOKEN LIMITER ':'

		StringBuffer temp = new StringBuffer("");
		String character  = "";

		for(int x=0; x < strName.length(); x++) {

			character = strName.substring(x,x+1);

			if(character.equals("s")) {
				temp.append(" ");
			} else if(character.equals("$")) {
				temp.append(":");
			} else if(character.equals("h")) {
				temp.append("-");	
			} else if(character.equals("l")) {
				temp.append("/");
			} else if(character.equals("b")) {
				temp.append("\\");
			} else {
				temp.append(character);
			}
		}
		strName = temp.toString();
						
		// Finished the Revesring

		return strName;
	}
	
	// This method is used to remove commas in a string retrived from the request
	// which represents an amount. This is neccessary for storing the amounts in java strings
	// as hidden fields or in javascript variables;
	
	public String removeCommaFromAmountString(String strNumber) {
		StringBuffer temp = new StringBuffer("");
		String character="";
		for (int i=0; i < strNumber.length(); i++) {
			character = strNumber.substring(i,i+1);
			if(character.equals(",")==false) {	// If not a comma use it
				temp.append(character);
			}
		}
		return temp.toString();	
	}

	// This method is used in Invoice Allocation for checking the name of the textfield if it is
	// proper or not
	
	public boolean isTextFieldNameOk( String textfieldname ) {
		
		boolean	bTextFieldOk	=	false;

		if(textfieldname!=null && textfieldname.length() > 0) {
		
			if(textfieldname.equalsIgnoreCase("amtCurrentlyAvailable")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("balance")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("partyAcctCode")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("VNumVType")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("from")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("check")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("type")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("operation")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("submit")) {
				bTextFieldOk	=	false;
			} else if (textfieldname.equalsIgnoreCase("reset")) {
				bTextFieldOk	=	false;
			} else {
				bTextFieldOk	=	true;
			}
		} else {
			bTextFieldOk	=	false;
		}
		
		return	bTextFieldOk;
	}
	
	

	/*	This method compares if the give account is a Fixed Asset Account or not
	*/	
	
	public boolean isFixedAssetLedger( String strFixedAssetGroup, String strAcctCode ) {
		
		System.out.println("in FA method :");
		
		boolean bIsFixedAssetAccount	=	false;
		
		// if te fixed asset group is reserved see if the account is under it
		if(strFixedAssetGroup!=null && strFixedAssetGroup.length() > 0) {
			
			String strAcctGroupCode	=	strAcctCode.substring(0, strAcctCode.length()-7);
			
			if(strAcctGroupCode.length() >= strFixedAssetGroup.length()) {
				
				if(strAcctGroupCode.substring(0 , strFixedAssetGroup.length()).equals(strFixedAssetGroup) ) {
					bIsFixedAssetAccount	=	true;
				} else {
					bIsFixedAssetAccount	=	false;
				}	
			}
		} else {
			bIsFixedAssetAccount	=	false;
		}
		
		System.out.println("bIsFixedAssetAccount = "+bIsFixedAssetAccount);
		
		return bIsFixedAssetAccount;
	} //
	
	
	/*	This method takes a double amount, a String as the major currency unit and another string
		as the minor currency unit and returns a String thatbis trhe amount in words
	*/
	
	static	String[]	numbers		=	new String[]{"","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifthteen","sixteen","seventeen","eighteen","nineteen"};
	static	String[]	numbers10   = 	new String[]{"","ten","twenty","thirty","fourty","fifty","sixty","seventy","eighty","ninety"};

	public String	getAmountInWords(double  numberToConvert, String  majorUnit, String minorUnit)
    {
		
	  double	dollars	 =		Math.floor(numberToConvert);
	  double	cents	 =	 	Math.round((numberToConvert*100 - dollars*100));

      //Calculates the 'Crores'
	  int	  crores = (int) (dollars - dollars % 10000000) / 10000000;
	  dollars -= crores * 10000000;

      //Calculates the 'lacs'
	  int	 lacs = (int) (dollars - dollars % 100000) / 100000;
	  dollars -= lacs * 100000;

      //Calculates the 'thousands'
	  int	 thousands = (int) (dollars - dollars % 1000) / 1000;
	  dollars -= thousands * 1000;

      //Calculates the 'hundreds'
	  int	 hundreds = (int)(dollars - dollars % 100) / 100;
	  dollars -= hundreds * 100;

	  String	 numberInText		= "";

	  numberInText	+= (crores > 0 ? getLabel(crores) + " crore "  : "") +
									  (lacs > 0 ? getLabel(lacs) + " lac "  : "") +
									  (thousands > 0 ? getLabel(thousands) + " thousand "  : "") +
									  (hundreds > 0 ? getLabel(hundreds) + " hundred " : "") +
									  (dollars > 0 ? getLabel(dollars) + " " : "") +
									  ((Math.floor(numberToConvert) > 0 && cents > 0) ? "and " : "") +
									  (cents > 0 ? getLabel(cents) + " " + minorUnit + "(s)" : "");


		numberInText	=		numberInText.substring(0,1).toUpperCase() + numberInText.substring(1) ;
		
		numberInText  =		majorUnit +"(s) "+ numberInText;
		
		return  numberInText;
	}

	private String	 getLabel(double  number) 
	{
		//It is called  to add the label , 
		//Suppose if  the number is less than 20  the it is pronounced in different way 
		// So, for those numbers , it should take from numbers
		//Otherwise it should take from ordinary 'number10' array.

		  int   i   =  (int)number;
		  if (i<20) 
				return numbers[i];
		
		  int tens = (i - i % 10) / 10, units = i - (i - i % 10);
		  return numbers10[tens] + "  "+numbers[units];
	}
	
	
	// Method for Time Zone
	
	public String getTimeZoneName(String timeZoneId) {
		
		
		String	displayName	=	"";
		
		if(timeZoneId!=null && timeZoneId.length() > 0) {
		
			SimpleTimeZone	tzObj	=	new SimpleTimeZone( 0, timeZoneId);
			
			displayName	=	tzObj.getDisplayName();
			
		}
		
		return displayName;
	}

} 