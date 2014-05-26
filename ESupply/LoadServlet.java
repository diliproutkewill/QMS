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
 * File			: LoadServlet.java
 * sub-module	: AccessControl
 * module		: esupply
 *
 * This is used to display login form 
 * 
 * @author	Ramakumar 
 * @date	12-06-2002
 * 
 * modified history
 * Modifed By		Modified Date		Reason
 * Amit Parekh		16/10/2002			The logic has been changed a bit to enable better obfustcation.
 *										core functionality remains the same.
 * Amit Parekh		21/10/2002			The global datasource name 'jdbc/DB' or otherwise is now
 *										maintained globally in FoursoftWebConfig.java
 *
 **/
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.foursoft.esupply.common.java.FoursoftWebConfig;

public final class LoadServlet  extends HttpServlet {

	private static final String		FILENAME	=	"LoadServlet.java";
	private static Hashtable	htImageData	=	new Hashtable(5);
  private static Logger logger = null;
  		
	public void init(ServletConfig config) throws ServletException {
    logger  = Logger.getLogger(LoadServlet.class);
		super.init( config );
		loadData();
	}
	
	private static void loadData() {
	
		InitialContext	context	=	null;
		DataSource		ds		=	null;
		
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;	

	    try {
			if(context==null) {
		    	context = new InitialContext();
			}
			if(ds==null) {
		    	ds = (DataSource) context.lookup( FoursoftWebConfig.DATA_SOURCE );
			}
			
		} catch(NamingException nameExc) {
			//Logger.error(FILENAME,"LookUp of Images failed",nameExc);
      logger.error(FILENAME+"LookUp of Images failed"+nameExc);
        }
		
		try {
			conn = ds.getConnection();
			
			if(conn!=null) {

				int imageCount =0;
				st = conn.createStatement();
				rs = st.executeQuery( "SELECT NAME, RAWDATA FROM FS_FR_RAWDATA" );
				
				while(rs.next()) {
					byte[] imageBytes = new byte[6000];
					
					String	imageName	=	rs.getString(1);
					imageBytes			=	rs.getBytes(2);
/*
					System.out.println("");
					System.out.println("IMAGE '"+imageName+"' FROM DB:\n");
					for(int x=0;  x < 40; x++) {
						System.out.print(imageBytes[location[x]-1]+",");
					}
					System.out.println("");
*/					
					htImageData.put( imageName, imageBytes );
					
	            } // end while

			} // end if
			
		} catch(SQLException sqe) {
			//Logger.error(LoadServlet.FILENAME,"Exception in loadData() ",sqe.toString());
      logger.error(LoadServlet.FILENAME+"Exception in loadData() "+sqe.toString());
		} catch(Exception excep) {
			//Logger.error(LoadServlet.FILENAME,"Exception in loadData() ",excep.toString());
      logger.error(LoadServlet.FILENAME+"Exception in loadData() "+excep.toString());
		} finally {
            try {
                if(rs != null)
                    rs.close();
                if(st != null)
                    st.close();
                if(conn != null)
                    conn.close();
            } catch(Exception exc) {
				//Logger.error(LoadServlet.FILENAME,"Exception in connection close ",exc.toString());
        logger.error(LoadServlet.FILENAME+"Exception in connection close "+exc.toString());
            }                                
        } 
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{

		try
		{
			ByteArrayOutputStream	byteStream	= new ByteArrayOutputStream();
			ServletOutputStream		out			= res.getOutputStream();

			String imageName = req.getParameter("name");
			//Logger.info(FILENAME, "Image Name = "+imageName);

			if(imageName != null) {
				
				byte[]	imageBytes = (byte[]) htImageData.get( imageName );
				
				if(imageBytes!=null && imageBytes.length > 0){
					for(int n=0; n < imageBytes.length; n++) {
						byteStream.write( imageBytes[n] );
					}
					byteStream.writeTo( out );
					out.flush();
				} else {
					// try one more time			
					//System.out.println("IN ELSE BLOCK OF DOGET OF LOADSERVLET");					
					loadData();
					imageBytes = (byte[]) htImageData.get( imageName );

					for(int n=0; n < imageBytes.length; n++) {
						byteStream.write( imageBytes[n] );
					}
					byteStream.writeTo( out );
					out.flush();
					
				}
			}
		} catch(Exception exc) {
			//Logger.error(FILENAME,"Exception in loadIm ",exc.toString());
      logger.error(FILENAME+"Exception in loadIm "+exc.toString());
        }
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
	{
		doGet(req,res);
	}
	
}