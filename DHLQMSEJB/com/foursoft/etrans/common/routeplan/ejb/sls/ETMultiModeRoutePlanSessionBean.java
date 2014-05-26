package com.foursoft.etrans.common.routeplan.ejb.sls;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.rmi.RemoteException;   
import java.util.ArrayList;
import java.sql.Types;
 
import javax.naming.InitialContext;

import javax.sql.DataSource; 
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;
import com.foursoft.etrans.common.routeplan.dao.ETMultiModeRoutePlanDAO;
import com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanHdrDOB;
import com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanDtlDOB;

//import com.foursoft.etrans.air.mawb.bean.MasterDocument;
//import com.foursoft.etrans.air.mawb.bean.MasterDocumentHeader;
//import com.foursoft.etrans.air.mawb.bean.MasterDocumentFlightDetails;
/**
 * interface name ETMultiModeRoutePlanSessionBean
 * @author :
 * @version :1.6
 */
public class ETMultiModeRoutePlanSessionBean
	implements SessionBean

{
	private static final String FILE_NAME = "ETMultiModeRoutePlanSessionBean.java";
	private  SessionContext sessionContext = null;
	private	InitialContext	ictx = null;
	private	DataSource		dataSource = null;
  private static Logger logger = null;

  public ETMultiModeRoutePlanSessionBean()
  {
    logger  = Logger.getLogger(ETMultiModeRoutePlanSessionBean.class);
  }

	//Place your business methods here.
   /*
	public void updateRoutePlan( String masterdocId, String originGatewayId, 
								  String destinationGatewayId,
								  String[] housedocId, String[] status ) throws EJBException
	{
	
		PreparedStatement pstmt = null,pstmt1 = null;
		Connection connection   = null;
		ResultSet rs			= null;
		ArrayList routePlanIds 	= new ArrayList();
		
	    try
	    {

	    	connection  = this.getConnection();
			
			String rpQuery	= " SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE "+
							  "	DECODE(HAWB_ID,NULL,PRQ_ID,HAWB_ID) = ? ";
							  
			pstmt1  = connection.prepareStatement( rpQuery );
			int len = housedocId.length;
			
			Logger.info(FILE_NAME,"len : "+len);
			
			for( int row=0; row<len; row++ )
			{
				String strHousedocId = housedocId[row].trim();
				Logger.info(FILE_NAME,"strHousedocId first : "+strHousedocId+ " status[row] : "+status[row]);
					
				pstmt1.setString(1,strHousedocId);
				rs		= pstmt1.executeQuery();
				while(rs.next())
				{	
					Logger.info(FILE_NAME,"in rs first..."+rs.getString(1));
					routePlanIds.add(rs.getString(1));
				}
			}
			
			String sql1 = " UPDATE FS_RT_LEG SET MSTER_DOC_ID = ?, SHPMNT_STATUS = ? "+
						  "	WHERE RT_PLAN_ID=? AND ORIG_TRML_ID=? AND DEST_TRML_ID=?";
			
			pstmt   	= connection.prepareStatement( sql1 );						
			String routePlanId		= null;
			
			for(int i=0;i<routePlanIds.size();i++)
			{		
				routePlanId	= routePlanIds.get(i).toString();			
				pstmt.setString( 1, null);
				pstmt.setString( 2, "X" ); // letter 'O'
				pstmt.setString( 3, routePlanId );
				pstmt.setString( 4, originGatewayId );
				pstmt.setString( 5, destinationGatewayId );
				pstmt.executeUpdate();
			}
			
			routePlanIds = new ArrayList();
			
			for( int row=0; row<len; row++ )
			{
				String strHousedocId = housedocId[row].trim();
				Logger.info(FILE_NAME,"strHousedocId second : "+strHousedocId+ " status[row] : "+status[row]);
					
				if( status[row].equals("YES") )
				{
					pstmt1.setString(1,strHousedocId);
					rs		= pstmt1.executeQuery();
					while(rs.next())
					{	
						Logger.info(FILE_NAME,"in rs second ..."+rs.getString(1));
						routePlanIds.add(rs.getString(1));
					}
				}
			}
			
			for(int i=0;i<routePlanIds.size();i++)
			{		
				routePlanId	= routePlanIds.get(i).toString();
				
				for( int row=0; row<len; row++ )
				{
					Logger.info(FILE_NAME,"status[row] : "+status[row]);

					if( status[row].equals("YES") )
					{
						Logger.info(FILE_NAME,"If Query Details : "+masterdocId.trim()+ " "+routePlanId+ " "+originGatewayId+ " "+destinationGatewayId);
						
						pstmt.setString( 1, masterdocId.trim() );
						pstmt.setString( 2, "O" ); // letter 'O'
						pstmt.setString( 3, routePlanId );
						pstmt.setString( 4, originGatewayId );
						pstmt.setString( 5, destinationGatewayId );
						pstmt.executeUpdate();
					}
				}
		  	}
		}
		catch(SQLException sqEx)
        {
			Logger.error(FILE_NAME, "[updateRoutePlan] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			try
			{
			  if(rs!=null)
				  rs.close();
			  if(pstmt1!=null)
				  pstmt.close();
			  if(pstmt!=null)
				  pstmt.close();
			  if(connection!=null)
				  connection.close();
			
			}catch(Exception ex)
			{
			  ex.printStackTrace();
			}
        
		}
	}	

    */
  /**
   * 
   * @param documentType
   */
       public ArrayList getQuoteIds(String documentType,String terminalId,String userTerminalType)
    {
        PreparedStatement pstmt       = null;
        Connection connection         = null;
        ResultSet rs			            = null;
        ArrayList routePlanIds 	      = null;
        ArrayList terminalIds = null;
        String terminalIdQuery  = null;
        try
        {
            connection    = this.getConnection();
            routePlanIds  = new ArrayList();
            terminalIds = new ArrayList();
            //added for issue 174469 by VLAKSHMI ON 20090629
            if("O".equalsIgnoreCase(userTerminalType))
            {
            terminalIdQuery="SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = '"+userTerminalType+"'   AND TERMINALID LIKE '"+terminalId+"'   AND (INVALIDATE = 'F'OR INVALIDATE IS NULL) ";
            }else if("H".equalsIgnoreCase(userTerminalType))
            {
               terminalIdQuery="select terminalid term_id from fs_fr_terminalmaster";
            }
            else
            {
              terminalIdQuery="select child_terminal_id term_id from fs_fr_terminal_regn "+ 
           "  where parent_terminal_id='"+terminalId+"' union select '"+terminalId+"' from dual";
            }
           //end for issue 174469 by VLAKSHMI
            pstmt  = connection.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_ID LIKE '"+documentType+"%' AND QM.COMPLETE_FLAG = 'I' AND QM.SPOT_RATES_FLAG = 'N' AND QM.ACTIVE_FLAG = 'A' AND terminal_id  IN("+terminalIdQuery+") ");
            //"SELECT QUOTE_ID FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_ID LIKE '"+documentType+"%' AND QM.COMPLETE_FLAG='I' AND QM.SPOT_RATES_FLAG='N' AND QM.VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER QT WHERE QM.QUOTE_ID=QT.QUOTE_ID)");
            
            //pstmt1.setString(1,masterdocId);
            rs		= pstmt.executeQuery();
            while(rs.next())
            {	
              //Logger.info(FILE_NAME,"in rs first..."+rs.getString("QUOTE_ID"));
              routePlanIds.add(rs.getString("QUOTE_ID"));
            }
        }
        catch(SQLException sqEx)
        {
            //Logger.error(FILE_NAME, "[getQuoteIds] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getQuoteIds] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
          try
          {
            if(rs!=null)
              { rs.close(); }
            if(pstmt!=null)
              { pstmt.close(); }
            if(connection!=null)
              {connection.close(); }
			
          }
          catch(Exception ex)
          {
            ex.printStackTrace();
          }
        }
       return routePlanIds; 
    }
/**
   * 
   * @param quoteId
   * @param loginBean
   */
 public ArrayList getRoutePlanDetails(String quoteId,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginBean)
 {
    PreparedStatement pstmt                   = null;
    Connection connection                     = null;
    ResultSet rs			                        = null;
    ArrayList routePlanDtl 	                  = null;
    ETMultiModeRoutePlanDtlDOB  routeplanDob  = null;
    try
    {
        connection    = this.getConnection();
        routePlanDtl  = new ArrayList();
        pstmt  = connection.prepareStatement( "SELECT RL.RT_PLAN_ID RT_PLAN_ID,RL.ORIG_LOC ORIG_LOC,RL.DEST_LOC DEST_LOC,RL.SHPMNT_MODE SHPMNT_MODE,RL.LEG_TYPE LEG_TYPE,RL.SERIAL_NO SERIAL_NO,RL.REMARKS REMARKS  FROM  FS_RT_PLAN RP,FS_RT_LEG RL WHERE RP.RT_PLAN_ID=RL.RT_PLAN_ID AND QUOTE_ID=?");
        pstmt.setString(1,quoteId);
        rs		= pstmt.executeQuery();
        while(rs.next())
        {	
            routeplanDob    =   new ETMultiModeRoutePlanDtlDOB();
            //Logger.info(FILE_NAME,"in rs first..."+rs.getString("RT_PLAN_ID"));
            routeplanDob.setRoutePlanId(rs.getLong("RT_PLAN_ID"));
            routeplanDob.setOrgLoc(rs.getString("ORIG_LOC"));
            routeplanDob.setDestLoc(rs.getString("DEST_LOC"));
            routeplanDob.setShipmentMode(rs.getInt("SHPMNT_MODE"));
            routeplanDob.setLegType(rs.getString("LEG_TYPE"));
            routeplanDob.setRemarks(rs.getString("REMARKS"));
            routePlanDtl.add(routeplanDob);
        }
    }
    catch(SQLException sqEx)
    {
        //Logger.error(FILE_NAME, "[getQuoteIds] -> "+sqEx.toString());
        logger.error(FILE_NAME+ "[getQuoteIds] -> "+sqEx.toString());
        throw new EJBException(sqEx.toString());
    }
    finally
    {
      try
      {
        if(rs!=null)
          { rs.close(); }
        if(pstmt!=null)
          { pstmt.close(); }
        if(connection!=null)
          {connection.close(); }
			
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
    }
  return routePlanDtl; 
 }
   /**
   * 
   * @param terminalId
   * @param serchString
   * @param shipmentMode
   */
    public ArrayList getTerminalLocation(String terminalId,String serchString,String shipmentMode)
    {
        PreparedStatement pstmt       = null;
        Connection connection         = null;
        ResultSet rs			            = null;
        ArrayList routePlanIds 	      = null;
        try
        {
            connection    = this.getConnection();
            routePlanIds  = new ArrayList();
            pstmt  = connection.prepareStatement("SELECT LOCATIONID FROM FS_FR_TERMINALLOCATION WHERE TERMINALID='"+terminalId+"' AND LOCATIONID LIKE '"+serchString+"%'");
            //pstmt1.setString(1,masterdocId);
            rs		= pstmt.executeQuery();
            while(rs.next())
            {	
             // Logger.info(FILE_NAME,"in rs first..."+rs.getString("LOCATIONID"));
              routePlanIds.add(rs.getString("LOCATIONID"));
            }
        }
        catch(SQLException sqEx)
        {
            //Logger.error(FILE_NAME, "[getQuoteIds] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getQuoteIds] -> "+sqEx.toString());
            sqEx.printStackTrace();
            throw new EJBException(sqEx.toString());
            
        }
        finally
        {
          try
          {
            if(rs!=null)
              { rs.close(); }
            if(pstmt!=null)
              { pstmt.close(); }
            if(connection!=null)
              {connection.close(); }
			
          }
          catch(Exception ex)
          {
            ex.printStackTrace();
          }
        }
       return routePlanIds; 
    }
  /**
   * 
   * @param masterdocId
   * @param originGatewayId
   * @param destinationGatewayId
   * @param housedocId
   * @param status
   */
	public void updateRoutePlan( String masterdocId, String originGatewayId, 
								  String destinationGatewayId,
								  String[] housedocId, String[] status )
	{
	
		PreparedStatement pstmt = null,pstmt1 = null;
		Connection connection   = null;
		ResultSet rs			= null;
		ArrayList routePlanIds 	= new ArrayList();
			
	    try
	    {

	    	connection  = this.getConnection();
			
			pstmt1  = connection.prepareStatement( " SELECT RT_PLAN_ID FROM FS_RT_LEG WHERE "+
									  "	MSTER_DOC_ID = ? " );
			pstmt1.setString(1,masterdocId);
			rs		= pstmt1.executeQuery();
			while(rs.next())
			{	
				//Logger.info(FILE_NAME,"in rs first..."+rs.getString(1));
				routePlanIds.add(rs.getString(1));
			}
      //Added By RajKumari on 23-10-2008 for Connection Leakages.
      if(pstmt1!=null)
      {
        pstmt1.close();
      }
			String sql1 = " UPDATE FS_RT_LEG SET MSTER_DOC_ID = ?, SHPMNT_STATUS = ? "+
						  "	WHERE RT_PLAN_ID=? AND ORIG_TRML_ID=? AND DEST_TRML_ID=?";
			
			pstmt   	= connection.prepareStatement( sql1 );						
			int routePlainIdsSize	 =	 routePlanIds.size();
			for(int i=0;i<routePlainIdsSize;i++)
			{		
				pstmt.setString( 1, null);
				pstmt.setString( 2, "X" ); 
				pstmt.setString( 3, routePlanIds.get(i).toString() );
				pstmt.setString( 4, originGatewayId );
				pstmt.setString( 5, destinationGatewayId );
				pstmt.executeUpdate();
			}
			

			 int len = housedocId!=null ? housedocId.length : 0;
			routePlanIds = new ArrayList();
		
			pstmt1  	= connection.prepareStatement( " SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE "+
							  "	DECODE(HAWB_ID,NULL,PRQ_ID,HAWB_ID) = ? " );			
			
			for( int row=0; row<len; row++ )
			{

				//Logger.info(FILE_NAME,"strHousedocId second : "+housedocId[row].trim()+ " status[row] : "+status[row]);
					
				if( status[row].equals("YES") )
				{
					pstmt1.setString(1,housedocId[row]);
					rs		= pstmt1.executeQuery();
					while(rs.next())
					{	
						//Logger.info(FILE_NAME,"in rs second ..."+rs.getString(1));
						routePlanIds.add(rs.getString(1));
					}
				}
			}
			int rtPlanSize	=	routePlanIds.size();
			for(int i=0;i<rtPlanSize;i++)
			{		
				for( int row=0; row<len; row++ )
				{
					//Logger.info(FILE_NAME,"status[row] : "+status[row]);

					if( status[row].equals("YES") )
					{
						//Logger.info(FILE_NAME,"If Query Details : "+masterdocId.trim()+ " "+routePlanIds.get(i).toString()+ " "+originGatewayId+ " "+destinationGatewayId);
						
						pstmt.setString( 1, masterdocId.trim() );
						pstmt.setString( 2, "O" ); 
						pstmt.setString( 3, routePlanIds.get(i).toString() );
						pstmt.setString( 4, originGatewayId );
						pstmt.setString( 5, destinationGatewayId );
						pstmt.executeUpdate();
					}
				}
		  	}
		}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[updateRoutePlan] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[updateRoutePlan] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			try
			{
			  if(rs!=null)
				 { rs.close(); }
			  if(pstmt1!=null)
				 { pstmt1.close(); }
			  if(pstmt!=null)
				  { pstmt.close(); }
			  if(connection!=null)
				  {connection.close(); }
			
			}catch(Exception ex)
			{
			  ex.printStackTrace();
			}
        
		}
	}	


	//################################### Added for Sea freight ##############################
	
	//################################### Added for Sea freight ##############################
	
	
  /**
   * 
   * @param masterdocId
   * @param originGatewayId
   * @param destinationGatewayId
   * @param housedocId
   * @param status
   */
	public void updateRoutePlan_Sea( String masterdocId, String originGatewayId, 
								  String destinationGatewayId,
								  String[] housedocId, String[] status  )
	{
	
		PreparedStatement pstmt = null,pstmt1 = null;//pstmt2 = null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		PreparedStatement pStmtForRTPlan = null;
		ResultSet		  rsForRTPlan	 = null;	
		String			  masterStatus	 = null;

		Connection connection   = null;
		ResultSet rs			= null;
		ArrayList routePlanIds 	= new ArrayList();
		ArrayList primaryModeList = new ArrayList();	

		ArrayList routePlanForBNRs		  = new ArrayList();
		ArrayList primaryModeListForBNRs  = new ArrayList();
		int		  countOfBNRs			  = 0;		
	    String    sql1            = "";


	    try
	    {

	    	
	    	connection  = this.getConnection();
			
			pstmt1  = connection.prepareStatement( " SELECT RT_PLAN_ID FROM FS_RT_LEG WHERE "+
									  "	MSTER_DOC_ID = ? " );
			pstmt1.setString(1,masterdocId);
			rs		= pstmt1.executeQuery();
			while(rs.next())
			{	
				//Logger.info(FILE_NAME,"in rs first..."+rs.getString(1));
				routePlanIds.add(rs.getString(1));
			}

			

			 sql1 = " UPDATE FS_RT_LEG SET MSTER_DOC_ID = ?, SHPMNT_STATUS = ? "+
						  "	WHERE RT_PLAN_ID=? AND ORIG_TRML_ID=? AND DEST_TRML_ID=?";
			
			pstmt   	= connection.prepareStatement( sql1 );						
			int rtPlanIdSize	= routePlanIds.size();
			for(int i=0;i<rtPlanIdSize;i++)
			{		
				pstmt.setString( 1, null);
				pstmt.setString( 2, "X" ); 
				pstmt.setString( 3, routePlanIds.get(i).toString() );
				pstmt.setString( 4, originGatewayId );
				pstmt.setString( 5, destinationGatewayId );
				pstmt.executeUpdate();
			}
			

			 int len = housedocId!=null ? housedocId.length : 0;
			 routePlanIds 		= new ArrayList();
			primaryModeList	= new ArrayList();		
  		
		  //Added By RajKumari on 23-10-2008 for Connection Leakages.
      if(pstmt1!=null)
      {
      pstmt1.close();
      }
			pstmt1  	= connection.prepareStatement( " SELECT RT_PLAN_ID,PRMY_MODE FROM FS_RT_PLAN WHERE "+
							  "	DECODE(HAWB_ID,NULL,PRQ_ID,HAWB_ID) = ? " );			
			
			for( int row=0; row<len; row++ )
			{

				//Logger.info(FILE_NAME,"strHousedocId second : "+housedocId[row].trim()+ " status[row] : "+status[row]);
					
				if( status[row].equals("YES") )
				{
					pstmt1.setString(1,housedocId[row]);
					rs		= pstmt1.executeQuery();
					while(rs.next())
					{	
						//Logger.info(FILE_NAME,"in rs second ..."+rs.getString(1));
						routePlanIds.add(rs.getString(1));
						primaryModeList.add(rs.getString(2));
					}
			   }
			   else
			   {
			     pstmt1.setString(1,housedocId[row]);
				 rs		= pstmt1.executeQuery();
				 while(rs.next())
				 {	
						//Logger.info(FILE_NAME,"in rs second 44444..."+rs.getString(1));
						routePlanForBNRs.add(rs.getString(1));
						primaryModeListForBNRs.add(rs.getString(2));
				 }   
			   }
			}
			//System.out.println("the FINAL ROUTE PLAN ID ---> "+routePlanIds);
			//System.out.println("the primarymode list ---- >"+primaryModeList);
			
			if(routePlanIds!=null && routePlanIds.size() >0)
			{
			  pStmtForRTPlan = connection.prepareStatement("SELECT CONSOLESTATUS FROM FS_FRS_CONSOLEMASTER WHERE CONSOLEID= ? ");
			}
			
			int rtPlanSize	= routePlanIds.size();
			for(int i=0;i<rtPlanSize;i++)
			{		
				for( int row=0; row<len; row++ )
				{
					//Logger.info(FILE_NAME,"status[row] : "+status[row]);

					if( status[row].equals("YES") )
					{
						//Logger.info(FILE_NAME,"If Query Details : "+masterdocId.trim()+ " "+routePlanIds.get(i).toString()+ " "+originGatewayId+ " "+destinationGatewayId);
						
						//System.out.println("the primary mode is ---- >"+primaryModeList.get(i));
						if( ! (primaryModeList.get(i).toString().equals("2")) )
						{
							//System.out.println("this is inside teh primary mode one or four");
							pstmt.setString( 1, masterdocId.trim() );
							pstmt.setString( 2, "O" ); 
							pstmt.setString( 3, routePlanIds.get(i).toString() );
							pstmt.setString( 4, originGatewayId );
							pstmt.setString( 5, destinationGatewayId );
							pstmt.executeUpdate();
						}
						else
						{
							
							
							try
							{
								
								pStmtForRTPlan.setString(1,masterdocId.trim());
								rsForRTPlan = pStmtForRTPlan.executeQuery();
								
								while(rsForRTPlan.next())
								{
								  masterStatus = rsForRTPlan.getString(1);
								  // System.out.println("Status is " + masterStatus);
								}
								
								pStmtForRTPlan.clearParameters();
								rsForRTPlan = null;

								if(masterStatus==null)
								  {  masterStatus = "O";	 }
								else if(masterStatus.equals("CLOSED"))
								  { masterStatus="C"; }
								else if(masterStatus.equals("OPENED"))
								  { masterStatus="O"; }

								
								
								//System.out.println("Status is " + masterStatus);

								
								pstmt.setString( 1, masterdocId.trim() );
								pstmt.setString( 2, masterStatus); 
                				pstmt.setString( 3, routePlanIds.get(i).toString() );
								pstmt.setString(4, originGatewayId );
								pstmt.setString( 5, destinationGatewayId );
								pstmt.executeUpdate();

								//System.out.println("Executed Successfully");

								
								
								//System.out.println(" ^^^^^^^^^^^^^^ THIS IS BEFOR GOING TO UPDAT_ROUTE_PLAN NEW METHOD^^^^^^^^^^^^^^");
								update_Route_Plan(masterdocId, originGatewayId,destinationGatewayId,routePlanIds.get(i).toString()) ;
								//System.out.println("THIS IS INSIDE ############### COMING OUT METHOD #####");
							}
							catch(Exception e)
							{
								e.printStackTrace();
								//System.out.println("Exception has ");
							}		
						}		
					}
					
						
				
				}
		  	}
		  //Added By RajKumari on 23-10-2008 for Connection Leakages.
			if(pstmt!=null)
      {
      pstmt.close();
      }
			pstmt = null;
			pstmt = connection.prepareStatement(" UPDATE FS_RT_LEG SET MSTER_DOC_ID = ?, SHPMNT_STATUS = ? " +
						  "	WHERE RT_PLAN_ID=? AND ORIG_TRML_ID=? AND DEST_TRML_ID=? AND MSTER_DOC_ID = ?");	
			int rtPlanBNRSize	= routePlanForBNRs.size();
			for(int i=0;i<rtPlanBNRSize;i++)
			{
			   if((primaryModeListForBNRs.get(i).toString().equals("2")) )
			   {
			      
					  
			      //System.out.println(" routePlanForBNRs.get(i).toString() is  " + routePlanForBNRs.get(i).toString());

				  pstmt.setString( 1,"");
				  pstmt.setString( 2,"X" ); 
				  pstmt.setString( 3,routePlanForBNRs.get(i).toString());
				  pstmt.setString( 4,originGatewayId );
				  pstmt.setString( 5,destinationGatewayId );
                  pstmt.setString( 6,masterdocId.trim());
				  pstmt.executeUpdate();
					    
				  //System.out.println("Execution done perfectly ");
				
			   }
			}
		
		
		}
		catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[updateRoutePlan] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[updateRoutePlan] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			try
			{
				//Connection Leakage ------------------ 7-DEC-2004 --------------------- Santhosam.P
			  if(rs!=null)
				  { rs.close(); }
			  if(pstmt1!=null)
				  { pstmt1.close(); }
			  if(pstmt!=null)
				  { pstmt.close(); }
			  /*if(pstmt2!=null)
				  { pstmt2.close(); }*/
			  if(pStmtForRTPlan!=null)
				  { pStmtForRTPlan.close(); }
			  if(rsForRTPlan!=null)
				  { rsForRTPlan.close(); } 
			  if(connection!=null)
				  { connection.close(); }
			
			}catch(Exception ex)
			{
			  ex.printStackTrace();
			}
        
		}
	}
	
		
	
	//###################################  end of adding for sea freight ############################


    //##################################  private method  ###############################
 private void  update_Route_Plan(String consoleNo,String originGatewayId,String destinationGatewayId,String route_Id )
 {
	Connection			connection		=	null;
	PreparedStatement	pStmt			=	null;
	PreparedStatement	pStmt1			=	null;
	PreparedStatement	pStmt2			=	null;
	ResultSet			rs				=	null;
	ResultSet			rs1				=	null;
	ResultSet			rs2				=	null;
	
	
	
	

    ETMultiModeRoutePlanSessionHome routeHome	= null;
	ETMultiModeRoutePlanSession routeRemote		= null;
	ETMultiModeRoutePlanHdrDOB routeHdrDOB		= null;
	ETMultiModeRoutePlanDtlDOB[] planDtlDOB		= null;

	
	String				originTerminal	= "";
	String				destTerminal	= "";
	String				orginGateway	= "";
	String				destGateway		= "";
	String				originLoc		= "";
	String				destLoc			= "";
	String				hblId			= "";
	String				prqId			= "";
	String				masterstatus	= "";
	String				consoleOGW		= "";
	String				consoleDGW		= "";
	String				validFlag		= "Y";
	String				shipperId		= "";
	String				consigneeId		= "";
	String				prq_id			= "";
	String				h_id			= "";
	String				p_id			= "";
    String				overFlag		= "";	

	ETMultiModeRoutePlanDtlDOB   routePlanDtlDOB = null;
	String						 gatewayTerminal = null;
    PreparedStatement            pStmtForRTPlan  = null;  
    PreparedStatement            pStmtForGateway = null;
    ResultSet                    rsForRTPlan     = null;
    ResultSet                    rsForGateway    = null;
    int                          countOfRecords  = 0;
    String                       masterDocId     = null;
	String						 gatewayDest	 = null;		
	String						 tempGatewayDest = null;
    String                       shipmentStatus  = null;
    String                       legType         = null; 
	// @@ Replaced by Sreelakshmi KVA for TogetherJ
    //String                       stmtForRTPlan   = null;
	StringBuffer  stmtForRTPlan = new StringBuffer();
	//@@
    boolean                      flag            = false;
    int                          serialNo        = 0;   
	int							 tempSerialNo	 = 0; 
	String						 notInRoute		 = null;
	// @@ Replaced by Sreelakshmi KVA for TogetherJ    
	boolean						 updateRoutePlan1 = false; 
	String						 isAutoOrManual  = "M";
    

	try
	{
		
		
		ictx	 = new InitialContext();
		connection       = this.getConnection();
		
		routeHome = (ETMultiModeRoutePlanSessionHome)ictx.lookup("ETMultiModeRoutePlanSessionBean");
		routeRemote = (ETMultiModeRoutePlanSession)routeHome.create();


		String sql1		= " SELECT HOUSEDOCID,MASTERDOCID,MASTERSTATUS,ORIGINTERMINAL,DESTTERMINAL, ORIGINGATEWAY,DESTGATEWAY,ORIGINLOCATION,DESTINATIONLOCATION,SHIPPERID,CONSIGNEEID,PRQID FROM FS_FR_HOUSEDOCHDR,FS_RT_PLAN WHERE HOUSEDOCID =HAWB_ID AND RT_PLAN_ID=? " ;
		 
		String sql2		= " SELECT RM.ORIGINGATEWAY,RM.DESTGATEWAY FROM FS_FRS_ROUTEMASTER RM, FS_FRS_CONSOLEMASTER C WHERE C.ROUTEID=RM.ROUTEID AND C.CONSOLEID=? " ;
	
	    String sql3		= " SELECT PRQ_ID,HAWB_ID FROM FS_RT_PLAN WHERE RT_PLAN_ID=? " ;
		
		String sql4		= " SELECT PRQID,MASTERDOCID,CONSOLESTATUS,ORIGINTERMINAL,DESTTERMINAL,ORIGINID,DESTINATIONID,SHIPPERID,CONSIGNEEID FROM FS_FR_PICKUPREQUEST,FS_RT_PLAN WHERE  PRQID=PRQ_ID AND RT_PLAN_ID=? ";
		
	    //System.out.println("THIS IS INSIDE THE UPDATE _ ROUTE PLAN .............2");
		
		pStmt2  = connection.prepareStatement(sql3);
		pStmt2.setString(1,route_Id);
		rs2 = pStmt2.executeQuery();
		while(rs2.next())
		{
			p_id = rs2.getString(1);
			h_id = rs2.getString(2);
			
		}		
		//System.out.println("the p_id === >"+p_id+":h_id === >"+h_id);
		rs2		=	null;
		pStmt2 	= 	null;
		if(h_id != null &&  !h_id.equals(""))
		{ pStmt  = connection.prepareStatement(sql1); }
		if(h_id == null)
		{ pStmt  = connection.prepareStatement(sql4);	}
		pStmt.setString(1,route_Id);
		rs = pStmt.executeQuery();
		//Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
		pStmt1  = connection.prepareStatement(sql2);
    pStmtForGateway = connection.prepareStatement(" SELECT TERMINALID FROM FS_FR_TERMINALGATEWAY WHERE GATEWAYID=? AND TERMINALID=?");
    stmtForRTPlan.append(" SELECT COUNT(*) FROM FS_RT_LEG WHERE RT_PLAN_ID = ? ");
		pStmtForRTPlan = connection.prepareStatement(stmtForRTPlan.toString());
		while(rs.next())
		{
			
			if(h_id != null &&  !h_id.equals(""))
			{	
				hblId			= rs.getString(1);
				masterstatus	= rs.getString(3);
				originTerminal	= rs.getString(4);
				destTerminal	= rs.getString(5);
				orginGateway	= rs.getString(6);	
				destGateway		= rs.getString(7);
				originLoc		= rs.getString(8);
				destLoc			= rs.getString(9);
				shipperId		= rs.getString(10);
				consigneeId		= rs.getString(11);
				prq_id			= rs.getString(12);	

			}
			else if(h_id == null )
			{
				prq_id			= rs.getString(1);
				masterstatus	= rs.getString(3);
				originTerminal	= rs.getString(4);
				destTerminal	= rs.getString(5);
				originLoc		= rs.getString(6);
				destLoc			= rs.getString(7);
				shipperId		= rs.getString(8);
				consigneeId		= rs.getString(9);
					
			}	
					
			//pStmt1  = connection.prepareStatement(sql2);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
			pStmt1.setString(1,consoleNo);
			rs1 = pStmt1.executeQuery();
			while(rs1.next())
			{
				consoleOGW	= rs1.getString(1);
				consoleDGW	= rs1.getString(2);
				
			}
			
			pStmt1 	=null;
			rs1		= null; 
			
			
			if(routeHdrDOB == null)
			{	
				if(h_id != null &&  !h_id.equals(""))
				{	
					routeHdrDOB = new ETMultiModeRoutePlanHdrDOB(0,prq_id,hblId,originTerminal,
													 destTerminal,originLoc,
													 destLoc,shipperId,
													 consigneeId,2,"Y",null,
													  null,"N");
				}
				else if(h_id == null)
				{
					routeHdrDOB = new ETMultiModeRoutePlanHdrDOB(0,prq_id,null,originTerminal,
													 destTerminal,originLoc,
													 destLoc,shipperId,
													 consigneeId,2,"Y",null,
													  null,"N");	
				}											  
			}										 

			if( !(originTerminal.equals(consoleOGW)) && !(destTerminal.equals(consoleDGW)))
			{
				overFlag		=  "Y" ;
				countOfRecords	=  0;
				
				// Code Added by Anand for Shipments Which are not according to the Route Plan
        //Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
				/*stmtForRTPlan.append(" SELECT COUNT(*) FROM FS_RT_LEG WHERE RT_PLAN_ID = ? ");
				pStmtForRTPlan = connection.prepareStatement(stmtForRTPlan.toString());*/
				pStmtForRTPlan.setString(1,route_Id);
				rsForRTPlan	   = pStmtForRTPlan.executeQuery();
				
				while(rsForRTPlan.next())
				{
				  countOfRecords = rsForRTPlan.getInt(1);
				}
				
				planDtlDOB = new ETMultiModeRoutePlanDtlDOB[countOfRecords+2];
				
				// System.out.println(" Count of Records are " + (countOfRecords+2));
				
				planDtlDOB[0] = new ETMultiModeRoutePlanDtlDOB(1,0,originLoc,originTerminal,4,"X","A","","LT",validFlag);

			 //Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
				//pStmtForGateway = connection.prepareStatement(" SELECT TERMINALID FROM FS_FR_TERMINALGATEWAY WHERE GATEWAYID=? AND TERMINALID=?");

				rsForRTPlan	   = null;
				
				stmtForRTPlan  = null;
        //Added By RajKumari on 23-10-2008 for Connection Leakages.
        if(pStmtForRTPlan!=null)
        {
          pStmtForRTPlan.close();
        }
				pStmtForRTPlan = null;
				rsForRTPlan	   = null;
                // @@ Replaced by Sreelakshmi KVA for TogetherJ
				/*stmtForRTPlan  = " SELECT SERIAL_NO,LEG_TYPE,ORIG_TRML_ID,DEST_TRML_ID,SHPMNT_MODE,SHPMNT_STATUS,AUTO_MNUL_FLAG "
							   + " ,MSTER_DOC_ID,LEG_VALID_FLAG,PIECES_RECEIVED,RECEIVED_DATE,REMARKS,COSTAMOUNT "
							   + " FROM FS_RT_LEG WHERE RT_PLAN_ID = ? AND SERIAL_NO <> 1 ";*/
							   stmtForRTPlan.append( " SELECT SERIAL_NO,LEG_TYPE,ORIG_TRML_ID,DEST_TRML_ID,SHPMNT_MODE,SHPMNT_STATUS,AUTO_MNUL_FLAG ");
							   stmtForRTPlan.append (" ,MSTER_DOC_ID,LEG_VALID_FLAG,PIECES_RECEIVED,RECEIVED_DATE,REMARKS,COSTAMOUNT ");
							   stmtForRTPlan.append (" FROM FS_RT_LEG WHERE RT_PLAN_ID = ? AND SERIAL_NO <> 1 ");
				pStmtForRTPlan =  connection.prepareStatement(stmtForRTPlan.toString());
				//@@
				pStmtForRTPlan.setString(1,route_Id);
				rsForRTPlan	   =  pStmtForRTPlan.executeQuery();
				countOfRecords  = 1; 
				tempGatewayDest = ""; 
				while(rsForRTPlan.next())
				{
				  flag			  = false;	
				  if(countOfRecords>1)
				    { tempGatewayDest = gatewayDest; }

					   
				  gatewayTerminal = rsForRTPlan.getString(3);
				  gatewayDest	  = rsForRTPlan.getString(4);	   
				  shipmentStatus  = rsForRTPlan.getString(6);	
				  masterDocId	  = rsForRTPlan.getString(8);
				  serialNo		  = rsForRTPlan.getInt(1);	
				  legType		  = rsForRTPlan.getString(2);
				  isAutoOrManual  = rsForRTPlan.getString(7);
				  
				 /* System.out.println(" gatewayTerminal is " + gatewayTerminal);
				  System.out.println(" ConsoleOGW is " + consoleOGW);
				  System.out.println(" shipmentStatus is " + shipmentStatus);
				  System.out.println(" MasteDocId is " + masterDocId);
				  System.out.println(" TempGatewayDest is " + tempGatewayDest);
				  System.out.println(" isAutoOrManual is " + isAutoOrManual); */

				  pStmtForGateway.setString(1,consoleOGW);
				  pStmtForGateway.setString(2,gatewayTerminal);
				  
				  rsForGateway = pStmtForGateway.executeQuery();
				  
				  if(rsForGateway.next())
				  {
				     if(masterDocId==null && shipmentStatus.equals("X") && isAutoOrManual.equals("A"))
						 { flag = true;  }
				  }
				  else
				  {
				     if(gatewayTerminal.length()==4 && gatewayDest.length()>4)
					 {
					   if(gatewayTerminal.equals(consoleOGW) && !gatewayDest.equals(consoleDGW))
						  { flag = true;  }
					 }
				  }
				  // System.out.println("Flag is " + flag);
				  
				  if(flag == false)
				  {
						  if(notInRoute!=null && notInRoute.equals("Y"))
							{  
							  serialNo = countOfRecords+1;
							}
						
						
						// System.out.println("Serail No is " + serialNo);
						routePlanDtlDOB = new ETMultiModeRoutePlanDtlDOB(  serialNo,
																		   0,
																		   rsForRTPlan.getString(3),
																		   rsForRTPlan.getString(4),	
																	       rsForRTPlan.getInt(5),
																	       shipmentStatus,	
																	       rsForRTPlan.getString(7),		
																	       masterDocId,										
																	       rsForRTPlan.getString(2),		
																	       rsForRTPlan.getString(9)
																			
                                                                        );														 
						
						
						routePlanDtlDOB.setPiecesReceived(rsForRTPlan.getInt(10));
						routePlanDtlDOB.setReceivedDate(rsForRTPlan.getTimestamp(11));
						routePlanDtlDOB.setRemarks(rsForRTPlan.getString(12));
						routePlanDtlDOB.setCostAmount(rsForRTPlan.getDouble(13));
					   
						planDtlDOB[countOfRecords] = routePlanDtlDOB;
						countOfRecords++;
				}
				else
				{
               //      System.out.println("In the else block");   
				     
					 if(  (gatewayTerminal.equals(consoleOGW) || gatewayTerminal.equals(consoleDGW)) && updateRoutePlan1==false && (!gatewayTerminal.equals(consoleOGW)) )
					 {	 
						updateRoutePlan1 = true;
					 }
					 
					 notInRoute ="Y";
					 tempSerialNo = serialNo;
					/* System.out.println(" Gateway Destination is " + gatewayDest);
					 System.out.println(" Origin Terminal is " + originTerminal);
					 System.out.println(" Dest Terminal is " + destTerminal);
					 */
					 
					 
					 //System.out.println("updateRoutePlan is   " + updateRoutePlan);
					 
					 if(gatewayTerminal.equals(originTerminal) && gatewayDest.equals(destTerminal))
					 {
					   //System.out.println(" In Both Equals Block");
					   planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo,0,gatewayTerminal,consoleOGW,4,"X","M","","TG","Y");
					   countOfRecords++;
					   planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+1,0,consoleOGW,consoleDGW,2,"O","A",consoleNo,"TP","Y");
					   countOfRecords++;				      
				       planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+2,0,consoleDGW,destTerminal,4,"X","M","","GT","Y");
					   countOfRecords++;				        
					 }
					 else if(!gatewayTerminal.equals(originTerminal) && !gatewayDest.equals(destTerminal))
					 {
					  // System.out.println("In Both Not Equals Block");
					   planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo,0,gatewayTerminal,consoleOGW,4,"X","A","","TP","Y");
					   countOfRecords++;
					   planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+1,0,consoleOGW,consoleDGW,2,"O","A",consoleNo,"TP","Y");
					   countOfRecords++;				      
				       planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+2,0,consoleDGW,destTerminal,4,"X","A","","TP","Y");
					   countOfRecords++;				        
					 }
					 else if(gatewayTerminal.equals(originTerminal) && !gatewayDest.equals(destTerminal))
					 {
					  //System.out.println(" In the Origin Equals and Destination Not Equals");
					    planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo,0,gatewayTerminal,consoleOGW,4,"X","M","","TG","Y");
					    countOfRecords++;
					    planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+1,0,consoleOGW,consoleDGW,2,"O","A",consoleNo,"TP","Y");
					    countOfRecords++;				      
				        if(!gatewayTerminal.equals(consoleOGW))
					    {
					      planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+2,0,consoleDGW,destTerminal,4,"X","A","","TP","Y");
					      countOfRecords++;				      
					    }	
					    else
					    {
					      planDtlDOB[countOfRecords] = null;
					      countOfRecords++;				      
					    }
					  
					 }
					 else
					 {
					   //System.out.println("In the Origin Not Equals and Destination Not Equals");
					   if(!gatewayTerminal.equals(consoleOGW) && !gatewayDest.equals(consoleDGW))
					   {
					      planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo,0,gatewayTerminal,consoleOGW,4,"X","A","","TP","Y");
					      countOfRecords++;
					   }
					   else
					   {
					     planDtlDOB[countOfRecords] = null;
					     countOfRecords++;
					   }
					   planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+1,0,consoleOGW,consoleDGW,2,"O","A",consoleNo,"TP","Y");
					   countOfRecords++;				      
				       planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+2,0,consoleDGW,destTerminal,4,"X","M","","GT","Y");
					   countOfRecords++;				      
					 }
