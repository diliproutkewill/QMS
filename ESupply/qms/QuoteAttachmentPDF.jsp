<%@ page import="org.apache.log4j.Logger,
                                     java.util.ArrayList" buffer="1000kb" autoFlush="true" %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME = "QuoteAttachmentPDF.jsp ";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);
	ArrayList  af= (ArrayList)session.getAttribute("filepdf");
	int  bufferindex   =Integer.parseInt( request.getParameter("bufferno"));
	//System.out.println("af--------------------"+af);
//	System.out.println("bufferindex--------------------"+bufferindex);
	logger.info("pdf");
    byte[] bytebuffer = ( byte[])af.get(bufferindex);
//	session.removeAttribute("filepdf");
	logger.info("in pdf"+bytebuffer);
   java.io.OutputStream outStream = response.getOutputStream();
	 response.setContentType( "application/pdf" );	 
	outStream.write(bytebuffer);
	 outStream.flush();
	 outStream.close(); 
%>