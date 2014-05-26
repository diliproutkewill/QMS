<!--@@Added by kiran.v on 19/09/2011 for Wpbn Issue 271485 -->
<%@ page buffer="16kb" autoFlush="true"%>

<%
   System.out.println("request.getParameter(print)"+request.getParameter("print"));
   java.io.File f = (java.io.File)session.getAttribute("AbbrivationOuptutStream");
	 session.removeAttribute("AbbrivationOuptutStream");
	 response.setHeader("Content-Disposition","filename="+f.getName());
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
	 outStream.close();    
	 f.delete();
%>