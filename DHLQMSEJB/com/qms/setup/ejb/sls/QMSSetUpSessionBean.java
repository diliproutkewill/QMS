/*
	Program Name	:QMSSetUpSessionBean.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode SessionBean
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:sasa
*/


package com.qms.setup.ejb.sls;

import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;
import com.qms.setup.dao.QMSDensityGroupCodeDAO;
import com.qms.setup.dao.ZoneCodeMasterDAO;
import com.qms.setup.ejb.bmp.SalesPersonRegistrationLocal;
import com.qms.setup.ejb.bmp.SalesPersonRegistrationLocalHome;
import com.qms.setup.ejb.bmp.SalesPersonRegistrationPK;
import com.qms.setup.ejb.bmp.ZoneCodeMasterLocal;
import com.qms.setup.ejb.bmp.ZoneCodeMasterLocalHome;
import com.qms.setup.ejb.bmp.ZoneCodeMasterPK;
import com.qms.setup.ejb.cmp.DesignationLocal;
import com.qms.setup.ejb.cmp.DesignationLocalHome;
import com.qms.setup.ejb.cmp.DesignationPK;
import com.qms.setup.java.DensityGroupCodeDOB;
import com.qms.setup.java.DesignationDOB;
import com.qms.setup.java.SalesPersonRegistrationDOB;
import com.qms.setup.java.ZoneCodeChildDOB;
import com.qms.setup.java.ZoneCodeMasterDOB;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import com.qms.setup.ejb.cmp.DensityGroupCodeLocalHome;
import com.qms.setup.ejb.cmp.DensityGroupCodeLocal;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.setup.ejb.cmp.DensityGroupCodePK;
import com.foursoft.esupply.common.java.LookUpBean;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;

public class QMSSetUpSessionBean implements SessionBean 
{
    public  static final String FILE_NAME       =   "QMSSetUpSessionBean.java";
    private SessionContext     sessionContext   =   null;
    private InitialContext     ictxt            =   null;    
    private	OperationsImpl	   operationsImpl   =   null;
    private Connection         connection       =   null;
    private LookUpBean         lookUpBean       =   null;
    private DataSource         dataSource       =   null;
    private static Logger logger = null;
    
    public QMSSetUpSessionBean()
    {
        logger  = Logger.getLogger(QMSSetUpSessionBean.class);
    }
    
    public void ejbCreate()
    {
         operationsImpl	= new OperationsImpl();
		     operationsImpl.createDataSource();
    }

    public void ejbActivate()
    {
    }

    public void ejbPassivate()
    {
    }

    public void ejbRemove()
    {
    }

    public void setSessionContext(SessionContext ctx)
    {
        this.sessionContext      =     ctx;
    }    
    
