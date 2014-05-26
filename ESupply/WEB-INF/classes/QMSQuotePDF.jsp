<%@ page buffer="32kb" autoFlush="true"%>

<%
   System.out.println("request.getParameter(print)"+request.getParameter("print"));
   //if("on".equalsIgnoreCase(request.getParameter("print")))
   //{
   
    java.io.File f = (java.io.File)session.getAttribute("QuoteOuptutStream");
	  session.removeAttribute("QuoteOuptutStream");
    
    //response.setContentLength((int)f.length());
	  response.setHeader("Content-Disposition","filename="+f.getName());
	  //response.setHeader("Cache-Control", "no-cache"); 
   
   java.io.OutputStream outStream = response.getOutputStream();
	 response.setContentType( "application/pdf" );	 
	 System.out.println("File length ::"+f.length());
	 System.out.println("File Path ::"+f.getPath());	
	 
	 byte[] buf = new byte[9216];
	 
	 java.io.FileInputStream inStream = new java.io.FileInputStream(f);
	 int sizeRead = 0;
	 while ((sizeRead = inStream.read(buf, 0, buf.length)) > 0) {
		
		outStream.write(buf, 0, sizeRead);
	 }
	 inStream.close();
	 outStream.flush();
	 outStream.close(); //added by sanjay on 20/03/2006    
     f.delete();
	//}
%>