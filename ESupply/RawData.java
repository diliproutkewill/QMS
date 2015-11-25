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
 * File			: RawData.java
 * module 		: esupply
 * 
 * This is a Bean utility class which loads images from Database
 * 
 * @author	Madhu V, 
 * @date	12-06-2002
 * 
 * modified history
 * Modifed By		Modified Date		Reason
 * Amit Parekh		16/10/2002			The logic has been changed a bit to enable better obfustcation.
 *										core functionality remains the same.
 * Amit Parekh		21/10/2002			The global datasource name 'jdbc/DB' or otherwise is now
 *										maintained globally in FoursoftWebConfig.java
 * Amit Parekh		25/04/2003			The image 4slogo.jpg changed to show the name "Four Soft Limited". Hence the byte signature was changed.
 */
 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.NamingException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.sql.Timestamp;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.java.FoursoftWebConfig;
import com.foursoft.esupply.common.util.ConnectionUtil;
 
public final class RawData 
{
	private static final String FILENAME = "RawData.java";
	private static Logger logger = null;
	

	public RawData()
	{
    logger  = Logger.getLogger(RawData.class);
		for(int i=0;i<3;i++) {
			has.put(names[i],data[i]);
		}
				
        fsFlage = false;
        eSupplyFlage = false;
        indexFlage = false;
	}
	
	public void writeImagesToDB(String id,String imageName)
	{
        Connection conn = null;
        PreparedStatement pstmt = null;
        String SQLSTRING = "INSERT INTO FS_FR_RAWDATA VALUES(?,?)";
        try
		{
	    	conn = getConnection();
            FileInputStream fis = new FileInputStream(imageName);            
			pstmt = conn.prepareStatement(SQLSTRING);
			pstmt.setString(1,id);
			pstmt.setBinaryStream(2,fis,fis.available());
			pstmt.executeUpdate();
		}
        catch(FileNotFoundException fnfe)
        {
			//Logger.error(FILENAME,"File not found ",fnfe);
      logger.error(FILENAME+"File not found "+fnfe);
        }
		catch(Exception exc)
		{
			//Logger.error(FILENAME,"Exception in writeImagesToDB ",exc);
      logger.error(FILENAME+"Exception in writeImagesToDB "+exc);
		}
        finally
        {
            try
            {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                   conn.close();
            }
            catch(Exception exc)
            {
				//Logger.error(FILENAME,"Exception in connection close ",exc);
        logger.error(FILENAME+"Exception in connection close "+exc);
            }
        }
	}
    
    private Connection getConnection() throws SQLException
	{
        try {
			if(context==null) {
		    	context = new InitialContext();
			}
			if(ds==null) {
		    	ds = (DataSource) context.lookup( FoursoftWebConfig.DATA_SOURCE );
			}
        } catch(NamingException nameExc) {
			//Logger.error(FILENAME,"Look Up failed",nameExc);
      logger.error(FILENAME+"Look Up failed"+nameExc);
        }
		
		return ds.getConnection();
	}
	
	public String get4slogoName()
	{
        fsFlage = true;
		return _4sLogoImage;
	}
		
	public String getEsupplyLogoName()
	{
        eSupplyFlage = true;
		return _4sEsupplyLogo;
	}
	
	public String getIndexLogoName()
	{
        indexFlage = true;
		return _4sIndexCaption;
	}
    
    /*
	 *  This is for image corruption checking
	 *	
	 */

	private static java.util.Date	dtImagesLastCheckedOn	=	null;


