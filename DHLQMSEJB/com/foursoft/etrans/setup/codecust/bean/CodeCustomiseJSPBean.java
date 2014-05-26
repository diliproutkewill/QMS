/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/

/**
	Program Name	: CodeCustomiseJSPBean.java
	Module Name		: ETrans
	Task			: Code_Customization	
	Sub Task		: CodeCustomiseJavaBean
	Author Name		: Sivarama Krishna .V
	Date Started	: September 11,2001
	Date Completed	: September 11,2001
	Date Modified	: 
	Description		:
         This class is useful in manipulating the CodeCustomisation. 
         This class consist of accessors and mutators for these objects.
         This object is used in add, modify and view modules of the CodeCustomisation. In modify, view modules the CodeCustomisation 
         details will be fetched( with respect to FS_FR_CONFIGPARAM table in the database ) and the fetched data
         will be stored in this object,and this object will be returned back to the client. In add module, entered Carreir
         details by the client will send to the server through this object.
	Method Summary  :
		 public CodeCustomiseJSPBean()  //This is an empty Constructor in which all variables are assigned to null, if the variables 
                                          are of String data Type and 0 if the variable is of Double Or Integer data Type. 
         public CodeCustomiseJSPBean(String codeIdName,String valGrp1, int valLen1, String valDesc1, String valInd1, String valGrp2, int valLen2, String valDesc2, String valInd2, String valGrp3, int valLen3, String valDesc3, String valInd3, int noOfGrps,long startingSlNo)										 
				 						//This Constructor takes codeIdName, valGrp1, valLen1, valDesc1, valInd1, valGrp2, valLen2, valDesc2, valInd2,
										  valGrp3, valLen3, valDesc3, valInd3, noOfGrps, startingSlNo as arguments.
         public String getCodeIdName()  //This method returns the Code Id Name.	
         public String getValGrp1()     //This method returns the valGrp1 as String.It is the value in the first group.
         public int getValLen1()        //This method returns the ValLen1. It is the length in the first group.		 									   
         public String getValDesc1()    //This method returns the ValDesc1. It is used to write brief description of the Code Id Name selected.		 
         public String getValInd1()     //This method returns the ValInd1. It is the indicator in the first group. It can be either Static or Dynamic.		 
         public String getValGrp2()     //This method returns the valGrp2 as String.It is the value in the first group.
         public int getValLen2()        //This method returns the ValLen2. It is the length in the first group.		 									   
         public String getValDesc2()    //This method returns the ValDesc2. It is used to write brief description of the Code Id Name selected.		 
         public String getValInd2()     //This method returns the ValInd2. It is the indicator in the first group. It can be either Static or Dynamic.		 
         public String getValGrp3()     //This method returns the valGrp3 as String.It is the value in the first group.
         public int getValLen3()        //This method returns the ValLen3. It is the length in the first group.		 									   
         public String getValDesc3()    //This method returns the ValDesc3. It is used to write brief description of the Code Id Name selected.		 
         public String getValInd3()     //This method returns the ValInd3. It is the indicator in the first group. It can be either Static or Dynamic.		 
         public int getNoOfGrps()       //This method returns the No of Groups. 
         public int getStartingSlNo()   //This method returns the Starting Serial Number.
*/
package com.foursoft.etrans.setup.codecust.bean;

