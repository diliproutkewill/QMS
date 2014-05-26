/*<%--
 %
 % Copyright (c) 1999-2005 by FourSoft,Pvt Ltd.Reserved.
 % This software is the proprietary information of FourSoft Ltd.
 % Use is subject to license terms.
 % 
 % esupply - v 1.8
 %
--%>

<%--
% File		    :   ETMAWBAdvVO.java
% Sub-Module    :   MAWB - Advanced LOV Search. 
% Module        :   ETrans
%
% This is the Value Object  for the LOV of the MAWB Ids based on Different Parameters
% 
% Author        :   G.Srinivas 
% Date 			:   15/03/2005
% Modified date :   15/03/2005

--%>*/

package com.foursoft.esupply.common.java;


import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import java.util.HashMap;

/**
 * 
 */
public class ETMAWBAdvVO extends ETAdvancedLOVMasterVO
{
    protected String	mawbId;
    protected String	originGatewayId;	
    protected String	destinationGatewayId;
    protected String	originTerminal;	
    protected String	destinationTerminal;
	protected String	carrierId;
    protected String	chargeableWeight;
    protected String	chargeableWeightControl;
    protected String	blockedSpace;	
    protected String	blockedSpaceControl;	
    protected String	ETD;
    protected String    ETA;
	protected String    UOW;
	protected String    noOfDays;
	protected String    noOfDaysControl;
	protected String    entity;
	protected String    searchType;
	protected String    terminalId;
	protected String    operation;
	HashMap	params=null;
   
 public ETMAWBAdvVO()
    {
    }
 
 public void setMawbId(String mawbId)
	{
		this.mawbId=mawbId;
	}
  
 public void setOriginGatewayId(String originGatewayId)
	{
		this.originGatewayId=originGatewayId;
	}
  
 public void setDestinationGatewayId(String destinationGatewayId)
	{
		this.destinationGatewayId=destinationGatewayId;
	}
  
 public void setOriginTerminal(String originTerminal)
	{
		this.originTerminal=originTerminal;
	}
 public void setDestinationTerminal(String destinationTerminal)
	{
		this.destinationTerminal=destinationTerminal;
	}
 public void setCarrierId(String carrierId)
	{
		this.carrierId=carrierId;
	}
 public void setChargeableWeight(String chargeableWeight)
	{
		this.chargeableWeight=chargeableWeight;
	}
 public void setChargeableWeightControl(String chargeableWeightControl)
	{
		this.chargeableWeightControl=chargeableWeightControl;
	}
 public void setBlockedSpace(String blockedSpace)
	{
		this.blockedSpace=blockedSpace;
	}
 public void setBlockedSpaceControl(String blockedSpaceControl)
	{
		this.blockedSpaceControl=blockedSpaceControl;
	}
public void setETD(String ETD)
	{
		this.ETD=ETD;
	}
public void setETA(String ETA)
	{
		this.ETA=ETA;
	}
public void setUOW(String UOW)
	{
		this.UOW=UOW;
    }
public void setEntity(String entity)
	{
		this.entity=entity;
    }
public void setNoOfDays(String noOfDays)
	{
		this.noOfDays=noOfDays;
    }
public void setNoOfDaysControl(String noOfDaysControl)
	{
		this.noOfDaysControl=noOfDaysControl;
    }
public void setSearchType(String searchType)
	{
		this.searchType=searchType;
    }
public void setTerminalId(String terminalId)
	{
		this.terminalId=terminalId;
    }
public void setOperation(String operation)
	{
		this.operation=operation;
    }

 ///////////////////////////////////////

  
    public String getMawbId()
	{
		return mawbId;
	}
    public String getOriginGatewayId()
	{
		return originGatewayId;
    }
	public String getDestinationGatewayId()
	{
		return destinationGatewayId;
	}
  
	public String getOriginTerminal()
	{
		return originTerminal;
	}
  
	public String getDestinationTerminal()
	{
		return destinationTerminal;
	}
  
	public String getCarrierId()
	{
		return carrierId;
    }
	public String getChargeableWeight()
	{
		return chargeableWeight;
	}
  
	public String getChargeableWeightControl()
	{
		return chargeableWeightControl;
	}
 
	public String getBlockedSpace()
	{
		return blockedSpace;
	}
  
	public String getBlockedSpaceControl()
	{
		return blockedSpaceControl;
	}
  
	public String getETD()
	{
		return ETD;
	}
	public String getETA()
	{
		return ETA;
	}
	public String getUOW()
	{
		return UOW;
	}
	public String getEntity()
	{
		return entity;
	}
	public String getNoOfDays()
	{
		return noOfDays;
	}
	public String getNoOfDaysControl()
	{
		return noOfDaysControl;
	}
	public String getSearchType()
	{
		return searchType;
	}
	public String getTerminalId()
	{
		return terminalId;
    }
	public String getOperation()
	{
		return operation;
    }


}