    public boolean isImageCorrecpted()
	{
		/*	This method is called from the ESupplyLogin.jsp file to check if the
			Image file for the 4S logo has been tampered with or not. But, this
			method should actually check in the database only once a day even if
			multiple calls are made to this method. The information retrived from
			the DB in the first call of the day should be cached in static variables
			of this same class.
			
			For each call to this method we will now check if the date has changed
			or not.
		*/
		
		boolean imageCorrupted = false;
		PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rs = null;
        byte dbBytes[][] = new byte[3][5000];
        String imageNames[] = new String[3];
		
		boolean	firstTime = false;
        
        int imageCount =0;
		
        try
        {
			
			if(dtImagesLastCheckedOn==null) {
				dtImagesLastCheckedOn = new java.util.Date();
				firstTime = true;
				//System.out.println("dtImagesLastCheckedOn is SET in first call = '"+dtImagesLastCheckedOn+"'");
			}
			
			//System.out.println("timeCrossedTotally = "+timeCrossedTotally);
			
			if(timeCrossedTotally) {
				//System.out.println("  now returning true");
				return true;
			}
			
			//System.out.println("Checking for image corruption in subsequent requests");
			//System.out.println("dtImagesLastCheckedOn = '"+dtImagesLastCheckedOn+"'");
			
			long msImgLastCheckOn = dtImagesLastCheckedOn.getTime();
			
			java.util.Date	dtCurrentDate	=	 new java.util.Date();
			long msCurrentDate	=	dtCurrentDate.getTime();
			
			//System.out.println("        dtCurrentDate = '"+dtCurrentDate+"'");
			
			long milliSecsPassedSinceLastCheck = msCurrentDate - msImgLastCheckOn;
			
			//System.out.println("Days passed since last check = "+(milliSecsPassedSinceLastCheck/86400000));
			// Check here if at least a day has passed since the last image check
			
			if(milliSecsPassedSinceLastCheck >= 86400000 || firstTime==true) {
				//System.out.println("More than a day has passed. Making DB call");

				conn = getConnection();
				pstmt = conn.prepareStatement( "SELECT * FROM FS_FR_RAWDATA" );

				rs = pstmt.executeQuery();

				while (rs.next())
				{
					imageNames[imageCount]	= rs.getString(1);
					// Get image bytes from database
					dbBytes[imageCount] 	= rs.getBytes(2);

					/* STAGING code to get signature if images are altered
						System.out.println();System.out.println();
						System.out.println("Bytes Signature Starts: for Image : '"+imageNames[imageCount]+"' size = "+dbBytes[imageCount].length);
						System.out.println();
						for (int i=0; i < 40; i++){
							System.out.print( dbBytes[imageCount][location[i]-1] + ",");
						}

						System.out.println();
						System.out.println("Bytes Signature Ends");
						System.out.println();System.out.println();
					*/
					
					// Get image bytes from hashtable
					byte htBytes[] = (byte[]) has.get(imageNames[imageCount]);

					// Compare the image bytes in database to image bytes in the hashtable
					
					if(compare(htBytes, dbBytes[imageCount]) == false)
					{
						imageCorrupted = true;
						break;
					}			
					imageCount++;
				}
				//System.out.println("Updating last checked date to '"+dtCurrentDate+"'");
				dtImagesLastCheckedOn	=	dtCurrentDate;
				//System.out.println(" imageCorrupted = "+imageCorrupted);
				if(imageCorrupted) {
					// If image is corrupted, update corruption date in database
					updateCorrDate();
					//Logger.warning(FILENAME,"N.C.A.DB : ", ""+ new java.util.Date());
          logger.warn(FILENAME+"N.C.A.DB : "+ ""+ new java.util.Date());
					imageCorrupted = isTimeCrossed();
				} else {
					//Logger.info(FILENAME,"V.A.I. : ", ""+ new java.util.Date());
          logger.info(FILENAME+"V.A.I. : "+ ""+ new java.util.Date());
				}
			}

        }
		catch(SQLException se)
		{
			boolean rawdataTableExist = checkError( se.toString(), conn);

			if(!rawdataTableExist) {
				//Logger.warning(FILENAME,"N.C.A.DB : ", ""+ new java.util.Date());
        logger.warn(FILENAME+"N.C.A.DB : "+ ""+ new java.util.Date());
				updateCorrDate();
				imageCorrupted = isTimeCrossed();
			}
			//Logger.error(FILENAME,"SQLException in method isImCr",se.toString());	
      logger.error(FILENAME+"SQLException in method isImCr"+se.toString());	
		}
        catch(Exception exc)
        {
   				//Logger.error(FILENAME,"Exception in isImCr",exc);
          logger.error(FILENAME+"Exception in isImCr"+exc);
        }
        finally
        {
            try
            {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            }catch(Exception exc){}                                
        }		
		return imageCorrupted;
	}

    
    public boolean checkError(String error, Connection conn)
	{
		boolean	rawdataTableExists	=	false;

		//System.out.println();
		
		//System.out.println("STARTED Checking DB MetaData");
		
		DatabaseMetaData	dbMetaData	=	null;
		ResultSet			rsTableData	=	null;
		String[]			saTableType	=	{"TABLE"};
		
		try {
			if(conn!=null) {
				//System.out.println("Conn is OK");
				
				dbMetaData	=	conn.getMetaData();
				
				rsTableData	=	dbMetaData.getTables(null, null, "FS_FR_RAWDATA", saTableType );
				
				int i=0;
				
				while(rsTableData!=null && rsTableData.next()) {
					//String	tableName	=	rsTableData.getString(3);
					//System.out.println("TABLE '"+tableName+"' EXISTS IN THE DATABASE ");
					rawdataTableExists = true;
				}
			} else {
				//System.out.println("Conn in NULL");
			}
      //Added By RajKumari on 30-10-2008 for Connection Leakages.
			 if(rsTableData!=null)
      {
        rsTableData.close();
        rsTableData=null;
      }
		} catch(SQLException sqe) {
			//Logger.error(FILENAME,"SQL Excpetion while checking data", sqe );
      logger.error(FILENAME+"SQL Excpetion while checking data"+ sqe );
		}finally{// Added by Dilip for PMD Correction on 23/09/2015
			try{
				if(rsTableData!=null){
					rsTableData.close();
					rsTableData=null;
				}
			}catch(Exception e){}	
		}
    
  
		//System.out.println("ENDED Checking DB MetaData");
		//System.out.println();

				
		/* OLD CODE KEPT FOR BACKWARD COMPATIBILITY
		int i=0;
		i = error.indexOf("ORA-00942"); // check for sql exception code ORA-00942 table does not exist. 
		if(i == -1)
			return false;
		else
			return true;
		*/
		return rawdataTableExists;
	}
    
