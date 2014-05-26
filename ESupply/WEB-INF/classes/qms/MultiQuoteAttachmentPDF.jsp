<%@ page import="org.apache.log4j.Logger" buffer="1000kb" autoFlush="true" %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME = "QuoteAttachmentPDF.jsp ";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);
	logger.info("pdf");
    byte[] bytebuffer = ( byte[])session.getAttribute("filepdf");
	session.removeAttribute("filepdf"); 
	logger.info("in pdf"+bytebuffer);
   java.io.OutputStream outStream = response.getOutputStream();
	 response.setContentType( "application/pdf" );	 
	outStream.write(bytebuffer);
	 outStream.flush();
	 outStream.close(); 
%>