/**
 * @author Sivarama Krishna .V
 * @version etrans1.6
 */
	public class CodeCustomiseJSPBean implements java.io.Serializable
	{
         
  /**
   * @param
   */
		public CodeCustomiseJSPBean()
		{
		codeIdName		= null;
 		shipmentMode  = 0;    
    terminalId    = null;
		valGrp1			  = null;
		valLen1			  = 0;
		valDesc1		  = null;
		valInd1			  = null;
		valGrp2			  = null;
		valLen2			  = 0;
		valDesc2		  = null;
		valInd2			  = null;
		valGrp3			  = null;
		valLen3			  = 0;
		valDesc3		  = null;
		valInd3			  = null;

		valGrp4			  = null;
		valLen4			  = 0;
		valDesc4		  = null;
		valInd4			  = null;
		noOfGrps		  = 0;
		startingSlNo	= 0L;
    valGrp        = null;
    custFlag      = null;
    valInd        = null;
    valLen        = null;

	}

  
  /**
   * 
   * @param codeIdName
   * @param valGrp1
   * @param valLen1
   * @param valDesc1
   * @param valInd1
   * @param valGrp2
   * @param valLen2
   * @param valDesc2
   * @param valInd2
   * @param valGrp3
   * @param valLen3
   * @param valDesc3
   * @param valInd3
   * @param valGrp4
   * @param valLen4
   * @param valDesc4
   * @param valInd4
   * @param noOfGrps
   * @param startingSlNo
   * @param shipmentMode
   * @param terminalId
   * @param custFlag
   */
	public CodeCustomiseJSPBean(String codeIdName,String valGrp1, int valLen1, String valDesc1, String valInd1, String valGrp2, int valLen2, String valDesc2, String valInd2, String valGrp3, int valLen3, String valDesc3, String valInd3, String valGrp4, int valLen4, String valDesc4, String valInd4,int noOfGrps,long startingSlNo,int shipmentMode,String terminalId,String custFlag)
	{
		this.codeIdName   = codeIdName;
		this.valGrp1 	    = valGrp1;
		this.valLen1 	    = valLen1;
		this.valDesc1 	  = valDesc1;
		this.valInd1 	    = valInd1;
		this.valGrp2 	    = valGrp2;
		this.valLen2 	    = valLen2;
		this.valDesc2 	  = valDesc2;
		this.valInd2 	    = valInd2;
		this.valGrp3 	    = valGrp3;
		this.valLen3 	    = valLen3;
		this.valDesc3 	  = valDesc3;
		this.valInd3 	    = valInd3;
		this.valGrp4 	    = valGrp4;
		this.valLen4 	    = valLen4;
		this.valDesc4 	  = valDesc4;
		this.valInd4 	    = valInd4;
		this.noOfGrps 	  = noOfGrps;
		this.startingSlNo = startingSlNo;
    this.shipmentMode = shipmentMode;
    this.terminalId   = terminalId;
    this.custFlag     = custFlag;
    this.valInd       = valInd;
    this.valLen       = valLen;
	}

  /**
   * 
   * @return String
   */
	public String getCodeIdName()
	{
		return codeIdName;
	}
		
  /**
   * 
   * @return String
   */
	public String getValGrp1()
	{
	
		return valGrp1;
	}

  /**
   * 
   * @return int
   */
	public int getValLen1()
	{
		return valLen1;
	}

  /**
   * 
   * @return String
   */
	public String getValDesc1()
	{
		return valDesc1;
	}

  /**
   * 
   * @return String
   */
	public String getValInd1()
	{
		return valInd1;
	}

  /**
   * 
   * @return String
   */
	public String getValGrp2()
	{
		return  valGrp2;
	}

  /**
   * 
   * @return int
   */
	public int getValLen2()
	{
		return valLen2;
	}

  /**
   * 
   * @return String
   */
	public String getValDesc2()
	{
		return valDesc2;
	}

  /**
   * 
   * @return String
   */
	public String getValInd2()
	{
		return valInd2;
	}

  /**
   * 
   * @return String 
   */
	public String getValGrp3()
	{
		return valGrp3;
	}

  /**
   * 
   * @return int
   */
	public int getValLen3()
	{
		return valLen3;
	}

  /**
   * 
   * @return String
   */
	public String getValDesc3()
	{
		return valDesc3;
	}

  /**
   * 
   * @return String
   */
	public String getValInd3()
	{
		return valInd3;
	}	

  /**
   * 
   * @return String
   */
   public String getValGrp4()
	{
		return valGrp4;
	}

  /**
   * 
   * @return int
   */
	public int getValLen4()
	{
		return valLen4;
	}

  /**
   * 
   * @return String
   */
	public String getValDesc4()
	{
		return valDesc4;
	}

  /**
   * 
   * @return String 
   */
	public String getValInd4()
	{
		return valInd4;
	}	


  /**
   * 
   * @return int
   */
	public int getNoOfGrps()
	{	
		return noOfGrps;
	}
  /**
   * 
   * @return String[]
   */
	public String[] getValGrps()
	{
		return valGrp;
	}
  /**
   * 
   * @return int[]
   */
  public int[] getValLens()
	{
		return valLen;
	}
  /**
   * 
   * @return long
   */
	public long getStartingSlNo() 
	{
		return startingSlNo;
	}

  /**
   * 
   * @return int
   */
  public int getShipmentMode()
  {
    return shipmentMode;
  }

  /**
   * 
   * @return String
   */
  public String getTerminalId()
  {
    return terminalId;
  }
  /**
   * 
   * @return String
   */
  public String getCustFlag()
  {
    return custFlag;
  }
  /**
   * 
   * @return String[]
   */
  public String[] getValInds()
  {
    return valInd;
  }
  
  /**
   * 
   * @param codeIdName
   */
	public void setCodeIdName(String codeIdName)
	{
		this.codeIdName	= codeIdName;
	}


  /**
   * 
   * @param valGrp1
   */
	public void setValGrp1(String valGrp1)
	{
		this.valGrp1	= valGrp1;
		
	}

  /**
   * 
   * @param valLen1
   */
	public void setValLen1(int	valLen1)
	{
		this.valLen1	= valLen1;
	}

  /**
   * 
   * @param valDesc1
   */
	public void setValDesc1(String valDesc1)
	{
		this.valDesc1 = valDesc1;
	}

  /**
   * 
   * @param valInd1
   */
	public void setValInd1(String	valInd1)
	{
		this.valInd1	= valInd1;
	}

  /**
   * 
   * @param valGrp2
   */
	public void setValGrp2(String valGrp2)
	{
		this.valGrp2	= valGrp2;
	}

  /**
   * 
   * @param valLen2
   */
	public void setValLen2(int	valLen2)
	{
		this.valLen2	= valLen2;
	}

  /**
   * 
   * @param valDesc2
   */
	public void setValDesc2(String valDesc2)
	{
		this.valDesc2 = valDesc2;
	}

  /**
   * 
   * @param valInd2
   */
	public void setValInd2(String	valInd2)
	{
		this.valInd2	= valInd2;
	}

  /**
   * 
   * @param valGrp3
   */
	public void setValGrp3(String valGrp3)
	{
		this.valGrp3	= valGrp3;
	}

  /**
   * 
   * @param valLen3
   */
	public void setValLen3(int	valLen3)
	{
		this.valLen3	= valLen3;
	}

  /**
   * 
   * @param valDesc3
   */
	public void setValDesc3(String valDesc3)
	{
		this.valDesc3 = valDesc3;
	}

  /**
   * 
   * @param valInd3
   */
	public void setValInd3(String valInd3)
	{
		this.valInd3	= valInd3;
	}
  /**
   * 
   * @param valGrp4
   */
	public void setValGrp4(String valGrp4)
	{
		this.valGrp4	= valGrp4;
	}

  /**
   * 
   * @param valLen4
   */
	public void setValLen4(int	valLen4)
	{
		this.valLen4	= valLen4;
	}

  /**
   * 
   * @param valDesc4
   */
	public void setValDesc4(String valDesc4)
	{
		this.valDesc4 = valDesc4;
	}

  /**
   * 
   * @param valInd4
   */
	public void setValInd4(String valInd4)
	{
		this.valInd4	= valInd4;
	}
  /**
   * 
   * @param noOfGrps
   */
	public void setNoOfGrps(int	noOfGrps)
	{
		this.noOfGrps = noOfGrps;
	}

  /**
   * 
   * @param startingSlNo
   */
	public void setStartingSlNo(long startingSlNo)
	{
		this.startingSlNo = startingSlNo;
	}

  /**
   * 
   * @param shipmentMode
   */
  public void setShipmentMode(int shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }

  /**
   * 
   * @param terminalId
   */
  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }
  /**
   * 
   * @param custFlag
   */
  public void setCustFlag(String custFlag)
  {
    this.custFlag = custFlag;
  }
  /**
   * 
   * @param valGrp
   */
  public void setValGrps(String[] valGrp)
	{
		this.valGrp	= valGrp;
	}
  /**
   * 
   * @param valInd
   */
  public void setValInds(String[] valInd)
	{
		this.valInd	= valInd;
	}
  /**
   * 
   * @param valLen
   */
  public void setValLens(int[] valLen)
	{
		this.valLen	= valLen;
	}
 
  	// Data Members

	public String   codeIdName = "";  // Code Id Name to store the Name of the Code 	
 	public String	valGrp1 = "";     // Value Group1 to store the value corresponding to the indicator in the first group 
	public int		valLen1 = 0;      // Value Length1, length of the first group 
	public String	valDesc1 = "";    // Value Description1 uses to store brief description of the first group 
	public String	valInd1 = "";     // Value Indicator1 which can be either Static or Dynamic 
  public String	valGrp2 = "";     // Value Group2 to store the value corresponding to the indicator in the second group 	
	public int		valLen2 = 0;      // Value Length2, length of the second group 
	public String	valDesc2 = "";    // Value Description2 uses to store brief description of the second group 
	public String	valInd2 = "";     // Value Indicator2 which can be either Static or Dynamic 
	public String	valGrp3 = "";     // Value Group3 to store the value corresponding to the indicator in the third group 
	public int		valLen3 = 0;      // Value Length2, length of the third group 
	public String	valDesc3 = "";    // Value Description3 uses to store brief description of the third group 
	public String	valInd3 = "";     // Value Indicator3 which can be either Static or Dynamic 
	public String	valGrp4 = "";     
	public int		valLen4 = 0;      
	public String	valDesc4 = "";   
	public String	valInd4 = "";     
	public int		noOfGrps = 0;     // Number of Groups should the Code contain will be stored in this field 
	public long    startingSlNo = 0L;     // Starting serial number af the Code is stored here 
  public int    shipmentMode = 0;   // shipmentMode is used to store theh shipment mode for which codeidname is generated.
  public String terminalId   = "";  // terminalId is used to store terminal fo which id is created.  
	public String[] valGrp     = null; 
  public String  custFlag     = "";
  public String[]  valInd     = null;
  public int[]  valLen     = null;
	}		
