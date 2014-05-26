
/**
 * 
 * Copyright (c) 2000-2006 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * eConnect   : v3.2      
 * 
 * FileName   : JobSchedulerProcess.java
 * 
 */

/**
 * File       : JobSchedulerProcess.java
 *
 * Sub-Module : All
 * Module     : eTrans
 * 
 * Purpose of File: Covers complete operation for Document Type, start point for trigger job
 * 
 * @Author K. C SUBRAHMANYAM
 * @Date 04/03/2010
 */


import org.quartz.JobExecutionContext;
import org.quartz.StatefulJob;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.etrans.setup.currency.ejb.sls.CurrencySession;
import com.foursoft.etrans.setup.currency.ejb.sls.CurrencySessionHome;
import java.util.Date;
import java.util.HashMap;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.InstanceNotFoundException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.ArrayList;
import com.foursoft.esupply.common.util.Logger;

import com.foursoft.etrans.setup.currency.dob.CurrencyConversionDOB;
public class JobSchedulerProcess implements StatefulJob
{
	private static final  String FILE_NAME     = "JobSchedulerProcess"; 
	private String triggerId = null;
	private String FILE_SEPARATOR = System.getProperty("file.separator");
	//private FileUtil fu = new FileUtil();
	
	private final String msgProcessPath  = "FTPcopiedFiles";
	/**
	   * This method will execute when the trigger fires
	   * @param context
	*/
	public void execute( JobExecutionContext context )
	{    
	    System.out.println("In execute().......");  
		long startTime = System.currentTimeMillis();    // Start time of process
	      Logger.info("Job Name",context.getJobDetail().getName());
	      Logger.info("Trigger Name",context.getTrigger().getName());
	      Logger.info("Trigger Fire Time",context.getFireTime().toString());
	      triggerId = context.getTrigger().getName();
	      currencyProcess();
	      Logger.info("Process End ", "Trigger Name: " + context.getTrigger().getName() + ", Job Name: " + context.getJobDetail().getName() + ", Time taken(millisec): " + (System.currentTimeMillis() - startTime));
	}
	