/**					 planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo,0,gatewayDest,consoleOGW,4,"X","M","","TG","Y");
					 countOfRecords++;
					 planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+1,0,consoleOGW,consoleDGW,2,"O","A",consoleNo,"TP","Y");
					 countOfRecords++;				      
				     planDtlDOB[countOfRecords] = new ETMultiModeRoutePlanDtlDOB(serialNo+2,0,consoleDGW,destTerminal,4,"X","M","","GT","Y");
					 countOfRecords++;				       */
//				     break;
				}
					
			  } 
			
/**			  System.out.println("After the Break");
			  pStmtForRTPlan = null;
			  rsForRTPlan    = null;
			  pStmtForRTPlan = connection.prepareStatement(  "SELECT LEG_TYPE,ORIG_TRML_ID,DEST_TRML_ID,SHPMNT_MODE,SHPMNT_STATUS,AUTO_MNUL_FLAG "
													       + " ,MSTER_DOC_ID,LEG_VALID_FLAG,PIECES_RECEIVED,RECEIVED_DATE,REMARKS,COSTAMOUNT "
														   + " FROM FS_RT_LEG WHERE RT_PLAN_ID = ? AND SERIAL_NO >"+tempSerialNo );
			  pStmtForRTPlan.setString(1,route_Id);
			  rsForRTPlan = pStmtForRTPlan.executeQuery();
			  while(rsForRTPlan.next())
			  {
			    System.out.println(" countOfRecords is " + countOfRecords);
				routePlanDtlDOB = new ETMultiModeRoutePlanDtlDOB(  countOfRecords+1,
																		   0,
																		   rsForRTPlan.getString(3),
																		   rsForRTPlan.getString(4),	
																	       rsForRTPlan.getInt(5),
																	       shipmentStatus,	
																	       rsForRTPlan.getString(7),		
																	       masterDocId,										
																	       rsForRTPlan.getString(2),		
																	       rsForRTPlan.getString(9)
                                                                        );														 
						
						
						routePlanDtlDOB.setPiecesReceived(rsForRTPlan.getInt(10));
						routePlanDtlDOB.setReceivedDate(rsForRTPlan.getTimestamp(11));
						routePlanDtlDOB.setRemarks(rsForRTPlan.getString(12));
						routePlanDtlDOB.setCostAmount(rsForRTPlan.getDouble(13));
					   
						planDtlDOB[countOfRecords] = routePlanDtlDOB;
						countOfRecords++; 
			     System.out.println(" countOfRecords is " + countOfRecords);  
			 }  */
			  


            /**
			  
				
				
				

				
				planDtlDOB[1] = new ETMultiModeRoutePlanDtlDOB(2,0,originTerminal,consoleOGW,4,"X","M","","TG","Y");
				planDtlDOB[2] = new ETMultiModeRoutePlanDtlDOB(3,0,consoleOGW,consoleDGW,2,"O","A",consoleNo,"TP","Y");
				planDtlDOB[3] = new ETMultiModeRoutePlanDtlDOB(4,0,consoleDGW,destTerminal,4,"X","M","","GT","Y");
				planDtlDOB[4] = new ETMultiModeRoutePlanDtlDOB(5,0,destTerminal,destLoc,4,"X","A","","TL","Y");  */
			
			}
			else if( (originTerminal.equals(consoleOGW)) && !(destTerminal.equals(consoleDGW)))
			{
				overFlag="N" ;
				planDtlDOB = new ETMultiModeRoutePlanDtlDOB[4];

				planDtlDOB[0] = new ETMultiModeRoutePlanDtlDOB(1,0,originLoc,originTerminal,4,"X","A","","LT",validFlag);
				planDtlDOB[1] = new ETMultiModeRoutePlanDtlDOB(2,0,originTerminal,consoleDGW,2,"O","A",consoleNo,"TP","Y");
				planDtlDOB[2] = new ETMultiModeRoutePlanDtlDOB(3,0,consoleDGW,destTerminal,4,"X","M","","GT","Y");
				planDtlDOB[3] = new ETMultiModeRoutePlanDtlDOB(4,0,destTerminal,destLoc,4,"X","A","","TL","Y");
			}
			
			else if( !(originTerminal.equals(consoleOGW)) && (destTerminal.equals(consoleDGW)))
			{
				overFlag="N" ;				
				//updateRoutePlan = true;
				planDtlDOB = new ETMultiModeRoutePlanDtlDOB[4];

				planDtlDOB[0] = new ETMultiModeRoutePlanDtlDOB(1,0,originLoc,originTerminal,planDtlDOB[0].getShipmentMode(),"X","A","","LT",validFlag);
				planDtlDOB[1] = new ETMultiModeRoutePlanDtlDOB(2,0,originTerminal,consoleDGW,planDtlDOB[1].getShipmentMode(),"O","A",consoleNo,"TP","Y");
				planDtlDOB[2] = new ETMultiModeRoutePlanDtlDOB(3,0,consoleDGW,destTerminal,planDtlDOB[2].getShipmentMode(),"X","M","","GT","Y");
				planDtlDOB[3] = new ETMultiModeRoutePlanDtlDOB(4,0,destTerminal,destLoc,planDtlDOB[3].getShipmentMode(),"X","A","","TL","Y");
			}
			
			else
			{
				overFlag="N" ;				
				planDtlDOB = new ETMultiModeRoutePlanDtlDOB[3];
				planDtlDOB[0] = new ETMultiModeRoutePlanDtlDOB(1,0,originLoc,consoleOGW,4,"X","A","","LT",validFlag);
				planDtlDOB[1] = new ETMultiModeRoutePlanDtlDOB(2,0,consoleOGW,consoleDGW,2,"O","A",consoleNo,"TP","Y");
				planDtlDOB[2] = new ETMultiModeRoutePlanDtlDOB(3,0,consoleDGW,destLoc,4,"X","A","","TL","Y");
			}
            
			routeHdrDOB.setRoutePlanDtlDOB(planDtlDOB);
			//routeRemote.addRoutePlan(routeHdrDOB);
			//addRoutePlan(routeHdrDOB);
			if(h_id!=null && updateRoutePlan1 == false)
			{
				String docType= "HBL" ;	
				ETMultiModeRoutePlanHdrDOB newRoutePlan = getRoutePlan(docType, h_id);
				newRoutePlan.setHouseDocumentId(routeHdrDOB.getHouseDocumentId());
				newRoutePlan.setRoutePlanDtlDOB(routeHdrDOB.getRoutePlanDtlDOB());
				updateRoutePlan(newRoutePlan);
						
			}
			else if( (h_id==null) && (p_id != null || p_id !="" || p_id !="null") && updateRoutePlan1 == false)
			{
			     System.out.println("inside the prq id null conditon");
				 //addRoutePlan(routeHdrDOB);
				 addRoutePlan_Sea(routeHdrDOB,overFlag);
			}
		//@@	
		}//end of while outer loop       

	}//END OF TRY METHOD

	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"The exception in  update_Route_plan  -->"+e.toString());
    logger.error(FILE_NAME+"The exception in  update_Route_plan  -->"+e.toString());
	}
	finally
	{
		try
		{
			if(pStmt != null)
				{ pStmt.close(); }
			if(pStmt1 != null)
				{ pStmt1.close(); }
			if(pStmt2 != null)
				{ pStmt2.close(); }

            if(pStmtForRTPlan!=null)
              { pStmtForRTPlan.close(); }
            if(pStmtForGateway!=null)
              { pStmtForGateway.close();  }
            if(rsForRTPlan!=null)
              { rsForRTPlan.close(); }
            if(rsForGateway!=null)
              { rsForGateway.close();  }
			if(rs != null)
				{ rs.close(); }
			if(rs2 != null)
				{ rs2.close(); }
       		if(rs1 != null)
				{ rs1.close();	}
			if(connection != null)
				{ connection.close(); }
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in at finally() block in get_HBL_Details method  "+e.toString());
      logger.error(FILE_NAME+"Exception in at finally() block in get_HBL_Details method  "+e.toString());
		}
	}
			

  }
	
	
	
	//#################################  end of private method #############################




	/*public String isValidRoutePlan(ETMultiModeRoutePlanHdrDOB routePlanHdr)
	{
		String errors = null;
		Connection connection = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		ETMultiModeRoutePlanDtlDOB[] routePlanDtlDOB = routePlanHdr.getRoutePlanDtlDOB();

		try
		{
			connection = this.getConnection();
			
			for(int i=1; i<routePlanDtlDOB.length; i++)
			{
				String sql = "";
				int shipmentMode = routePlanDtlDOB[i].getShipmentMode();

				if(shipmentMode == 1)
					sql = "SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE ORIGINGATEWAYID = ? AND DESTINATIONGATEWAYID=? AND MASTERDOCID = ? AND SHIPMENTMODE = 1";
				else if(shipmentMode == 2)
					sql = "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ORIGINGATEWAY=? AND DESTGATEWAY = ?)  AND CONSOLTYPE NOT IN ('FCL_TO_FCL','FCL_BACK_TO_BACK','BREAK_BULK') AND (DIRECTCONSOLE IS NULL OR DIRECTCONSOLE='N') AND CONSOLEID = ?";
				else if(shipmentMode == 4)
					sql = "SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE ORIGINGATEWAYID = ? AND DESTINATIONGATEWAYID=? AND MASTERDOCID = ? AND SHIPMENTMODE = 4";

				Logger.info(FILE_NAME, "SQL -> "+sql);
				if(( routePlanDtlDOB[i].getMasterDocId() != null) && (!routePlanDtlDOB[i].getMasterDocId().equals("") ) )
				{
					System.out.println("Origin ....."+routePlanDtlDOB[i].getOriginTerminalId());
					System.out.println("Destination ....."+routePlanDtlDOB[i].getDestinationTerminalId());
					System.out.println("Master ....."+routePlanDtlDOB[i].getMasterDocId());
					pStmt = connection.prepareStatement(sql);
						pStmt.setString(1, routePlanDtlDOB[i].getOriginTerminalId());
						pStmt.setString(2, routePlanDtlDOB[i].getDestinationTerminalId());
						pStmt.setString(3, routePlanDtlDOB[i].getMasterDocId());
					rs = pStmt.executeQuery();
					boolean flag = false;
					if(rs.next())
						flag = true;
					if(!flag)
					{
						if(errors == null)
							errors = "Please Enter Valid MasterDocumentId for <br>";
						errors += "Origin "+routePlanDtlDOB[i].getOriginTerminalId()+" And Destination "+routePlanDtlDOB[i].getDestinationTerminalId()+"<br>";
					}
				}
			}
		}
		catch(Exception ex)
		{
			Logger.error(FILE_NAME, "[isValidRoutePlan(routePlanHdr)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pStmt, rs);
		}
		return errors;
	}*/

  /**
   * 
   * @param routePlanHdr
   * @return errors
   */
	public String isValidRoutePlan(ETMultiModeRoutePlanHdrDOB routePlanHdr)
	{
		// @@ Replaced by Sreelakshmi KVA for TogetherJ
		//String errors = null;
		//StringBuffer errors = new StringBuffer();
		// @@ Redefined by Sreelakshmi KVA for TogetherJ
		StringBuffer errors = null;
		Connection connection = null;
		PreparedStatement pStmt = null;
		PreparedStatement pStmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ETMultiModeRoutePlanDtlDOB[] routePlanDtlDOB = routePlanHdr.getRoutePlanDtlDOB();

		try
		{
			connection = this.getConnection();

			String sqlValid = 		"SELECT "+
										"MSTER_DOC_ID "+
									"FROM "+
										"FS_RT_PLAN RT, "+
										"FS_RT_LEG RL "+
									"WHERE "+
										"DECODE(HAWB_ID,NULL,PRQ_ID,HAWB_ID) = ? "+
										"AND RL.RT_PLAN_ID = RT.RT_PLAN_ID "+
										"AND RL.ORIG_TRML_ID = ? "+
										"AND RL.DEST_TRML_ID = ? "+
										"AND RL.MSTER_DOC_ID = ? ";

      pStmt1 = connection.prepareStatement(sqlValid);//Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
			int rtPlanDOBLen	=	routePlanDtlDOB.length;
      		for(int i=1; i<rtPlanDOBLen; i++)
			{
				String sql = "";
				int shipmentMode = routePlanDtlDOB[i].getShipmentMode();

				if(shipmentMode == 1)
					{ sql = "SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE ORIGINGATEWAYID = ? AND DESTINATIONGATEWAYID=? AND MASTERDOCID = ? AND SHIPMENTMODE = 1 AND STATUS NOT IN ('CLOSED','COB') ";}
				else if(shipmentMode == 2)
				{	sql = "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ORIGINGATEWAY=? AND DESTGATEWAY = ?)  AND CONSOLTYPE NOT IN ('FCL_TO_FCL','FCL_BACK_TO_BACK','BREAK_BULK') AND (DIRECTCONSOLE IS NULL OR DIRECTCONSOLE='N') AND CONSOLEID = ? AND CONSOLESTATUS != 'CLOSED' "; }
				else if(shipmentMode == 4)
				 {	sql = "SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE ORIGINGATEWAYID = ? AND DESTINATIONGATEWAYID=? AND MASTERDOCID = ? AND SHIPMENTMODE = 4 AND STATUS NOT IN ('CLOSED') "; }
         


        pStmt = connection.prepareStatement(sql);//Added by Rajkumari on 04-11-2008 for Connection Leakages in Loop
				//Logger.info(FILE_NAME, "SQL -> "+sql);
				if(( routePlanDtlDOB[i].getMasterDocId() != null) && (!routePlanDtlDOB[i].getMasterDocId().equals("") ) )
				{
					/*System.out.println("Origin ....."+routePlanDtlDOB[i].getOriginTerminalId());
					System.out.println("Destination ....."+routePlanDtlDOB[i].getDestinationTerminalId());
					System.out.println("Master ....."+routePlanDtlDOB[i].getMasterDocId());*/
					//pStmt = connection.prepareStatement(sql);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
					pStmt.setString(1, routePlanDtlDOB[i].getOriginTerminalId());
					pStmt.setString(2, routePlanDtlDOB[i].getDestinationTerminalId());
					pStmt.setString(3, routePlanDtlDOB[i].getMasterDocId());
					rs = pStmt.executeQuery();
					boolean flag = false;
					String masterDocId = "";
					if(rs.next())
					{
						masterDocId = rs.getString(1);
						flag = true;
					}
					if(!flag)
					{

						//pStmt1 = connection.prepareStatement(sqlValid);//Commented by Rajkumari on 04-11-2008 for Connection Leakages in Loop
						if(routePlanHdr.getPRQId()!=null)
							{ pStmt1.setString(1, routePlanHdr.getPRQId()); }
						else if(routePlanHdr.getHouseDocumentId()!=null)
							{ pStmt1.setString(1, routePlanHdr.getHouseDocumentId()); }

						pStmt1.setString(2, routePlanDtlDOB[i].getOriginTerminalId());
						pStmt1.setString(3, routePlanDtlDOB[i].getDestinationTerminalId());
						pStmt1.setString(4, routePlanDtlDOB[i].getMasterDocId());

						rs1 = pStmt1.executeQuery();
						boolean flag1 = false;
						if(rs1.next())
							{ flag1 = true; }
						
						if(!flag1)
						{
							if(errors == null)
								// @@ Added by Sreelakshmi KVA for TogetherJ
							 {
								// @@ Replaced by Sreelakshmi KVA for TogetherJ
								errors = new StringBuffer();
								errors.append( "Please Enter Valid MasterDocumentId for <br>");
							}//@@

							//errors += "Origin "+routePlanDtlDOB[i].getOriginTerminalId()+" And Destination "+routePlanDtlDOB[i].getDestinationTerminalId()+"<br>";
							errors.append("Origin ");
							errors.append(routePlanDtlDOB[i].getOriginTerminalId());
							errors.append(" And Destination ");
							errors.append(routePlanDtlDOB[i].getDestinationTerminalId());
							errors.append("<br>");
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, "[isValidRoutePlan(routePlanHdr)] -> "+ex.toString());
      logger.error(FILE_NAME+ "[isValidRoutePlan(routePlanHdr)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, pStmt, rs);
			//Connection Leakage --------------- 7-DEC-2004 ----------------------- Santhosam.P
			try
			{
				if( pStmt1 != null )
    			{	pStmt1.close(); }
				if( rs1 != null )
    				{ rs1.close(); }
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}
		

		}
		// @@ Added by Sreelakshmi KVA for TogetherJ
		if(errors!=null){
			return errors.toString();
		}
		else{
			return null;
		}
		//@@
	}

  /**
   * 
   * @param routePlanHdr
   * @return flag
   */
	public boolean addRoutePlan(ETMultiModeRoutePlanHdrDOB routePlanHdr)
	{
		boolean flag = true;

		OIDSessionHome					oidHome		= null;
		OIDSession						oidRemote	= null;

		try
		{
			ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
			ictx	= new InitialContext();
			oidHome		= (OIDSessionHome)ictx.lookup("OIDSessionBean");
			oidRemote	= (OIDSession)oidHome.create();
			long routePlanId = oidRemote.getRoutePlanId();
			routePlanHdr.setRoutePlanId(routePlanId);
			

			boolean isHBLGenerated = getStatus("HBL", routePlanHdr.getPRQId(), routePlanHdr.getPrimaryMode());

			if(isHBLGenerated)
			{
				routePlanId = getRoutePlanId("HBL", routePlanHdr.getPRQId(), routePlanHdr.getPrimaryMode());
				boolean isRoutePlanGenerated = getRoutePlanStatus(routePlanId);

				int primaryMode = routePlanHdr.getPrimaryMode();
				String docType="PRQ";

				if(primaryMode==2)
				{
					docType="BOOKING";
				}
				ETMultiModeRoutePlanHdrDOB newRoutePlan = getRoutePlan(docType, routePlanHdr.getPRQId());
				newRoutePlan.setHouseDocumentId(routePlanHdr.getHouseDocumentId());
				if(!isRoutePlanGenerated)
				{
					newRoutePlan.setRoutePlanDtlDOB(routePlanHdr.getRoutePlanDtlDOB());
				}
				updateRoutePlan(newRoutePlan);
			}
			else
			{
				routePlanDAO.create(routePlanHdr);
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [addRoutePlan(routePlanHdr)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [addRoutePlan(routePlanHdr)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		return flag;
	}

	private boolean addRoutePlan_Sea(ETMultiModeRoutePlanHdrDOB routePlanHdr,String over_flag)
	{
		boolean flag = true;

		OIDSessionHome					oidHome		= null;
		OIDSession						oidRemote	= null;

		try
		{
			ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
			ictx	= new InitialContext();
			oidHome		= (OIDSessionHome)ictx.lookup("OIDSessionBean");
			oidRemote	= (OIDSession)oidHome.create();
			long routePlanId = oidRemote.getRoutePlanId();
			routePlanHdr.setRoutePlanId(routePlanId);
			

			boolean isHBLGenerated = getStatus("HBL", routePlanHdr.getPRQId(), routePlanHdr.getPrimaryMode());
			
			if(isHBLGenerated)
			{
				
				routePlanId = getRoutePlanId("HBL", routePlanHdr.getPRQId(), routePlanHdr.getPrimaryMode());
				boolean isRoutePlanGenerated = getRoutePlanStatus(routePlanId);
				int primaryMode = routePlanHdr.getPrimaryMode();
				String docType="PRQ";
				if(primaryMode==2)
				{
					docType="BOOKING";
				}
				ETMultiModeRoutePlanHdrDOB newRoutePlan = getRoutePlan(docType, routePlanHdr.getPRQId());
				newRoutePlan.setHouseDocumentId(routePlanHdr.getHouseDocumentId());
				if(over_flag != null && over_flag.equals("Y"))
				 { isRoutePlanGenerated = false ; }
				if(!isRoutePlanGenerated)
				{
					newRoutePlan.setRoutePlanDtlDOB(routePlanHdr.getRoutePlanDtlDOB());
				}
				updateRoutePlan(newRoutePlan);
				
			}
			else
			{
				
				routePlanDAO.create(routePlanHdr);
			}
			
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [addRoutePlan_Sea(routePlanHdr)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [addRoutePlan_Sea(routePlanHdr)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		return flag;
	}	

	
	private long getRoutePlanId(String documentType, String documentId, int primaryMode)
	{
		long routePlanId = 0L;

		Connection	connection	= null;
		Statement	stmt		= null;
		ResultSet	rs			= null;

		try
		{
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE PRQ_ID='"+documentId+"' AND PRMY_MODE = "+primaryMode);
			while(rs.next())
		 {		routePlanId = rs.getLong(1); }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getRoutePlanId(documentType, documentId, primaryMode)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [getRoutePlanId(documentType, documentId, primaryMode)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return routePlanId;
	}

	private boolean getRoutePlanStatus(long routePlanId)
	{
		boolean flag = false;

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT SHPMNT_STATUS FROM FS_RT_PLAN WHERE RT_PLAN_ID ="+routePlanId);

			String status = null;

			while(rs.next())
			{	status = rs.getString(1); }
			if(!status.equals("N"))
				{ flag = true; }
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [getRoutePlanStatus(routePlanId)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [getRoutePlanStatus(routePlanId)] -> "+sqEx.toString());

			throw new EJBException(sqEx.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return flag;
	}

	private boolean getStatus(String documentType, String documentId, int primaryMode)
	{
		boolean flag = false;

		Connection	connection	= null;
		Statement	stmt		= null;
		ResultSet	rs			= null;

		try
		{
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT PRQ_ID FROM FS_RT_PLAN WHERE PRQ_ID='"+documentId+"' AND PRMY_MODE = "+primaryMode);
			while(rs.next())
				{ flag = true; }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getStatus(documentType, documentId, primaryMode)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [getStatus(documentType, documentId, primaryMode)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return flag;
	}

  /**
   * 
   * @param routePlanHdr
   * @param consoleAttach
   * @param documentType
   * @param prqHouseMasterId
   * @return flag
   */
	public boolean updateRoutePlan(ETMultiModeRoutePlanHdrDOB routePlanHdr, String consoleAttach,String documentType,String prqHouseMasterId)
	{
		boolean flag = true;

		try
		{
			ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
			routePlanDAO.store(routePlanHdr);
			if(documentType.equals("PRQ") || documentType.equals("BOOKING") || documentType.equals("HBL"))
				{ updatePrqAndHouse(routePlanHdr,prqHouseMasterId); }
			if(consoleAttach.equals("Changed"))
				 { updateConsole(routePlanHdr,prqHouseMasterId); }

		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [updateRoutePlan(routePlanHdr)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [updateRoutePlan(routePlanHdr)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		return flag;
	}
// Added By NageswaraRao.D - 2/11/02
 public StringBuffer validateSellRatesHdrData(String[] orignLoc,String[] destLoc,String[] shipmentMode)
{
      PreparedStatement     pStmt		 		      =   null;
      ResultSet    					rs            	  = 	null;
      Connection            connection        =   null;
      StringBuffer          errorMassege      =   null;
      String                message           =   ""; 
      boolean               oFlag             =   false;
      String                originQuery       =   null;
      String                portsQuery        =   null;
      try
      {
         
          errorMassege  = new StringBuffer();
          connection = this.getConnection(); 
          originQuery  = "SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID=? ";
          portsQuery   = "SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID=?";
          //System.out.println("sellDob.getOrigin()sellDob.getOrigin() ::: in dao : "+sellDob.getOrigin());
          if(orignLoc!=null && orignLoc.length>0)
          {
            //  pStmt             =   connection.prepareStatement(originQuery);
              int originIdsSize =   orignLoc.length;
              System.out.println("originIdsSize in RTbean :: "+originIdsSize);
              for(int i=0;i<originIdsSize;i++)
              {
               // System.out.println("originIds in bean :: "+orignLoc[i]);
               if(shipmentMode!=null)
               {
                 if("2".equalsIgnoreCase(shipmentMode[i]))
                    pStmt             =   connection.prepareStatement(portsQuery);
                 else
                    pStmt             =   connection.prepareStatement(originQuery);
               }
                pStmt.setString(1,orignLoc[i]);
                rs = pStmt.executeQuery();
                if(!rs.next())
                {
                  message += orignLoc[i]+" ";
                  oFlag  = true;
                }
                if(rs!=null)rs.close();
                if(pStmt!=null)pStmt.close();
              }
              if(oFlag || message!="")
                errorMassege.append(message+" :Orign Ids are not valid .<br>");
              else
                errorMassege.append(message);
                
            if(rs!=null)rs.close();
            if(pStmt!=null)pStmt.close();
          }
          if(destLoc!=null && destLoc.length>0)
          {
             // pStmt           =   connection.prepareStatement(originQuery);
              oFlag = false;
              message = "";
              int destIdsSize = destLoc.length;
        
              //System.out.println("destIdsSize in RTbean :: "+destIdsSize);
              for(int i=0;i<destIdsSize;i++)
              {
                //destValues = (String)destIds.get(i);
                //System.out.println("destValues in bean :: "+destLoc[i]);
                if(shipmentMode!=null)
               {
                 if("2".equalsIgnoreCase(shipmentMode[i]))
                    pStmt             =   connection.prepareStatement(portsQuery);
                 else
                    pStmt             =   connection.prepareStatement(originQuery);
               }
                pStmt.setString(1,destLoc[i]);
                rs = pStmt.executeQuery();
                if(!rs.next())
                {
                  message += destLoc[i]+" ";
                  oFlag  = true;
                }
                if(rs!=null)rs.close();
                if(pStmt!=null)pStmt.close();
          }
          if(oFlag || message!="")
            errorMassege.append(message+" :Destination Ids are not valid .<br>");
          else
            errorMassege.append(message);
            
          if(rs!=null)rs.close();
          if(pStmt!=null)pStmt.close();
        }
        
      }
     /* catch(SQLException sqle)
      {
          Logger.error(FILE_NAME,"SQLEXception in validateSellRatesHdrData()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException(sqle.toString());
      }*/
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"EXception in validateSellRatesHdrData()-->"+e.toString());
        logger.error(FILE_NAME+"EXception in validateSellRatesHdrData()-->"+e.toString());
        e.printStackTrace();
        throw new EJBException(e.toString());
      }
      finally
		  {
		      try
		      {
             if(rs!=null)
                {rs.close();}
              if(pStmt!=null)
                {pStmt.close();}
              if(connection!=null)
                {connection.close();}
          }
          catch(Exception ex)
          {
              //Logger.error(FILE_NAME,"Exception caught :: finally :: isExetIds() " + ex.toString() );
              logger.error(FILE_NAME+"Exception caught :: finally :: isExetIds() " + ex.toString() );
          }
		  
		  }
      return errorMassege;
 }

/**
   * 
   * @param routePlanHdr
   * @param consoleAttach
   * @param documentType
   * @param prqHouseMasterId
   * @return flag
   */
	public boolean updateRoutePlan(ArrayList routePlanList, String quoteId,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginBean)
	{
      boolean flag = true;
      try
      {
          ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
          routePlanDAO.store(routePlanList,quoteId,loginBean);
			}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [updateRoutePlan(routePlanList,quoteId,loginBean)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [updateRoutePlan(routePlanList,quoteId,loginBean)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		return flag;
	}
private void updatePrqAndHouse(ETMultiModeRoutePlanHdrDOB routePlanHdrDOB,String prqHouseMasterId)
	{
		PreparedStatement	pStmt1	= null;
		PreparedStatement	pStmt2	= null;
		//ResultSet			rs		= null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		Connection	connection	= null;
		
		String[]	updateQuery = new String[2];

		updateQuery[0]	= "UPDATE FS_FR_PICKUPREQUEST SET MASTERDOCID=? WHERE PRQID = ?";
		updateQuery[1]	= "UPDATE FS_FR_HOUSEDOCHDR SET MASTERDOCID=? WHERE HOUSEDOCID = ?";

		try
		{
			connection = this.getConnection();
			pStmt1 = connection.prepareStatement(updateQuery[0]);

			if(routePlanHdrDOB.getPRQId() != null)
			{
				if(prqHouseMasterId.equals("")||prqHouseMasterId.equals("null"))
				{	pStmt1.setNull(1, Types.VARCHAR); }
				else
					{ pStmt1.setString(1,prqHouseMasterId); }

				pStmt1.setString(2, routePlanHdrDOB.getPRQId());
			}
			pStmt1.executeUpdate();

			pStmt2 = connection.prepareStatement(updateQuery[1]);
			if(routePlanHdrDOB.getHouseDocumentId() != null)
			{
				if(prqHouseMasterId.equals("")||prqHouseMasterId.equals("null"))
					{ pStmt2.setNull(1, Types.VARCHAR); }
				else
					{ pStmt2.setString(1,prqHouseMasterId); }
				pStmt2.setString(2, routePlanHdrDOB.getHouseDocumentId());
			}
			pStmt2.executeUpdate();
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [updatePrqAndHouse(connection, routePlanDtlDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [updatePrqAndHouse(connection, routePlanDtlDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
			/*	if(rs!=null)
				  { rs.close(); }*///Commented By RajKumari on 23-10-2008 for Connection Leakages.
				if(pStmt1!=null)
				  { pStmt1.close(); }
				if(pStmt2 != null)
				  { pStmt2.close(); }
                if(connection!=null)
					{ connection.close(); }

			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [updatePrqAndHouse(connection, routePlanDtlDOB)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [updatePrqAndHouse(connection, routePlanDtlDOB)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
	}
	// end here

  /**
   * 
   * @param routePlanHdr
   * @return flag
   */
	public boolean updateRoutePlan(ETMultiModeRoutePlanHdrDOB routePlanHdr)
	{
		boolean flag = true;

		try
		{
			ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
			routePlanDAO.store(routePlanHdr);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [updateRoutePlan(routePlanHdr)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [updateRoutePlan(routePlanHdr)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		return flag;
	}

	
	private void updateConsole(ETMultiModeRoutePlanHdrDOB routePlanHdrDOB, String prqHouseMasterId)
	{
		PreparedStatement	pStmt1	= null;
		PreparedStatement	pStmt2	= null;
		//ResultSet			rs		= null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		Connection	connection	= null;

		String[]	updateQuery = new String[2];

		updateQuery[0]	= "UPDATE FS_FR_PICKUPREQUEST SET  MASTERDOCID=? WHERE PRQID = ?";
		updateQuery[1]	= "UPDATE FS_FR_HOUSEDOCHDR SET MASTERDOCID=? WHERE HOUSEDOCID = ?";

		try
		{
			connection = this.getConnection();

			if(routePlanHdrDOB.getPRQId() != null)
			{
				pStmt1 = connection.prepareStatement(updateQuery[0]);

				if(prqHouseMasterId!=null && (prqHouseMasterId.equals("")||prqHouseMasterId.equals("null")))
				{	pStmt1.setNull(1, Types.VARCHAR); }
				else
					{ pStmt1.setString(1,prqHouseMasterId); }

				pStmt1.setString(2, routePlanHdrDOB.getPRQId());
	
				pStmt1.executeUpdate();
			}

			if(routePlanHdrDOB.getHouseDocumentId() != null)
			{
				pStmt2 = connection.prepareStatement(updateQuery[1]);

				if(prqHouseMasterId!=null && (prqHouseMasterId.equals("")||prqHouseMasterId.equals("null")))
					{ pStmt2.setNull(1, Types.VARCHAR); }
				else
					 { pStmt2.setString(1,prqHouseMasterId); }

				pStmt2.setString(2, routePlanHdrDOB.getHouseDocumentId());
	
				pStmt2.executeUpdate();
			}
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [updateConsole(connection, routePlanDtlDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [updateConsole(connection, routePlanDtlDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			try
			{
				/*if(rs!=null)
				  { rs.close(); }*///Commented By RajKumari on 23-10-2008 for Connection Leakages.
				if(pStmt1!=null)
				  { pStmt1.close(); }
				if(pStmt2 != null)
				  { pStmt2.close(); }
                if(connection!=null)
					{ connection.close(); }

			}
			catch(SQLException sqEx)
			{
				//Logger.error(FILE_NAME, " [updateConsole(connection, routePlanDtlDOB)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ " [updateConsole(connection, routePlanDtlDOB)] -> "+sqEx.toString());
				throw new EJBException(sqEx.toString());
			}
		}
	}

  /**
   * 
   * @param documentId
   */
	public void deleteRoutePlan(String documentId)
	{
		try
		{
			ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
			routePlanDAO.remove(getRoutePlanId("PRQ", documentId));
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [updateRoutePlan(routePlanHdr)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [updateRoutePlan(routePlanHdr)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
	}
/*
	Added by NageswaraRao.D for Description Lov at Route Plan EnterId screeen date 14/08/02 
*/	  
  /**
   * 
   * @param forWhat
   * @param documentType
   * @param searchStr
   * @param terminalId
   * @param shipmentMode
   * @return masterDocumentHeader
   */
 /* public MasterDocumentHeader[] getDocumentNos(String forWhat, String documentType, String searchStr, String terminalId, String shipmentMode)
	{
		Connection		connection	=	null;
		Statement		stmt		=	null;
		ResultSet		rs			=	null;
		String			sql			=	null;
		String			chekcSql	=	null;
		String			assignMode	=	null;
		int				mode		=	0;
		int				count		=	0;
		String			prqId		=	"";
		String			primMode	=	"";
    String hblCondition ="";
		MasterDocumentHeader[] masterDocumentHeader =null;
		try
		{
			connection = this.getConnection();
			if(documentType.equals("PRQ")||documentType.equals("BOOKING"))
				{ prqId	=	" P.PRQ_ID "; }
			else
				{ prqId	=	" P.HAWB_ID "; }

			if(documentType.equals("PRQ"))
			{
				primMode	= " P.PRMY_MODE IN (1,4) AND R.PRQID=P.PRQ_ID AND R.OPENBOOKINGSTATUS = 'N' ";
				hblCondition=" ,FS_FR_PICKUPREQUEST R ";	
			}
			if(documentType.equals("BOOKING"))
			{
				primMode	= " P.PRMY_MODE =2 AND R.PRQID=P.PRQ_ID AND R.MASTERTYPE NOT IN ('FCL_TO_FCL','BREAK_BULK','FCL_BACK_TO_BACK') AND R.OPENBOOKINGSTATUS = 'N' ";
				hblCondition=" ,FS_FR_PICKUPREQUEST R ";	
				
			}
			if(documentType.equals("HAWB"))
				{ primMode	= " P.PRMY_MODE =1 "; }
			if(documentType.equals("HBL"))
			{
				primMode	= " P.PRMY_MODE =2 AND H.HOUSEDOCID=P.HAWB_ID AND H.CONSOLTYPE NOT IN ('FCL_TO_FCL','FCL_BACK_TO_BACK','BREAK_BULK') ";
				hblCondition =" ,FS_FR_HOUSEDOCHDR H ";
			}
			if(documentType.equals("CONSIGNMENTNOTE"))
				 { primMode	= " P.PRMY_MODE =4 "; }

					
			sql	=		" SELECT DISTINCT "+prqId+",P.SHIPPER_ID, "+ 
						" P.CONSIGNEE_ID,P.ORIG_TRML_ID,P.DEST_TRML_ID,P.PRMY_MODE "+
						" FROM FS_RT_PLAN P,FS_RT_LEG L "+hblCondition+" "+
						" WHERE P.RT_PLAN_ID = L.RT_PLAN_ID "+
						" AND "+prqId+" LIKE '"+searchStr+"%' "+
						" AND "+primMode+" AND P.SHPMNT_STATUS IN ('N', 'O') "+ 
						" AND L.LEG_VALID_FLAG='Y' "+ 
						" AND( L.SHPMNT_MODE= "+shipmentMode+" OR P.PRMY_MODE = "+shipmentMode+") "+
						" AND (L.ORIG_TRML_ID='"+terminalId+"' OR P.DEST_TRML_ID='"+terminalId+"') "+
						" ORDER BY "+prqId+" DESC "; 

	
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
				{ count++; }
			rs = stmt.executeQuery(sql);
			masterDocumentHeader = new MasterDocumentHeader[count];
			int cnt = 0;
			while(rs.next())
			{
				masterDocumentHeader[cnt]=	new MasterDocumentHeader();	
				masterDocumentHeader[cnt].setDocId(rs.getString(1));
				masterDocumentHeader[cnt].setShipperId(rs.getString(2));
				masterDocumentHeader[cnt].setConsigneeId(rs.getString(3));
				masterDocumentHeader[cnt].setOriginTerminal(rs.getString(4));
				masterDocumentHeader[cnt].setDestTerminal(rs.getString(5));
				masterDocumentHeader[cnt].setPrimaryMode(rs.getInt(6));
				cnt++;				
			}
		}
		catch(SQLException sqEx)
		{
			Logger.error(FILE_NAME, " [getDocumentNos(documentType)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}

	return masterDocumentHeader;
	}*/
/* Code commented by Nageswara Rao.D 
	public String[] getDocumentNos(String forWhat, String documentType, String searchStr, String terminalId, String shipmentMode)
	{
		Connection	connection	= null;
		Statement	stmt		= null;
		ResultSet	rs			= null;
		String		sql			= "";
		boolean		flag		= false;
		String[]	documentNos = null;
		int			count		= 0;

		try
		{
			connection = this.getConnection();
			if(documentType.equals("PRQ"))
			{
					sql =	"SELECT "+
								"DISTINCT P.PRQ_ID "+
							"FROM "+
								"FS_RT_PLAN P, "+
								"FS_RT_LEG L "+
							"WHERE "+
								"P.PRQ_ID LIKE '"+searchStr+"%' "+
								"AND P.SHPMNT_STATUS IN ('N', 'O') "+
								"AND "+
								"( "+
									"( "+
										"P.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.PRMY_MODE IN (1, 4) "+
									") "+
									"OR "+
									"( "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND L.LEG_VALID_FLAG='Y' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
									"OR "+
									"( "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.DEST_TRML_ID='"+terminalId+"' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
								") "+
								"AND P.RT_PLAN_ID = L.RT_PLAN_ID";
			}
			else if(documentType.equals("BOOKING"))
			{
					//sql = "SELECT DISTINCT P.PRQ_ID FROM FS_RT_PLAN P, FS_RT_LEG L WHERE P.PRQ_ID LIKE '"+searchStr+"%' AND P.SHPMNT_STATUS IN ('N', 'O') AND ((P.ORIG_TRML_ID='"+terminalId+"' AND P.PRMY_MODE=2) OR (L.ORIG_TRML_ID='"+terminalId+"' AND L.LEG_VALID_FLAG='Y' AND L.SHPMNT_MODE="+shipmentMode+")) AND P.RT_PLAN_ID = L.RT_PLAN_ID";
					sql	=	"SELECT "+
								"DISTINCT P.PRQ_ID "+
							"FROM "+
								"FS_RT_PLAN P, "+
								"FS_RT_LEG L "+
							"WHERE "+
								"P.PRQ_ID LIKE '"+searchStr+"%' "+
								"AND P.SHPMNT_STATUS IN ('N', 'O') "+
								"AND "+
								"( "+
									"( "+
										"P.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.PRMY_MODE=2 "+
									") "+
									"OR "+
									"( "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND L.LEG_VALID_FLAG='Y' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
									"OR "+
									"( "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.DEST_TRML_ID='"+terminalId+"' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
								") "+
								"AND P.RT_PLAN_ID = L.RT_PLAN_ID";
			}
			else if(documentType.equals("HAWB"))
			{
					//sql = "SELECT DISTINCT P.HAWB_ID FROM FS_RT_PLAN P, FS_RT_LEG L WHERE P.HAWB_ID LIKE '"+searchStr+"%' AND P.SHPMNT_STATUS IN ('N', 'O') AND P.HAWB_ID IS NOT NULL AND ((P.ORIG_TRML_ID='"+terminalId+"' AND P.PRMY_MODE=1) OR (L.ORIG_TRML_ID='"+terminalId+"' AND L.LEG_VALID_FLAG='Y' AND L.SHPMNT_MODE="+shipmentMode+")) AND P.RT_PLAN_ID = L.RT_PLAN_ID";
					sql =	"SELECT "+
								"DISTINCT P.HAWB_ID "+
							"FROM "+
								"FS_RT_PLAN P, "+
								"FS_RT_LEG L "+
							"WHERE "+
								"P.HAWB_ID LIKE '"+searchStr+"%' "+
								"AND P.SHPMNT_STATUS IN ('N', 'O') "+
								"AND P.HAWB_ID IS NOT NULL "+
								"AND "+
								"( "+
									"( "+
										"P.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.PRMY_MODE=1 "+
									") "+
									"OR "+
									"( "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND L.LEG_VALID_FLAG='Y' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
									"OR "+
									"( "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.DEST_TRML_ID='"+terminalId+"' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
								") "+
								"AND P.RT_PLAN_ID = L.RT_PLAN_ID";
			}
			else if(documentType.equals("HBL"))
			{
					//sql = "SELECT DISTINCT P.HAWB_ID FROM FS_RT_PLAN P, FS_RT_LEG L WHERE P.HAWB_ID LIKE '"+searchStr+"%' AND P.SHPMNT_STATUS IN ('N', 'O') AND P.HAWB_ID IS NOT NULL AND ((P.ORIG_TRML_ID='"+terminalId+"' AND P.PRMY_MODE=2) OR (L.ORIG_TRML_ID='"+terminalId+"' AND L.LEG_VALID_FLAG='Y' AND L.SHPMNT_MODE="+shipmentMode+")) AND P.RT_PLAN_ID = L.RT_PLAN_ID";
					sql		= "SELECT "+
								"DISTINCT P.HAWB_ID "+
							"FROM "+
								"FS_RT_PLAN P, "+
								"FS_RT_LEG L "+
							"WHERE "+
								"P.HAWB_ID LIKE '"+searchStr+"%' "+
								"AND P.SHPMNT_STATUS IN ('N', 'O') "+
								"AND P.HAWB_ID IS NOT NULL "+
								"AND "+
								"( "+
									"( "+
										"P.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.PRMY_MODE=2 "+
									") "+
									"OR "+
									"(	 "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND L.LEG_VALID_FLAG='Y' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
									"OR "+
									"(	 "+
										"L.ORIG_TRML_ID='"+terminalId+"' "+
										"AND P.DEST_TRML_ID='"+terminalId+"' "+
										"AND L.SHPMNT_MODE="+shipmentMode+" "+
									") "+
								") "+
								"AND P.RT_PLAN_ID = L.RT_PLAN_ID";
			}
			else if(documentType.equals("CONSIGNMENTNOTE"))
			{
					//sql = "SELECT DISTINCT P.HAWB_ID FROM FS_RT_PLAN P, FS_RT_LEG L WHERE P.HAWB_ID LIKE '"+searchStr+"%' AND P.SHPMNT_STATUS IN ('N', 'O') AND P.HAWB_ID IS NOT NULL AND ((P.ORIG_TRML_ID='"+terminalId+"' AND P.PRMY_MODE=4) OR (L.ORIG_TRML_ID='"+terminalId+"' AND L.LEG_VALID_FLAG='Y' AND L.SHPMNT_MODE="+shipmentMode+")) AND P.RT_PLAN_ID = L.RT_PLAN_ID";
				sql	=	"SELECT "+
							"DISTINCT P.HAWB_ID "+
						"FROM "+
							"FS_RT_PLAN P, "+
							"FS_RT_LEG L "+
						"WHERE "+
							"P.HAWB_ID LIKE '"+searchStr+"%' "+
							"AND P.SHPMNT_STATUS IN ('N', 'O') "+
							"AND P.HAWB_ID IS NOT NULL "+
							"AND "+
							"( "+
								"( "+
									"P.ORIG_TRML_ID='"+terminalId+"' "+
									"AND P.PRMY_MODE=4 "+
								") "+
								"OR "+
								"( "+
									"L.ORIG_TRML_ID='"+terminalId+"' "+
									"AND L.LEG_VALID_FLAG='Y' "+
									"AND L.SHPMNT_MODE="+shipmentMode+" "+
								") "+
								"OR "+
								"( "+
									"L.ORIG_TRML_ID='"+terminalId+"' "+
									"AND P.DEST_TRML_ID='"+terminalId+"' "+
									"AND L.SHPMNT_MODE="+shipmentMode+" "+
								") "+
							") "+
							"AND P.RT_PLAN_ID = L.RT_PLAN_ID";
			}

			Logger.info(FILE_NAME, "SQL Second -> "+sql);

			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
				count++;

			documentNos = new String[count];
			count = 0;

			rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				documentNos[count] = rs.getString(1);
				count++;
			}
		}
		catch(SQLException sqEx)
		{
			Logger.error(FILE_NAME, " [getDocumentNos(documentType)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return documentNos;
	}
*/
  /**
   * 
   * @param documentType
   * @param houseDocNo
   * @param terminalId
   * @param shipmentMode
   * @return flag
   */
   // modofied by VALKSHMI for issue 174469 on 26/06/09
	public boolean isValidDocumentNo(String documentType, String houseDocNo, String terminalId,String userTerminalType, String shipmentMode)
	{
		Connection	connection	= null;
		Statement	stmt		= null;
		ResultSet	rs			= null;
		String			prqId		=	"";
		String			primMode	=	"";

		boolean flag = false;
		String sql = "";
		int shipmentCode = 0;
    String hblCondition ="";
String  terminalIdQuery="";
		try
		{
    /*
			if(documentType.equals("PRQ")||documentType.equals("BOOKING"))
				{ prqId	=	" P.PRQ_ID "; }
			else
				{ prqId	=	" P.HAWB_ID "; }

			if(documentType.equals("PRQ"))
			{
				primMode	= " P.PRMY_MODE IN (1, 4) AND R.PRQID=P.PRQ_ID AND R.OPENBOOKINGSTATUS != 'Y' ";
				hblCondition=" ,FS_FR_PICKUPREQUEST R ";
			}
			if(documentType.equals("BOOKING"))
			{
				primMode	= " P.PRMY_MODE =2 AND R.PRQID=P.PRQ_ID AND R.MASTERTYPE NOT IN ('FCL_TO_FCL','BREAK_BULK','FCL_BACK_TO_BACK') AND R.OPENBOOKINGSTATUS != 'Y' ";
				hblCondition=" ,FS_FR_PICKUPREQUEST R ";
			}
			if(documentType.equals("HAWB"))
				{ primMode	= " P.PRMY_MODE =1 "; }
			if(documentType.equals("HBL"))
			{
				primMode	= " P.PRMY_MODE =2 AND H.HOUSEDOCID=P.HAWB_ID AND H.CONSOLTYPE NOT IN ('FCL_TO_FCL','FCL_BACK_TO_BACK','BREAK_BULK') ";
				hblCondition =" ,FS_FR_HOUSEDOCHDR H ";
			}
			if(documentType.equals("CONSIGNMENTNOTE"))
				{ primMode	= " P.PRMY_MODE =4 "; }

					
			sql	=		" SELECT DISTINCT "+prqId+" "+
						" FROM FS_RT_PLAN P,FS_RT_LEG L "+hblCondition+" "+
						" WHERE P.RT_PLAN_ID = L.RT_PLAN_ID "+
						" AND "+prqId+"= '"+houseDocNo+"' "+
						" AND "+primMode+" AND P.SHPMNT_STATUS IN ('N', 'O') "+ 
						" AND L.LEG_VALID_FLAG='Y' "+ 
						" AND( L.SHPMNT_MODE= "+shipmentMode+" OR P.PRMY_MODE = "+shipmentMode+") "+
						" AND (L.ORIG_TRML_ID='"+terminalId+"' OR P.DEST_TRML_ID='"+terminalId+"') "+
						" ORDER BY  "+prqId+" DESC "; 

      */
      if("O".equalsIgnoreCase(userTerminalType))
            {
            terminalIdQuery="SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = '"+userTerminalType+"'   AND TERMINALID LIKE '"+terminalId+"'   AND (INVALIDATE = 'F'OR INVALIDATE IS NULL) ";
            }else if("H".equalsIgnoreCase(userTerminalType))
            {
               terminalIdQuery="select terminalid term_id from fs_fr_terminalmaster";
            }
            else
            {
              terminalIdQuery="select child_terminal_id term_id from fs_fr_terminal_regn "+ 
           "  where parent_terminal_id='"+terminalId+"' union select '"+terminalId+"' from dual";
            }
      sql	=		"SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE COMPLETE_FLAG = 'I' AND SPOT_RATES_FLAG IN ('N', null) AND QUOTE_ID = '"+houseDocNo+"' AND ACTIVE_FLAG = 'A' AND terminal_id  IN("+terminalIdQuery+")";
      //"SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE COMPLETE_FLAG='I' AND SPOT_RATES_FLAG IN('N',null) AND QUOTE_ID='"+houseDocNo+"' AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID='"+houseDocNo+"')";
      
			//Logger.info(FILE_NAME, "SQL -> "+sql);
			
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
				{ flag = true; }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [isValidDocumentNo(documentType, houseDocNo)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [isValidDocumentNo(documentType, houseDocNo)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return flag;
	}


  /**
   * 
   * @param documentType
   * @param houseDocNo
   * @return routePlan
   */
	public ETMultiModeRoutePlanHdrDOB getRoutePlan(String documentType, String houseDocNo)
	{
		Connection	connection	= null;
		Statement	stmt		= null;
		ResultSet	rs			= null;

		String sql = "";
		// @@ Added by Sreelakshmi KVA for TogetherJ
		StringBuffer query1 = new StringBuffer();

		ETMultiModeRoutePlanHdrDOB routePlan = null;
		String routePlanId = null;

		try
		{
			ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
			if(documentType.equals("PRQ") || documentType.equals("BOOKING"))
			{
				sql = "SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE PRQ_ID = '"+houseDocNo+"'";
			}
			else if(documentType.equals("HAWB") || documentType.equals("HBL") || documentType.equals("CONSIGNMENTNOTE"))
			{
				sql = "SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE HAWB_ID = '"+houseDocNo+"'";
			}
      else if(documentType.equals("Quote"))
      {
        sql = "SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE QUOTE_ID = '"+houseDocNo+"'";
      }
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{	routePlanId = rs.getString(1);}

			if(routePlanId != null)
				{ routePlan = routePlanDAO.load(Long.parseLong(routePlanId)); }
			else
				{ throw new ObjectNotFoundException("CouldNotFindRouteId"); }


			if(routePlan != null)
			{
				int primaryMode = routePlan.getPrimaryMode();

				String query = "";

			/*	if(primaryMode == 1)
				{

					//if(documentType.equals("PRQ"))
					if(documentType.equals("PRQ") || documentType.equals("BOOKING"))
					{
						query = "SELECT P.SERVICELEVEL, S.SERVICELEVELDESC, P.DELIVERYTERMS, P.TOTALPCS,"+
								//@@ Avinash replaced on 20041024
								//" P.TOTALWEIGHT,"+
								" P.CHARGABLEWEIGHT,"+
								//@@ 
								"P.PRQSTATUS,P.WHSTATUS FROM FS_FR_PICKUPREQUEST P, FS_FR_SERVICELEVELMASTER S WHERE P.PRQID='"+routePlan.getPRQId()+"' AND S.SERVICELEVELID = P.SERVICELEVEL AND S.SHIPMENTMODE IN (1, 3, 5, 7)";
					}
					//else if(documentType.equals("HAWB"))
					else if(documentType.equals("HAWB") || documentType.equals("HBL") || documentType.equals("CONSIGNMENTNOTE"))
					{
						query = "SELECT P.SERVICELEVEL, S.SERVICELEVELDESC, P.PAYMENTMODE, P.TOTPIECES,"+
								//@@ Avinash replaced on 20041024
								//" P.GROSSWEIGHT"+
								" P.CHARGABLEWEIGHT"+
								//@@ 
								" FROM FS_FR_HOUSEDOCHDR P, FS_FR_SERVICELEVELMASTER S WHERE P.HOUSEDOCID = '"+routePlan.getHouseDocumentId()+"' AND S.SERVICELEVELID = P.SERVICELEVEL AND S.SHIPMENTMODE IN (1, 3, 5, 7)";
					}
				}
				else if(primaryMode == 2)
				{
					//if(documentType.equals("BOOKING"))
					if(documentType.equals("PRQ") || documentType.equals("BOOKING"))
					{
						query = "SELECT P.SERVICELEVEL, S.SERVICELEVELDESC, P.DELIVERYTERMS, P.TOTALPCS, P.CHARGABLEWEIGHT,P.PRQSTATUS,P.WHSTATUS FROM FS_FR_PICKUPREQUEST P, FS_FR_SERVICELEVELMASTER S WHERE P.PRQID='"+routePlan.getPRQId()+"' AND S.SERVICELEVELID = P.SERVICELEVEL AND S.SHIPMENTMODE IN (2, 3, 6, 7)";
					}
					//else if(documentType.equals("HBL"))
					else if(documentType.equals("HAWB") || documentType.equals("HBL") || documentType.equals("CONSIGNMENTNOTE"))
					{
						query = "SELECT P.SERVICELEVEL, S.SERVICELEVELDESC, P.SHIPMENTTYPE, P.TOTPIECES,"+
								//@@ Avinash replaced on 20041024
								//" P.GROSSWEIGHT"+
								" P.CHARGABLEWEIGHT"+
								//@@ 
								" FROM FS_FR_HOUSEDOCHDR P, FS_FR_SERVICELEVELMASTER S WHERE P.HOUSEDOCID='"+routePlan.getHouseDocumentId()+"' AND S.SERVICELEVELID = P.SERVICELEVEL AND S.SHIPMENTMODE IN (2, 3, 6, 7)";
					}
				}
				else if(primaryMode == 4)
				{
					//if(documentType.equals("PRQ"))
					if(documentType.equals("PRQ") || documentType.equals("BOOKING"))
					{
						query = "SELECT P.SERVICELEVEL, S.SERVICELEVELDESC, P.DELIVERYTERMS, P.TOTALPCS,"+
								//@@ Avinash replaced on 20041024
								//" P.TOTALWEIGHT,"+
								" P.CHARGABLEWEIGHT,"+
								//@@ 
								"P.PRQSTATUS,P.WHSTATUS FROM FS_FR_PICKUPREQUEST P, FS_FR_SERVICELEVELMASTER S WHERE P.PRQID='"+routePlan.getPRQId()+"' AND S.SERVICELEVELID = P.SERVICELEVEL AND S.SHIPMENTMODE IN (4, 5, 6, 7)";
					}
					//else if(documentType.equals("CONSIGNMENTNOTE"))
					else if(documentType.equals("HAWB") || documentType.equals("HBL") || documentType.equals("CONSIGNMENTNOTE"))
					{
						query = "SELECT P.SERVICELEVEL, S.SERVICELEVELDESC, P.PAYMENTMODE, P.TOTPIECES,"+
								//@@ Avinash replaced on 20041024
								//" P.GROSSWEIGHT"+
								" P.CHARGABLEWEIGHT"+
								//@@ 
								" FROM FS_FR_HOUSEDOCHDR P, FS_FR_SERVICELEVELMASTER S WHERE P.HOUSEDOCID = '"+routePlan.getHouseDocumentId()+"' AND S.SERVICELEVELID = P.SERVICELEVEL AND S.SHIPMENTMODE IN (4, 5, 6, 7)";
					}
				}*/
        System.out.println("routePlan.getPRQId()routePlan.getPRQId() :: "+routePlan.getPRQId());
        query ="SELECT SERVICE_LEVEL_ID,INCO_TERMS_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID ='"+routePlan.getPRQId()+"'";
				//Logger.info(FILE_NAME, "Query -> "+query);

				rs = stmt.executeQuery(query);
				while(rs.next())
				{
					routePlan.setServiceLevelId(rs.getString(1));
					//routePlan.setServiceLevelDesc(rs.getString(2));
					routePlan.setDeliveryTerms(rs.getString(2));
					//routePlan.setTotalPieces(rs.getInt(4));
					//routePlan.setWeight(rs.getDouble(5));
				 /* if(documentType.equals("PRQ") || documentType.equals("BOOKING"))
					{
					  routePlan.setPRQStatus(rs.getString(6));
					  routePlan.setWHStatus(rs.getString(7));
					}*/
				
					if(primaryMode == 1)
          {// @@ Replaced by Sreelakshmi KVA for TogetherJ
                       /*query = " SELECT TERMINALTYPE FROM FS_FR_TERMINALMASTER WHERE "
                             + " TERMINALID ='"+routePlan.getOriginTerminalId()+"'";*/ 
							 query1.append(" SELECT TERMINALTYPE FROM FS_FR_TERMINALMASTER WHERE ");
							 query1.append(" TERMINALID ='");
							 query1.append (routePlan.getOriginTerminalId());
							 query1.append ("'"); 
                       rs = null;
                       rs = stmt.executeQuery(query1.toString());
					   //@@
                       /*System.out.println("Query is " + query); */
                       while(rs.next())
                       {
//                         System.out.println("routePlan.setIsNonSystemInBound is " +routePlan.getIsNonSystemInBound());
                         if(rs.getString(1).equals("N"))
                           { routePlan.setIsNonSystemInBound(true); }
                         else
                           { routePlan.setIsNonSystemInBound(false); }
                         //System.out.println("routePlan.setIsNonSystemInBound is " +routePlan.getIsNonSystemInBound());
                       }   
                       
                   }

			  
			  }
			}
		}
		catch(Exception ex)
		{
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [getRoutePlan(documentType, houseDocNo)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [getRoutePlan(documentType, houseDocNo)] -> "+ex.toString());
			throw new EJBException(ex.toString());
      
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return routePlan;
	}

	private String getLocationName(String type, String locationId)
	{
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";
		String locationName = null;

		try
		{
			if(type.equals("Location"))
				{ sql = "SELECT CITY FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID='"+locationId+"'"; }
			else if(type.equals("Terminal"))
				{ sql = "SELECT A.CITY FROM FS_ADDRESS A, FS_FR_TERMINALMASTER T WHERE T.TERMINALID='"+locationId+"' AND T.CONTACTADDRESSID = A.ADDRESSID"; }
			else if(type.equals("Gateway"))
		  {		sql = "SELECT A.CITY FROM FS_ADDRESS A, FS_FR_GATEWAYMASTER G WHERE G.GATEWAYID='"+locationId+"' AND G.CONTACTADDRESSID = A.ADDRESSID"; }

			//Logger.info(FILE_NAME, "SQL -> "+sql);

			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
				{ locationName = rs.getString(1); }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getLocationName(type, terminalId)] -> ", ex);
      logger.error(FILE_NAME+ " [getLocationName(type, terminalId)] -> "+ ex);
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return locationName;
	}

  /**
   * 
   * @param terminalId
   * @return gatewayIds
   */
	public String[][] getGatewayIds(String terminalId)
	{
		String sql = "SELECT GATEWAYID FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID='"+terminalId+"' ORDER BY GATEWAYID";

		Connection	connection	= null;
		Statement	stmt		= null;
		ResultSet	rs			= null;
		String[][] gatewayIds = null;

		try
		{
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			int count = 0;
			while(rs.next())
			{	count++;}
			gatewayIds = new String[count][2];
			count = 0;
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				String gatewayId = rs.getString(1);
				gatewayIds[count][0] = gatewayId;
				gatewayIds[count][1] = getLocationName("Gateway", gatewayId);
				count++;
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getGatewayIds(terminalId)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [getGatewayIds(terminalId)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return gatewayIds;
	}

  /**
   * 
   * @param originTerminal
   * @param destinationTerminal
   * @return terminalIds
   */
	public String[] getTerminalIds(String originTerminal, String destinationTerminal)
	{
		String terminalSQL	= "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='S' AND TERMINALID NOT IN ('"+originTerminal+"', '"+destinationTerminal+"')";

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String[] terminalIds = null;

		try
		{
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(terminalSQL);
			int count = 0;

			while(rs.next())
				{ count++; }

			terminalIds = new String[count];
			count = 0;
			rs = stmt.executeQuery(terminalSQL);
			while(rs.next())
			{
				terminalIds[count] = rs.getString(1);
				count++;
			}
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [getTerminalIds(originTerminalId, DestinationTerminalId)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [getTerminalIds(originTerminalId, DestinationTerminalId)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return terminalIds;
	}

  /**
   * 
   * @param originGateway
   * @param destinationGateway
   * @param searchStr
   * @return gatewayIds
   */
	public String[] getGatewayIds(String originGateway, String destinationGateway, String searchStr)
	{
		/*String gatewaySQL	=	"SELECT DISTINCT GATEWAYID "+
								"FROM FS_FR_TERMINALGATEWAY "+
								"WHERE GATEWAYID LIKE '"+searchStr+"%' AND GATEWAYID NOT IN ('"+originGateway+"', '"+destinationGateway+"')";
		*/ //comment by shivaram on 6th dec 2002 to fix a bug i.e nonsystem gateway ids coming in lov for routeplan
		String gatewaySQL	=	"SELECT DISTINCT G.GATEWAYID "+
						" FROM FS_FR_TERMINALGATEWAY  G,FS_FR_GATEWAYMASTER T "+
						" WHERE G.GATEWAYID LIKE '"+searchStr+"%' AND G.GATEWAYID NOT IN ('"+originGateway+"', '"+destinationGateway+"')"+
						" AND T.GATEWAY_TYPE='S' AND T.GATEWAYID=G.GATEWAYID";

		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		String[] gatewayIds = null;

		try
		{
			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(gatewaySQL);
			int count = 0;

			while(rs.next())
				{ count++; }

			gatewayIds = new String[count];
			count = 0;
			rs = stmt.executeQuery(gatewaySQL);
			while(rs.next())
			{
				gatewayIds[count] = rs.getString(1);
				count++;
			}
		}
		catch(SQLException sqEx)
		{
			//Logger.error(FILE_NAME, " [getGatewayIds(originGateway destinationGateway)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ " [getGatewayIds(originGateway destinationGateway)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return gatewayIds;
	}

  /**
   * 
   * @param shipmentMode
   * @param searchStr
   * @param originTerminalId
   * @param destinationTerminalId
   * @return masterDocNos 
   */
	public String[] getMasterDocumentNos(String shipmentMode, String searchStr, String originTerminalId, String destinationTerminalId)
	{
		Connection connection = null;
		Statement  stmt = null;
		ResultSet  rs = null;

		String[] masterDocNos = null;

		String sql = "";

		try
		{
			if(shipmentMode.equals("Air"))
			{
				sql = "SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE STATUS != 'CLOSED' AND ORIGINGATEWAYID = '"+originTerminalId+"' AND DESTINATIONGATEWAYID='"+destinationTerminalId+"' AND MASTERDOCID LIKE '"+searchStr+"%' AND SHIPMENTMODE = 1";
			}
			else if(shipmentMode.equals("Sea"))
			{
				sql = "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ORIGINGATEWAY='"+originTerminalId+"' AND DESTGATEWAY = '"+destinationTerminalId+"')  AND CONSOLESTATUS != 'CLOSED' AND CONSOLEID LIKE '"+searchStr+"%'";
			}
			else if(shipmentMode.equals("Truck"))
			{
				sql = "SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE STATUS != 'CLOSED' AND ORIGINGATEWAYID = '"+originTerminalId+"' AND DESTINATIONGATEWAYID='"+destinationTerminalId+"' AND MASTERDOCID LIKE '"+searchStr+"%' AND SHIPMENTMODE = 4";
			}

			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);

			int count = 0;
			while(rs.next())
				{ count++; }
			masterDocNos = new String[count];

			count = 0;
			rs = stmt.executeQuery(sql);

			while(rs.next())
			{
				masterDocNos[count] = rs.getString(1);
				count++;
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getMasterDocumentNos(shipmentMode)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [getMasterDocumentNos(shipmentMode)] -> "+ex.toString());
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return masterDocNos;
	}

////////////////////////////////////
  /**
   * 
   * @param shipmentMode
   * @param searchStr
   * @param originTerminalId
   * @param destinationTerminalId
   * @return masterdocIds
   * @throws java.sql.SQLException
   */
	/*public MasterDocument[] getDescriptiveMasterDocumentNos(String shipmentMode, String searchStr, String originTerminalId, String destinationTerminalId) throws	SQLException
	{
		Statement  stmt					 	= null;
		PreparedStatement pstmt				= null;
		ResultSet  rs	  					= null;
		ResultSet  rs1	  					= null;
		Connection connection 				= null;
		MasterDocument masterdocIds[]		= null;
		MasterDocument masterdoc			= null;
		MasterDocumentHeader masterheader	= null;
		MasterDocumentFlightDetails flightdetails	= null;

		String sql = "";
		int len = 0;
		int row = 0;

		try
		{
			if(shipmentMode.equals("Air"))
			{
				sql =	"SELECT "+
							"MASTERDOCID, "+
							"ORIGINGATEWAYID, "+
							"DESTINATIONGATEWAYID, "+
							"CARRIERID, "+
							"CHARGEABLEWEIGHT, "+
							"MAWB_BLCKD_WT "+
						"FROM "+
							"FS_FR_MASTERDOCHDR "+
						"WHERE "+
							"STATUS NOT IN ('CLOSED','COB') "+
							"AND ORIGINGATEWAYID = '"+originTerminalId+"' "+
							"AND DESTINATIONGATEWAYID='"+destinationTerminalId+"' "+
							"AND MASTERDOCID LIKE '"+searchStr+"%' "+
							" AND MASTERTYPE IS NULL AND SUB_AGENT_ID IS NULL "+
							"AND SHIPMENTMODE = 1";
			}
			else if(shipmentMode.equals("Truck"))
			{
				sql	=	"SELECT "+
							"MASTERDOCID, "+
							"ORIGINGATEWAYID, "+
							"DESTINATIONGATEWAYID, "+
							"CARRIERID, "+
							"CHARGEABLEWEIGHT, "+
							"MAWB_BLCKD_WT "+
						"FROM "+
							"FS_FR_MASTERDOCHDR "+
						"WHERE "+
							"STATUS != 'CLOSED' "+
							"AND ORIGINGATEWAYID = '"+originTerminalId+"' "+
							"AND DESTINATIONGATEWAYID='"+destinationTerminalId+"' "+
							"AND MASTERDOCID LIKE '"+searchStr+"%' "+
							"AND SHIPMENTMODE = 4";
			}

			connection = this.getConnection();
			stmt = connection.createStatement();
			
			Logger.info(FILE_NAME,"Query for getDescriptiveMasterDocumentNos \n",sql);
			rs	= stmt.executeQuery(sql);

			while(rs.next())
			   { len++; }

			if(len == 0)
				{ return null; }

			masterdocIds = new  MasterDocument[len];
			rs = 	stmt.executeQuery( sql );
			while(rs.next())
			{
				
				masterdoc	=new  MasterDocument();
				masterheader= new MasterDocumentHeader();
				flightdetails= new MasterDocumentFlightDetails();
				String masterdocId=rs.getString("MASTERDOCID");
				masterheader.setMasterDocId(rs.getString("MASTERDOCID"));
				masterheader.setOriginGatewayId(rs.getString("ORIGINGATEWAYID"));
				masterheader.setDestinationGatewayId(rs.getString("DESTINATIONGATEWAYID"));
				masterheader.setCarrierId(rs.getString("CARRIERID"));
				//@@ Avinash replaced on 20041201
				//masterheader.setChargeableWeight(rs.getInt("CHARGEABLEWEIGHT"));
				masterheader.setChargeableWeight(rs.getDouble("CHARGEABLEWEIGHT"));
				//@@ 20041201
				masterheader.setBlockedSpace(rs.getInt("MAWB_BLCKD_WT"));
				//masterdoc.setMasterDocHeader(masterheader);

				String sqlFrom	=	"SELECT "+
										"MASTERDOCID, "+
										"SLNO, "+
										"FLIGHTFROM, "+
										"ETD "+
									"FROM "+
										"FS_FR_MASTERFLIGHTDTL "+
									"WHERE "+
										"MASTERDOCID=? "+
										"AND SLNO=1";
				String sqlTo	=	"SELECT "+
										"MASTERDOCID, "+
										"SLNO, "+
										"FLIGHTTO, "+
										"ETA "+
									"FROM "+
										"FS_FR_MASTERFLIGHTDTL "+
									"WHERE "+
										"MASTERDOCID=? "+
										"AND SLNO=(SELECT MAX(SLNO) FROM  FS_FR_MASTERFLIGHTDTL  WHERE MASTERDOCID=?)";

				pstmt = connection.prepareStatement(sqlFrom);
				pstmt.setString(1,masterdocId);
				rs1 = pstmt.executeQuery();

				String[]  flightFrom	=  new String[1]; 
				Timestamp[]  etd			=  new Timestamp[1]; 
				String[]  flightTo		=  new String[1]; 
				Timestamp[]  eta			=  new Timestamp[1]; 
				if(rs1.next())
				{
					flightFrom[0]=rs1.getString("FLIGHTFROM");
					etd[0]=rs1.getTimestamp("ETD");
				}
				pstmt=null;
				pstmt = connection.prepareStatement(sqlTo);
				pstmt.setString(1,masterdocId);
				pstmt.setString(2,masterdocId);
				rs1 = pstmt.executeQuery();
				if(rs1.next())
				{
					flightTo[0]=rs1.getString("FLIGHTTO");
					eta[0]=rs1.getTimestamp("ETA");
				}
				flightdetails.setFlightFrom(flightFrom);
				flightdetails.setEtd(etd);
				flightdetails.setFlightTo(flightTo);
				flightdetails.setEta(eta); 
				
				masterdoc.setMasterDocHeader(masterheader);
				masterdoc.setMasterDocFlightDetails(flightdetails);
				masterdocIds[row]	= masterdoc;
				row++;
			}
			return masterdocIds;
		}
		catch(SQLException sqlexp)
		{
			sqlexp.printStackTrace();
			Logger.error(FILE_NAME,"getDescriptiveMasterDocumentNos() ",sqlexp);
			throw new SQLException();
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
			//Connection Leakage ------------------ 7-DEC-2004 -------------------- Santhosam.P
			try
			{
				if( pstmt != null )
    			{	pstmt.close(); }
				if( rs1 != null )
    				{ rs1.close(); }
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}
		
		}
	}*/
  //End of getDescriptiveMasterDocumentNos description method

  /**
   * 
   * @param shipmentMode
   * @param searchStr
   * @param originTerminalId
   * @param destinationTerminalId
   * @return consoleIds
   * @throws java.sql.SQLException
   */
	public ArrayList getDescriptiveConsoleIds(String shipmentMode, String searchStr, String originTerminalId, String destinationTerminalId) throws	SQLException
	{
		Statement  stmt					 	= null;
		ResultSet  rs	  					= null;
		Connection connection 				= null;

		String sql = "";
		ArrayList consoleIds = null;

		//sql = "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ORIGINGATEWAY='"+originTerminalId+"' AND DESTGATEWAY = '"+destinationTerminalId+"')  AND CONSOLESTATUS != 'CLOSED' AND CONSOLEID LIKE '"+searchStr+"%'";
		sql =	"SELECT "+
					"A.CONSOLEID, "+
					"B.ORIGINGATEWAY, "+
					"B.DESTGATEWAY, "+
					"B.PORTOFLOADING, "+
					"B.PORTOFDISCHARGE, "+
					"A.CUTOFFDATE, "+
					"D.ETA, "+
					"D.ETD, "+
					"A.ROUTEID "+
				"FROM "+
					"FS_FRS_CONSOLEMASTER A, "+
					"FS_FRS_ROUTEMASTER B, "+
					"FS_FRS_CONSOLEVESSELDTL D "+
				"WHERE "+
					"B.ORIGINGATEWAY='"+originTerminalId+"' "+
					" AND A.CONSOLTYPE NOT IN ('FCL_TO_FCL','FCL_BACK_TO_BACK','BREAK_BULK')"+
					"AND B.DESTGATEWAY = '"+destinationTerminalId+"' "+
					"AND A.ROUTEID = B.ROUTEID "+
					"AND CONSOLESTATUS != 'CLOSED' "+
					"AND A.CONSOLEID = D.CONSOLEID "+
					"AND A.CUTOFFDATE > SYSDATE-1 "+
					"AND (A.DIRECTCONSOLE IS NULL OR A.DIRECTCONSOLE='N') "+
					"AND A.CONSOLEID LIKE '"+searchStr+"%' "+
				"ORDER BY A.CONSOLEID "; 

		try
		{
			connection = this.getConnection();
			stmt = connection.createStatement();
			
			//Logger.info(FILE_NAME,"Query for getDescriptiveConsoleIds \n",sql);
			rs	= stmt.executeQuery(sql);

			rs = 	stmt.executeQuery(sql);
			
			consoleIds = new ArrayList();
			while(rs.next())
			{
				String[] val = new String[9];

				val[0] = rs.getString(1);
				val[1] = rs.getString(2);
				val[2] = rs.getString(3);
				val[3] = rs.getString(4);
				val[4] = rs.getString(5);
				val[5] = rs.getString(6);
				val[6] = rs.getString(7);
				val[7] = rs.getString(8);
				val[8] = rs.getString(9);

				consoleIds.add(val);
			}
		}
		catch(SQLException sqlexp)
		{
			sqlexp.printStackTrace();
			//Logger.error(FILE_NAME,"getDescriptiveConsoleIds() ",sqlexp);
      logger.error(FILE_NAME+"getDescriptiveConsoleIds() "+sqlexp);
			throw new SQLException();
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return consoleIds;
	}//End of getDescriptiveConsoleIds description method


////////////////////////////////////

	
  /**
   * 
   * @param documentType
   * @param documentNo
   * @return flag
   */
	public boolean removeRoutePlan(String documentType, String documentNo)
	{
		boolean flag = false;

		try
		{
			ETMultiModeRoutePlanDAO	routePlanDAO = new ETMultiModeRoutePlanDAO();
			long routePlanId = getRoutePlanId(documentType, documentNo);
			if(routePlanId == 0)
				{ throw new ObjectNotFoundException("Could Not Find the Document."); }

			ETMultiModeRoutePlanHdrDOB routePlanHdr = routePlanDAO.load(routePlanId);

			if(documentType.equals("PRQ"))
			{
				if(routePlanHdr.getHouseDocumentId() == null || routePlanHdr.getHouseDocumentId().equals(""))
				{
					routePlanDAO.remove(routePlanId);
				}
				else
				{
					routePlanHdr.setPRQId(null);
					routePlanDAO.store(routePlanHdr);
				}
			}
			else if(documentType.equals("HBL"))
			{
				if(routePlanHdr.getPRQId() == null || routePlanHdr.getPRQId().equals(""))
				{
					routePlanDAO.remove(routePlanId);
				}
				else
				{
					routePlanHdr.setHouseDocumentId(null);
					routePlanDAO.store(routePlanHdr);
				}
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [removeRoutePlan(documentType, documentNo)] "+ex.toString());
      logger.error(FILE_NAME+ " [removeRoutePlan(documentType, documentNo)] "+ex.toString());
			throw new EJBException(ex.toString());
		}
		return flag;
	}

	private long getRoutePlanId(String documentType, String documentId)
	{
		long routePlanId = 0L;

		Connection	connection	= null;
		Statement	stmt		= null;
		ResultSet	rs			= null;
		String sql = "";

		try
		{
			if(documentType.equals("PRQ"))
			{
				sql = "SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE PRQ_ID = '"+documentId+"'";
			}
			else if(documentType.equals("HBL") || documentType.equals("HAWB"))
			{
				sql = "SELECT RT_PLAN_ID FROM FS_RT_PLAN WHERE HAWB_ID  = '"+documentId+"'";
			}

			connection = this.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next())
				{ routePlanId = rs.getLong(1); }
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getRoutePlanId(documentType, documentId, primaryMode)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [getRoutePlanId(documentType, documentId, primaryMode)] -> "+ex.toString());
			ex.printStackTrace();
			throw new EJBException(ex.toString());
		}
		finally
		{
			ConnectionUtil.closeConnection(connection, stmt, rs);
		}
		return routePlanId;
	}


  /**
   * 
   * @throws javax.ejb.CreateException
   */
	public void ejbCreate()
		throws CreateException
	{
	}
	
	//Implementing SessionBeanInterface methods.....//
  /**
   * 
   * @param sessionCtx
   * @throws javax.ejb.EJBException
   */
	public void setSessionContext(SessionContext sessionCtx)
		throws javax.ejb.EJBException
	{
		this.sessionContext = sessionCtx;
	}
	
  /**
   * 
   * @throws javax.ejb.EJBException
   */
	public void ejbRemove()
		throws javax.ejb.EJBException
	{
	}
	
  /**
   * 
   * @throws javax.ejb.EJBException
   */
	public void ejbActivate()
		throws javax.ejb.EJBException
	{
	}
	
  /**
   * 
   * @throws javax.ejb.EJBException
   */
	public void ejbPassivate()
		throws javax.ejb.EJBException
	{
	}
	
	//End of Implementation of SessionBeanInterface methods.

	//Serialization of BeanObject is to be done here.....//
  /**
   * 
   * @param out
   * @throws java.io.IOException
   */
   //@@ Commented by Sreelakshmi KVA -TogetherJ-UPCM
   /*
	private void writeObject(java.io.ObjectOutputStream out)
		throws java.io.IOException
	{
		//write non-serializable attributes here

		out.defaultWriteObject();
	}*/

  /**
   * 
   * @param in
   * @throws java.io.IOException
   * @throws java.lang.ClassNotFoundException
   */
   /*
	private void readObject(java.io.ObjectInputStream in)
		throws java.io.IOException, ClassNotFoundException
	{
		//read non-serializable attributes here

		in.defaultReadObject();
	}*/
	// serialization done //

	private java.sql.Connection getConnection() throws java.sql.SQLException
	{
		try
		{
			if(dataSource == null)
			{
				ictx = new InitialContext();
				dataSource = (DataSource) ictx.lookup("java:comp/env/jdbc/DB");
			}
		}catch(Exception ex)
		{
			throw new javax.ejb.EJBException(ex);
		}
		return dataSource.getConnection();
	}
}

