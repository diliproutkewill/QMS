package com.foursoft.esupply.common.dao;

//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETCarrierContractAdvVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

public class ETCarrierContractAdvDAO extends ETAdvancedLOVMasterDAO 
{
 private static Logger logger = null;

  public ETCarrierContractAdvDAO()
  {
  logger  = Logger.getLogger(ETCarrierContractAdvDAO.class);
  }
  public static String FILE_NAME = "ETCarrierContractAdvDAO.java";
 
  
  public  ArrayList getResult(ETAdvancedLOVMasterVO searchValues)
	{  
	   ArrayList details =new ArrayList();
	   
	   String query=null;
	   PreparedStatement pStmt = null;
       Connection connection = null;
       ResultSet rs=null;
       ETCarrierContractAdvVO carContractAttributes =null;
	   String searchString =null;
       

	   
     try {
     
			connection = this.getConnection();
//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5236 on 20050430.
			StringBuffer selectClause		=new StringBuffer("SELECT MASTER.CACONTRACTID,MASTER.CARRIERID,A.CARRIERNAME,DTL.ORIGINGATEWAYID, DTL.DESTGATEWAYID,DTL.SERVICELEVEL,MASTER.VALIDDATE,MASTER.ACTV_FLAG ");

			StringBuffer fromClause =new StringBuffer(" FROM FS_FR_CACONTRACTMASTER MASTER,FS_FR_CACONTRACTDTL DTL,FS_FR_CAMASTER A ");
			StringBuffer whereClause =new StringBuffer(" WHERE DTL.CACONTRACTID = MASTER.CACONTRACTID AND MASTER.CARRIERID =A.CARRIERID    ");

			

			StringBuffer actualQuery=new StringBuffer(" AND  MASTER.CACONTRACTID IN(SELECT DISTINCT(FS_FR_CACONTRACTDTL.CACONTRACTID) CACONTRACTID FROM FS_FR_CACONTRACTDTL,	FS_FR_CACONTRACTMASTER WHERE	FS_FR_CACONTRACTDTL.CACONTRACTID = FS_FR_CACONTRACTMASTER.CACONTRACTID  AND FS_FR_CACONTRACTMASTER.SHIPMENTMODE =	1 AND ((SELECT SYSDATE FROM DUAL) BETWEEN CONTRACTDATE AND (VALIDDATE+1))  )");
 
            

        	carContractAttributes = (ETCarrierContractAdvVO)searchValues;
			//Logger.info(FILE_NAME,"...............ETCarrierContractAdvVO........searchValues..........."+searchValues.getTerminalID());
			if(searchValues.getTerminalID()!=null && !searchValues.getTerminalID().equals(""))
		         { 
						actualQuery.append(" AND MASTER.TERMINALID	= '");
						actualQuery.append(searchValues.getTerminalID());
						actualQuery.append("' ");
			     }
            
			if(carContractAttributes.getContractId()!=null && !carContractAttributes.getContractId().equals(""))
		         { 
				    whereClause.append("AND MASTER.CACONTRACTID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),carContractAttributes.getContractId()));
				 }
            if(carContractAttributes.getCarrierId()!=null && !carContractAttributes.getCarrierId().equals(""))
		         { 
				    whereClause.append("AND MASTER.CARRIERID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),carContractAttributes.getCarrierId()));
				 }
            if(carContractAttributes.getOriginTerminalId()!=null && !carContractAttributes.getOriginTerminalId().equals(""))
		         { 
				    whereClause.append("AND DTL.ORIGINGATEWAYID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),carContractAttributes.getOriginTerminalId()));
				 }
            if(carContractAttributes.getDestTerminalId()!=null && !carContractAttributes.getDestTerminalId().equals(""))
		         { 
				    
					whereClause.append("AND DTL.DESTGATEWAYID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),carContractAttributes.getDestTerminalId()));
				 }
             if(carContractAttributes.getServiceLevelId()!=null && !carContractAttributes.getServiceLevelId().equals(""))
		         { 
				   
					whereClause.append("AND DTL.SERVICELEVEL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),carContractAttributes.getServiceLevelId()));
				 }
             if(carContractAttributes.getActiveStatus()!=null && !carContractAttributes.getActiveStatus().equals(""))
		         { 
				    
					whereClause.append("AND MASTER.ACTV_FLAG "); 
					//Added by Srivegi on 20050328
					whereClause.append(getSearchString(searchValues.getSearchType(),carContractAttributes.getActiveStatus()));
				 }
             
				// Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
            if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"MASTER.VALIDDATE");
    				 whereClause.append(searchString);
		         }

			
	 
               
            StringBuffer resultQuery= new StringBuffer();
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(actualQuery);
			resultQuery.append(" ORDER BY MASTER.CACONTRACTID DESC ");

			
            //Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........11..........."+resultQuery.toString());     	
			pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

            //Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........44...........");
            logger.info(FILE_NAME+"...............ETHAWBAdvDAO........44...........");
			while(rs.next()){
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO.....55..............");
        ETCarrierContractAdvVO carContractAttributesArr =new ETCarrierContractAdvVO();
			  
              carContractAttributesArr.setContractId(rs.getString(1));
			  carContractAttributesArr.setCarrierId(rs.getString(2));
			  carContractAttributesArr.setContractName(rs.getString(3));
			  carContractAttributesArr.setOriginTerminalId(rs.getString(4));
			  carContractAttributesArr.setDestTerminalId(rs.getString(5));
			  carContractAttributesArr.setServiceLevelId(rs.getString(6));
  			  carContractAttributesArr.setValidUpto(rs.getString(7));
			  carContractAttributesArr.setActiveStatus(rs.getString(8));
			  

			 				
				details.add(carContractAttributesArr);
			}		
			return details;
			}
       catch(Exception e){
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
				if(pStmt!=null){
					pStmt.close();
				}
				if(connection!=null){
					connection.close();
				}
			}catch(SQLException sq){
				throw new EJBException(sq.getMessage());
			}

	}
}
private  Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
}