    //Serialization of BeanObject is to be done here.....//
    private void writeObject(java.io.ObjectOutputStream out)
      throws java.io.IOException
    {
      //write non-serializable attributes here
  
      out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, ClassNotFoundException
    {
      //read non-serializable attributes here
  
      in.defaultReadObject();
    }
    
    /**
     * 
     * @param dgcCode
     * @param uom
     * @param perKg     
     * @return DensityGroupCodeDOB
     */
    public double getDensityGroupCodeDetails(int dgcCode,double perKg)
    {         
         /*DensityGroupCodeDOB          dgcDOB         =             new DensityGroupCodeDOB();
         DensityGroupCodePK           pk             =             new DensityGroupCodePK();
         DensityGroupCodeLocalHome    home           =             null;
         DensityGroupCodeLocal        remote         =             null;*/
         QMSDensityGroupCodeDAO       dao     =        new QMSDensityGroupCodeDAO();
         double perLB   = 0.0;
      try
      {
         
       /*  pk.dgcCode          =          dgcCode;
         pk.perKG            =          perKg;
         pk.uom              =          uom;
         ictxt               =          new InitialContext();         
         home                =          (DensityGroupCodeLocalHome)ictxt.lookup("java:comp/env/DensityGroupCodeBean");
         remote              =          (DensityGroupCodeLocal)home.findByPrimaryKey(pk);                
         dgcDOB.setDGCCode(remote.getDgcCode());
         dgcDOB.setPerKG(remote.getPerKG());
         dgcDOB.setPerLB(remote.getPerLB());
         dgcDOB.setUOM(remote.getUom());      */
         perLB  =  dao.selectDensityGroupCodeDetails(dgcCode,perKg);
         
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());   
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());   
         throw new EJBException("Error while Getting DensityGroupCode Details ");
      }
      return perLB;
    }
    
    /**
     * 
     * @param        
     * @return 
     */
    public ArrayList getDensityGroupCodeDetails()
    {                 
         DensityGroupCodeLocalHome    home            =                  null;
         DensityGroupCodeLocal        remote          =                  null;         
         ArrayList                    dgcList         =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
         ResultSet                    resultSet       =                  null;
         String                       selectQuery     =                  null; 
         DensityGroupCodeDOB          dgcDOB          =                  null;
         
      try
      {              
         dgcList         =    new ArrayList(5);
         connection      =    operationsImpl.getConnection();
         selectQuery     =    "SELECT DGCCODE,KG_PER_M3,LB_PER_F3,UOM,INVALIDATE FROM QMS_DENSITY_GROUP_CODE ORDER BY DGCCODE";         
         pStmt           =    connection.prepareStatement(selectQuery);
                 
         resultSet        =    pStmt.executeQuery(); 
        
          while(resultSet.next())
          {
            dgcDOB      =    new DensityGroupCodeDOB();
            dgcDOB.setDGCCode(resultSet.getInt(1));
            dgcDOB.setPerKG(resultSet.getDouble(2));
            dgcDOB.setPerLB(resultSet.getDouble(3));
            dgcDOB.setUOM(resultSet.getString(4));
            dgcDOB.setInvalidate(resultSet.getString(5));
            dgcList.add(dgcDOB);
          } 
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());    
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());    
         return null;
      }    
      finally
      {
        dgcDOB    =    null;
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
      }
      return dgcList;
    }
    
    /**
     * 
     * @param     dgcDOB   
     * @return    boolean
     */
    public boolean insertDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) 
    {
         /* DensityGroupCodeLocalHome    home    =        null;
          DensityGroupCodeLocal        remote  =        null;*/
          QMSDensityGroupCodeDAO       dao     =        new QMSDensityGroupCodeDAO();
          boolean   flag    =false;
     try
      {
      
         /*ictxt                                  =          new InitialContext();         
         home      =          (DensityGroupCodeLocalHome)ictxt.lookup("java:comp/env/DensityGroupCodeBean");
         Logger.info(FILE_NAME,"home  "+home);
         remote    =          (DensityGroupCodeLocal)home.create(dgcDOB.getDGCCode(),dgcDOB.getPerKG(),dgcDOB.getPerLB(),dgcDOB.getUOM(),dgcDOB.getInvalidate());         
         Logger.info(FILE_NAME,"remote  "+remote);*/
         if(dao.selectDensityGroupCodeDetails(dgcDOB.getDGCCode(),dgcDOB.getPerKG())<=0.0)
            flag = dao.insertDensityGroupCodeDetails(dgcDOB);           
         
       }
       catch(Exception e)
       {
          e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in insertDensityGroupCode module",e.toString());         
         logger.error(FILE_NAME+"Error in insertDensityGroupCode module"+e.toString());         
         throw new EJBException("Error in inserting DensityGroupCode ");
       }
       finally
       {
         /*home    =    null;
         remote  =    null;
         ictxt   =    null;*/
         dao   =  null;
       }
       return flag;            
    }
    
    /**
     * 
     * @param     dgcDOB   
     * @return    boolean
     */
    public boolean updateDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) 
    {
        /* DensityGroupCodePK           pk              =                 new DensityGroupCodePK();
         DensityGroupCodeLocalHome    home            =                  null;
         DensityGroupCodeLocal        remote          =                  null;*/
         
         QMSDensityGroupCodeDAO       dao     =        new QMSDensityGroupCodeDAO();
         boolean  flag  = false;
      try
      {
         
       /*  pk.dgcCode                         =           dgcDOB.getDGCCode();
         pk.perKG                           =           dgcDOB.getPerKG();
         pk.uom                             =           dgcDOB.getUOM();         
         ictxt                              =          new InitialContext();         
         home                               =          (DensityGroupCodeLocalHome)ictxt.lookup("java:comp/env/DensityGroupCodeBean");
         remote                             =          (DensityGroupCodeLocal)home.findByPrimaryKey(pk);                      
         
         remote.setPerLB(dgcDOB.getPerLB());         */
         flag  = dao.updateDensityGroupCodeDetails(dgcDOB);
         
      }
      catch(Exception e)
      {
        /* pk            =      null;
         ictxt         =      null;
         home          =      null;
         remote        =      null;
         Logger.error(FILE_NAME,"Error in updateDensityGroupCodeDetails module",e.toString());         
         return false;*/
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in updateDensityGroupCodeDetails module",e.toString());         
         logger.error(FILE_NAME+"Error in updateDensityGroupCodeDetails module"+e.toString());         
         throw new EJBException("Error while modifieng Details");
      }      
      return flag;
    }
    
    /**
     * 
     * @param     dgcList   
     * @return    boolean
     */
    public boolean invalidateDensityGroupCodeDetails(ArrayList dgcList) 
    {
        /* DensityGroupCodePK             pk              =          new DensityGroupCodePK();
         DensityGroupCodeLocalHome      home            =                  null;
         DensityGroupCodeLocal          remote          =                  null;         
         DensityGroupCodeDOB            dgcDOB          =                  null;*/
         QMSDensityGroupCodeDAO       dao     =        new QMSDensityGroupCodeDAO();
         boolean flag  = false;
      try
      {
      
      /*   ictxt                              =          new InitialContext();         
         home                               =          (DensityGroupCodeLocalHome)ictxt.lookup("java:comp/env/DensityGroupCodeBean");
         
         for(int i=0;i<dgcList.size();i++)
         {
           dgcDOB            =   new DensityGroupCodeDOB();
           dgcDOB            =   (DensityGroupCodeDOB)dgcList.get(i);
           pk.dgcCode        =   dgcDOB.getDGCCode();
           pk.perKG          =   dgcDOB.getPerKG();
           pk.uom            =   dgcDOB.getUOM();    
           remote            =   (DensityGroupCodeLocal)home.findByPrimaryKey(pk);
           remote.setInvaliDate(dgcDOB.getInvalidate());  
           remote = null;
         }        
        */ 
         flag    =  dao.invalidateDensityGroupCodeDetails(dgcList);
      }
      catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Error in invalidateDensityGroupCodeDetails module",e.toString());         
         logger.error(FILE_NAME+"Error in invalidateDensityGroupCodeDetails module"+e.toString());         
        throw new EJBException("Error while Invalidating DensityGroupCode Details ");
      }      
      finally
      {
      /*  ictxt           =         null;
        home            =         null;
        remote          =         null;
        dgcDOB          =         null;*/
        dao = null;
      }
      return flag;
    }
    
    /**
     * 
     * @param     dgcDOB   
     * @return    boolean
     */
    public boolean deleteDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) 
    {
         
        /* DensityGroupCodeLocalHome    home  =           null;
         DensityGroupCodePK           pk    =           null;
         DensityGroupCodeLocal        remote=           null;*/
         QMSDensityGroupCodeDAO       dao     =        new QMSDensityGroupCodeDAO();
         boolean flag  = false;
      try
      {              
      
        /* pk                                  =          new DensityGroupCodePK();
         pk.dgcCode                          =          dgcDOB.getDGCCode();
         pk.perKG                            =          dgcDOB.getPerKG();
         pk.uom                              =          dgcDOB.getUOM();
         ictxt                               =          new InitialContext();         
         home                                =          (DensityGroupCodeLocalHome)ictxt.lookup("java:comp/env/DensityGroupCodeBean");
         remote                              =          (DensityGroupCodeLocal)home.findByPrimaryKey(pk);   
                
         remote.remove();*/
         flag  =  dao.deleteDensityGroupCodeDetails(dgcDOB.getDGCCode(),dgcDOB.getPerKG());
       }
       catch(Exception e)
       {
         //Logger.error(FILE_NAME,"Error in deleteDensityGroupCodeDetails module",e.toString());         
         logger.error(FILE_NAME+"Error in deleteDensityGroupCodeDetails module"+e.toString());         
         return false;
       }
        finally
        {
        /*  ictxt           =         null;
          home            =         null;
          remote          =         null;
          pk              =         null;*/
        }
      return flag;
    }
    
    /**
     * 
     * @param     dgcCode   
     * @param     uom   
     * @param     perKG   
     * @return    ArrayList
     */
    public ArrayList getPerKGIds(int dgcCode,String operation,String perKG,String terminalId)
    {
         
         //PreparedStatement        pStmt          =             null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
         ResultSet                resultSet      =             null;
         String                   selectQuery    =             null; 
         ArrayList                dgcList        =             null;         
         String                   whereClause    =             null;
         CallableStatement        cstmt          =             null;
      try
      {  
      
         if(perKG.length()>0)
           whereClause =   "AND KG_PER_M3='"+Double.parseDouble(perKG)+"'";
         else
           whereClause =   "";
           
         dgcList         =    new ArrayList();
         connection      =    operationsImpl.getConnection();
         //selectQuery     =    "SELECT KG_PER_M3 FROM QMS_DENSITY_GROUP_CODE  WHERE DGCCODE =? AND UOM=? AND INVALIDATE='F' "+whereClause;
         
         //pStmt           =    connection.prepareStatement(selectQuery);
         //pStmt.setInt(1,dgcCode);
         //pStmt.setString(2,uom);        
         //resultSet          =    pStmt.executeQuery();         
		 String dgcCode1 = dgcCode+"";
         //  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_DENSITYGROUPCODE(?,?,?,?)}");
         //@@Commented and Modified by Kameswari for LOV issue
          cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_DENSITYGROUPCODE(?,?,?,?,?)}");
          cstmt.registerOutParameter(1,OracleTypes.CURSOR);
          cstmt.setString(2,terminalId);
          cstmt.setString(3,dgcCode1.trim());
          cstmt.setString(4,("F"));  
           cstmt.setString(5,perKG.trim());//@@Added by Kameswari for LOV issue
          cstmt.setString(6,operation);
          cstmt.execute();
		  resultSet  =  (ResultSet)cstmt.getObject(1);
         while(resultSet.next())
         {           
           dgcList.add(resultSet.getString("KG_PER_M3"));
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getPerKGIds module",e.toString());  
         logger.error(FILE_NAME+"Error in getPerKGIds module"+e.toString());  
         return null;
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,null,resultSet);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
       
      }return dgcList;
    }
    
     public String designationRegistration(DesignationDOB desiDob,String invalidate)
	{
		String message=null;
		try
		{
		 // LookUpBean lookup=new LookUpBean();
		  
		  DesignationLocalHome designationLocalHome=(DesignationLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/DesignationBean");
		  DesignationLocal designationLocal=designationLocalHome.create(desiDob.getDesignationId(),desiDob.getDescription(),desiDob.getLevelNo(),invalidate);
		  
		  message="success";
		  return message;
    
		}catch(Exception e)
		{
		    e.printStackTrace();
		    message="failure";
		    return message;
		}
    }
	public ArrayList getDesignationIs(String searchString)
	{
		Connection	connection	= null;
		Statement	desiStmt	=null;
		ResultSet	desiResutltSet=null;
		ArrayList	designationIds= new ArrayList();

  
		if(searchString!=null && !(searchString.equals("")))
		{
			searchString = " WHERE DESIGNATION_ID LIKE '"+searchString+"%'";
		}
		else
		{
			searchString = "";
		}
 		String sql = "SELECT DESIGNATION_ID  FROM QMS_DESIGNATION "+searchString+" ORDER BY DESIGNATION_ID";
    
	   // Logger.info(FILE_NAME,"  getDesignationIs(String searchString)"+sql);
	    try
        {
			connection = this.getConnection();
            desiStmt = connection.createStatement();
            desiResutltSet = desiStmt.executeQuery(sql);
            while(desiResutltSet.next())
			{
				designationIds.add(desiResutltSet.getString(1));
			}
        }
        catch(SQLException sqEx)
        {
			//Logger.error(FILE_NAME, "[getDesignationIs(whereclause)] -> "+sqEx.toString());
      logger.error(FILE_NAME+ "[getDesignationIs(whereclause)] -> "+sqEx.toString());
            throw new EJBException(sqEx.toString());
        }
        finally
        {
			ConnectionUtil.closeConnection(connection, desiStmt, desiResutltSet);
        }
        return designationIds;
	}
	 public DesignationDOB getDesignationDetails(String	designationId) 
	{
			Connection	connection = null;
			Statement stmt	= null;
			ResultSet rs =	null;
			DesignationDOB desiDOB	= null;
			try
			{
				String sqlQuery	=	"SELECT	DESIGNATION_ID,DESCRIPTION,LEVEL_NO FROM QMS_DESIGNATION WHERE DESIGNATION_ID = '"+designationId+"'";
				//Logger.info(FILE_NAME,sqlQuery);
				connection=getConnection();
				stmt = connection.createStatement();
				rs = stmt.executeQuery(	sqlQuery );
				if(	rs.next() )
				{
					desiDOB=new	DesignationDOB();
					desiDOB.setDesignationId(rs.getString("DESIGNATION_ID"));
					desiDOB.setDescription(rs.getString("DESCRIPTION"));
					desiDOB.setLevelNo(rs.getString("LEVEL_NO"));
				}
				else
				return	null;
			}
			catch(	SQLException sqle)
			{
			//Logger.error(FILE_NAME,"SQLException in	getCountryMasterDetails	of ETransHOSuperUserSetupSessionBean" +sqle.toString());
      logger.error(FILE_NAME+"SQLException in	getCountryMasterDetails	of ETransHOSuperUserSetupSessionBean" +sqle.toString());
			}
			finally
			{
					ConnectionUtil.closeConnection(connection,stmt,rs);
			}
		return desiDOB;
	}
	public boolean updateDesignationDetails(DesignationDOB desiDob)
	{
        DesignationPK    pk             =          new DesignationPK();
         DesignationLocalHome    home            =                  null;
         DesignationLocal        remote          =                  null;
         boolean                 flag            =                  false;
         
      try
      {
         
         pk.designationId                         =           desiDob.getDesignationId();
         InitialContext ictxt                              =          new InitialContext();         
         home                               =          (DesignationLocalHome)ictxt.lookup("java:comp/env/DesignationBean");
         remote                             =          (DesignationLocal)home.findByPrimaryKey(pk);   
         //Logger.info(FILE_NAME,"remote"+remote);        
         remote.setDescription(desiDob.getDescription());
         remote.setLevelNo(desiDob.getLevelNo());
         flag=true;
      }
      catch(Exception e)
      {
        flag=false;
         e.printStackTrace();
         
      }
      finally
      {
            
      }
      return flag;
	}
  public boolean deleteDesignation(String designationId)
  {
         DesignationPK    pk             =          new DesignationPK();
         DesignationLocalHome    home            =                  null;
         DesignationLocal        remote          =                  null;
         boolean                 flag            =                  false;
    
    try
    {
        pk.designationId= designationId;
        InitialContext ictxt                              =      new InitialContext();         
         home                               =          (DesignationLocalHome)ictxt.lookup("java:comp/env/DesignationBean");
         remote                             =          (DesignationLocal)home.findByPrimaryKey(pk);
         remote.remove();
         flag=true;
    }catch(Exception e)
    {
      flag=false;
      e.printStackTrace();
    }
        return flag;
  }
  public ArrayList getDesignationDetails()
  {
                  
         
         ArrayList                    dgcList         =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
         ResultSet                    resultSet       =                  null;
         String                       selectQuery     =                  null; 
         DesignationDOB          desiDOB        =                  null;
           try
      {              
         dgcList         =    new ArrayList();
   			connection=getConnection();
	
	       selectQuery     =    "SELECT DESIGNATION_ID,DESCRIPTION,LEVEL_NO,INVALIDATE FROM QMS_DESIGNATION ORDER BY LEVEL_NO,DESIGNATION_ID";         
         pStmt           =    connection.prepareStatement(selectQuery);
         resultSet       =    pStmt.executeQuery();         
          while(resultSet.next())
          {
            desiDOB      =    new DesignationDOB();
            desiDOB.setDesignationId(resultSet.getString(1));
            desiDOB.setDescription(resultSet.getString(2));
            desiDOB.setLevelNo(resultSet.getString(3));
            desiDOB.setInvalidate(resultSet.getString(4));
            dgcList.add(desiDOB);
            
          } 
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());         
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());         
      }    
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
      }
      return dgcList;
    }
    
  public boolean invalidateDesignation(ArrayList dobModList)
  {
    
         DesignationPK    pk             =          new DesignationPK();
         DesignationLocalHome    home            =                  null;
         DesignationLocal        remote          =                  null;
         boolean                 flag            =                  false;
    
    
    try
    {
    	int dobModListSize	=	dobModList.size();
    		for(int i=0;i<dobModListSize;++i)
		    {
		    	DesignationDOB desiDOB=new DesignationDOB();
		    	desiDOB=(DesignationDOB)dobModList.get(i);
		        pk.designationId= desiDOB.getDesignationId();
		        InitialContext ictxt                              =      new InitialContext();         
		         home                               =          (DesignationLocalHome)ictxt.lookup("java:comp/env/DesignationBean");
		         remote                             =          (DesignationLocal)home.findByPrimaryKey(pk);
		         remote.setInvalidate(desiDOB.getInvalidate());
		      }
		      flag=true;
		    }catch(Exception e)
		    {
		      flag=false;
		      e.printStackTrace();
		    }
    
		        return flag;
  }
  
	private Connection  getConnection() throws EJBException
	{
		Connection con = null;
 
		try
		{
			if(dataSource == null)
				{createDataSource();}
				 
			con	= dataSource.getConnection();
		}
		catch( Exception ex)
		{
		   throw new EJBException( "getConnection: "+ex.toString() );
		}
		return con;
	}
  private void	createDataSource() throws EJBException
	{
    InitialContext  initialContext  =   null;
		try
		{
			initialContext = new InitialContext();
			dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
		}
		catch( Exception e )
		{
			throw new EJBException( "createDataSource:: "+e.toString() );
		}
	}	
    
    
    public SalesPersonRegistrationDOB getSalePersonDetails(String salesPersonCode)
    {         
         SalesPersonRegistrationDOB          sprDOB         =             new SalesPersonRegistrationDOB();
         SalesPersonRegistrationPK           pk             =             new SalesPersonRegistrationPK();
         SalesPersonRegistrationLocalHome    home           =             null;
         SalesPersonRegistrationLocal        remote         =             null;
         
      try
      {     
         pk.salesPersonCode  =          salesPersonCode;
         ictxt               =          new InitialContext();         
         home               =          (SalesPersonRegistrationLocalHome)lookUpBean.getEJBLocalHome("java:comp/env/SalesPersonRegistrationBean");
         remote              =          (SalesPersonRegistrationLocal)home.findByPrimaryKey(pk);   
         sprDOB              =          remote.getSalesPersonRegistrationDOB();
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getDensityGroupCodeDetails module",e.toString());  
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());  
         return null;
      }
      return sprDOB;
    }
    
    /**
     * 
     * @param        
     * @return 
     */
    public ArrayList getSalesPersonDetails()
    {                 
         ArrayList                    dgcList         =                  null;
         Connection                   connection      =                  null;
         PreparedStatement            pStmt           =                  null;
         ResultSet                    resultSet       =                  null;
         String                       selectQuery     =                  null; 
         SalesPersonRegistrationDOB     sprDOB        =                  null;
         
      try
      {              
         dgcList         =    new ArrayList(5);
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         selectQuery     =    "SELECT LOCATION_ID ,TERMINAL_ID ,SALESPERSON_CODE ,SALESPERSON_NAME ,DESIGNATION,LEVELID  ,TO_CHAR(DOJ,'DD/MM/YY') ,S.ADDRESSID ,REP_OFFICERS_CODE ,ALLOTED_TIME,REMARKS ,REP_OFFICERS_NAME ,REP_OFFICERS_LEVEL,REP_OFFICERS_DESIGNATION,INVALIDATE ,ADDRESSLINE1  ,ADDRESSLINE2  ,CITY  ,STATE ,ZIPCODE   ,COUNTRYID  ,PHONENO  ,EMAILID  ,FAX ,MOBILENO,INVALIDATE  FROM QMS_SALESPERSON_REG S,FS_ADDRESS A WHERE S.ADDRESSID =A.ADDRESSID ";         
         pStmt           =    connection.prepareStatement(selectQuery);
         resultSet       =    pStmt.executeQuery(); 
         
        
          while(resultSet.next())
          { 
           sprDOB           =      new SalesPersonRegistrationDOB();
           sprDOB.setLocationId(resultSet.getString(1)!=null?resultSet.getString(1):"");
           sprDOB.setTerminalId(resultSet.getString(2)!=null?resultSet.getString(2):"");
           sprDOB.setSalesPersonCode(resultSet.getString(3)!=null?resultSet.getString(3):"");
           sprDOB.setSalesPersonName(resultSet.getString(4)!=null?resultSet.getString(4):"");
           sprDOB.setDesignation(resultSet.getString(5)!=null?resultSet.getString(5):"");
           sprDOB.setLevel(resultSet.getString(6)!=null?resultSet.getString(6):"");
           sprDOB.setDateOfJoining(resultSet.getString(7)!=null?resultSet.getString(7):"");
           sprDOB.setAddressId(resultSet.getString(8)!=null?resultSet.getString(8):"");
           sprDOB.setRepOffCode(resultSet.getString(9)!=null?resultSet.getString(9):"");
           sprDOB.setTime(resultSet.getString(10)!=null?resultSet.getString(10):"");
           sprDOB.setRemarks(resultSet.getString(11)!=null?resultSet.getString(11):"");
           sprDOB.setRepOffName(resultSet.getString(12)!=null?resultSet.getString(12):"");
           sprDOB.setSuperLevel(resultSet.getString(13)!=null?resultSet.getString(13):"");
           sprDOB.setSuperDesignationId(resultSet.getString(14)!=null?resultSet.getString(14):"");
           sprDOB.setInvalidate(resultSet.getString(15)!=null?resultSet.getString(15):"");
           sprDOB.setAddressLine1(resultSet.getString(16)!=null?resultSet.getString(16):"");
           sprDOB.setAddressLine2(resultSet.getString(17)!=null?resultSet.getString(17):"");
           sprDOB.setCity(resultSet.getString(18)!=null?resultSet.getString(18):"");
           sprDOB.setState(resultSet.getString(19)!=null?resultSet.getString(19):"");
           sprDOB.setZipCode(resultSet.getString(20)!=null?resultSet.getString(20):"");
           sprDOB.setCountryId(resultSet.getString(21)!=null?resultSet.getString(21):"");
           sprDOB.setPhoneNo(resultSet.getString(22)!=null?resultSet.getString(22):"");
           sprDOB.setEmailid(resultSet.getString(23)!=null?resultSet.getString(23):"");
           sprDOB.setFax(resultSet.getString(24)!=null?resultSet.getString(24):"");
           sprDOB.setMobilePhoneNo(resultSet.getString(25)!=null?resultSet.getString(25):"");  
           sprDOB.setInvalidate(resultSet.getString(26)!=null?resultSet.getString(26):"");  
           dgcList.add(sprDOB);
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());         
         logger.error(FILE_NAME+"Error in getDensityGroupCodeDetails module"+e.toString());         
         return null;
      }    
      finally
      {
        sprDOB    =    null;
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
      }
      return dgcList;
    }
    
    /**
     * 
     * @param     sprDOB   
     * @return    boolean
     */
    public String insertSalesPersonDetails(SalesPersonRegistrationDOB sprDOB) 
    {
          SalesPersonRegistrationLocalHome    home    =        null;
          SalesPersonRegistrationLocal        remote  =        null;
          OIDSession               oidRemote   =     null;
          OIDSessionHome           oidHome     =     null;
          Connection               connection  =     null;
          PreparedStatement        pStmt       =     null;
          ResultSet                rs          =     null;
          String                   selQuery    =     null;
          int                      hasRows     =     0;
          String                   message     =     null;
          
      try
      {
         operationsImpl                       =          new OperationsImpl();
         connection                           =          operationsImpl.getConnection();
         
         hasRows                              =          0;
         selQuery                             =          "SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID=?";
         pStmt                                =          connection.prepareStatement(selQuery);
         pStmt.setString(1,sprDOB.getLocationId());
         rs          =         pStmt.executeQuery();
         if(rs.next())
           hasRows++;
          if(hasRows==0){
           return "Location Id is not a Valid Location "+sprDOB.getLocationId();
         }
        
         
         if(pStmt!=null)           pStmt.close();
         if(rs!=null)           rs.close();   
         hasRows                              =          0;
         selQuery                             =          "SELECT SALESPERSON_CODE FROM QMS_SALESPERSON_REG WHERE SALESPERSON_CODE=?";
         pStmt                                =          connection.prepareStatement(selQuery);
         pStmt.setString(1,sprDOB.getSalesPersonCode());
         rs          =         pStmt.executeQuery();
         if(rs.next()){
           hasRows++;
           return "Record Already Exist with SalesPersonCode"+sprDOB.getSalesPersonCode();
         }
         
         if(hasRows==0)
         {
             ictxt                                =          new InitialContext();         
             oidHome                              =          (OIDSessionHome)ictxt.lookup("OIDSessionBean");
             oidRemote                            =          (OIDSession)oidHome.create();
             sprDOB.setAddressId(new Integer(oidRemote.getAddressOID()).toString());         
             home                                 =          (SalesPersonRegistrationLocalHome)lookUpBean.getEJBLocalHome("java:comp/env/SalesPersonRegistrationBean");         
             remote                               =          (SalesPersonRegistrationLocal)home.create(sprDOB);  
         }
         
      }
       catch(Exception e)
       {
         //Logger.error(FILE_NAME,"Error in insertDensityGroupCode module",e.toString());  
         logger.error(FILE_NAME+"Error in insertDensityGroupCode module"+e.toString());  
         e.printStackTrace();
         return "Error While Inserting Record";
       }
       finally
       {
         home    =    null;
         remote  =    null;
         ictxt   =    null;
         ConnectionUtil.closeConnection(connection,pStmt,rs);
       }
       return "Record with Salesperson Code "+sprDOB.getSalesPersonCode()+" is added successfully";            
    }
    
    public boolean updateSalesPersonDetails(SalesPersonRegistrationDOB sprDOB)
    {
          SalesPersonRegistrationLocalHome    home    =        null;
          SalesPersonRegistrationLocal        remote  =        null;
          SalesPersonRegistrationPK           pkObj   =        null;
      try
      {
         pkObj                 =  new SalesPersonRegistrationPK();
         pkObj.salesPersonCode =  sprDOB.getSalesPersonCode();         
         home      =          (SalesPersonRegistrationLocalHome)lookUpBean.getEJBLocalHome("java:comp/env/SalesPersonRegistrationBean");
         remote    =          (SalesPersonRegistrationLocal)home.findByPrimaryKey(pkObj);  
         remote.setSalesPersonRegistrationDOB(sprDOB);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        return false;
      }
      return true;
    }
    public boolean removeSalesPersonDetails(String salesPersonCode)
    {
          SalesPersonRegistrationLocalHome    home    =        null;
          SalesPersonRegistrationLocal        remote  =        null;
          SalesPersonRegistrationPK           pkObj   =        null;
      try
      {
         pkObj                 =  new SalesPersonRegistrationPK();
         pkObj.salesPersonCode =  salesPersonCode;
         home                  =  (SalesPersonRegistrationLocalHome)lookUpBean.getEJBLocalHome("java:comp/env/SalesPersonRegistrationBean");
         remote                =  (SalesPersonRegistrationLocal)home.findByPrimaryKey(pkObj);  
         remote.remove();
      }
      catch(Exception e)
      {
        e.printStackTrace();
        return false;
      }
      return true;
    }
    
     public ArrayList getSalesPersonIds(String salesPersonCode,String terminalId,String operation)
    {
         
         //PreparedStatement        pStmt          =             null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
         ResultSet                resultSet      =             null;
        // String                   selectQuery    =             null; 
         ArrayList                dgcList        =             null;         
         String                   whereClause    =             null;
         Connection               connection     =             null;
		 CallableStatement        cstmt          =             null;
      try
      {  
      
         
           
         dgcList         =    new ArrayList();
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         //selectQuery     =    "SELECT SALESPERSON_CODE  FROM QMS_SALESPERSON_REG  WHERE INVALIDATE='F' AND SALESPERSON_CODE LIKE '"+salesPersonCode+"%'";
         
         //pStmt           =    connection.prepareStatement(selectQuery);
         //pStmt.setString(1,salesPersonCode);                
         //resultSet          =    pStmt.executeQuery();         
		 cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_SALESPERSON(?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,(salesPersonCode+"%"));
              //cstmt.setString(4,("F"));          
              cstmt.setString(4,operation);
              cstmt.execute();
              resultSet  =  (ResultSet)cstmt.getObject(1);
         while(resultSet.next())
         {           
           dgcList.add(resultSet.getString("SALESPERSON_CODE"));
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getSalesPersonIds module",e.toString());     
         logger.error(FILE_NAME+"Error in getSalesPersonIds module"+e.toString());     
         return null;
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,null,resultSet);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
       
      }return dgcList;
    }
    
    public ArrayList getRepOffIds(String salesPersonCode,String level)
    {
         
         PreparedStatement        pStmt          =             null;
         ResultSet                resultSet      =             null;
         String                   selectQuery    =             null; 
         ArrayList                sprList        =             null;         
         String                   whereClause    =             null;
         Connection               connection     =             null;
         String[]                 values         =             new String[4];
      try
      {  
      
         
           
         sprList         =    new ArrayList();
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
         selectQuery     =    "SELECT SALESPERSON_CODE,SALESPERSON_NAME,LEVELID,DESIGNATION   FROM QMS_SALESPERSON_REG  WHERE INVALIDATE='F' AND SALESPERSON_CODE LIKE '"+salesPersonCode+"%'";
         
         pStmt           =    connection.prepareStatement(selectQuery);                        
         resultSet          =    pStmt.executeQuery();         
         while(resultSet.next())
         {           
           //Logger.info(FILE_NAME,"level.compareTo(resultSet.getString(LEVELID)"+level.compareTo(resultSet.getString("LEVELID")));
          // Logger.info(FILE_NAME,"resultSet.getString(LEVELID)"+resultSet.getString("LEVELID"));
           if(level.compareTo(resultSet.getString("LEVELID"))<0){
               values         =  new String[4];
               values[0]      =  resultSet.getString("SALESPERSON_CODE");
               values[1]      =  resultSet.getString("SALESPERSON_NAME");
               values[2]      =  resultSet.getString("LEVELID");
               values[3]      =  resultSet.getString("DESIGNATION");
               sprList.add(values);
               values        =   null;              
           }
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
         //Logger.error(FILE_NAME,"Error in getSalesPersonIds module",e.toString());     
         logger.error(FILE_NAME+"Error in getSalesPersonIds module"+e.toString());     
         return null;
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
       
      }return sprList;
    }
    
//     public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel,String empId)//@@MOdified by kameswari for the issue
    public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel,String empId,String fromWhere) //@@ Modified by subrahmanyam  for the pbn id:220125 on 07-oct-10
    {
         
         PreparedStatement        pStmt          =             null;
         ResultSet                resultSet      =             null;
         String                   selectQuery    =             null; 
         ArrayList                dgcList        =             null;         
         String                   whereClause    =             null;
         String                   whereCondition  =            "";
        // String                   empId          =             "";
         Connection               connection     =             null;
           String[]                 terminalIdList      =  null;   
      try
      {  
      
         /*if("HO_TERMINAL".equalsIgnoreCase(accessLevel))
            accLevelQuery = "SELECT DISTINCT TERMINALID FROM FS_FR_TERMINALMASTER";
         else
            accLevelQuery = "SELECT CHILD_TERMINAL_ID TERMID FROM FS_FR_TERMINAL_REGN WERE PARENT_TERMINAL_ID ='"+terminalId+"' UNION SELECT '"+terminalId+"' TERMID FROM DUAL";*/
            
       
         dgcList         =    new ArrayList();
         operationsImpl  =    new OperationsImpl();
         connection      =    operationsImpl.getConnection();
       //  empId           =    accessLevel;//@@ Emp Id is passed to this method instead of AccessLevel
         
         if(salesPersonCode!=null && salesPersonCode.trim().length()!=0)
            whereCondition = "AND EMPID LIKE '"+salesPersonCode+"%' ";
         
         /*selectQuery     =    "SELECT EMPID,USERNAME FROM FS_USERMASTER WHERE DESIGNATION_ID IN (SELECT DESIGNATION_ID FROM QMS_DESIGNATION "
                              +" WHERE TO_NUMBER(LEVEL_NO) >= (SELECT TO_NUMBER(LEVEL_NO) FROM QMS_DESIGNATION DES, FS_USERMASTER UM WHERE DES.DESIGNATION_ID = UM.DESIGNATION_ID "
                              +" AND UM.EMPID = ? ))"+whereCondition+"  ORDER BY EMPID";*/
         //@@Modified by Kameswari for the issue
//       if("HO_TERMINAL".equalsIgnoreCase(accessLevel) )
//         if("HO_TERMINAL".equalsIgnoreCase(accessLevel) && "pendingReport".equalsIgnoreCase(fromWhere) ) 
         if("HO_TERMINAL".equalsIgnoreCase(accessLevel) && "DHLCORP".equalsIgnoreCase(terminalId) )
         {
           selectQuery     = "SELECT EMPID,USERNAME FROM FS_USERMASTER WHERE DESIGNATION_ID IN (SELECT DESIGNATION_ID FROM QMS_DESIGNATION "
                              +" WHERE TO_NUMBER(LEVEL_NO) >= (SELECT TO_NUMBER(LEVEL_NO) FROM QMS_DESIGNATION DES, FS_USERMASTER UM WHERE DES.DESIGNATION_ID = UM.DESIGNATION_ID "
                              +" AND UM.EMPID = ? ))"+whereCondition+"AND LOCATIONID IN( SELECT TERMINALID FROM FS_FR_TERMINALMASTER) ORDER BY EMPID";
         // added by VLAKSHMI for issue 145483 on 20/11/2008 
			pStmt           =    connection.prepareStatement(selectQuery);
           pStmt.setString(1,empId);                
           resultSet          =    pStmt.executeQuery();         
           while(resultSet.next())
           {           
             dgcList.add(resultSet.getString("EMPID")+"["+resultSet.getString("USERNAME")+"]");
           }
		   // end of issue 145483
         }       else
        {
          terminalIdList = terminalId.split(",");
          int terminalIdsListLen	=	terminalIdList.length;
          for(int i=0;i<terminalIdsListLen;i++)
          {
            //@@Modified by Kameswari for the issue-79047
         /* selectQuery     =    "SELECT EMPID,USERNAME FROM FS_USERMASTER WHERE DESIGNATION_ID IN (SELECT DESIGNATION_ID FROM QMS_DESIGNATION "
                              +" WHERE TO_NUMBER(LEVEL_NO) >= (SELECT TO_NUMBER(LEVEL_NO) FROM QMS_DESIGNATION DES, FS_USERMASTER UM WHERE DES.DESIGNATION_ID = UM.DESIGNATION_ID "
                              +" AND UM.EMPID = ? ))"+whereCondition+" AND LOCATIONID IN( SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID ='"+terminalId+"' UNION SELECT"+ 
                              " CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalId+
                              "' ) ORDER BY EMPID";*/
                              /*"UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH"+
                              " CHILD_TERMINAL_ID='"+terminalId+"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H' ) ORDER BY EMPID";   */                 
         selectQuery     =    "SELECT EMPID,USERNAME FROM FS_USERMASTER WHERE DESIGNATION_ID IN (SELECT DESIGNATION_ID FROM QMS_DESIGNATION "
                              +" WHERE TO_NUMBER(LEVEL_NO) >= (SELECT TO_NUMBER(LEVEL_NO) FROM QMS_DESIGNATION DES, FS_USERMASTER UM WHERE DES.DESIGNATION_ID = UM.DESIGNATION_ID "
                              +" AND UM.EMPID = ? ))"+whereCondition+" AND LOCATIONID IN( SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID ='"+terminalIdList[i]+"' UNION SELECT"+ 
                              " CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+terminalIdList[i]+
                              "' ) ORDER BY EMPID";
              pStmt           =    connection.prepareStatement(selectQuery);
         pStmt.setString(1,empId);                
         resultSet          =    pStmt.executeQuery();         
         while(resultSet.next())
         {           
           dgcList.add(resultSet.getString("EMPID")+"["+resultSet.getString("USERNAME")+"]");
         }
          }
        }
      }
      catch(Exception e)
      {
         e.printStackTrace();
//<<<<<<< QMSSetUpSessionBean.java
         //Logger.error(FILE_NAME,"Error in getPerKGIds module",e.toString());         
         logger.error(FILE_NAME+"Error in getPerKGIds module"+e.toString());         
         //return null;
//=======
         //Logger.error(FILE_NAME,"Error in getPerKGIds module",e.toString());         
         dgcList  = null;
         //return null;
//>>>>>>> 1.7.2.1.2.3.2.8
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmt,resultSet);
       
      }return dgcList;
    }
    
    public ArrayList getDesignationIds(String searchString,String terminalId,String operation)
	  {
        Connection	          connection	          =                 null;
        //Statement	            desiStmt	            =                 null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet	            desiResutltSet        =                 null;
        CallableStatement     cstmt                 =                 null;
        ArrayList	designationIds= new ArrayList();
    
      
        
        //String sql = "SELECT DESIGNATION_ID ,LEVEL_NO    FROM QMS_DESIGNATION  WHERE DESIGNATION_ID LIKE '"+searchString+"%' ORDER BY DESIGNATION_ID";
         
        try
          {
              operationsImpl    =  new OperationsImpl();
              connection = operationsImpl.getConnection();
              //desiStmt = connection.createStatement();
              //desiResutltSet = desiStmt.executeQuery(sql);
			  cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.GET_DESIGNATION(?,?,?)}");
              cstmt.registerOutParameter(1,OracleTypes.CURSOR);
              cstmt.setString(2,terminalId);
              cstmt.setString(3,(searchString+"%"));
              //cstmt.setString(4,("F"));          
              cstmt.setString(4,operation);
              cstmt.execute();
              desiResutltSet  =  (ResultSet)cstmt.getObject(1);			  
              while(desiResutltSet.next())
              {
               designationIds.add(desiResutltSet.getString(1)+"["+desiResutltSet.getString(2)+","+desiResutltSet.getString(3)+"]");
              }
          }
          catch(SQLException sqEx)
          {
            
            //Logger.error(FILE_NAME, "[getDesignationIs(whereclause)] -> "+sqEx.toString());
            logger.error(FILE_NAME+ "[getDesignationIs(whereclause)] -> "+sqEx.toString());
                              throw new EJBException(sqEx.toString());
          }
          catch(Exception e)
          {
            e.printStackTrace();
            return null;
          }
          finally
          {
            ConnectionUtil.closeConnection(connection, null, desiResutltSet);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
          }
            return designationIds;
            
	}
  
  public boolean  invalidateSalesPersonDetails(ArrayList sprList)
  {
    Connection	           connection	= null;
		PreparedStatement	      pStmt	= null;
    String    sqlInvalidate        =  "UPDATE QMS_SALESPERSON_REG SET INVALIDATE = ? WHERE SALESPERSON_CODE=? ";
    SalesPersonRegistrationDOB  sprDOB  =  null;
    
    try
    {
         
            operationsImpl    =  new OperationsImpl();
			      connection = operationsImpl.getConnection();
            pStmt = connection.prepareStatement(sqlInvalidate);  
            int sprListSize	=	sprList.size();
            for(int i=0;i<sprListSize;i++)
            {
                sprDOB  =  (SalesPersonRegistrationDOB)sprList.get(i);
                pStmt.clearParameters();
                pStmt.setString(1,sprDOB.getInvalidate());
                pStmt.setString(2,sprDOB.getSalesPersonCode());
                int k = pStmt.executeUpdate();
               
            }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection, pStmt);
      sprDOB   =  null;
    }
    return true;
  }
  
  
  
  /**
     * 
     * @param dgcCode
     * @param uom
     * @param perKg     
     * @return DensityGroupCodeDOB
     */
   
    
    public boolean insertZoneCodeDetails(ZoneCodeMasterDOB dob) throws FoursoftException
    {       
       //OIDSession               oidRemote   =     null;
       //OIDSessionHome           oidHome     =     null;
       ZoneCodeMasterDAO  dao                 = new ZoneCodeMasterDAO();
       boolean            flag                = false; 
       ArrayList          duplicateList       = null;
      try
      {
        
         /*ictxt                                =          new InitialContext();         
         oidHome                              =          (OIDSessionHome)ictxt.lookup("OIDSessionBean");
         oidRemote                            =          (OIDSession)oidHome.create();
         dob.setZoneCode(new Long(oidRemote.getZoneCodeId()).toString());*/
         //home                                 =          (ZoneCodeMasterLocalHome)ictxt.lookup("java:comp/env/ZoneCodeMasterBean");         
         //remote                               =           home.create(dob);
         flag = validate(dob.getOriginLocation(),dob.getTerminalId(),dob.getShipmentMode());
         if(flag)
            duplicateList =  dao.create(dob);
         else
           return flag;
           
          if(duplicateList!=null && duplicateList.size()>0)
              throw new FoursoftException("The Following Records Were Not Inserted as the Zip Codes Already Exist for : "+duplicateList);
      }
      catch(FoursoftException fs)
      {
        fs.printStackTrace();
        throw new FoursoftException(fs.getMessage());
      }
      catch(Exception e)
      {
        e.printStackTrace();
        throw new EJBException("Error while inserting details");
      }
      return true;
    }
    private boolean validate(String locationId,String terminalId,String shipmentMode)throws SQLException
    {
      PreparedStatement pStmtMaster = null;
      ResultSet         rsMaster    = null;
      boolean           flag        = false;
      String            shipModeStr = null;
      
      try
      {
          if("1".equalsIgnoreCase(shipmentMode))
              shipModeStr = "1,3,5,7";
          else 
              shipModeStr = "2,3,6,7";
              
          String        sql         = "SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE SHIPMENTMODE IN ("+shipModeStr+") AND LOCATIONID IN "+
                                      "(SELECT LOCATIONID FROM FS_FR_TERMINALLOCATION WHERE LOCATIONID= ? AND TERMINALID =?)";
          
          connection = this.getConnection();
          pStmtMaster = connection.prepareStatement(sql);
          pStmtMaster.setString(1,locationId);
          pStmtMaster.setString(2,terminalId);
          rsMaster = pStmtMaster.executeQuery();
          if(rsMaster.next())
          {           
           return true;
          }
      }catch(SQLException e)
     {
        e.printStackTrace();
        throw new SQLException("Error while uploading the data");
     }
     catch(Exception e)
     {
        e.printStackTrace();
        throw new EJBException("Error while uploading the data");
     }
     finally
     {
       ConnectionUtil.closeConnection(connection,pStmtMaster,rsMaster);
       
     }return flag;
    }
    public boolean updateZoneCodeDetails(ZoneCodeMasterDOB dob) throws FoursoftException
    {
       //ZoneCodeMasterLocalHome  home        =     null;
       //ZoneCodeMasterLocal      remote      =     null;
       boolean                 flag           =  false;
       //ZoneCodeMasterPK         pkObj       =     null;
       ZoneCodeMasterDAO       dao            =  new ZoneCodeMasterDAO();
       ArrayList               duplicateList  =  null;
      try
      {
        //pkObj                                =          new ZoneCodeMasterPK();
        //pkObj.zoneCode                       =          dob.getZoneCode();
        //ictxt                                =          new InitialContext(); 
       // home                                 =          (ZoneCodeMasterLocalHome)ictxt.lookup("java:comp/env/ZoneCodeMasterBean");         
       // remote                               =           (ZoneCodeMasterLocal)home.findByPrimaryKey(pkObj);         
        //remote.setZoneCodeMasterDOB(dob);
        duplicateList = dao.store(dob);
        
        if(duplicateList!=null && duplicateList.size()>0)
              throw new FoursoftException("The Following Records Were Not Updated as the Zip Codes Already Exist for Zone: "+duplicateList);
      }
      catch(FoursoftException fs)
      {
        fs.printStackTrace();
        throw new FoursoftException(fs.getMessage());
      }
      catch(Exception e)
      {
        e.printStackTrace();
        throw new EJBException("Error while updating Details");
      }
      return true;
    
  }
