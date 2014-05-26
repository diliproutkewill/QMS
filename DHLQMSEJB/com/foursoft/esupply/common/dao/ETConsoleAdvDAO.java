package com.foursoft.esupply.common.dao;

//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETConsoleAdvVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;


public class ETConsoleAdvDAO extends ETAdvancedLOVMasterDAO
{
  public static String FILE_NAME = "ETConsoleAdvDAO.java";
  private static Logger logger = null;

  public ETConsoleAdvDAO()
  {
   logger  = Logger.getLogger(ETConsoleAdvDAO.class);
  }
  
  public  ArrayList getResult(ETAdvancedLOVMasterVO searchValues)
	{  
      ArrayList details =new ArrayList();
	    String query=null;
      PreparedStatement pStmt = null;
      Connection connection = null;
      ResultSet rs=null;
      ETConsoleAdvVO consoleAttributes =null;
	    String searchString = null;
      String operation          = null ;
      String fromWhat           = null ;
      StringBuffer whereClause  = null ; 
      StringBuffer fromClause   = null ;
      StringBuffer selectClause = null ; 
      
      try 
      {
     			connection = this.getConnection();
          consoleAttributes = (ETConsoleAdvVO)searchValues;
          operation = consoleAttributes.getOperation();
          fromWhat  = consoleAttributes.getFromWhat();
           
          //Logger.info(FILE_NAME,"operation is  :::: "+operation);
          logger.info(FILE_NAME+"operation is  :::: "+operation);
        //  Logger.info(FILE_NAME,"fromWhat is  :::: "+fromWhat);
                    
          if(fromWhat.equals("Consolidation"))
          {
          
                selectClause		=new StringBuffer("SELECT DISTINCT A.CONSOLEID,B.ORIGINGATEWAY,")
                  .append("B.DESTGATEWAY,B.PORTOFLOADING,B.PORTOFDISCHARGE,")
                  .append("B.CARRIERID,A.SERVICELEVELID,A.CUTOFFDATE,A.CONSOLTYPE");  
                              
                    if(operation.equals("Add") || operation.equals("Modify") || operation.equals("Close")
                                               || operation.equals("View"))
                    {
                         
                         fromClause =new StringBuffer(" FROM FS_FRS_CONSOLEMASTER A,FS_FRS_ROUTEMASTER B,FS_FRS_CONSOLEVESSELDTL V ");    
                    }
                    if(operation.equals("Add"))
                    {
                              whereClause =new StringBuffer(" WHERE B.ROUTEID=A.ROUTEID AND A.CONSOLESTATUS IN  ('N') ")
                              .append(" AND B.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("'")
                              .append(" AND A.CONSOLTYPE IN  (").append(consoleAttributes.getConsoleType()).append(")")
                              .append(" AND V.CONSOLEID=A.CONSOLEID ")
                              .append(" AND V.SNO =1 AND A.DIRECTCONSOLE='N' ");  
                    }
                    
                    if(operation.equals("Modify"))
                    {
              
                               whereClause =new StringBuffer("WHERE B.ROUTEID=A.ROUTEID AND A.CONSOLESTATUS IN   ('OPENED') ")
                              .append("AND  A.CONSOLEID NOT IN(SELECT CONSOLEID FROM FS_FRS_OBLMASTER)  ")
                              .append(" AND B.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("'")
                              .append(" AND A.CONSOLTYPE IN  (").append(consoleAttributes.getConsoleType()).append(")")
                              .append("AND V.CONSOLEID=A.CONSOLEID AND V.SNO =1 AND A.DIRECTCONSOLE='N' ");
                    }
                    
                    if(operation.equals("Close"))
                    {
              
                      whereClause =new StringBuffer("WHERE B.ROUTEID=A.ROUTEID AND A.CONSOLESTATUS IN   ('OPENED') ")
                      .append(" AND B.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("'")
                      .append(" AND A.CONSOLTYPE IN  (").append(consoleAttributes.getConsoleType()).append(")")
                      .append("AND V.CONSOLEID=A.CONSOLEID AND V.SNO =1 AND A.DIRECTCONSOLE='N'")   ;
                    }
                          
        
                    if(operation.equals("View"))
                    {
              
                        whereClause =new StringBuffer("WHERE B.ROUTEID=A.ROUTEID AND A.CONSOLESTATUS IN  ('OPENED','CLOSED')")
                        .append(" AND B.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("'")
                        .append(" AND A.CONSOLTYPE IN  (").append(consoleAttributes.getConsoleType()).append(")")  
                        .append("AND V.CONSOLEID=A.CONSOLEID AND V.SNO =1 AND A.DIRECTCONSOLE='N'");
                    }
                  
                  if(consoleAttributes.getConsoleId()!=null && !consoleAttributes.getConsoleId().equals(""))
                  { 
                        whereClause.append("AND A.CONSOLEID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getConsoleId()));
                        
                  }
                  if(consoleAttributes.getOriginGateway()!=null && !consoleAttributes.getOriginGateway().equals(""))
                  { 
                       whereClause.append("AND B.ORIGINGATEWAY ");
                       whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getOriginGateway()));
                       
                  }
                  if(consoleAttributes.getDestinationGateway()!=null && !consoleAttributes.getDestinationGateway().equals(""))
                  { 
                        whereClause.append("AND B.DESTGATEWAY ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getDestinationGateway()));
                        
                  }
                  if(consoleAttributes.getPortOfLoading()!=null && !consoleAttributes.getPortOfLoading().equals(""))
                  { 
                        whereClause.append("AND B.PORTOFLOADING ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfLoading()));
                        
                  }
                  if(consoleAttributes.getPortOfDestination()!=null && !consoleAttributes.getPortOfDestination().equals(""))
                  { 
                        whereClause.append("AND B.PORTOFDISCHARGE ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfDestination()));
                        
                  }
                  if(consoleAttributes.getCarrierId()!=null && !consoleAttributes.getCarrierId().equals(""))
                  { 
                        whereClause.append("AND B.CARRIERID "); 
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getCarrierId()));
                        
                  }
                  if(consoleAttributes.getServiceLevel()!=null && !consoleAttributes.getServiceLevel().equals(""))
                  { 
                        whereClause.append("AND A.SERVICELEVELID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getServiceLevel()));
                        
                  }
                  if(consoleAttributes.getCutOffDays()!=null && !consoleAttributes.getCutOffDays().equals(""))
                  { 
                      
                      whereClause.append("AND TO_DATE(TO_CHAR(A.CUTOFFDATE,'dd-mon-yyyy'),'dd-mon-yyyy') = ")
                      .append(" TO_DATE('").append(consoleAttributes.getCutOffDays()).append("','")
                      .append(consoleAttributes.getDateFormat()).append("')");
                      
                  }
                  if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                  {
                       searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"A.CONSOLEDATE");
                       whereClause.append(searchString);
                  }
                  
            StringBuffer resultQuery= new StringBuffer();
            resultQuery.append(selectClause);
            resultQuery.append(fromClause);
            resultQuery.append(whereClause);
            resultQuery.append(" ORDER BY A.CONSOLEID ");
            pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

            while(rs.next())
            {
                 ETConsoleAdvVO consoleAttributesArr =new ETConsoleAdvVO();
                 consoleAttributesArr.setConsoleId(rs.getString(1));
                 consoleAttributesArr.setOriginGateway(rs.getString(2));
                 consoleAttributesArr.setDestinationGateway(rs.getString(3));
                 consoleAttributesArr.setPortOfLoading(rs.getString(4));
                 consoleAttributesArr.setPortOfDestination(rs.getString(5));
                 consoleAttributesArr.setCarrierId(rs.getString(6));
                 consoleAttributesArr.setServiceLevel(rs.getString(7));
                 consoleAttributesArr.setCutOffDays(rs.getString(8));
                 consoleAttributesArr.setConsoleType(rs.getString(9)); 
                 details.add(consoleAttributesArr);
            }	
            //Added By RajKumari on 23-10-2008 for Connection Leakages.
             if(pStmt!=null)
              pStmt.close();
              
            return details;                                
          
         }
         
          
          else if(fromWhat.equals("OceanBL"))
          {
            
            if(operation.equals("Add"))
            {
            
                selectClause		=new StringBuffer("SELECT DISTINCT CCD.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                      .append("RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE ");
                fromClause =new StringBuffer(" FROM FS_FRS_CONSOLECONSOLIDATIONDTL CCD,FS_FRS_CONSOLEMASTER CM,FS_FRS_ROUTEMASTER RM ");
                whereClause =new StringBuffer("WHERE CM.ROUTEID = RM.ROUTEID AND CM.CONSOLEID = CCD.CONSOLEID AND  ")
                      .append("CCD.CONSOLEID IN (SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER MM,  ")
                      .append("FS_FRS_ROUTEMASTER RD WHERE  MM.CONSOLESTATUS = 'CLOSED' ")
                      .append(" AND  MM.ROUTEID = RD.ROUTEID  ")
                      .append(" AND RD.ORIGINGATEWAY= '").append(consoleAttributes.getOriginGateway()).append("')")             
                      .append("  AND  CCD.CONSOLEID NOT IN (SELECT CONSOLEID ")
                      .append(" FROM FS_FRS_OBLMASTER) ");
            }
            if(operation.equals("Modify"))
            {
            
                selectClause		=new StringBuffer("SELECT DISTINCT CCD.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                      .append("RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE ");
                fromClause =new StringBuffer(" FROM FS_FRS_CONSOLECONSOLIDATIONDTL CCD,FS_FRS_CONSOLEMASTER CM,FS_FRS_ROUTEMASTER RM ");
                whereClause =new StringBuffer("WHERE CM.ROUTEID = RM.ROUTEID AND CM.CONSOLEID = CCD.CONSOLEID AND  ")
                      .append("CCD.CONSOLEID IN (SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER MM,  ")
                      .append("FS_FRS_ROUTEMASTER RD WHERE  MM.CONSOLESTATUS = 'CLOSED' ")
                      .append("AND  MM.RECEIVEDSTATUS IS NULL  AND  MM.ROUTEID = RD.ROUTEID  ")
                      .append("AND RD.ORIGINGATEWAY= '").append(consoleAttributes.getOriginGateway()).append("')")             
                      .append("  AND  CCD.CONSOLEID IN (SELECT CONSOLEID ")
                      .append(" FROM FS_FRS_OBLMASTER) ");
            }
            if(operation.equals("CostAdd"))
            {
            
                    selectClause		=new StringBuffer("SELECT DISTINCT CCD.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING,")
                         .append("RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE");
                    fromClause =new StringBuffer(" FROM FS_FRS_CONSOLECONSOLIDATIONDTL CCD,FS_FRS_CONSOLEMASTER CM,")
                         .append(" FS_FRS_ROUTEMASTER RM   ");
                    whereClause =new StringBuffer(" WHERE CM.CONSOLEID     = CCD.CONSOLEID AND CM.ROUTEID   = RM.ROUTEID AND")    
                          .append(" CCD.CONSOLEID IN (SELECT CONSOLEID 	FROM FS_FRS_CONSOLEMASTER MM, FS_FRS_ROUTEMASTER RD  WHERE ")
                          .append(" MM.CONSOLESTATUS = 'CLOSED' AND  MM.ROUTEID = RD.ROUTEID  AND ")
                          .append(" RM.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("')")
                          .append(" AND   CCD.CONSOLEID IN  ( SELECT CONSOLEID FROM FS_FRS_OBLMASTER  WHERE  ACCOUNTSTATUS IS NULL  ) ")
                          .append(" AND RM.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("'");
                          // @@ Murali on 20050401
                          if(consoleAttributes.getConsoleId()!=null && !consoleAttributes.getConsoleId().equals(""))
                          { 
                                  whereClause.append("AND CCD.CONSOLEID ");
                                  whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getConsoleId()));
                      
                            }
                            if(consoleAttributes.getOriginGateway()!=null && !consoleAttributes.getOriginGateway().equals(""))
                            { 
                      
                                  whereClause.append("AND RM.ORIGINGATEWAY ");
                                  whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getOriginGateway()));
                      
                            }
                            if(consoleAttributes.getDestinationGateway()!=null && !consoleAttributes.getDestinationGateway().equals(""))
                            { 
                                  whereClause.append("AND RM.DESTGATEWAY ");
                                  whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getDestinationGateway()));
                                  
                            }
                            if(consoleAttributes.getPortOfLoading()!=null && !consoleAttributes.getPortOfLoading().equals(""))
                            { 
                                  whereClause.append("AND RM.PORTOFLOADING ");
                                  whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfLoading()));
                                  
                            }
                            if(consoleAttributes.getPortOfDestination()!=null && !consoleAttributes.getPortOfDestination().equals(""))
                            { 
                                  whereClause.append("AND RM.PORTOFDISCHARGE ");
                                  whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfDestination()));
                                  
                            }
                            if(consoleAttributes.getCarrierId()!=null && !consoleAttributes.getCarrierId().equals(""))
                            { 
                                  whereClause.append("AND RM.CARRIERID "); 
                                  whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getCarrierId()));
                                  
                            }
                            if(consoleAttributes.getServiceLevel()!=null && !consoleAttributes.getServiceLevel().equals(""))
                            { 
                                  whereClause.append("AND CM.SERVICELEVELID ");
                                  whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getServiceLevel()));
                                  
                            }
                            if(consoleAttributes.getCutOffDays()!=null && !consoleAttributes.getCutOffDays().equals(""))
                            { 
                                
                                whereClause.append("AND TO_DATE(TO_CHAR(CM.CUTOFFDATE,'dd-mon-yyyy'),'dd-mon-yyyy') = ")
                                .append(" TO_DATE('").append(consoleAttributes.getCutOffDays()).append("','")
                                .append(consoleAttributes.getDateFormat()).append("')");
                                
                            }
                            if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                            {
                                 searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CM.CONSOLEDATE");
                                 whereClause.append(searchString);
                            }
                          // @@ Murali
                          whereClause.append(" UNION SELECT  DISTINCT    CM.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                          .append(" RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE FROM  ")
                          .append(" FS_FRS_CONSOLEMASTER  CM,FS_FRS_ROUTEMASTER RM WHERE CM.ROUTEID   = RM.ROUTEID  AND ")
                          .append(" CM.CONSOLEID IN ( SELECT CONSOLEID FROM FS_FRS_OBLMASTER WHERE ACCOUNTSTATUS IS NULL )  AND  ")
                          .append(" ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                          
            }
            if(operation.equals("CostModify"))
            {
                
                  selectClause		=new StringBuffer("SELECT   DISTINCT CCD.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                          .append(" RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE ");
                  fromClause =new StringBuffer(" FROM  FS_FRS_CONSOLECONSOLIDATIONDTL CCD,FS_FRS_CONSOLEMASTER CM,FS_FRS_ROUTEMASTER RM ");
                  whereClause =new StringBuffer(" WHERE CM.ROUTEID = RM.ROUTEID AND CM.CONSOLEID = CCD.CONSOLEID AND ")
                          .append(" CCD.CONSOLEID IN (SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER MM,FS_FRS_ROUTEMASTER RD  ")
                          .append(" WHERE  MM.CONSOLESTATUS = 'CLOSED' AND  MM.ROUTEID = RD.ROUTEID  AND ")
                          .append(" RM.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("')")
                          .append(" AND CCD.CONSOLEID IN (SELECT CONSOLEID FROM FS_FRS_OBLMASTER  WHERE  ")
                          .append(" ACCOUNTSTATUS = 'N' )  AND ")
                          .append(" ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                          if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                          {
                               searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CM.CONSOLEDATE");
                               whereClause.append(searchString);
                          }
                          whereClause.append(" UNION SELECT  DISTINCT    CM.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                          .append(" RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE FROM  ")
                          .append(" FS_FRS_CONSOLEMASTER  CM,FS_FRS_ROUTEMASTER RM WHERE CM.ROUTEID   = RM.ROUTEID  AND ")
                          .append(" CM.CONSOLEID IN ( SELECT 	CONSOLEID FROM FS_FRS_OBLMASTER WHERE  ACCOUNTSTATUS = 'N' )  AND ")
                          .append(" ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");                          
            }
            if(operation.equals("CostView"))
            {
            
                  selectClause		=new StringBuffer("SELECT   DISTINCT CCD.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                          .append(" RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE ");
                  fromClause =new StringBuffer(" FROM  FS_FRS_CONSOLECONSOLIDATIONDTL CCD,FS_FRS_CONSOLEMASTER CM,FS_FRS_ROUTEMASTER RM ");
                  whereClause =new StringBuffer(" WHERE CM.ROUTEID = RM.ROUTEID AND CM.CONSOLEID = CCD.CONSOLEID AND ")
                          .append(" CCD.CONSOLEID IN (SELECT CONSOLEID 	FROM FS_FRS_CONSOLEMASTER MM, FS_FRS_ROUTEMASTER RD  WHERE  ")
                          .append(" MM.CONSOLESTATUS = 'CLOSED' AND  MM.ROUTEID = RD.ROUTEID  AND ")
                          .append(" RM.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("')")
                          .append(" AND CCD.CONSOLEID IN (SELECT CONSOLEID FROM FS_FRS_OBLMASTER  WHERE  ACCOUNTSTATUS IN ('N','C')  )  ")
                           .append(" AND ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                          if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                          {
                               searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CM.CONSOLEDATE");
                               whereClause.append(searchString);
                          }
                           whereClause.append(" UNION SELECT  DISTINCT    CM.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                          .append(" RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE FROM  ")
                          .append(" FS_FRS_CONSOLEMASTER  CM,FS_FRS_ROUTEMASTER RM WHERE CM.ROUTEID   = RM.ROUTEID  AND ")
                          .append(" CM.CONSOLEID IN ( SELECT CONSOLEID FROM FS_FRS_OBLMASTER ")
                          .append(" WHERE  ACCOUNTSTATUS IN ('N','C') )  AND ")
                          .append(" ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                        
            }
            if(operation.equals("TransferToAccounts"))
            {
            
                  selectClause		=new StringBuffer("SELECT   DISTINCT CCD.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                          .append(" RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE ");
                  fromClause =new StringBuffer(" FROM  FS_FRS_CONSOLECONSOLIDATIONDTL CCD,FS_FRS_CONSOLEMASTER CM,FS_FRS_ROUTEMASTER RM ");
                  whereClause =new StringBuffer(" WHERE CM.ROUTEID = RM.ROUTEID AND CM.CONSOLEID = CCD.CONSOLEID AND ")
                          .append(" CCD.CONSOLEID IN (SELECT CONSOLEID 	FROM FS_FRS_CONSOLEMASTER MM, FS_FRS_ROUTEMASTER  ")
                          .append(" RD  WHERE  MM.CONSOLESTATUS = 'CLOSED' AND  MM.ROUTEID = RD.ROUTEID  AND ")
                          .append(" RM.ORIGINGATEWAY = '").append(consoleAttributes.getOriginGateway()).append("')")
                          .append(" AND   CCD.CONSOLEID IN  (SELECT CONSOLEID FROM FS_FRS_OBLMASTER  WHERE  ACCOUNTSTATUS = 'N'  ) ")
                           .append(" AND ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                          if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                          {
                               searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CM.CONSOLEDATE");
                               whereClause.append(searchString);
                          }
                           whereClause.append(" UNION SELECT  DISTINCT    CM.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING, ")
                          .append(" RM.PORTOFDISCHARGE,RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE FROM  ")
                          .append(" FS_FRS_CONSOLEMASTER  CM,FS_FRS_ROUTEMASTER RM WHERE CM.ROUTEID   = RM.ROUTEID  AND ")
                          .append(" CM.CONSOLEID IN ( SELECT CONSOLEID ")
                          .append(" FROM FS_FRS_OBLMASTER WHERE  ACCOUNTSTATUS = 'N' )  ")
                          .append(" AND ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");                          
            }
            
            
            
                  if(consoleAttributes.getConsoleId()!=null && !consoleAttributes.getConsoleId().equals(""))
                  { 
                        // @@ Murali 
                        whereClause.append("AND CM.CONSOLEID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getConsoleId()));
            
                  }
                  if(consoleAttributes.getOriginGateway()!=null && !consoleAttributes.getOriginGateway().equals(""))
                  { 
            
                        whereClause.append("AND RM.ORIGINGATEWAY ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getOriginGateway()));
            
                  }
                  if(consoleAttributes.getDestinationGateway()!=null && !consoleAttributes.getDestinationGateway().equals(""))
                  { 
                        whereClause.append("AND RM.DESTGATEWAY ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getDestinationGateway()));
                        
                  }
                  if(consoleAttributes.getPortOfLoading()!=null && !consoleAttributes.getPortOfLoading().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFLOADING ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfLoading()));
                        
                  }
                  if(consoleAttributes.getPortOfDestination()!=null && !consoleAttributes.getPortOfDestination().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFDISCHARGE ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfDestination()));
                        
                  }
                  if(consoleAttributes.getCarrierId()!=null && !consoleAttributes.getCarrierId().equals(""))
                  { 
                        whereClause.append("AND RM.CARRIERID "); 
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getCarrierId()));
                        
                  }
                  if(consoleAttributes.getServiceLevel()!=null && !consoleAttributes.getServiceLevel().equals(""))
                  { 
                        whereClause.append("AND CM.SERVICELEVELID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getServiceLevel()));
                        
                  }
                  if(consoleAttributes.getCutOffDays()!=null && !consoleAttributes.getCutOffDays().equals(""))
                  { 
                      
                      whereClause.append("AND TO_DATE(TO_CHAR(CM.CUTOFFDATE,'dd-mon-yyyy'),'dd-mon-yyyy') = ")
                      .append(" TO_DATE('").append(consoleAttributes.getCutOffDays()).append("','")
                      .append(consoleAttributes.getDateFormat()).append("')");
                      
                  }
                  if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                  {
                       searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CM.CONSOLEDATE");
                       whereClause.append(searchString);
                  }
                  
            
          
            StringBuffer resultQuery= new StringBuffer();
            resultQuery.append(selectClause);
            resultQuery.append(fromClause);
            resultQuery.append(whereClause);
            // resultQuery.append(" ORDER BY CCD.CONSOLEID ");
            pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

            while(rs.next())
            {
                 ETConsoleAdvVO consoleAttributesArr =new ETConsoleAdvVO();
                 consoleAttributesArr.setConsoleId(rs.getString(1));
                 consoleAttributesArr.setOriginGateway(rs.getString(2));
                 consoleAttributesArr.setDestinationGateway(rs.getString(3));
                 consoleAttributesArr.setPortOfLoading(rs.getString(4));
                 consoleAttributesArr.setPortOfDestination(rs.getString(5));
                 consoleAttributesArr.setCarrierId(rs.getString(6));
                 consoleAttributesArr.setServiceLevel(rs.getString(7));
                 consoleAttributesArr.setCutOffDays(rs.getString(8));
                 consoleAttributesArr.setConsoleType(rs.getString(9)); 
                 details.add(consoleAttributesArr);
            }		
            //Added By RajKumari on 23-10-2008 for Connection Leakages.
             if(pStmt!=null)
              pStmt.close();
              
            return details;
          }
          
          else if(fromWhat.equals("DirectConsole") || fromWhat.equalsIgnoreCase("Console") ) 
                  
          {
            
            
            if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify")
               || (operation.equals("View"))     || operation.equals("Delete")
               || operation.equals("Close")     || operation.equalsIgnoreCase("OBLGeneration"))
            {
                  selectClause		=new StringBuffer("SELECT CON.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,")
                          .append(" RM.PORTOFLOADING,RM.PORTOFDISCHARGE,")
                          .append(" RM.CARRIERID,CON.SERVICELEVELID,CON.CUTOFFDATE,CON.CONSOLTYPE ");
                  fromClause =new StringBuffer(" FROM FS_FRS_CONSOLEMASTER CON,FS_FRS_ROUTEMASTER RM ");        
            }
            
            
            if(operation.equals("Modify"))
            {
                 
                  if(fromWhat.equalsIgnoreCase("DirectConsole"))
                  {
                      whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.DIRECTCONSOLE='Y' AND CON.CONSOLESTATUS!='CLOSED' ")
                          .append(" AND  CON.ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                  }
                  else
                  {
                      whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.ROUTEID IN (SELECT DISTINCT(ROUTEID) ")
                          .append(" FROM FS_FRS_ROUTEMASTER WHERE ")
                          .append(" ORIGINGATEWAY= '").append(consoleAttributes.getOriginGateway()).append("')")             
                          .append(" AND CON.CONSOLESTATUS ='N' AND CON.CONSOLTYPE NOT IN('FCL_BACK_TO_BACK','BREAK_BULK') AND CON.DIRECTCONSOLE = 'N' ");
                  }
            
            }
            if(operation.equals("View"))
            {
                
                  if(fromWhat.equalsIgnoreCase("DirectConsole"))
                  {
                      whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.DIRECTCONSOLE='Y' ")
                          .append(" AND  CON.ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                  }
                  else
                  {
                      whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.ROUTEID IN (SELECT DISTINCT(ROUTEID) ")
                          .append(" FROM FS_FRS_ROUTEMASTER WHERE ")
                          .append(" ORIGINGATEWAY= '").append(consoleAttributes.getOriginGateway()).append("')")             
                          .append(" AND CON.CONSOLTYPE NOT IN('FCL_BACK_TO_BACK','BREAK_BULK') AND CON.DIRECTCONSOLE = 'N' ");
                  }
           
            }
            if(operation.equals("Delete"))
            {
           
                  if(fromWhat.equalsIgnoreCase("DirectConsole"))
                  {
                     whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.DIRECTCONSOLE='Y' AND CON.CONSOLESTATUS!='CLOSED' ")
                          .append(" AND ORIGINSTATUS IS NULL  AND ")
                          .append(" CON.ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
                  }
                  else
                  {
                      whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.ROUTEID IN ")
                         .append(" (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ")
                         .append("  ORIGINGATEWAY= '").append(consoleAttributes.getOriginGateway()).append("')")             
                         .append("  AND CON.CONSOLTYPE NOT IN ('FCL_BACK_TO_BACK','BREAK_BULK' ) AND CON.CONSOLEID NOT IN ( ")
                         .append(" SELECT CM.CONSOLEID FROM  FS_FRS_CONSOLEMASTER CM,FS_FRS_OBLMASTER OM ")
                         .append(" WHERE CM.CONSOLEID = OM.CONSOLEID ) AND CON.DIRECTCONSOLE = 'N'  ");
                  }
            
            }
            if(operation.equals("Close"))
            {
           
                  whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.DIRECTCONSOLE='Y' AND CON.CONSOLESTATUS!='CLOSED' ")
                          .append(" AND  CON.ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'");
           
            }
           
            if(operation.equalsIgnoreCase("OBLGeneration"))
            {
           
                  whereClause =new StringBuffer(" WHERE CON.ROUTEID  = RM.ROUTEID AND CON.DIRECTCONSOLE='Y' ")
                          .append(" AND  ORIGINTERMINAL = '").append(consoleAttributes.getOriginGateway()).append("'")
                          .append(" AND CONSOLESTATUS='CLOSED' AND CONSOLEID NOT IN ( SELECT CONSOLEID FROM FS_FRS_OBLMASTER) ") ;
           
            }
            
                  if(consoleAttributes.getConsoleId()!=null && !consoleAttributes.getConsoleId().equals(""))
                  { 
                        whereClause.append("AND CON.CONSOLEID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getConsoleId()));
           
                  }
                  if(consoleAttributes.getOriginGateway()!=null && !consoleAttributes.getOriginGateway().equals(""))
                  { 
           
                       whereClause.append("AND RM.ORIGINGATEWAY ");
                       whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getOriginGateway()));
           
                  }
                  if(consoleAttributes.getDestinationGateway()!=null && !consoleAttributes.getDestinationGateway().equals(""))
                  { 
                        whereClause.append("AND RM.DESTGATEWAY ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getDestinationGateway()));
           
                  }
                  if(consoleAttributes.getPortOfLoading()!=null && !consoleAttributes.getPortOfLoading().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFLOADING ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfLoading()));
           
                  }
                  if(consoleAttributes.getPortOfDestination()!=null && !consoleAttributes.getPortOfDestination().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFDISCHARGE ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfDestination()));
           
                  }
                  if(consoleAttributes.getCarrierId()!=null && !consoleAttributes.getCarrierId().equals(""))
                  { 
                        whereClause.append("AND RM.CARRIERID "); 
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getCarrierId()));
           
                  }
                  if(consoleAttributes.getServiceLevel()!=null && !consoleAttributes.getServiceLevel().equals(""))
                  { 
                        whereClause.append("AND CON.SERVICELEVELID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getServiceLevel()));
           
                  }
                  if(consoleAttributes.getCutOffDays()!=null && !consoleAttributes.getCutOffDays().equals(""))
                  { 
                      whereClause.append("AND TO_DATE(TO_CHAR(CON.CUTOFFDATE,'dd-mon-yyyy'),'dd-mon-yyyy') = ")
                      .append(" TO_DATE('").append(consoleAttributes.getCutOffDays()).append("','")
                      .append(consoleAttributes.getDateFormat()).append("')");
           
                  }
                  if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                  {
                       searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CON.CONSOLEDATE");
                       whereClause.append(searchString);
                  }
           
          
           
            StringBuffer resultQuery= new StringBuffer();
            resultQuery.append(selectClause);
            resultQuery.append(fromClause);
            resultQuery.append(whereClause);
            resultQuery.append(" ORDER BY CON.CONSOLEID ");
            pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

            while(rs.next())
            {
                 ETConsoleAdvVO consoleAttributesArr =new ETConsoleAdvVO();
                 consoleAttributesArr.setConsoleId(rs.getString(1));
                 consoleAttributesArr.setOriginGateway(rs.getString(2));
                 consoleAttributesArr.setDestinationGateway(rs.getString(3));
                 consoleAttributesArr.setPortOfLoading(rs.getString(4));
                 consoleAttributesArr.setPortOfDestination(rs.getString(5));
                 consoleAttributesArr.setCarrierId(rs.getString(6));
                 consoleAttributesArr.setServiceLevel(rs.getString(7));
                 consoleAttributesArr.setCutOffDays(rs.getString(8));
                 consoleAttributesArr.setConsoleType(rs.getString(9)); 
                 details.add(consoleAttributesArr);
            }
            //Added By RajKumari on 23-10-2008 for Connection Leakages.
             if(pStmt!=null)
              pStmt.close();
              
            return details;
          }
           
            if(fromWhat.equals("ConsoleActivity") && operation.equalsIgnoreCase("Add"))
            {
              if(fromWhat.equals("ConsoleActivity") && operation.equalsIgnoreCase("Add"))
              {
           
                 selectClause		=new StringBuffer("SELECT CON.CONSOLEID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,")
                          .append(" RM.PORTOFLOADING,RM.PORTOFDISCHARGE,")
                          .append(" RM.CARRIERID,CON.SERVICELEVELID,CON.CUTOFFDATE,CON.CONSOLTYPE ");
                  fromClause =new StringBuffer(" FROM FS_FRS_CONSOLEMASTER CON,FS_FRS_ROUTEMASTER RM ");    
               
                  whereClause =new StringBuffer(" WHERE CON.ROUTEID = RM.ROUTEID AND CON.ROUTEID IN (SELECT ")
                    .append(" ROUTEID FROM FS_FRS_ROUTEMASTER WHERE ")
                    .append(" ORIGINGATEWAY= '").append(consoleAttributes.getOriginGateway()).append("')")     
                    .append("  AND CON.CONSOLEID NOT IN (SELECT DOCID FROM FS_FR_HOUSEACTIVITYHDR WHERE ")
                    .append("TERMINALID = '").append(consoleAttributes.getOriginGateway()).append("'")
                    .append(" ) AND CON.CONSOLESTATUS!='CLOSED' ");
               
            }
            
                  if(consoleAttributes.getConsoleId()!=null && !consoleAttributes.getConsoleId().equals(""))
                  { 
                        whereClause.append("AND CON.CONSOLEID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getConsoleId()));
            
                  }
                  if(consoleAttributes.getOriginGateway()!=null && !consoleAttributes.getOriginGateway().equals(""))
                  { 
            
                       whereClause.append("AND RM.ORIGINGATEWAY ");
                       whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getOriginGateway()));
            
                  }
                  if(consoleAttributes.getDestinationGateway()!=null && !consoleAttributes.getDestinationGateway().equals(""))
                  { 
                        whereClause.append("AND RM.DESTGATEWAY ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getDestinationGateway()));
            
                  }
                  if(consoleAttributes.getPortOfLoading()!=null && !consoleAttributes.getPortOfLoading().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFLOADING ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfLoading()));
            
                  }
                  if(consoleAttributes.getPortOfDestination()!=null && !consoleAttributes.getPortOfDestination().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFDISCHARGE ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfDestination()));
            
                  }
                  if(consoleAttributes.getCarrierId()!=null && !consoleAttributes.getCarrierId().equals(""))
                  { 
                        whereClause.append("AND RM.CARRIERID "); 
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getCarrierId()));
            
                  }
                  if(consoleAttributes.getServiceLevel()!=null && !consoleAttributes.getServiceLevel().equals(""))
                  { 
                        whereClause.append("AND CON.SERVICELEVELID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getServiceLevel()));
            
                  }
                  if(consoleAttributes.getCutOffDays()!=null && !consoleAttributes.getCutOffDays().equals(""))
                  { 
                      whereClause.append("AND TO_DATE(TO_CHAR(CON.CUTOFFDATE,'dd-mon-yyyy'),'dd-mon-yyyy') = ")
                      .append(" TO_DATE('").append(consoleAttributes.getCutOffDays()).append("','")
                      .append(consoleAttributes.getDateFormat()).append("')");
            
                  }
                  if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                  {
                       searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CON.CONSOLEDATE");
                       whereClause.append(searchString);
                  }
            
          
            
            StringBuffer resultQuery= new StringBuffer();
            resultQuery.append(selectClause);
            resultQuery.append(fromClause);
            resultQuery.append(whereClause);
            resultQuery.append(" ORDER BY CON.CONSOLEID ");
            pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

            while(rs.next())
            {
                 ETConsoleAdvVO consoleAttributesArr =new ETConsoleAdvVO();
                 consoleAttributesArr.setConsoleId(rs.getString(1));
                 consoleAttributesArr.setOriginGateway(rs.getString(2));
                 consoleAttributesArr.setDestinationGateway(rs.getString(3));
                 consoleAttributesArr.setPortOfLoading(rs.getString(4));
                 consoleAttributesArr.setPortOfDestination(rs.getString(5));
                 consoleAttributesArr.setCarrierId(rs.getString(6));
                 consoleAttributesArr.setServiceLevel(rs.getString(7));
                 consoleAttributesArr.setCutOffDays(rs.getString(8));
                 consoleAttributesArr.setConsoleType(rs.getString(9)); 
                 details.add(consoleAttributesArr);
            }
            //Added By RajKumari on 23-10-2008 for Connection Leakages.
             if(pStmt!=null)
              pStmt.close();
              
            return details;
          }
            
            
            if(fromWhat.equals("ConsoleActivity")) 
            {
                if(fromWhat.equals("ConsoleActivity") && operation.equalsIgnoreCase("Modify")
                   || operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("Delete"))
                {
                      
            
                      selectClause		=new StringBuffer("SELECT HA.DOCID,RM.ORIGINGATEWAY,RM.DESTGATEWAY,RM.PORTOFLOADING,RM.PORTOFDISCHARGE,")
                                 .append("RM.CARRIERID,CM.SERVICELEVELID,CM.CUTOFFDATE,CM.CONSOLTYPE ");
                      fromClause =new StringBuffer(" FROM FS_FR_HOUSEACTIVITYHDR HA, FS_FRS_CONSOLEMASTER CM, FS_FRS_ROUTEMASTER RM");    
                      whereClause =new StringBuffer(" WHERE CM.CONSOLEID(+)  = HA.DOCID AND CM.ROUTEID   = RM.ROUTEID(+) ")
                        .append(" AND HA.TERMINALID = '").append(consoleAttributes.getOriginGateway()).append("'")     
                        .append("  AND HA.SHIPMENTMODE= 2 AND HA.DOCTYPE='M' AND CM.CONSOLESTATUS != 'CLOSED' ");
                  }
                  if(consoleAttributes.getConsoleId()!=null && !consoleAttributes.getConsoleId().equals(""))
                  { 
                        whereClause.append("AND HA.DOCID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getConsoleId()));
            
                  }
                  if(consoleAttributes.getOriginGateway()!=null && !consoleAttributes.getOriginGateway().equals(""))
                  { 
            
                       whereClause.append("AND RM.ORIGINGATEWAY ");
                       whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getOriginGateway()));
                       
                  }
                  if(consoleAttributes.getDestinationGateway()!=null && !consoleAttributes.getDestinationGateway().equals(""))
                  { 
                        whereClause.append("AND RM.DESTGATEWAY ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getDestinationGateway()));
                       
                  }
                  if(consoleAttributes.getPortOfLoading()!=null && !consoleAttributes.getPortOfLoading().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFLOADING ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfLoading()));
                       
                  }
                  if(consoleAttributes.getPortOfDestination()!=null && !consoleAttributes.getPortOfDestination().equals(""))
                  { 
                        whereClause.append("AND RM.PORTOFDISCHARGE ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getPortOfDestination()));
                       
                  }
                  if(consoleAttributes.getCarrierId()!=null && !consoleAttributes.getCarrierId().equals(""))
                  { 
                        whereClause.append("AND RM.CARRIERID "); 
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getCarrierId()));
                       
                  }
                  if(consoleAttributes.getServiceLevel()!=null && !consoleAttributes.getServiceLevel().equals(""))
                  { 
                        whereClause.append("AND CM.SERVICELEVELID ");
                        whereClause.append(getSearchString(searchValues.getSearchType(),consoleAttributes.getServiceLevel()));
                       
                  }
                  if(consoleAttributes.getCutOffDays()!=null && !consoleAttributes.getCutOffDays().equals(""))
                  { 
                      
                      whereClause.append("AND TO_DATE(TO_CHAR(A.CUTOFFDATE,'dd-mon-yyyy'),'dd-mon-yyyy') = ")
                      .append(" TO_DATE('").append(consoleAttributes.getCutOffDays()).append("','")
                      .append(consoleAttributes.getDateFormat()).append("')");
                      
                      
                  }
                  if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
                  {
                       searchString = getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"CM.CONSOLEDATE");
                       whereClause.append(searchString);
                  }
            
                   
            StringBuffer resultQuery= new StringBuffer();
            resultQuery.append(selectClause);
            resultQuery.append(fromClause);
            resultQuery.append(whereClause);
            // resultQuery.append(" ORDER BY CON.CONSOLEID ");
            pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

            while(rs.next())
            {
                 ETConsoleAdvVO consoleAttributesArr =new ETConsoleAdvVO();
                 consoleAttributesArr.setConsoleId(rs.getString(1));
                 consoleAttributesArr.setOriginGateway(rs.getString(2));
                 consoleAttributesArr.setDestinationGateway(rs.getString(3));
                 consoleAttributesArr.setPortOfLoading(rs.getString(4));
                 consoleAttributesArr.setPortOfDestination(rs.getString(5));
                 consoleAttributesArr.setCarrierId(rs.getString(6));
                 consoleAttributesArr.setServiceLevel(rs.getString(7));
                 consoleAttributesArr.setCutOffDays(rs.getString(8));
                 consoleAttributesArr.setConsoleType(rs.getString(9)); 
                 details.add(consoleAttributesArr);
            }		
                             
            return details;      
            }            
      }
      

      catch(Exception e)
      {
            e.printStackTrace();
            throw new EJBException(e.getMessage());
      }
      finally
      {
            try
            {
                if(rs!=null)
                {
                    rs.close();
                }
                if(pStmt!=null)
                {
                    pStmt.close();
                }
                if(connection!=null)
                {
                    connection.close();
                }
            }
            catch(SQLException sq)
            {
                  throw new EJBException(sq.getMessage());
            }
      }
      return  details;           
}
private  Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
  
}