    private	static boolean timeCrossedTotally = false;
	
    private boolean isTimeCrossed()
    {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs   = null;	
		Timestamp corrDate = null;
		boolean timeCrossed = false;
		
		try
		{
			con = getConnection();
			stmt = con.createStatement();
			
			rs = stmt.executeQuery(	"SELECT DOCDATE FROM FS_USERLOG WHERE DOCTYPE = 'IMG' AND DOCREFNO = 'C' ");

			//rs = stmt.executeQuery("SELECT CORRDATE FROM FS_FR_RAWDATA WHERE NAME='index_caption.jpg'");
			if(rs.next()) 
				corrDate = rs.getTimestamp(1);
				
			long x = 30*86400;
			long y = new java.util.Date().getTime();
			long z = 0l;
			if(corrDate!=null) 
				 z = corrDate.getTime();
							
			long diff = (y - z)/1000;
            if (diff > x) {
				timeCrossed = true;
				timeCrossedTotally = true;
				//Logger.warning(FILENAME,"Updated T.C.");
        logger.warn(FILENAME+"Updated T.C.");
            }	
		}
		catch(Exception e)
		{
			//Logger.error(FILENAME,"Exception in isTC",e);
      logger.error(FILENAME+"Exception in isTC"+e);
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
				if(stmt!=null)
					stmt.close();
				if(con!=null)
					con.close();		
			}
			catch(SQLException se)
			{
				//Logger.error(FILENAME,"Exception in finally of isTC",se);
        logger.error(FILENAME+"Exception in finally of isTC"+se);
			}			
		}	
		return timeCrossed;
    }

    private static boolean compare(byte htBytes[],byte dbBytes[])
	{
		boolean f = true;
		
		for (int i=0; i < 40; i++)
		{
			if(htBytes[i]	!= dbBytes[location[i]-1])
			{
				f = false;
				break;
			}
		}
		
		return f;
	}
	
    /*
	 * End for image checking.
	 */	
    
    private void updateCorrDate()
    {
		
		boolean	corrEntryDone	=	false;
		Connection con			= null;
		Statement stmt			= null;
		PreparedStatement pstmt	= null;
		ResultSet rs			= null;	
		Timestamp corrDate		= null;
		
		try
		{
			con = getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(	"SELECT DOCDATE FROM FS_USERLOG WHERE "+
									"DOCTYPE = 'IMG' AND DOCREFNO = 'C' ");
			
			if(rs.next()) {
				corrDate = rs.getTimestamp(1);
				corrEntryDone = true;
			}
							
			if(!corrEntryDone) {
				//PreparedStatement pstmt = con.prepareStatement("UPDATE FS_FR_RAWDATA SET CORRDATE=? WHERE NAME='index_caption.jpg' AND CORRDATE IS NULL");
				pstmt = con.prepareStatement(	"INSERT INTO FS_USERLOG "+
												"(DOCTYPE, DOCREFNO, DOCDATE) "+
												"VALUES (?,?,?) ");
				
				corrDate = new Timestamp( new java.util.Date().getTime());
					//System.out.println("CORRDATE to be inserted in FS_USERLOG : '"+corrDate+"'");

				pstmt.setString(	1, "IMG");
				pstmt.setString(	2, "C");
				pstmt.setTimestamp( 3, corrDate);
				
				int cnt = pstmt.executeUpdate();
				
				//System.out.println("Corrdate entry inserted = "+cnt);				
			}
		} catch(Exception e) {
				//Logger.error(FILENAME,"Exception in finally of updateCD",e);
        logger.error(FILENAME+"Exception in finally of updateCD"+e);
		} finally {
			try {
				if(rs!=null)
					rs.close();
				if(stmt!=null)
					stmt.close();
        //Added By RajKumari on 30-10-2008 for Connection Leakages.
        if(pstmt != null)
        pstmt.close();
				if(con!=null)
					con.close();		
			} catch(SQLException se) {
				//Logger.error(FILENAME,"Exception in finally of updateCD",se);
        logger.error(FILENAME+"Exception in finally of updateCD"+se);
			}			
		}
		
    }
    
    public boolean isAllTrue()
    {
        return fsFlage&&eSupplyFlage&&indexFlage;
    }
	
	public static boolean isLicenseValid()
	{
		return FourSoftExpiry.isLicenseValid();
	}
	
	private static final String _4sLogoImage	=	"4slogo.jpg";	
	private static final String _4sEsupplyLogo	=	"eSupply_product_logo.gif";
	private static final String _4sIndexCaption	=	"index_caption.jpg";
	
	Hashtable has = new Hashtable();

	private static final int location[]  = {1,2,4,8,16,32,64,128,256,512,3,6,9,12,15,18,21,24,27,30,5,10,15,20,25,30,35,40,45,50,1,8,27,64,125,216,343,512,729,1000};

	private static final byte data[][] = {//{-1,-40,-32,70, 44,3,13, 20,35, 73,-1,16,73,1,1, 44,  -1,  67,  2,2, 0,70,1,0, 0,2,  3,  5,  4, 6, -1, 70,  2,  13,20, 0,-89,  73,-103, -10},	//---OLD 4slogo.jpg
										  //{71, 73, 56, 1,-116,34,-116,-20,-50,-34,70,97,72,0,-123, 97,  76,  65, 61,89,57, 0,-123,77, 98,89, 28, 74,-94, -66, 71, 1, 61,-116, -99,115,-52,-34,  65,-110}, //---NEW 4slogo.jpg "FOUR SOFT PVT. LTD>"
											{-1,-40,-32,70,96,73,1,1,1,-31,-1,16,73,1,0,96,-1,22,105,0,0,70,0,0,69,0,8,0,-1,1,-1,70,105,1,1,0,121,-31,-1,40}, // latest 4slogo.jpg "FOUR SOFT LIMITED"
											{71, 73, 56, 0,   0, 0,   0,-64, 64,-64,70,97,28,0,   0,  0,-128,-128,  0, 0,57, 0,   0, 0,  0, 0,-64,-64, 32,-128, 71, 0,  0,   0, -96,  0, 64,-64, -96, 110},	//----eSupply_product_logo.gif
											{-1,-40,-32,70, 100, 0,  11, 15,  1, 18,-1,16,73,1,   0,100,  -1,  17, 99, 0, 0,70,   0, 0, 68, 0,  0, -1,100, 100, -1,70, 99,  11,   1,  3,  0, 18, -98,  34}	//---index_caption.jpg
										};
	
    private static final String names[] = {_4sLogoImage,_4sEsupplyLogo,_4sIndexCaption};
	
    private boolean fsFlage = false;
    private boolean eSupplyFlage = false;
    private boolean indexFlage = false;
	
	private InitialContext context = null;
	private DataSource ds = null;
   // private Hashtable hTable = null;
    
}