	//public void handleNotification(Notification notif, Object handback)
	public void currencyProcess()
	 {
	    //System.out.println(">>> "+(new Date())+" timer handleNotification="+notif+", handback="+handback);
		 Logger.info(FILE_NAME,">>> currencyProcess called.");
	    CurrencySessionHome home   =  null;
	    CurrencySession     remote =  null;
	    ArrayList           currDOBs= null;
	    boolean             checkFlag = false;    
	    try
	    {     
	      home        = (CurrencySessionHome)LookUpBean.getEJBHome("CurrencySession");
	      remote      = home.create();
	      checkFlag   = remote.checkCurrencyUpdation();
	      Logger.info(FILE_NAME,">>> checkFlag called."+checkFlag);
	      if(!checkFlag)
	      {
	        currDOBs    = getCurrencyDOBs();
	        if(currDOBs!=null)
	         remote.insertCurrencyMasterDetails(currDOBs);
	      }
	    }    
	    catch(Exception ex)
			{
	      ex.printStackTrace();
				Logger.error(FILE_NAME, " [error in currencyProcess()] -> "+ex.toString());
			} 
	    Logger.info(FILE_NAME,">>> currencyProcess ended.");
	 }
	 private ArrayList getCurrencyDOBs() throws Exception
	 {
		 Logger.info(FILE_NAME,">>> getCurrencyDOBs called.");
	    ArrayList currArr = new ArrayList();
	    HashMap urlProxyDatails = null;
	    urlProxyDatails = getUrlProxyDatails();
	    
	    String currencyURL  = null;    
	    String proxyAddress = null;
	    int    proxyPort    = -1;
	    boolean isFileDownloaded = false;
	    
	    if(urlProxyDatails!=null)
	    {
	      currencyURL  = (String) urlProxyDatails.get("CURRENCYURL");
	      proxyAddress = (String) urlProxyDatails.get("PROXYADDRESS");
	      proxyPort    = Integer.parseInt((String)urlProxyDatails.get("PROXYPORT"));
	      
	      isFileDownloaded = doURLRequest(currencyURL, proxyAddress, proxyPort);
	    }
	  
	    
	    //String expr = "AFA_ALL_AMD_AOA_AON_ARS_ATS_AUD_AWG_AZM_BAM_BBD_BDT_BEF_BGL_BHD_BIF_BMD_BND_BOB_BRL_BSD_BTN_BWP_BYB_BZD_CAD_CDF_CHF_CLP_CNY_COP_CRC_CUP_CYP_ZK_DEH_DEM_DJF_DKK_DZD_EEK_EGP_ERN_ESP_ETB_EUR_FIM_FJD_FKP_FRF_GBP_GEL_GHC_GIP_GMD_GNS_GRD_GTQ_GYD_HKD_HRK_HTG_IDR_IEP_ILS_INR_IQD_IRR_ISK_JMD_JOD_JPY_KES_KGS_KHR_KPW_KRW_KWD_KZT_LAK_LBP_LKR_LRD_LSL_LTL_LVL_LYD_MAD_MGF_MKD_MMK_MNT_MOP_MRO_MTL_MUR_MVR_MWK_MXN_MYR_MZM_NAD_NGN_NIC_NLG_NOK_NPR_NZD_OMR_PAB_PEN_PGK_PHP_PKR_"+
	    //              "PLN_PTE_PYG_QAR_ROL_RUB_RWF_SAR_SCR_SDP_SEK_SGD_SHP_SIT_SKK_SLL_SOS_STD_SVC_SYP_SZL_THB_TMM_TND_TOP_TRL_TWD_TZS_UAH_UGX_USD_UYU_UZS_VEB_VND_VUV_WST_XCD_XOF_YER_YUM_ZAR_ZMK_ZWD";
	    /*Commented by Sanjay on 14 Feb 2006 as per the Connection through Proxy Host requirement
	      
	    String expr = "AUD_CAD_CHF_CNY_DKK_EUR_FIM_FJD_GBP_GBP_GBP_HKD_IDR_INR_JPY_KHR_KPW_KRW_LAK_LKR_MMK_MYR_NOK_NZD_PGK_PHP_PKR_SEK_SGD_THB_TWD_USD_VND_ZAR";
	    URL u = new URL("http://www.oanda.com/cgi-bin/fxml/fxml?fxmlrequest=%3cconvert><client_id>Quoteshop</client_id><expr>"+expr+"</expr><exch>"+expr+"</exch></convert>");
	    URLConnection uc=   u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
			String str;
	    BufferedWriter out = new BufferedWriter(new FileWriter("fxml.xml"));
	    while ((str = in.readLine()) != null) {          
	    out.write(str);
			}
			out.close();
			in.close();*/
	    Logger.info(FILE_NAME,">>> isFileDownloaded "+isFileDownloaded);
	    if(isFileDownloaded)
	    {
	    	Logger.info(FILE_NAME,">>> isFileDownloaded called.");
	    	
	      File docFile = new File("fxml.xml");	
	      CurrencyConversionDOB currDob = null;
	      Document doc = null;
	      try {
	          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	          DocumentBuilder db = dbf.newDocumentBuilder();
	          doc = db.parse(docFile);
	      } catch (java.io.IOException e) {
	         // System.out.println("Can't find the file");
	          Logger.info(FILE_NAME,">>> Can't find the file.");
	          Logger.error(FILE_NAME, " [error in getCurrencyDOBs()] -> "+e.toString());
	      } catch (Exception e) {
	        //  System.out.print("Problem parsing the file.");
	          Logger.info(FILE_NAME,">>> Problem parsing the file.");
	          Logger.error(FILE_NAME, " [error in getCurrencyDOBs()] -> "+e.toString());
	      }
	      Element root = doc.getDocumentElement();						 
	      NodeList children  = root.getChildNodes();
	      for (Node child = root.getFirstChild();child != null;child = child.getNextSibling())
	      {            
	        if(child.getNodeName().equalsIgnoreCase("EXPR"))
	           currDob = new CurrencyConversionDOB();			
	  
	        if (child.getNodeType() == child.ELEMENT_NODE)
	        {
	         if(child.getNodeName().equalsIgnoreCase("CONVERSION"))
	          {				
	          NodeList nls = child.getChildNodes();							
	          for (int i=0;i<nls.getLength();i++)
	           {
	             Node nd = nls.item(i);
	             if(nd.getNodeType() == child.ELEMENT_NODE)
	             {
	              if(null!=nd.getFirstChild())
	               {
	                 if(nd.getNodeName().equalsIgnoreCase("ASK"))
	                 {
	                  currDob.setExchangeSell(nd.getFirstChild().getNodeValue().equalsIgnoreCase("na")? 0.0 : Double.parseDouble(nd.getFirstChild().getNodeValue()));
	                  //System.out.println(nd.getNodeName()+" = "+nd.getFirstChild().getNodeValue()+" -- "+currDob.getExchangeSell());
	                 }
	                 else if(nd.getNodeName().equalsIgnoreCase("BID"))
	                 {
	                    currDob.setExchangeBuy(nd.getFirstChild().getNodeValue().equalsIgnoreCase("na")? 0.0 :Double.parseDouble(nd.getFirstChild().getNodeValue()));
	                  //System.out.println(nd.getNodeName()+" = "+nd.getFirstChild().getNodeValue()+" -- "+currDob.getExchangeBuy());
	                 }
	                 else if(nd.getNodeName().equalsIgnoreCase("DATE"))
	                 {
	                    currDob.setDate(getTimestampWithTime(nd.getFirstChild().getNodeValue()));
	                  //System.out.println(nd.getNodeName()+" = "+getTimestampWithTime(nd.getFirstChild().getNodeValue())+" -- "+currDob.getDate());
	                 }
	               }
	             }
	           }
	         }
	         else
	          {
	           if(null!=child.getFirstChild())
	             {
	             if(child.getNodeName().equalsIgnoreCase("EXPR"))
	               {
	               currDob.setConvTo(child.getFirstChild().getNodeValue());
	                  //System.out.println(child.getNodeName()+" = "+child.getFirstChild().getNodeValue()+" -- "+currDob.getConvFrom());
	               }
	             else if(child.getNodeName().equalsIgnoreCase("EXCH"))
	               {
	               currDob.setConvFrom(child.getFirstChild().getNodeValue());
	              //System.out.println(child.getNodeName()+" = "+child.getFirstChild().getNodeValue()+" -- "+currDob.getConvTo());
	               }
	             }
	          }
	        }
	        if(child.getNodeName().equalsIgnoreCase("EXPR"))
	          currArr.add(currDob);
	      }
	     // System.out.println("Size : "+currArr.size());
	   }
	    Logger.info(FILE_NAME,">>> getCurrencyDOBs ended.");
	   return currArr;
	 }
	 private HashMap getUrlProxyDatails()
	 {
		 Logger.info(FILE_NAME,">>> getUrlProxyDatails called.");
	    //System.out.println("In getUrlProxyData() ");
	    HashMap urlProxyDetails    =  null;
	    CurrencySessionHome home   =  null;
	    CurrencySession     remote =  null;
	    
	    try
	    {      
	      home        = (CurrencySessionHome)LookUpBean.getEJBHome("CurrencySession");
	      remote      = home.create();
	      urlProxyDetails = remote.getUrlProxyDetails();
	    }    
	    catch(Exception ex)
			{
	      ex.printStackTrace();
				Logger.error(FILE_NAME, " [error in handleNotification()] -> "+ex.toString());
			}
	    Logger.info(FILE_NAME,">>> getUrlProxyDatails end.");
	    return urlProxyDetails;
	 }
	 private static Timestamp getTimestampWithTime(String str1)
		{
		   //String str ="Tue, 30 Aug 2005 19:00:00 GMT";
		   String date = str1.substring(5,16);
		   String stringTime = str1.substring(17,25);
		   StringTokenizer       df       = null;
		   StringTokenizer       st       = null;
		   StringTokenizer       stTime   = null;
		   GregorianCalendar     gc       = null;
		   Timestamp             timeStamp= null;						

		   String[] months = {"","JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};

		   int year        = 0;
		   int month       = 0;
		   int day         = 0;
		   int hour        = 0;
		   int minute      = 0;
		   int seconds     = 0;
		   st               =  new  StringTokenizer(date," ");
		   df               =  new  StringTokenizer("dd MMM yyyy"," ");                   

		   while(df.hasMoreTokens())
			{
					 String dToken = df.nextToken();
					 String sToken = st.nextToken();
					 if(dToken.startsWith("y"))
							  { year = Integer.parseInt(sToken); }
					 else if(dToken.equalsIgnoreCase("MM"))
							  { month = Integer.parseInt(sToken); } 
					 else if(dToken.equalsIgnoreCase("MMM"))
						 {
							  for(int i=1;i<=13;i++)
							  {
								  if(months[i].equalsIgnoreCase(sToken))
									{
									  month = i;
									  break;
									}
							  }
						 }
					 else if(dToken.startsWith("d"))
						 { day = Integer.parseInt(sToken); }
		    }
		   try
		    {
			  if(stringTime.equals(""))
					  { stringTime = null; }
	       	  stTime =        new  StringTokenizer(stringTime,":");
			  if(stTime.hasMoreTokens())
					  { hour = Integer.parseInt(stTime.nextToken());}
			  if(stTime.hasMoreTokens())
					  { minute = Integer.parseInt(stTime.nextToken());}
			  if(stTime.hasMoreTokens())
					  { seconds = Integer.parseInt(stTime.nextToken()); }
		    }
	  catch(Exception e)
	  {
				hour            = 0;
				minute          = 0;
				seconds         = 0;
	  }
	  gc = new GregorianCalendar(year, month-1, day, hour, minute, seconds);
	  timeStamp = new Timestamp((gc.getTime()).getTime());
	  return timeStamp;
	 }
	 private boolean doURLRequest(String strURL, String strProxy, int iProxyPort)
		{
		 Logger.info(FILE_NAME,">>> doURLRequest called.");
			boolean rc = false;

			URL url = null;
			URLConnection c = null;

			try
			{
				//System.out.println("\nHTTP Request: " + strURL);

				URL urlOriginal = new URL(strURL);

				if ((null != strProxy) && (0 < strProxy.length()))
				{
					URL urlProxy = new URL(urlOriginal.getProtocol(),
					strProxy,
					iProxyPort,// A value of -1 means use the default port for the specified protocol.
					strURL);// The original URL is passed as "the file on the host".

					//System.out.println("Using Proxy: " + strProxy);
					if (-1 != iProxyPort)
					{
						//System.out.println("Using Proxy Port: " + iProxyPort);
						Logger.info(FILE_NAME,">>> Using Proxy Port: "+iProxyPort);
					}

					//url = urlProxy;Commented by govind for fxml
					
					url = urlOriginal;
				}
				else
				{
					url = urlOriginal;
				}
				 Logger.info(FILE_NAME,">>> URL"+url);
				c = url.openConnection();

				// In this example, we only consider HTTP connections.
				if (c instanceof HttpURLConnection)// instanceof returns true only if the object is not null.
				{//System.out.println("Im in if before connect()");

					Logger.info(FILE_NAME,">>> before Connect ");
					HttpURLConnection h = (HttpURLConnection) c;
					h.connect();
	               // System.out.println("Im in if after connect()");
					String strStatus = h.getResponseMessage() + " (" + h.getResponseCode() + ")";
					Logger.info(FILE_NAME,">>> h.getResponseMessage():"+h.getResponseMessage());
					Logger.info(FILE_NAME,">>> h.getResponseCode():"+h.getResponseCode());
					//System.out.println("HTTP Status: " + strStatus);

					//System.out.println("HTTP Response Headers: "); 

					// Evidently, index 0 always returns null, so we start with index 1.
					for (int i = 1; ; i++)
					{
						String strKey = h.getHeaderFieldKey(i);
						if (null == strKey)
						{
							break;
						}
						//System.out.println(i + ": " + strKey + ": " + h.getHeaderField(i));
					}

					// Normally at this point, one would download data from the connection.
					// For example, if the MIME type is xml, then download the string.
					String strContentType = h.getContentType();			
					
					Logger.info(FILE_NAME,">>> strContentType:"+strContentType);
					Logger.info(FILE_NAME,">>> getResponseCode:"+h.getResponseCode());
				  if ((null != strContentType) && (0 == strContentType.compareTo("text/xml")))
					{					
							int iNumLines = 0;

							try
							{
								InputStream in = h.getInputStream();
								BufferedReader data = new BufferedReader(new InputStreamReader(in));
								String str = null;
								BufferedWriter out = new BufferedWriter(new FileWriter("fxml.xml"));
								while ((str = data.readLine()) != null) {          
								out.write(str);
								iNumLines++;
									}
									out.close();
									in.close();
	                data.close();
							}
							catch(Exception exc2)
							{
								//System.out.println("**** IO failure: " + exc2.toString());
								Logger.error(FILE_NAME, " [error in doURLRequest()] -> "+exc2.toString());
							}
							finally
							{							
								//System.out.println("Received text/xml has " + iNumLines + " lines");
								Logger.info(FILE_NAME,">>> Received text/xml has "+ iNumLines + " lines");
							}
					}
				  h.disconnect();
				}
				else
				{
					//System.out.println("**** No download: connection was not HTTP");
					Logger.info(FILE_NAME,">>> No download: connection was not HTTP ");
				}
				rc = true;
			}
			// Catch all exceptions.
			catch(Exception exc)
			{
				//System.out.println("**** Connection failure: " + exc.toString());
				Logger.info(FILE_NAME,">>> in catch Connect ");
				Logger.error(FILE_NAME, " [error in doURLRequest()] -> "+exc.toString());
				// System.out.println("**** Connection failure: " + exc.getMessage());// Same as above line but without the exception class name.
			}
			finally
			{
				// Do cleanup here.
				// For example, the following, in theory, could make garbage collection more efficient.
				// This might be the place where you choose to put your method call to your connection's "disconnect()";
				// curiously, while every URLConnection has a connect() method, they don't necessarily have a disconnect() method.
				// HttpURLConnection has a disconnect() which is called above.
				c = null;
				url = null;

				
			}
			Logger.info(FILE_NAME,">>> doURLRequest end ");
	    return rc;
		}
	
			
}