//  @@ Commentd & Added by subrahmanyam for 216629 on 31-AUG-10    
    //public boolean removeZoneCodeDetails(ZoneCodeMasterDOB dob)
    public String removeZoneCodeDetails(ZoneCodeMasterDOB dob)
    {
       //ZoneCodeMasterLocalHome  home        =     null;
      // ZoneCodeMasterLocal      remote      =     null;
       boolean                  flag        =     false;
       //ZoneCodeMasterPK         pkObj       =     null;
       ZoneCodeMasterDAO        dao           =     new ZoneCodeMasterDAO();
       String 			remainingZones			= "";
      try
      {
        //pkObj                                =          new ZoneCodeMasterPK();
        //pkObj.zoneCode                       =          dob.getZoneCode();
        //ictxt                                =          new InitialContext(); 
        //home                                 =          (ZoneCodeMasterLocalHome)ictxt.lookup("java:comp/env/ZoneCodeMasterBean");         
       // remote                               =          (ZoneCodeMasterLocal)home.findByPrimaryKey(pkObj);         
        //remote.setZoneCodeMasterDOB(dob);
        //remote.remove();  
       // flag = dao.remove(dob);
    	  remainingZones	=	dao.remove(dob);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.info(FILE_NAME,"Error while removing record"+e.toString());
        logger.info(FILE_NAME+"Error while removing record"+e.toString());
        throw new EJBException("Error while deleting Details");
      }
      return remainingZones;
   
    }
    
    public ZoneCodeMasterDOB selectZoneCodeDetails(String location,String zoneCodeType,String shipmentMode,String consoleType,String operation)
    { 
     
       //ZoneCodeMasterLocalHome  home        =     null;
       //ZoneCodeMasterLocal      remote      =     null;
       boolean                  flag        =     false;
       ZoneCodeMasterDOB        dob         =     null;
       //ZoneCodeMasterPK         pkObj       =     null;
       //String loadMasterQuery    = "SELECT ZONE_CODE FROM QMS_ZONE_CODE_MASTER WHERE ORIGIN_LOCATION=? AND ZIPCODE_TYPE=? AND INVALIDATE='F'";
     
       //PreparedStatement  pStmtMaster  = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
       //PreparedStatement  pStmtDtl     = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
       //ResultSet          rsMaster     = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
       //ResultSet          rsDtl        = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
       ZoneCodeMasterDAO  dao          = new ZoneCodeMasterDAO();
      try
      {
      /*  connection             =  operationsImpl.getConnection();
       pStmtMaster            =  connection.prepareStatement(loadMasterQuery);
       pStmtMaster.setString(1,location);
       pStmtMaster.setString(2,zipCode);       
      // pStmtMaster.setString(4,port);
       Logger.info(FILE_NAME,"inside session bean connection "+connection);
       rsMaster               =  pStmtMaster.executeQuery();   
       //pkObj                                =          new ZoneCodeMasterPK();
         while(rsMaster.next())
         {
            //pkObj.zoneCode               =         rsMaster.getString("ZONE_CODE");
         }
         
        //ictxt                                =          new InitialContext(); 
        //home                                 =          (ZoneCodeMasterLocalHome)ictxt.lookup("java:comp/env/ZoneCodeMasterBean");         
        //remote                               =          (ZoneCodeMasterLocal)home.findByPrimaryKey(pkObj);
       // dob                                  =           remote.getZoneCodeMasterDOB();*/
       dob  = dao.load(location,zoneCodeType,shipmentMode,consoleType, operation);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.info(FILE_NAME,"Error while selectZoneCodeDetails record"+e.toString());
        logger.info(FILE_NAME+"Error while selectZoneCodeDetails record"+e.toString());
        return null;
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,null,null);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
      }
      return dob;
    }
    public boolean invalidateZoneCodeDetails(ArrayList zoneList)throws SQLException
    {
       String             updateQuery    =   "UPDATE QMS_ZONE_CODE_DTL SET INVALIDATE =? WHERE ZONE_CODE =? AND ROWNO=?";
       PreparedStatement  pStmtMaster    =   null;
       ZoneCodeChildDOB childDOB         =   null;
       ArrayList        zoneChildList     =   null;
       ZoneCodeMasterDOB dob             =   null;
       ZoneCodeChildDOB  dob1            =null;
      try
      {
         connection             =  operationsImpl.getConnection();
         pStmtMaster            =  connection.prepareStatement(updateQuery);
        /* zoneCodeList           =  dob.getZoneCodeList();
         for(int i=0;i<zoneCodeList.size();i++)
         {
           childDOB        =  (ZoneCodeChildDOB)zoneCodeList.get(i);
           pStmtMaster.clearParameters();
           pStmtMaster.setString(1,childDOB.getInvalidate());
           pStmtMaster.setString(2,childDOB.getZoneCode());
           pStmtMaster.setString(3,childDOB.getRowNo());
           pStmtMaster.executeUpdate();           
         }*/
         int zoneListSize	=	zoneList.size();
      /*   for(int i=0;i<zoneListSize;i++)
          {
            dob  = (ZoneCodeMasterDOB)zoneList.get(i);
               zoneChildList       = dob.getZoneCodeList();
               int zoneChldListSize	=	zoneChildList.size();
              for(int j=0;j<zoneChldListSize;j++)
              {
                 childDOB    =    (ZoneCodeChildDOB)zoneChildList.get(j);
                 pStmtMaster.clearParameters();
                 pStmtMaster.setString(1,childDOB.getInvalidate());
                 pStmtMaster.setString(2,childDOB.getZoneCode());
                 pStmtMaster.setString(3,childDOB.getRowNo());
                 pStmtMaster.addBatch(); 
                //pStmtMaster.executeUpdate();
              }				
          }*/
         //@@Modified by kiran.v on 06/02/2012 for Wpbn Issue -289556
         for(int i=0;i<zoneListSize;i++)
          {
              dob1  = (ZoneCodeChildDOB)zoneList.get(i);
                
                   pStmtMaster.clearParameters();
                   pStmtMaster.setString(1,dob1.getInvalidate());
                   pStmtMaster.setString(2,dob1.getZoneCode());
                   pStmtMaster.setString(3,dob1.getRowNo());
                   pStmtMaster.addBatch(); 
                  //pStmtMaster.executeUpdate();
                }				
            //@@Ended by kiran.v 
        pStmtMaster.executeBatch();        
      }
      catch(SQLException e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error in invalidating"+e.toString());
        logger.error(FILE_NAME+"Error in invalidating"+e.toString());
        throw new EJBException("Error while invalidating the records(SQL Exception)");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"Error in invalidating"+e.toString());
        logger.error(FILE_NAME+"Error in invalidating"+e.toString());
        throw new EJBException("Error while invalidating the records");
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmtMaster);
      }
      return true;
    }
    public ArrayList viewAllZoneCodeDetails()
    {
          ZoneCodeMasterDOB dob     =  null;           
          String loadMasterQuery    = "SELECT DISTINCT MAS.ZONE_CODE,ORIGIN_LOCATION,TERMINALID,CITY,STATE,ZIPCODE_TYPE,DECODE(MAS.SHIPMENT_MODE, 1, 'Air', 'Sea') SHIPMENT_MODE,MAS.CONSOLE_TYPE,"+
                                      "MAS.INVALIDATE FROM QMS_ZONE_CODE_MASTER MAS, QMS_ZONE_CODE_DTL DTL WHERE MAS.ZONE_CODE = DTL.ZONE_CODE ORDER BY ORIGIN_LOCATION";
//@@ Modified by subrahmanyam for 212863 on 26-Jul-10
          String loadDtlQuery       = "SELECT DISTINCT ZONE_CODE,FROM_ZIPCODE ,TO_ZIPCODE ,ZONE  ,ESTIMATED_TIME  ,ESTIMATED_DISTANCE ,ALPHANUMERIC,ROWNO,INVALIDATE FROM QMS_ZONE_CODE_DTL WHERE ZONE_CODE=?  ORDER BY ZONE";
          PreparedStatement  pStmtMaster    =   null;
          PreparedStatement  pStmtDtl			  =		null;
          ResultSet          rsMaster			  =		null;
          ResultSet          rsDtl				  =		null;          
          ArrayList			zoneCodeList        =		new ArrayList();
          ZoneCodeChildDOB childDOB         =   null;
          ArrayList        zoneChildList     =   null;
      try
      {
        connection             =  operationsImpl.getConnection();
        pStmtMaster            =  connection.prepareStatement(loadMasterQuery);
        pStmtDtl               =  connection.prepareStatement(loadDtlQuery);
        rsMaster               =  pStmtMaster.executeQuery();
        
         while(rsMaster.next())
         {
           dob      =   new ZoneCodeMasterDOB();
           //System.out.println("rsMaster.getString(3)  "+rsMaster.getString(3));
           dob.setOriginLocation(rsMaster.getString(2)!=null?rsMaster.getString(2):"");
           dob.setTerminalId(rsMaster.getString(3)!=null?rsMaster.getString(3):"");
           dob.setCity(rsMaster.getString(4)!=null?rsMaster.getString(4):"");
           dob.setState(rsMaster.getString(5)!=null?rsMaster.getString(5):"");
           dob.setZipCode(rsMaster.getString(6)!=null?rsMaster.getString(6):"");
           dob.setShipmentMode(rsMaster.getString(7)!=null?rsMaster.getString(7):"");
           dob.setConsoleType(rsMaster.getString(8)!=null?rsMaster.getString(8):"");
           //dob.setPort(rsMaster.getString(7));
           dob.setZoneCode(rsMaster.getString(1));
           pStmtDtl.setString(1,rsMaster.getString(1));
           rsDtl   = pStmtDtl.executeQuery();
           zoneChildList    =  new ArrayList();
           while(rsDtl.next())
          {    
               childDOB      =   new ZoneCodeChildDOB();
               
               childDOB.setZoneCode(rsDtl.getString(1));
               childDOB.setFromZipCode(rsDtl.getString(2)!=null?rsDtl.getString(2):"");
               childDOB.setToZipCode(rsDtl.getString(3)!=null?rsDtl.getString(3):"");
               childDOB.setZone(rsDtl.getString(4)!=null?rsDtl.getString(4):"");
               childDOB.setEstimationTime(rsDtl.getString(5)!=null?rsDtl.getString(5):"");
               childDOB.setEstimatedDistance(rsDtl.getString(6)!=null?rsDtl.getString(6):"");
               childDOB.setAlphaNumaric(rsDtl.getString(7)!=null?rsDtl.getString(7):"");
               childDOB.setInvalidate(rsDtl.getString(9)!=null?rsDtl.getString(9):"");
               childDOB.setRowNo(rsDtl.getString(8)!=null?rsDtl.getString(8):"");               
               zoneChildList.add(childDOB);
           }
           dob.setZoneCodeList(zoneChildList);
           
           zoneCodeList.add(dob);
           
        }
       
      }
      catch(Exception e)
      {
        e.printStackTrace();
        return null;
      } 
      finally
      {
         ConnectionUtil.closeConnection(connection,pStmtDtl,rsDtl);
         ConnectionUtil.closeConnection(connection,pStmtMaster,rsMaster);
      }
      return zoneCodeList;
    }
   public ArrayList getLocationIds(String searchString)
    {
        ArrayList      vec              = new ArrayList();
        Connection     connection       = null;
        //Statement      stmt             = null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
        ResultSet      rs               = null;
        String		   sMode			= "";	
        String		   temp				= "";	
        String		   locId			= "";	
        String       city       = "";
        CallableStatement cstmt  = null;
        try
        {/*
            if(searchString!=null && !(searchString.equals(""))){
              searchString = " WHERE LOCATIONID LIKE '"+searchString+"%' ";
            }else{
              searchString = "";
            }*/
            connection = operationsImpl.getConnection();
            //stmt = connection.createStatement();
            //String sqlQuery = "SELECT LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER " + searchString +" ORDER BY LOCATIONID ";
            //rs = stmt.executeQuery(sqlQuery);
            if(searchString==null)searchString = "";
            cstmt        = connection.prepareCall("{ ?= call QMS_SETUPS.get_location(?,?,?,?)}");
            cstmt.registerOutParameter(1,OracleTypes.CURSOR);
            cstmt.setString(2,"");
            cstmt.setString(3,"");
            cstmt.setString(4,(searchString+"%"));
            cstmt.setString(5,"");
            cstmt.execute();
			      rs  =  (ResultSet)cstmt.getObject(1);
            while(rs.next())
            {
              StringBuffer   sMode1            = new StringBuffer();
              StringBuffer   locId1           = new StringBuffer();
              
              locId = rs.getString(1);	
              temp  = rs.getString(2);
              sMode = rs.getString(3);
              city  = rs.getString(4);
              
              if(sMode==null)
              {  sMode="   ";}
                          
              if(sMode!=null)
              { 
                if(sMode.equals("7"))	{sMode="AST";}
                if(sMode.equals("1"))	{sMode="A  ";}
                if(sMode.equals("2"))	{sMode=" S ";}		
                if(sMode.equals("3"))	{sMode="AS ";}
                if(sMode.equals("4"))	{sMode="  T";}
                if(sMode.equals("5"))	{sMode="A T";}
                if(sMode.equals("6"))	{sMode=" ST";}
              }	
                
              if(locId.length() == 2)	              
              {
                locId1.append(locId);
                locId1.append(" ");
                locId = locId1.toString();
              }
              if(locId.length() == 1)
              {
                locId1.append(locId);
                locId1.append(" ");
                locId = locId1.toString();
              }	            
              sMode1.append(locId);
              sMode1.append("[" );
              sMode1.append(sMode);
              sMode1.append( "]");
              sMode1.append("[" );
              sMode1.append(temp);
              sMode1.append( "]");
              sMode1.append("[" );
              sMode1.append(city);
              sMode1.append( "]");
              sMode = sMode1.toString();                 
              vec.add(sMode);
			}
      }
      catch(SQLException sqEx)
      {
		sqEx.printStackTrace();
        //Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
          throw new EJBException(sqEx.toString());
      }
	  catch(EJBException sqEx)
      {
		sqEx.printStackTrace();
        //Logger.error(FILE_NAME, "[getLocationIds(whereclause)] -> "+sqEx.toString());
        logger.error(FILE_NAME+ "[getLocationIds(whereclause)] -> "+sqEx.toString());
          throw new EJBException(sqEx.toString());
      }
      finally
      {
        ConnectionUtil.closeConnection(connection, null, rs);//Modified By RajKumari on 30-10-2008 for Connection Leakages.
      }
      return vec;
    }
    
