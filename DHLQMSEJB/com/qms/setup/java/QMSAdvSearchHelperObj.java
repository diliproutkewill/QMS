//Added by kalyan.P
package com.qms.setup.java;
import java.io.Serializable;
import org.apache.log4j.Logger;
public class QMSAdvSearchHelperObj implements Serializable
{
  private String whereCondition;
  private String operation;
  private String terminalId;
  private String designationId;
  private String shipmentMode;
  private String accessLevel;
  private String buyRatesPermission;//@@Added by Kameswari for the WPBN issue-26514
  private String empId;//@@Added by Kameswari for the WPBN issue-26514
  private static Logger logger = null;
private String localTerminal ;  /////added by VLAKSHMI on 22/05/2009
private String localAcceslevel;///added by VLAKSHMI on 22/05/2009
 private String countryId = null;//Added by Rakesh for Issue:       on 04-03-2011
 private String multiQuote=null;
  public String getWhereCondition()
  {
    return getWhereConditionSpecificToOperation();
  }

  public void setWhereCondition(String whereCondition)
  {
    this.whereCondition = whereCondition;
  }

  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }
  
  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }

  public String getDesignationId()
  {
    return designationId;
  }

  public void setDesignationId(String designationId)
  {
    this.designationId = designationId;
  }

  public String getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }
  
  public String getAccessLevel()
  {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }
  
  private String getWhereConditionSpecificToOperation()
  {
       logger  = Logger.getLogger(QMSAdvSearchHelperObj.class);
    StringBuffer  sb  = new StringBuffer();
    
    String  tempWhere = "";
    String  shMode  = null;
    
    if(this.whereCondition!=null && this.whereCondition.length()!=0)
    {
//@@ Commented by subrahmanyam for the wpbn issue: 150461 on 20/12/2008      
           /* if(this.whereCondition.toUpperCase().indexOf("WHERE")!=-1)
            {
             if("BUYCHARGEBASIS".equalsIgnoreCase(this.operation)||"SELLCHARGEBASIS".equalsIgnoreCase(this.operation))
              tempWhere = " and "+this.whereCondition.replaceAll("where","");
              else
              
              tempWhere = " and "+this.whereCondition.toUpperCase().replaceAll("WHERE","");
            }
            else
              tempWhere = this.whereCondition;*/
              
//@@ Added by subrahmanyam for the WPBN ISSUE: 150461 ON 20/12/2008              
      if(this.whereCondition.indexOf("Corporate")!=-1||this.whereCondition.indexOf("Customer")!=-1)//@@ if conditi
      {
            tempWhere = this.whereCondition;
      }
      else
      {
            if(this.whereCondition.toUpperCase().indexOf("WHERE")!=-1)
            {
             if("BUYCHARGEBASIS".equalsIgnoreCase(this.operation)||"SELLCHARGEBASIS".equalsIgnoreCase(this.operation))
              tempWhere = " and "+this.whereCondition.replaceAll("where","");
              else
              
              tempWhere = " and "+this.whereCondition.toUpperCase().replaceAll("WHERE","");
            }
            else
              tempWhere = this.whereCondition;
      }
      
//@@ Ended by subrahmanyam for the WPBN ISSUE: 150461 ON 20/12/2008      
    }
    else
    {
      tempWhere = "";
    }
    if("1".equalsIgnoreCase(this.shipmentMode) || "Air".equalsIgnoreCase(this.shipmentMode))
    {
      shMode  = "(1,3,5,7)";
    }
    else if("2".equalsIgnoreCase(this.shipmentMode) || "Sea".equalsIgnoreCase(this.shipmentMode))
    {
      shMode  = "(2,3,6,7)";
    }
    else if("4".equalsIgnoreCase(this.shipmentMode) || "Truck".equalsIgnoreCase(this.shipmentMode))
    {
      shMode  = "(4,5,6,7)";
    }
    else
    {
      shMode  = "(1,2,3,4,5,6,7)";
    }
    
    if(this.operation!=null && this.operation.length()!=0 )//IMPLEMENT HIERARCHY CONDITION HERE BASED ON THE DIFFERENT OPERATIONS
    {
    //  if("SALESPERSON".equalsIgnoreCase(this.operation) || "REPORTINGOFF".equalsIgnoreCase(this.operation))
     //@@Modified by kameswari for the issue
      if("REPORTINGOFF".equalsIgnoreCase(this.operation)) 
      {
       //sb.append("");  
       ///added by VLAKSHMI on 22/05/2009 for CR 167659
       // modified by VALSKHMI for issue 170508 on 13/05/09
       if("O".equalsIgnoreCase(this.localAcceslevel))
        {
        
             sb.append(" where locationid in (select  parent_terminal_id term_id from fs_fr_terminal_regn ")
              .append(" where child_terminal_id = '"+this.localTerminal+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
             .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append("where parent_terminal_id in(select parent_terminal_id term_id  from fs_fr_terminal_regn where child_terminal_id= '"+this.localTerminal+"')) ");
           
         } else if("A".equalsIgnoreCase(this.localAcceslevel))
       {
          sb.append(" where locationid in (select child_terminal_id term_id from fs_fr_terminal_regn  ")
           .append(" where parent_terminal_id='"+this.localTerminal+"' union select '"+this.localTerminal+"' from dual ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H') ");
            
       }
        else if("H".equalsIgnoreCase(this.localAcceslevel))
        {
          sb.append(" where locationid in (select terminalid term_id from fs_fr_terminalmaster) ");
        }
        else if("L".equalsIgnoreCase(this.localAcceslevel))
        {
          sb.append(" where locationid in (select LOCATIONID from FS_USERMASTER where USER_LEVEL='LICENSEE') ");
        }
        //end of  on 22/05/2009
      }
     else if("SALESPERSON".equalsIgnoreCase(this.operation))
      {
      // modified by VLAKSHMI for issue 168093 on 20/04/09
        if("O".equalsIgnoreCase(this.accessLevel))
        {
         /* sb.append(" where locationid in (select parent_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by child_terminal_id = prior parent_terminal_id ")
            .append(" start with child_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
            .append(" select '"+this.terminalId+"' term_id  from dual ")
            .append(" union ")
            .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by prior child_terminal_id = parent_terminal_id ")
            .append(" start with parent_terminal_id = '"+this.terminalId+"') ");*/
             sb.append(" where locationid in (select  parent_terminal_id term_id from fs_fr_terminal_regn ")
              .append(" where child_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
             .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append("where parent_terminal_id in(select parent_terminal_id term_id  from fs_fr_terminal_regn where child_terminal_id= '"+this.terminalId+"')) ");
           
      /*    sb.append(" where locationid in (select parent_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by child_terminal_id = prior parent_terminal_id ")
            .append(" start with child_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
            .append(" select '"+this.terminalId+"' term_id  from dual ")
            .append(" union ")  
            .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by prior child_terminal_id = parent_terminal_id ")
            .append(" start with parent_terminal_id in(select parent_terminal_id term_id  from fs_fr_terminal_regn where child_terminal_id= '"+this.terminalId+"'))");*/
        } else if("A".equalsIgnoreCase(this.accessLevel))
       {
          sb.append(" where locationid in (select child_terminal_id term_id from fs_fr_terminal_regn  ")
           .append(" where parent_terminal_id='"+this.terminalId+"' union select '"+this.terminalId+"' from dual ")
              .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H') ");
            
       }
        else if("H".equalsIgnoreCase(this.accessLevel))
        {
          sb.append(" where locationid in (select terminalid term_id from fs_fr_terminalmaster) ");
        }
        else if("L".equalsIgnoreCase(this.accessLevel))
        {
          sb.append(" where locationid in (select LOCATIONID from FS_USERMASTER where USER_LEVEL='LICENSEE') ");
        }
      }
      else if("QUOTECUSTOMER".equalsIgnoreCase(this.operation) || "COSTINGQUOTEID".equalsIgnoreCase(this.operation)||"QUOTEVIEW".equalsIgnoreCase(this.operation))//@@Modified by Kameswari for the WPBN issue-26514
      {
//@@ Commented by subrahmanyam for the WPBN ISSUE: 150461 ON 20/12/2008     
        /*if(!"H".equalsIgnoreCase(this.accessLevel))
        {
          sb.append(" where terminalid in (select parent_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by child_terminal_id = prior parent_terminal_id ")
            .append(" start with child_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
            .append(" select '"+this.terminalId+"' term_id  from dual ")
            .append(" union ")
            .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by prior child_terminal_id = parent_terminal_id ")
            .append(" start with parent_terminal_id = '"+this.terminalId+"') ");
        }
        else
        {
          sb.append(" where terminalid in (select terminalid term_id from fs_fr_terminalmaster) ");
        }*/
        
//@@ Added by subrahmanyam for the wpbn issue: 150461 on 20/12/2008

      if(this.whereCondition.indexOf("Corporate")!=-1 || this.whereCondition.indexOf("Customer")!=-1)
      {
      sb.append("");
      }
      else
      {
        if(!"H".equalsIgnoreCase(this.accessLevel))
        {
          //@@ COMMENTED BY SUBRAHMANYAM FOR CR_Enhancement_167669        
        /*
         * sb.append(" where terminalid in (select parent_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by child_terminal_id = prior parent_terminal_id ")
            .append(" start with child_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
            .append(" select '"+this.terminalId+"' term_id  from dual ")
            .append(" union ")
            .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by prior child_terminal_id = parent_terminal_id ")
            .append(" start with parent_terminal_id = '"+this.terminalId+"') ");
         * */
//ADDED BY SUBRAHMANYAM FOR CUSTOMER CR_Enhancement_167669         
          if("A".equalsIgnoreCase(this.accessLevel))
          {
        	/*if("IN".equalsIgnoreCase(this.getCountryId()))
             sb.append(" where terminalid in ('"+this.getTerminalId()+"')");
             else */
            sb.append(" where terminalid in (select parent_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by child_terminal_id = prior parent_terminal_id ")
            .append(" start with child_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
            .append(" select '"+this.terminalId+"' term_id  from dual ")
            .append(" union ")
            .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by prior child_terminal_id = parent_terminal_id ")
            .append(" start with parent_terminal_id = '"+this.terminalId+"') ");
          }
          else
          {
        	 if("IN".equalsIgnoreCase(this.getCountryId()) && !"QUOTECUSTOMER".equalsIgnoreCase(this.operation))
             sb.append(" where terminalid in ('"+this.getTerminalId()+"')");
             else 
            sb.append(" where terminalid in (select parent_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by child_terminal_id = prior parent_terminal_id ")
            .append(" start with child_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
            .append(" select '"+this.terminalId+"' term_id  from dual ")
            .append(" union ")
            .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by prior child_terminal_id = parent_terminal_id ")
            .append(" start with parent_terminal_id = '"+this.terminalId+"' ")
            .append(" union ")
            .append("  select child_terminal_id from fs_fr_terminal_regn  ")
            .append(" where parent_terminal_id in(select parent_terminal_id from fs_fr_terminal_regn fr1")
            .append(" where fr1.child_terminal_id= '"+this.terminalId +"'))");
          }
//ENDED BY SUBRAHMANYAM CR_Enhancement_167669  
        }
        else
        {
          if("IN".equalsIgnoreCase(this.getCountryId()))
        	  sb.append(" where terminalid in ('"+this.getTerminalId()+"')");
          else
          sb.append(" where terminalid in (select terminalid term_id from fs_fr_terminalmaster) ");
        	  
        }
      }
      
//@@ ended by subrahmanyam for the wpbn issue: 150461 on 20/12/2008        
     
      }
      else if("CARTAGELOC".equalsIgnoreCase(this.operation) )
      {
        if(!"H".equalsIgnoreCase(this.accessLevel))
        {
          sb.append(" where terminalid in ( ")
            .append(" select '"+this.terminalId+"' term_id  from dual ")
            .append(" union ")
            .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append(" connect by prior child_terminal_id = parent_terminal_id ")
            .append(" start with parent_terminal_id = '"+this.terminalId+"') ");
        }
        else
        {
          sb.append(" where terminalid in (select terminalid term_id from fs_fr_terminalmaster) ");
        }
      }
      //@@Added for the WPBN issue-26514
      else if("QUOTEMODIFY".equalsIgnoreCase(this.operation)||"QUOTECOPY".equalsIgnoreCase(this.operation))
      {
        if(!"H".equalsIgnoreCase(this.accessLevel))
        {
//Commented by subrahmanyam for CR_Enhancement_167669 on 26/May/09
          /*
          sb.append("where terminalid in (SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID "+
                    "START WITH PARENT_TERMINAL_ID = '").append(this.terminalId).append("'").append(" UNION SELECT TERMINALID "+
                    "FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(this.terminalId).append("')") ;
          */
//@@Added  by subrahmanyam for CR_Enhancement_167669 on 26/May/09    
          if("A".equalsIgnoreCase(this.accessLevel))
          {
        	  if("IN".equalsIgnoreCase(this.getCountryId()))
           	  sb.append(" where terminalid in ('"+this.getTerminalId()+"')");
             else
            sb.append("where terminalid in (SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID "+
                    "START WITH PARENT_TERMINAL_ID = '").append(this.terminalId).append("'").append(" UNION SELECT TERMINALID "+
                    "FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(this.terminalId).append("')") ;
          }
          else
          {
        	     if("IN".equalsIgnoreCase(this.getCountryId()))
               	  sb.append(" where terminalid in ('"+this.getTerminalId()+"')");
                 else
            sb.append("where terminalid in (SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID "+
                    "START WITH PARENT_TERMINAL_ID = '").append(this.terminalId).append("'").append(" UNION SELECT TERMINALID "+
                    "FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(this.terminalId).append("'") 
                    .append("UNION SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN FRT WHERE FRT.PARENT_TERMINAL_ID IN(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID='").append(this.terminalId).append("'))");
          }
//@@ Ended  by subrahmanyam for CR_Enhancement_167669 on 26/May/09         
        }
        else
        {
        	if("IN".equalsIgnoreCase(this.getCountryId()))
             	  sb.append(" where terminalid in ('"+this.getTerminalId()+"')");
               else	
          sb.append(" where terminalid in (select terminalid term_id from fs_fr_terminalmaster) ");
        }
      }
      //@@WPBN issue-26514
      
      else if("LOCATIONS".equalsIgnoreCase(this.operation))
      {
        sb.append(" where shipmentmode in "+shMode);
      }
      else if("LOCSETUPMODIFY".equalsIgnoreCase(this.operation))
      {
        sb.append(" where shipmentmode in "+shMode+" and (INVALIDATE='F' OR INVALIDATE IS NULL) ")
          .append(tempWhere);                  
      }
      else if("LOCSETUPVIEW".equalsIgnoreCase(this.operation))
      {
        sb.append(" where shipmentmode in "+shMode)
          .append(tempWhere);
      }
    }
    
    if(this.operation!=null && this.operation.length()!=0)//ADD EXTRA CONDITIONS THAT ARE REQUIRED FOR THIS OPERATION
    {
      if("SALESPERSON".equalsIgnoreCase(this.operation) || "CARTAGELOC".equalsIgnoreCase(this.operation)||"LOCATIONS".equalsIgnoreCase(this.operation)||"PORTS".equalsIgnoreCase(this.operation) ||"QUOTECUSTOMER".equalsIgnoreCase(this.operation) || "BUYCHARGEBASIS".equalsIgnoreCase(this.operation) || "SELLCHARGEBASIS".equalsIgnoreCase(this.operation))
      {
        sb.append(tempWhere);
      }
      else if("REPORTINGOFF".equalsIgnoreCase(this.operation))
      {
       /*sb.append(" and designation_id in (select designation_id from qms_designation where to_number(level_no)<=(select to_number(level_no) from qms_designation where designation_id='"+this.designationId+"')) ")
          .append(tempWhere);*/
        sb.append(" and  designation_id in (select designation_id from qms_designation where to_number(level_no)<=(select to_number(level_no) from qms_designation where designation_id='"+this.designationId+"')) ")
          .append(tempWhere);//@@Modified by Kameswari for the issue
      }
      else if("COSTINGQUOTEID".equalsIgnoreCase(this.operation))
      {
        sb.append(" AND COMPLETE_FLAG <>'I' AND shipmentmode in "+shMode)
          .append(tempWhere);
        //Added by Rakesh on 16-03-2011
        if(this.getMultiQuote()!=null && "MultiQuote".equalsIgnoreCase(this.getMultiQuote())){
        	sb.append(" AND IS_MULTI_QUOTE='Y' ")
            .append(tempWhere);
        }//Ended by Rakesh on 16-03-2011
        else{ //Added by Kishore on 25-Apr-11 For Single and MultiQuoteLOvs
        	sb.append(" AND IS_MULTI_QUOTE='N' ")
            .append(tempWhere);
        }
        	
        
      }
      else if("CHARGEBASIS".equalsIgnoreCase(this.operation))
      {
        /*sb.append(" AND CHARGEBASIS NOT IN ( SELECT CHARGE_BASIS FROM QMS_CARTAGE_BUYDTL UNION ALL SELECT CHARGEBASIS FROM QMS_BUYSELLCHARGESMASTER) ")
         .append(tempWhere);*/
        sb.append(" CGM WHERE NOT EXISTS (SELECT 'X' FROM QMS_CARTAGE_BUYDTL WHERE CHARGE_BASIS = CGM.CHARGEBASIS UNION ALL  ")
        .append("SELECT 'X' FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEBASIS = CGM.CHARGEBASIS)")
         .append(tempWhere);
        
      }
      //@@Added by Kameswari for the WPBN issue-26514
      else if("QUOTEMODIFY".equalsIgnoreCase(this.operation))
      {
        sb.append(" AND ((ESCALATION_FLAG = 'Y' AND '").append(this.empId).append("'").append(" = ESCALATED_TO) OR " +
                  "(ESCALATION_FLAG = 'N' AND NOT (BASIS = 'Y' AND '").append(this.buyRatesPermission).append("'").append(" = 'N'))) " + 
                  "AND QUOTE_STATUS NOT IN ('ACC', 'NAC') " + 
                  "AND ID NOT IN " + 
                  "(SELECT QUOTE_ID FROM QMS_QUOTES_UPDATED WHERE CONFIRM_FLAG IS NULL)").append(tempWhere);
      }
      else if("QUOTECOPY".equalsIgnoreCase(this.operation)||"QUOTEVIEW".equalsIgnoreCase(this.operation))
      {
        sb.append(" AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                  "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A')) " + 
                  "AND (('QUOTECOPY' = 'QUOTECOPY' AND COMPLETE_FLAG = 'C') OR " + 
                  "('QUOTECOPY' <> 'QUOTECOPY' AND COMPLETE_FLAG IN ('I', 'C')))").append(tempWhere);
      }
      //@@WPBN issue-26514
      if("surcharge".equalsIgnoreCase(operation))
      {
    	  sb.append(whereCondition);//Govind
      }
    }
    else
    {
      sb.append("");
    }
    
    tempWhere = sb.toString();
  
    if(tempWhere.toUpperCase().indexOf("WHERE")==-1)
    {
      if("BUYCHARGEBASIS".equalsIgnoreCase(this.operation)||"SELLCHARGEBASIS".equalsIgnoreCase(this.operation))
         tempWhere = tempWhere.replaceFirst("and","where");
      else
         tempWhere = tempWhere.toUpperCase().replaceFirst("AND","WHERE");
       
    }
    
    sb.replace(0,sb.length(),tempWhere);
   
    return sb.toString();
  }
  //@@Added by Kameswari for the WPBN issue-26514
  public String getBuyRatesPermission()
  {
    return buyRatesPermission;
  }

  public void setBuyRatesPermission(String buyRatesPermission)
  {
    this.buyRatesPermission = buyRatesPermission;
  }

  public String getEmpId()
  {
    return empId;
  }

  public void setEmpId(String empId)
  {
    this.empId = empId;
  }


  public void setLocalTerminal(String localTerminal)
  {
    this.localTerminal = localTerminal;
  }


  public String getLocalTerminal()
  {
    return localTerminal;
  }


  public void setLocalAcceslevel(String localAcceslevel)
  {
    this.localAcceslevel = localAcceslevel;
  }


  public String getLocalAcceslevel()
  {
    return localAcceslevel;
  }
  //@@WPBN issue-26514

public String getCountryId() {
	return countryId;
} 

public void setCountryId(String countryId) {
	this.countryId = countryId;
}

public String getMultiQuote() {
	return multiQuote;
}

public void setMultiQuote(String multiQuote) {
	this.multiQuote = multiQuote;
}

}