public HashMap uploadZoneCodeMasterDetails(ArrayList zoneCodeList,boolean addModFlag)
    {
      ZoneCodeMasterDAO dao = new ZoneCodeMasterDAO();
      HashMap           map = null; 
      try
      {
        map   = dao.uploadZoneCodeMasterDetails(zoneCodeList);
      }     
      catch(Exception e)
      {
        e.printStackTrace();
        throw new EJBException("Error while uploading the data");
      }return map;
    }
  /**
   * Used for Uploading Canada Zip Codes.
   * @throws java.rmi.RemoteException
   * @return Hashmap containing Inserted/Updated & Failed List Objects
   * @param zoneCodesList
   */
  public HashMap uploadCanadaZoneCodes(ArrayList zoneCodesList) throws EJBException
  {
    ZoneCodeMasterDAO dao = new ZoneCodeMasterDAO();
    HashMap           map = null; 
    try
    {
      map = dao.uploadCanadaZoneCodes(zoneCodesList);
    }
    catch(Exception e)
    {
      logger.error("Exception in uploadCanadaZoneCodes -- "+e);
      e.printStackTrace();
      throw new EJBException("Error While Uploading Data!");      
    }
    return map;
  }
   /* public HashMap uploadZoneCodeMasterDetails(ArrayList zoneCodeList,boolean addModFlag)
    {
                Connection connection =	null;
                PreparedStatement pStmt =	null;
                PreparedStatement pStmtDtl =	null;
                String  sqlStr = "";
                ZoneCodeMasterDOB zoneCodeMasterDOB = null;
                ZoneCodeChildDOB zoneCodeChildDOB = null;
                ArrayList   existingZoneCodeList = new ArrayList(5);
                ArrayList   nonExistingZoneCodeList = new ArrayList(5);
                HashMap totalMap = new HashMap(2,2);
                OIDSessionHome   oidHome          =    null;
                OIDSession       oidRemote        =    null;  
                String           zoneCode         =    null;
                ArrayList        zoneCodeChildList  =  null;
                String           sqlStrDtl          =  null;
            try{
                 Logger.info(FILE_NAME," Inside uploadZoneCodeMasterDetails"+addModFlag);
                    connection=operationsImpl.getConnection();
                    totalMap = validateZoneCodeUploadData(zoneCodeList,addModFlag,connection);
              if(addModFlag){
                        ictxt                                =          new InitialContext();         
                         oidHome                              =          (OIDSessionHome)ictxt.lookup("OIDSessionBean");
                         oidRemote                            =          (OIDSession)oidHome.create();
                         
                        nonExistingZoneCodeList = (ArrayList)totalMap.get("NONEXISTS");
                        sqlStr = "INSERT INTO QMS_ZONE_CODE_MASTER VALUES(?,?,?,?,?,?,?,?)";
                        sqlStrDtl  =  "INSERT INTO QMS_ZONE_CODE_DTL VALUES(?,?,?,?,?,?,?,?,?)";
                        pStmt= connection.prepareStatement(sqlStr);
                        pStmtDtl= connection.prepareStatement(sqlStrDtl);                    
                        Logger.info(FILE_NAME,"nonExistingZoneCodeList.size()     "+nonExistingZoneCodeList.size());
                        for(int i = 0;i<nonExistingZoneCodeList.size();i++){
                      // while(nonExistingZoneCodeList.size()>0){
                            zoneCodeMasterDOB = (ZoneCodeMasterDOB)nonExistingZoneCodeList.get(i);   
                            zoneCode   =  new Long(oidRemote.getZoneCodeId()).toString();
                            pStmt.clearParameters();
                            pStmt.setString(1,zoneCode);
                            pStmt.setString(2,zoneCodeMasterDOB.getOriginLocation());
                            pStmt.setString(3,zoneCodeMasterDOB.getControlStation());
                            pStmt.setString(4,zoneCodeMasterDOB.getCity());
                            pStmt.setString(5,zoneCodeMasterDOB.getState());
                            pStmt.setString(6,zoneCodeMasterDOB.getZipCode());
                            pStmt.setString(7,zoneCodeMasterDOB.getPort());
                            pStmt.setString(8,"F");
                            pStmt.addBatch();
                        
                        //int count[] = pStmt.executeBatch();
                          
                      Logger.info(FILE_NAME,"zoneCode     "+zoneCode); 
                      zoneCodeChildList   =  zoneCodeMasterDOB.getZoneCodeList();
                      for(int j=0;j<zoneCodeChildList.size();j++)
                      {
                            zoneCodeChildDOB   = (ZoneCodeChildDOB )zoneCodeChildList.get(j);
                            pStmtDtl.clearParameters();
                            pStmtDtl.setString(1,zoneCode);
                            Logger.info(FILE_NAME,"getFromZipCode"+zoneCodeChildDOB.getFromZipCode());
                            pStmtDtl.setString(2,zoneCodeChildDOB.getFromZipCode());
                            pStmtDtl.setString(3,zoneCodeChildDOB.getToZipCode());
                            pStmtDtl.setString(4,zoneCodeChildDOB.getZone());
                            pStmtDtl.setString(5,zoneCodeChildDOB.getEstimationTime());
                            pStmtDtl.setString(6,zoneCodeChildDOB.getEstimatedDistance());
                            pStmtDtl.setString(7,zoneCodeChildDOB.getAlphaNumaric());
                            pStmtDtl.setString(8,new Integer(j+1).toString());
                            pStmtDtl.setString(9,"F");
                            pStmtDtl.addBatch();
                    }
                    //int count1[] = pStmt.executeBatch();
                    //if(count1.length != zoneCodeChildList.size())                           
                           // throw new java.sql.SQLException("Problem In Performing Upload DTL Opertion.");  
                  }
					pStmt.executeBatch();
                    pStmtDtl.executeBatch();
                      
              }else{
                        existingZoneCodeList = (ArrayList)totalMap.get("EXISTS");
                        sqlStr = "UPDATE QMS_ZONE_CODE_MASTER SET ORIGIN_LOCATION  =?,CONTROL_STATION  =?,CITY  =?,STATE =? ,ZIPCODE_TYPE =?,PORT =? WHERE	ZONE_CODE =?";
                        sqlStrDtl = "UPDATE QMS_ZONE_CODE_DTL SET FROM_ZIPCODE=? , TO_ZIPCODE =? , ZONE =? , ESTIMATED_TIME =? , ESTIMATED_DISTANCE =? , ALPHANUMERIC =? WHERE ZONE_CODE =? AND ROWNO =?";
                        pStmt= connection.prepareStatement(sqlStr);
                        pStmtDtl= connection.prepareStatement(sqlStrDtl);
                        for(int i = 0;i<existingZoneCodeList.size();i++){
                            zoneCodeMasterDOB = (ZoneCodeMasterDOB)existingZoneCodeList.get(i);
                            pStmt.clearParameters();
                            pStmt.setString(1,zoneCodeMasterDOB.getOriginLocation());
                            pStmt.setString(2,zoneCodeMasterDOB.getControlStation());
                            pStmt.setString(3,zoneCodeMasterDOB.getCity());
                            pStmt.setString(4,zoneCodeMasterDOB.getState());
                            pStmt.setString(5,zoneCodeMasterDOB.getZipCode());
                            pStmt.setString(6,zoneCodeMasterDOB.getPort());
                            pStmt.setString(7,zoneCodeMasterDOB.getZoneCode());
                            pStmt.addBatch();
                            zoneCodeList   = zoneCodeMasterDOB.getZoneCodeList();
                            for(int j=0;j<zoneCodeList.size();j++)
                             {
                               zoneCodeChildDOB     =   (ZoneCodeChildDOB)zoneCodeList.get(j);
                               pStmtDtl.clearParameters();
                               pStmtDtl.setString(1,zoneCodeChildDOB.getFromZipCode());
                               pStmtDtl.setString(2,zoneCodeChildDOB.getToZipCode());
                               pStmtDtl.setString(3,zoneCodeChildDOB.getZone());
                               pStmtDtl.setString(4,zoneCodeChildDOB.getEstimationTime());
                               pStmtDtl.setString(5,zoneCodeChildDOB.getEstimatedDistance());
                               pStmtDtl.setString(6,zoneCodeChildDOB.getAlphaNumaric());
                               pStmtDtl.setString(7,zoneCodeMasterDOB.getZoneCode());
                               pStmtDtl.setString(8,zoneCodeChildDOB.getRowNo());
                               pStmtDtl.executeUpdate();                              
                             }
                        }
                        int count[] = pStmt.executeBatch();
                        if(count.length != existingZoneCodeList.size())
                           // connection.commit();
                        //else
                            throw new java.sql.SQLException("Problem In Performing Upload Opertion.");
                        }
                        
                    
            }catch(SQLException sqle){
            sqle.printStackTrace();
              throw new EJBException("SQLException	in uploadZoneCodeMasterDetails	QMSSETUP SESSION BEAN "+sqle.toString());
            }catch(Exception	cnfe){
            cnfe.printStackTrace();
              throw new javax.ejb.EJBException("Exception in	uploadZoneCodeMasterDetails QMSSETUP SESSION BEAN  "+cnfe.toString());
            }finally{
              ConnectionUtil.closeConnection(connection,pStmt);
              ConnectionUtil.closeConnection(connection,pStmtDtl);
            }
                return totalMap;
    }
    private HashMap validateZoneCodeUploadData(ArrayList zoneCodeList,boolean addModFlag,Connection connection)throws SQLException
    {   
        PreparedStatement pStmt = null;
        ResultSet   rs  = null;
        ResultSet   rs1  = null;
        ZoneCodeMasterDOB zoneCodeMasterDOB = null;
        HashMap     hashMap = new HashMap(2,2);
        ArrayList   existingZoneCodeList = new ArrayList(5);
        ArrayList   nonExistingZoneCodeList = new ArrayList(5);
        try	{
            Logger.info(FILE_NAME," Inside validateCommodityUploadData"+zoneCodeList.size());
            for(int i=0;i<zoneCodeList.size();i++){
                zoneCodeMasterDOB = (ZoneCodeMasterDOB)zoneCodeList.get(i);
                pStmt = connection.prepareStatement("SELECT ZONE_CODE  FROM QMS_ZONE_CODE_MASTER WHERE ORIGIN_LOCATION =? AND CONTROL_STATION =? AND ZIPCODE_TYPE =? AND PORT=?");
                pStmt.setString(1,zoneCodeMasterDOB.getOriginLocation());
                pStmt.setString(2,zoneCodeMasterDOB.getControlStation());
                pStmt.setString(3,zoneCodeMasterDOB.getZipCode().toUpperCase());
                pStmt.setString(4,zoneCodeMasterDOB.getPort());
                rs = pStmt.executeQuery();
                if(addModFlag){
                    if(rs.next()){
                        zoneCodeMasterDOB.setRemarks(zoneCodeMasterDOB.getRemarks() + " Zone Code Details Already Exists ");
                        zoneCodeMasterDOB.setZoneCode(rs.getString("ZONE_CODE"));
                        Logger.info(FILE_NAME,"zoneCodeMasterDOB"+zoneCodeMasterDOB.getZoneCodeList());
                        existingZoneCodeList.add(zoneCodeMasterDOB);
                        validateZoneCodeDtlUploadData(zoneCodeMasterDOB,connection);
                    }else
                    {                                                
                        nonExistingZoneCodeList.add(zoneCodeMasterDOB);                        
                    }
                }else{
                    if(!rs.next()){
                        zoneCodeMasterDOB.setRemarks(zoneCodeMasterDOB.getRemarks() + " Zone Code Details Doesn't Exists. ");
                        nonExistingZoneCodeList.add(zoneCodeMasterDOB);
                    }else{
                        zoneCodeMasterDOB.setZoneCode(rs.getString("ZONE_CODE"));
                        existingZoneCodeList.add(zoneCodeMasterDOB);
                        
                    }
                }
                
            }
            hashMap.put("EXISTS",existingZoneCodeList);
            hashMap.put("NONEXISTS",nonExistingZoneCodeList);
        }catch (SQLException sqle){
        sqle.printStackTrace();
          Logger.error(FILE_NAME,"SQLException in validateUploadData of SetupSessionBean "+sqle.toString());
          
        }catch( Exception	cnfe ){
        cnfe.printStackTrace();
          throw new java.sql.SQLException("Exception	in validateUploadData of	SetupSessionBean "	+cnfe.toString());
        }finally{
                nonExistingZoneCodeList = null;
                existingZoneCodeList = null;
                zoneCodeMasterDOB = null;
          ConnectionUtil.closePreparedStatement(pStmt,rs);	
        }
        return hashMap;
    }
    private void validateZoneCodeDtlUploadData(ZoneCodeMasterDOB zoneCodeMasterDOB,Connection connection)
    {
       PreparedStatement pStmt  =  null;
       PreparedStatement pStmtZone  =  null;
       ResultSet         rs     =  null;
       String        selectQry  =  "SELECT MAX(ROWNO),ZONE_CODE FROM QMS_ZONE_CODE_DTL WHERE ZONE_CODE=(SELECT ZONE_CODE  FROM QMS_ZONE_CODE_MASTER WHERE ORIGIN_LOCATION =? AND CONTROL_STATION =? AND ZIPCODE_TYPE =? AND PORT=?)GROUP BY ZONE_CODE";
       String        selectDtlQry  =  "SELECT ZONE_CODE FROM QMS_ZONE_CODE_DTL WHERE FROM_ZIPCODE  =? AND TO_ZIPCODE  =? AND ZONE  =? ";
       String        insQuery   =  "INSERT INTO QMS_ZONE_CODE_DTL VALUES(?,?,?,?,?,?,?,?,?)";
       ArrayList     zoneChildList  =  null;
       ZoneCodeChildDOB zoneCodeChildDOB = null;
       int           maxRow         =  0;
       String        zoneCode       =  null;
       boolean       flag           =  false;
      try
      {  
                pStmt = connection.prepareStatement(selectQry);
                pStmt.setString(1,zoneCodeMasterDOB.getOriginLocation());
                pStmt.setString(2,zoneCodeMasterDOB.getControlStation());
                pStmt.setString(3,zoneCodeMasterDOB.getZipCode());
                pStmt.setString(4,zoneCodeMasterDOB.getPort());
                rs  = pStmt.executeQuery();
                if(rs.next()){
                  maxRow  =  new Integer(rs.getString(1)).intValue();
                  zoneCode  =  rs.getString(2);
                }
                if(pStmt!=null)pStmt.close();
                if(rs!=null)rs.close();
                
                if("NUMERIC".equalsIgnoreCase(zoneCodeMasterDOB.getZipCode()))
                   selectDtlQry  =  selectDtlQry+"ALPHANUMERIC ='"+zoneCodeChildDOB.getAlphaNumaric()+"'";
                   
                
              pStmt = connection.prepareStatement(insQuery);   
              pStmtZone = connection.prepareStatement(selectDtlQry);  
              zoneChildList    =  zoneCodeMasterDOB.getZoneCodeList();
              Logger.info(FILE_NAME,"zoneChildList   "+zoneChildList.size());
              for(int i=0;i<zoneChildList.size();i++){
                            zoneCodeChildDOB  =  (ZoneCodeChildDOB)zoneChildList.get(i);                             
                            pStmtZone.clearParameters();
                            pStmtZone.setString(1,zoneCodeChildDOB.getFromZipCode());
                            pStmtZone.setString(2,zoneCodeChildDOB.getToZipCode());
                            pStmtZone.setString(3,zoneCodeChildDOB.getZone());
                            rs  = pStmtZone.executeQuery();
                           Logger.info(FILE_NAME,"zoneCode    "+zoneCode);
                           if(rs.next()){
                              flag = zoneCode.equals(rs.getString(1));
                              Logger.info(FILE_NAME,"rs.getString(1)    "+rs.getString(1));
                           }
                          Logger.info(FILE_NAME,"flag   "+flag);
                           if(!flag){
                            pStmt.setString(1,zoneCode);
                            pStmt.setString(2,zoneCodeChildDOB.getFromZipCode());
                            pStmt.setString(3,zoneCodeChildDOB.getToZipCode());
                            pStmt.setString(4,zoneCodeChildDOB.getZone());
                            pStmt.setString(5,zoneCodeChildDOB.getEstimationTime());
                            pStmt.setString(6,zoneCodeChildDOB.getEstimatedDistance());
                            pStmt.setString(7,zoneCodeChildDOB.getAlphaNumaric());
                            pStmt.setString(8,new Integer(maxRow+1).toString());
                            pStmt.setString(9,"F");
                            pStmt.executeUpdate();
                            maxRow++;
                        }
                        else
                          zoneCodeMasterDOB.setRemarks(zoneCodeMasterDOB.getRemarks() + " Zone Code Detail Details Already Exists with FROM ZIPCODE"+zoneCodeChildDOB.getFromZipCode() +"AND TO ZIPCODE"+zoneCodeChildDOB.getToZipCode()+"AND ZONE="+zoneCodeChildDOB.getZone());
                        if(rs!=null)rs.close();
              }        
      }
      catch(Exception e)
      {
        e.printStackTrace();
        
      }
      finally
      {
        ConnectionUtil.closePreparedStatement(pStmt,rs);
        ConnectionUtil.closePreparedStatement(pStmtZone,rs);
      }
    }*/
    
    
    public ArrayList downloadZoneCodeMasterDetails(String locationIds,String shipmentMode,String consoleType)throws SQLException 
    {
      Connection         connection      =  null;
      //PreparedStatement  pStmt           =  null;//Commented By RajKumari on 30-10-2008 for Connection Leakages.
      String[]           tempLocationIds =  null;
      String             locationId      =  "";
      tempLocationIds    = locationIds.split(",");
      int tempLocIdsLen	=	tempLocationIds.length;
      for(int i=0;i<tempLocIdsLen;i++)
      {
        locationId  = locationId+"'"+tempLocationIds[i]+"',";
      }
       String loadMasterQuery             = "SELECT ZONE_CODE,DECODE(SHIPMENT_MODE,'1','Air','Sea')SHIPMENT_MODE,CONSOLE_TYPE,ORIGIN_LOCATION,TERMINALID,CITY ,STATE ,ZIPCODE_TYPE,INVALIDATE FROM QMS_ZONE_CODE_MASTER WHERE ZONE_CODE IN "+
                                           "(SELECT DISTINCT(MAS.ZONE_CODE) FROM QMS_ZONE_CODE_MASTER MAS,QMS_ZONE_CODE_DTL DTL WHERE DTL.ZONE_CODE = MAS.ZONE_CODE AND DTL.INVALIDATE='F') "+
                                           " AND ORIGIN_LOCATION IN ("+locationId.substring(0,(locationId.length()-1))+") AND SHIPMENT_MODE = ? AND NVL(CONSOLE_TYPE, '~') = ? ORDER BY ORIGIN_LOCATION";
       
      // String loadDtlQuery                = "SELECT ZONE_CODE,FROM_ZIPCODE ,TO_ZIPCODE ,ZONE  ,ESTIMATED_TIME  ,ESTIMATED_DISTANCE ,ALPHANUMERIC,ROWNO,INVALIDATE FROM QMS_ZONE_CODE_DTL WHERE ZONE_CODE=?  ORDER BY ZONE ";
       	 String loadDtlQuery                = "SELECT DISTINCT ZONE_CODE,FROM_ZIPCODE ,TO_ZIPCODE ,ZONE  ,ESTIMATED_TIME  ,ESTIMATED_DISTANCE ,ALPHANUMERIC,ROWNO,INVALIDATE FROM QMS_ZONE_CODE_DTL WHERE ZONE_CODE=?  ORDER BY ZONE ";
       
       PreparedStatement  pStmtMaster    = null;
       PreparedStatement  pStmtDtl       = null;
       ResultSet          rsMaster       = null;
       ResultSet          rsDtl          = null;
       ZoneCodeMasterDOB  dob            = null;
       int                count          = 0;     
       ArrayList zoneCodeList            =  null;
       ZoneCodeChildDOB   childDOB       =  null;
       //Hashtable          zoneHashTable  =  null;
       ArrayList          zoneHashTable   = null;
       //int                k              =  1;
     
     try
     {
       operationsImpl         =  new OperationsImpl();
       connection             =  operationsImpl.getConnection();
       pStmtMaster            =  connection.prepareStatement(loadMasterQuery);
       pStmtDtl               =  connection.prepareStatement(loadDtlQuery);
       //zoneHashTable          =  new Hashtable();
       zoneHashTable          = new ArrayList();
       pStmtMaster.clearParameters();
       pStmtMaster.setString(1,shipmentMode);
       if("1".equalsIgnoreCase(shipmentMode))
          pStmtMaster.setString(2,"~");
       else
          pStmtMaster.setString(2,consoleType);
       rsMaster               =  pStmtMaster.executeQuery();
       dob                    =  new ZoneCodeMasterDOB();
       zoneCodeList           =  new ArrayList();
             
       while(rsMaster.next())
       { 
         dob   =  new ZoneCodeMasterDOB();
         dob.setOriginLocation(rsMaster.getString("ORIGIN_LOCATION")!=null?rsMaster.getString("ORIGIN_LOCATION"):"");
         dob.setTerminalId(rsMaster.getString("TERMINALID")!=null?rsMaster.getString("TERMINALID"):"");
         dob.setCity(rsMaster.getString("CITY")!=null?rsMaster.getString("CITY"):"");
         dob.setState(rsMaster.getString("STATE")!=null?rsMaster.getString("STATE"):"");
         dob.setZipCode(rsMaster.getString("ZIPCODE_TYPE")!=null?rsMaster.getString("ZIPCODE_TYPE"):"");
         dob.setShipmentMode(rsMaster.getString("SHIPMENT_MODE")!=null?rsMaster.getString("SHIPMENT_MODE"):"");
         dob.setConsoleType(rsMaster.getString("CONSOLE_TYPE")!=null?rsMaster.getString("CONSOLE_TYPE"):"");
        // dob.setPort(rsMaster.getString(7));           
       
       
         pStmtDtl.clearParameters();
         pStmtDtl.setString(1,rsMaster.getString("ZONE_CODE"));
         rsDtl= pStmtDtl.executeQuery();
         zoneCodeList  =  new ArrayList();
         while(rsDtl.next())
         {
             childDOB  =    new ZoneCodeChildDOB();
             childDOB.setZoneCode(rsDtl.getString(1));
             childDOB.setFromZipCode(rsDtl.getString(2)!=null?rsDtl.getString(2):"");
             childDOB.setToZipCode(rsDtl.getString(3)!=null?rsDtl.getString(3):"");
             childDOB.setZone(rsDtl.getString(4)!=null?rsDtl.getString(4):"");
             childDOB.setEstimationTime(rsDtl.getString(5)!=null?rsDtl.getString(5):"");
             childDOB.setEstimatedDistance(rsDtl.getString(6)!=null?rsDtl.getString(6):"");
             childDOB.setAlphaNumaric(rsDtl.getString(7)!=null?rsDtl.getString(7):"");
             childDOB.setRowNo(rsDtl.getString(8)!=null?rsDtl.getString(8):"");
             zoneCodeList.add(childDOB);                   
         }
         dob.setZoneCodeList(zoneCodeList);
         //Logger.info(FILE_NAME,"k before       "+k);
         //zoneHashTable.put(new Integer(k),dob);
         zoneHashTable.add(dob);
         //Logger.info(FILE_NAME,"k after        "+k);
         //Logger.info(FILE_NAME,"object         "+zoneHashTable.get(new Integer(k)));
         //k++;
      }
              
              
      
     }
      catch(SQLException e)
      {
        e.printStackTrace();
        throw new EJBException("Error while getting data");
      }
      catch(Exception e)
      {
        e.printStackTrace();
        throw new EJBException("Error while getting data");
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pStmtMaster,rsMaster);
        ConnectionUtil.closeConnection(connection,pStmtDtl,rsDtl);
      }
      return zoneHashTable;
    }
    
  /**
   * Called from the Canada Zones Controller for Downloading the Zone Codes for the given locations
   * The Query returns Master Data with a nested cursor (Details) Containing the detail data.
   * @throws javax.ejb.EJBException
   * @return ArrayList Containing Master & Details Data
   * @param masterDOB
   */
    public ArrayList downloadCanadaZones(ZoneCodeMasterDOB masterDOB) throws EJBException
    {
      Connection         connection      =  null;
      PreparedStatement  pStmt           =  null;
      ResultSet          rs              =  null;
      ResultSet          rsDetails       =  null;
      ArrayList          list            =  new ArrayList();
      ZoneCodeMasterDOB  dob             =  null;
      ArrayList          zoneCodesList   =  null;
      ZoneCodeChildDOB   childDOB        =  null;
      
      //@@Temporarily added as fn inlist wasn't created on prodn;remove when inlist gets created
      String             locationIds      =  null;
      String[]           locationArray    =  null;
      String             tempQuery        =  "";
      String             sqlQuery         =  "";
      StringBuffer       locationIdBuffer = new StringBuffer();
      //@@
      
      /*String             sqlQuery        =  "SELECT ZONE_CODE,DECODE(SHIPMENT_MODE, '1', 'AIR', 'SEA') SHIPMENT_MODE,CONSOLE_TYPE,LOCATION_ID,MAS.TERMINALID,MAS.CITY,MAS.STATE, "+
                                            "CURSOR (SELECT ZONE_CODE, FROM_ZIPCODE, TO_ZIPCODE, ZONE, ROWNO FROM QMS_ZONE_CODE_DTL_CA WHERE ZONE_CODE = MAS.ZONE_CODE ORDER BY ZONE) DETAILS "+
                                            "FROM QMS_ZONE_CODE_MASTER_CA MAS, FS_FR_LOCATIONMASTER LM WHERE "+
                                            "LOCATION_ID IN (SELECT COLUMN_VALUE FROM TABLE(INLIST(?))) AND LM.LOCATIONID = MAS.LOCATION_ID AND LM.COUNTRYID = 'CA' "+
                                            "AND SHIPMENT_MODE = ? AND NVL(CONSOLE_TYPE, '~') = ?  ORDER BY LOCATION_ID";     */
      try
      {
        //@@Temporarily added as fn inlist wasn't created on prodn;remove when inlist gets created
        locationArray = masterDOB.getOriginLocation().split("~");
        locationIds   = masterDOB.getOriginLocation().replaceAll("~",",");
        
        if(locationArray!=null && locationArray.length>0)
        {
        	int locArrLen	=	locationArray.length;
          for(int i=0;i<locArrLen;i++)
          {
              if(i==(locationArray.length-1))
                  locationIdBuffer.append("?");
              else
                  locationIdBuffer.append("?,");
          }
          tempQuery     = "AND LOCATION_ID IN ( "+locationIdBuffer.toString() +") ";
        }
        
        
        
        sqlQuery    = "SELECT ZONE_CODE,DECODE(SHIPMENT_MODE, '1', 'AIR', 'SEA') SHIPMENT_MODE,CONSOLE_TYPE,LOCATION_ID,MAS.TERMINALID,MAS.CITY,MAS.STATE, "+
                      "CURSOR (SELECT ZONE_CODE, FROM_ZIPCODE, TO_ZIPCODE, ZONE, ROWNO FROM QMS_ZONE_CODE_DTL_CA WHERE ZONE_CODE = MAS.ZONE_CODE ORDER BY ZONE) DETAILS "+
                      "FROM QMS_ZONE_CODE_MASTER_CA MAS, FS_FR_LOCATIONMASTER LM WHERE "+
                      "LM.LOCATIONID = MAS.LOCATION_ID AND LM.COUNTRYID = 'CA' "+
                      " AND SHIPMENT_MODE = ? AND NVL(CONSOLE_TYPE, '~') = ? "+
                      tempQuery+
                      //"LOCATION_ID IN (SELECT COLUMN_VALUE FROM TABLE(INLIST(?))) 
                     " ORDER BY LOCATION_ID";   
        //@@
        
        connection    =   getConnection();
        pStmt         =   connection.prepareStatement(sqlQuery);
        //pStmt.setString(1,masterDOB.getOriginLocation());//@@Contains list of Locations seperated by ~(tilde) symbol
        pStmt.setString(1,masterDOB.getShipmentMode());
        if("1".equalsIgnoreCase(masterDOB.getShipmentMode()))
            pStmt.setString(2,"~");
        else
            pStmt.setString(2,masterDOB.getConsoleType());
        
        if(locationArray!=null && locationArray.length>0)
        {
        	int locArrLen	=	locationArray.length;
          for(int k=0;k<locArrLen;k++)
              pStmt.setString(k+3,locationArray[k]);
        }
        
        rs = pStmt.executeQuery();
        
        while(rs.next())
        {
          dob   =   new ZoneCodeMasterDOB();
          dob.setShipmentMode(rs.getString("SHIPMENT_MODE"));
          dob.setConsoleType(rs.getString("CONSOLE_TYPE"));
          dob.setOriginLocation(rs.getString("LOCATION_ID"));
          dob.setTerminalId(rs.getString("TERMINALID"));
          dob.setCity(rs.getString("CITY"));
          dob.setState(rs.getString("STATE"));
          
          rsDetails     =   (ResultSet)rs.getObject("DETAILS");
          zoneCodesList =   new ArrayList();
          while (rsDetails.next())
          {
            childDOB    =   new ZoneCodeChildDOB();
            childDOB.setFromZipCode(rsDetails.getString("FROM_ZIPCODE"));
            childDOB.setToZipCode(rsDetails.getString("TO_ZIPCODE"));
            childDOB.setZone(rsDetails.getString("ZONE"));
            childDOB.setRowNo(rsDetails.getString("ROWNO"));
            zoneCodesList.add(childDOB);
          }
          dob.setZoneCodeList(zoneCodesList);
          list.add(dob);
          
          if(rsDetails!=null)
              rsDetails.close();
        }
      }
      catch (SQLException sql)
      {
        sql.printStackTrace();
        logger.error("SQL Exception in downloadCanadaZones ::"+sql +"Error Code: "+sql.getErrorCode());
        throw new EJBException(sql);
        
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error("Exception in downloadCanadaZones :"+e);
        throw new EJBException(e);
      }
      finally
      {
        try
        {
          if(rsDetails!=null)
            rsDetails.close();
          if(rs!=null)
            rs.close();
          if(pStmt!=null)
            pStmt.close();
          if(connection!=null)
            connection.close();
        }
        catch(SQLException sql)
        {
          sql.printStackTrace();
          logger.error("Error While Closing the Resources.");
        }
      }
      return list;
    }
    
    public ArrayList getMultipleLocationIds(String controlStation)
    {
            Connection     connection       = null;
            Statement      stmt             = null;
            ResultSet      rs               = null;
            ArrayList      list              = new ArrayList();
      try
      {
            connection = operationsImpl.getConnection();
            stmt = connection.createStatement();
            String sqlQuery = "SELECT DISTINCT ORIGIN_LOCATION FROM  QMS_ZONE_CODE_MASTER  WHERE CONTROL_STATION='"+controlStation+"' ORDER BY ORIGIN_LOCATION  ";
            rs = stmt.executeQuery(sqlQuery);
            while(rs.next())
            {
               list.add(rs.getString(1));
            }
      }
      catch(Exception e)
      {
        e.printStackTrace();
        return null;
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,stmt,rs);
      }
      return list;
    }
    public ArrayList getCustAddresses(String customerId,String addrType,String operation) 
    {
      Connection connection = null;
      Statement stmt = null;
      ResultSet resultset = null;
      ArrayList custAddress = null;
      try 
      {
        //Logger.info(FILE_NAME,"Entered into PRQ Session Bean getCustAddress() Method");
        String sqlQuery = null;
        //Logger.info(FILE_NAME,"addrTypeaddrType::"+addrType);
        if("".equals(addrType))
        {
          //Logger.info(FILE_NAME,"addrTypeaddrType222::"+addrType);
          sqlQuery = " SELECT ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,COUNTRYNAME,ZIPCODE,(SELECT OPERATIONS_EMAILID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"+customerId+"') OPERATIONS_EMAILID "
          + " FROM FS_ADDRESS FSA,FS_COUNTRYMASTER FSC "
          + " WHERE FSA.COUNTRYID = FSC.COUNTRYID AND "
          + " ADDRESSID IN (SELECT CUSTOMERADDRESSID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"
          + customerId
          + "') ";
        }
        else
        {
        sqlQuery = " SELECT ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,COUNTRYNAME,ZIPCODE,(SELECT OPERATIONS_EMAILID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"+customerId+"') OPERATIONS_EMAILID "
          + " FROM FS_ADDRESS FSA,FS_COUNTRYMASTER FSC "
          + " WHERE FSA.COUNTRYID = FSC.COUNTRYID AND "
          + " ADDRESSID IN (SELECT CUSTOMERADDRESSID FROM FS_FR_CUSTOMER_ADDRESS WHERE CUSTOMERID='"
          + customerId
          + "' AND ADDRESS_TYPE='"
          + addrType
          + "') ORDER BY ADDRESSID ";
        }
        //Logger.info(FILE_NAME,"getCustAddress() Method SQLQUERY :"+ sqlQuery);
        connection = this.getConnection();
        stmt = connection.createStatement();
        resultset = stmt.executeQuery(sqlQuery);
  
        String strTemp = null;
        final int i = 0;
        custAddress = new ArrayList();
        while (resultset.next()) {
          custAddress.add(resultset.getString("ADDRESSID"));
          custAddress.add(resultset.getString("ADDRESSLINE1"));
  
          strTemp = resultset.getString("ADDRESSLINE2");
          if ((strTemp == null) || strTemp.equals("null")) {
            strTemp = "";
          }
          custAddress.add(strTemp);
          
          strTemp = resultset.getString("ADDRESSLINE3");
          if ((strTemp == null) || strTemp.equals("null")) {
            strTemp = "";
          }
          custAddress.add(strTemp);
  
          strTemp = resultset.getString("CITY");
          if ((strTemp == null) || strTemp.equals("null")) {
            strTemp = "";
          }
          custAddress.add(strTemp);
  
          strTemp = resultset.getString("STATE");
          if ((strTemp == null) || strTemp.equals("null")) {
            strTemp = "";
          }
          custAddress.add(strTemp);
  
          strTemp = resultset.getString("COUNTRYNAME");
          if ((strTemp == null) || strTemp.equals("null")) {
            strTemp = "";
          }
          custAddress.add(strTemp);
  
          strTemp = resultset.getString("ZIPCODE");
          if ((strTemp == null) || strTemp.equals("null")) {
            strTemp = "";
          }
          custAddress.add(strTemp);
          
          strTemp = resultset.getString("OPERATIONS_EMAILID");
          if ((strTemp == null) || strTemp.equals("null")) {
            strTemp = "";
          }
          custAddress.add(strTemp);
        }
        //Logger.info(FILE_NAME,"Leaving from getCustAddress() Method");
  
      } catch (Exception e) {
        //Logger.error(FILE_NAME, "getCustAddress()   :" + e.toString());
        logger.error(FILE_NAME+ "getCustAddress()   :" + e.toString());
      } finally {
        ConnectionUtil.closeConnection(connection, stmt, resultset);
      }
      return custAddress;
	}

}